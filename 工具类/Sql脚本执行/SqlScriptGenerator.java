package com.ping.job.cover.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author liuwei
 * @date 2020-08-16 19:10
 * @desc sql脚本生成器
 */
public class SqlScriptGenerator {

    /**
     * 数据类型枚举
     */
    public enum DataType {
        NUMBER, STRING, DATETIME
    }

    //一条sql语句最多包含的数据行数
    private static final int BATCH_SIZE = 100;

    private static final String SCRIPT_SUFFIX = ".sql";

    private static final String DEFAULT_SCRIPT_DIR = "sql_auto_gen";

    private static final String BIG_FILE_DIR = DEFAULT_SCRIPT_DIR + File.separator + "big_file";

    //大文件分割小文件时的默认分割行数
    private static final int DFT_ROW_NUM = 10000;

    private static final int _1MB = 1 << 20;

    private static final String DFT_CHAR_SET = "UTF-8";

    private static final String LINE_SEP = System.lineSeparator();

    private static final String LINE_SEP_AGAIN = LINE_SEP+LINE_SEP;

    private static final String SEMICOLON = ";";

    private static final String COMMA = ",";

    private static final String QUOTATION = "'";

    private static final String LEFT_BUTT = "(";

    private static final String RIGHT_BUTT = ")";



    /**
     * 对一个大文本文档生成批量的sql语句脚本
     * <p>
     * 文本文档每一行的数据格式形如：
     * 列1值分隔符列2值分隔符列3值分隔符...
     * 不支持列使用默认值(特别是null值)，即，所有的指定列都必须给定值
     * 支持字符串类型列值为空字符串值，对于字符串类型空值，补''
     * 对于其它类型，不允许空值，否则生成的sql要么是编译时错误，要么是运行时错误
     * 如数字空值，values (a,,c)，编译时错误
     * 如日期时间空值，values (a,'',c)，运行时类型错误
     * <p>
     * 每DFT_ROW_NUM行生成一个小的sql脚本，数据形如：
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * ...
     *
     * @param originFile      文本文档文件
     * @param separator       文档中每一列数据的分隔符
     * @param tableName       生成的表名
     * @param columnNameTypes 生成的列名及其数据类型，使用LinkedHashMap保证顺序
     * @param createId        是否需要创建32位UUID
     * @return 分割的小文件相对路径 & 生成的sql脚本绝对路径
     * @throws IOException 文件流读写失败时抛出异常
     */
    private static Map<String, List<String>> bigTextToScript(File originFile, String separator, String tableName,
                                                             LinkedHashMap<String, DataType> columnNameTypes,
                                                             boolean createId) throws IOException {
        Map<String, List<String>> map = new HashMap<>();

        //切割小文件
        String originFilePath = originFile.getAbsolutePath();
        String targetFilePrefix = tableName;
        List<String> smallFiles = FileSplitUtil.bigFileSplit(originFilePath, BIG_FILE_DIR, targetFilePrefix, DFT_ROW_NUM);

        List<String> scriptFilePaths = new ArrayList<>();
        map.put("originSmallFilePaths", smallFiles);

        //遍历每一个小文件生成小脚本
        for (String smallFile : smallFiles) {
            String scriptFilePath = BIG_FILE_DIR + File.separator + smallFile.substring(smallFile.lastIndexOf("/") + 1);
            scriptFilePath = textToScript(new File(smallFile), separator, tableName, columnNameTypes, createId, scriptFilePath);
            scriptFilePaths.add(scriptFilePath);
        }
        map.put("scriptFilePaths", scriptFilePaths);

        return map;
    }

    /**
     * 对一个list生成sql语句脚本
     * <p>
     * 不支持列使用默认值(但允许null值，null值会转换为空字符串)，即，所有的指定列都必须给定值
     * 支持字符串类型列值为null值或空字符串值，对于字符串类型空值，补''
     * 对于其它类型，不允许空值，否则生成的sql会产生运行时错误
     * 如数字和日期时间空值，values (a,'',c)，运行时类型错误
     * <p>
     * 生成的sql脚本数据形如：
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * ...
     *
     * @param dataList        list数据
     * @param tableName       生成的表名
     * @param columnNameTypes 生成的列名及其数据类型，使用LinkedHashMap保证顺序
     * @param createId        是否需要创建32位UUID
     * @param scriptFilePath  生成的sql脚本路径，支持绝对路径和相对路径，以.sql为后缀
     *                        允许为null值，当为null值时，返回路径为当前程序启动目录下的sql_auto_gen/${tableName}.sql
     *                        如果传参后缀非.sql，会自动拼接此后缀
     * @return 生成的sql脚本的绝对路径
     * @throws IOException 文件流读写失败时抛出异常
     */
    public static String listToScript(List<Map<String, Object>> dataList,
                                      String tableName, LinkedHashMap<String, DataType> columnNameTypes, boolean createId,
                                      String scriptFilePath) throws IOException {
        scriptFilePath = scriptFilePathCheck(scriptFilePath, tableName);

        //获取列名以及每一列是否需要添加引号
        List<String> columnNames = new ArrayList<>();
        List<Boolean> columnQuotation = new ArrayList<>();
        Set<Map.Entry<String, DataType>> entrySet = columnNameTypes.entrySet();
        Iterator<Map.Entry<String, DataType>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DataType> columnNameType = iterator.next();
            columnNames.add(columnNameType.getKey());
            //只要数据类型不是number，都需要添加引号
            columnQuotation.add(DataType.NUMBER != columnNameType.getValue());
        }

        //创建文件通道写入scriptFilePath
        RandomAccessFile scriptFile = new RandomAccessFile(scriptFilePath, "rw");
        // 文件的通道
        FileChannel outChannel = scriptFile.getChannel();
        // 缓冲区对象(不支持字符缓冲区，使用字节缓冲区，并设置缓冲区大小，该缓冲区直接向系统申请)
        ByteBuffer buf = ByteBuffer.allocate(_1MB*5);

        //写入一些备注
        String remark = "-- script automatic generation at " + LocalDateTime.now() + LINE_SEP+
                "-- data from a list and size is " + dataList.size() + LINE_SEP_AGAIN;
        flushStringValue(remark,outChannel,buf,scriptFile);

        //创建sql语句头
        String header = getSqlHeader(tableName,createId,columnNames);

        //创建sqlBuilder
        StringBuilder sqlBuilder = new StringBuilder(header);

        //遍历每一条数据，生成sql语句
        int count = 0;
        for (Map<String, Object> dataMap : dataList) {
            //转为sql value语句
            sqlBuilder.append(LEFT_BUTT);
            if (createId) {
                sqlBuilder.append(QUOTATION).append(getUUID()).append("',");
            }
            for (int i = 0; i < columnNames.size() - 1; i++) {
                sqlBuilder.append(wrapValue(getStringTrans(dataMap.get(columnNames.get(i))), columnQuotation.get(i))).append(COMMA);
            }
            sqlBuilder.append(wrapValue(getStringTrans(dataMap.get(columnNames.get(columnNames.size() - 1))), columnQuotation.get(columnNames.size() - 1))).append(RIGHT_BUTT);
            dataMap = null;
            //拼接分隔符并换行
            if (++count < BATCH_SIZE) {
                sqlBuilder.append(COMMA).append(LINE_SEP);
            } else {
                //批量写入文件
                count = 0;
                sqlBuilder.append(SEMICOLON).append(LINE_SEP_AGAIN);
                flushStringValue(sqlBuilder.toString(),outChannel,buf,scriptFile);
                //重置sqlBuilder
                sqlBuilder = new StringBuilder(header);
            }
        }
        //拼接残余的不满BATCH_SIZE的sql并写入文件
        if (count > 0) {
            String lastSql = sqlBuilder.toString();
            int lastComma = lastSql.lastIndexOf(COMMA);
            flushStringValue(lastSql.substring(0, lastComma) + SEMICOLON + LINE_SEP,outChannel,buf,scriptFile);
        }

        /**
         * 关闭通道和文件
         * 实际上JDK 7+ 会自动关闭，此处可以省略，但保留会加速流的关闭
         */
        outChannel.close();
        scriptFile.close();

        return new File(scriptFilePath).getAbsolutePath();
    }


    /**
     * 对一个文本文档生成sql语句脚本
     * <p>
     * 文本文档每一行的数据格式形如：
     * 列1值分隔符列2值分隔符列3值分隔符...
     * 不支持列使用默认值(特别是null值)，即，所有的指定列都必须给定值
     * 支持字符串类型列值为空字符串值，对于字符串类型空值，补''
     * 对于其它类型，不允许空值，否则生成的sql要么是编译时错误，要么是运行时错误
     * 如数字空值，values (a,,c)，编译时错误
     * 如日期时间空值，values (a,'',c)，运行时类型错误
     * <p>
     * 生成的sql脚本数据形如：
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * insert into 表名 (列1,列2,列3,...) values
     * (值1,值2,值3,...),
     * ...
     * (值1,值2,值3,...);
     * <p>
     * ...
     *
     * @param originFile      文本文档文件
     * @param separator       文档中每一列数据的分隔符
     * @param tableName       生成的表名
     * @param columnNameTypes 生成的列名及其数据类型，使用LinkedHashMap保证顺序
     * @param createId        是否需要创建32位UUID
     * @param scriptFilePath  生成的sql脚本路径，支持绝对路径和相对路径，以.sql为后缀
     *                        允许为null值，当为null值时，返回路径为当前程序启动目录下的sql_auto_gen/${tableName}.sql
     *                        如果传参后缀非.sql，会自动拼接此后缀
     * @return 生成的sql脚本的绝对路径
     * @throws IOException 文件流读写失败时抛出异常
     */
    public static String textToScript(File originFile, String separator,
                                      String tableName, LinkedHashMap<String, DataType> columnNameTypes, boolean createId,
                                      String scriptFilePath) throws IOException {
        scriptFilePath = scriptFilePathCheck(scriptFilePath, tableName);

        //获取列名以及每一列是否需要添加引号
        List<String> columnNames = new ArrayList<>();
        List<Boolean> columnQuotation = new ArrayList<>();
        Set<Map.Entry<String, DataType>> entrySet = columnNameTypes.entrySet();
        Iterator<Map.Entry<String, DataType>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DataType> columnNameType = iterator.next();
            columnNames.add(columnNameType.getKey());
            //只要数据类型不是number，都需要添加引号
            columnQuotation.add(DataType.NUMBER != columnNameType.getValue());
        }

        //创建流写入scriptFilePath
        //创建文件通道写入scriptFilePath
        RandomAccessFile scriptFile = new RandomAccessFile(scriptFilePath, "rw");
        // 文件的通道
        FileChannel outChannel = scriptFile.getChannel();
        // 缓冲区对象(不支持字符缓冲区，使用字节缓冲区，并设置缓冲区大小，该缓冲区直接向系统申请)
        ByteBuffer buf = ByteBuffer.allocate(_1MB*5);

        //写入一些备注
        String remark = "-- script automatic generation at " + LocalDateTime.now() + LINE_SEP+
                "-- data from originFile " + originFile.getAbsolutePath() + LINE_SEP_AGAIN;
        flushStringValue(remark,outChannel,buf,scriptFile);

        //创建流读取originFile
        InputStreamReader read = new InputStreamReader(new FileInputStream(originFile), DFT_CHAR_SET);
        BufferedReader reader = new BufferedReader(read);


        //创建sql语句头
        String header = getSqlHeader(tableName,createId,columnNames);

        //创建sqlBuilder
        StringBuilder sqlBuilder = new StringBuilder(header);

        //遍历读取每一行数据，生成sql语句
        String lineTxt;
        String[] fieldValue;
        int count = 0;
        int total = 0;
        while ((lineTxt = reader.readLine()) != null) {
            total++;
            //转为sql value语句
            sqlBuilder.append(LEFT_BUTT);
            if (createId) {
                sqlBuilder.append(QUOTATION).append(getUUID()).append("',");
            }
            fieldValue = lineTxt.split(separator);
            for (int i = 0; i < fieldValue.length - 1; i++) {
                sqlBuilder.append(wrapValue(fieldValue[i], columnQuotation.get(i))).append(COMMA);
            }
            sqlBuilder.append(wrapValue(fieldValue[fieldValue.length - 1], columnQuotation.get(fieldValue.length - 1))).append(RIGHT_BUTT);

            //拼接分隔符并换行
            if (++count < BATCH_SIZE) {
                sqlBuilder.append(COMMA).append(LINE_SEP);
            } else {
                //批量写入文件
                count = 0;
                sqlBuilder.append(SEMICOLON).append(LINE_SEP_AGAIN);
                flushStringValue(sqlBuilder.toString(),outChannel,buf,scriptFile);
                //重置sqlBuilder
                sqlBuilder = new StringBuilder(header);
            }
        }
        //拼接残余的不满BATCH_SIZE的sql并写入文件
        if (count > 0) {
            String lastSql = sqlBuilder.toString();
            int lastComma = lastSql.lastIndexOf(COMMA);
            flushStringValue(lastSql.substring(0, lastComma) + SEMICOLON + LINE_SEP,outChannel,buf,scriptFile);
        }

        //统计数据的总行数
        flushStringValue(LINE_SEP + "-- totalRowNum " + total + LINE_SEP + LINE_SEP,outChannel,buf,scriptFile);

        return new File(scriptFilePath).getAbsolutePath();

    }

    /**
     * 脚本路径检查 & 文件内容清空，防止同一个文件使用不同的通道多次写入内容时追加脏数据
     * @param scriptFilePath
     * @param tableName
     * @return
     */
    private static String scriptFilePathCheck(String scriptFilePath, String tableName) {
        //路径检查
        if (null == scriptFilePath || "".endsWith(scriptFilePath)) {
            File sqlAutoGenDir = new File(DEFAULT_SCRIPT_DIR);
            if (!sqlAutoGenDir.exists()) {
                sqlAutoGenDir.mkdirs();
            }
            scriptFilePath = DEFAULT_SCRIPT_DIR + File.separator + tableName + SCRIPT_SUFFIX;
        }
        if (!scriptFilePath.endsWith(SCRIPT_SUFFIX)) {
            scriptFilePath = scriptFilePath + SCRIPT_SUFFIX;
        }
        //如果文件存在则删除
        File scriptFile = new File(scriptFilePath);
        if (scriptFile.exists()) {
            scriptFile.delete();
        }
        return scriptFilePath;
    }

    /**
     * 对一个字符串添加单引号
     *
     * @param value       一个字符串
     * @param isQuotation 是否添加单引号
     * @return
     */
    private static String wrapValue(String value, Boolean isQuotation) {
        return isQuotation ? QUOTATION + value + QUOTATION : value;
    }

    /**
     * 获取一个32位字母全大写的UUID
     */
    private static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    //对象转换String，空值全部转为空字符串
    private static String getStringTrans(Object obj) {
        return null == obj ? "" : String.valueOf(obj);
    }

    //创建sql语句头
    private static String getSqlHeader(String tableName,boolean createId,List<String> columnNames) {
        StringBuilder headerBuilder = new StringBuilder("insert into " + tableName + " (");
        if (createId) {
            headerBuilder.append("id,");
        }
        for (int i = 0; i < columnNames.size() - 1; i++) {
            headerBuilder.append(columnNames.get(i) + COMMA);
        }
        headerBuilder.append(columnNames.get(columnNames.size() - 1) + ") values ").append(LINE_SEP);
        return headerBuilder.toString();
    }

    /**
     * 文件通道追加写入字符串，并清空字节缓存
     * @param value String字符串
     * @param fileChannel 文件通道
     * @param buf 字节缓存
     * @param file 通道指向的文件
     * @throws IOException 流写入异常
     */
    private static void flushStringValue(String value, FileChannel fileChannel, ByteBuffer buf, RandomAccessFile file) throws IOException {
        // 1.字符串转为字节数组，并写入buf
        buf.put(value.getBytes(DFT_CHAR_SET));
        /**
         * 2.切换buffer从写模式到读模式，准备读取buf到文件
         * buf是读写双向的，读和写的下标明显是不同的
         * 默认为写模式，在需要读时，必须切换为读模式
         */
        buf.flip();
        // 3.通道从缓冲区读数据(到文件)
        fileChannel.write(buf);
        /**
         * 4.清空buffer
         * 在下一次读写之前，必须清空buffer
         * 一是由于flip已经把buffer的position切换为0，下次会从0开始写，如果不清除buffer，下次几乎一定会读入尾部的脏数据
         * 二是防止内存溢出，buffer在空间不足以写入新的数据时会抛出内存溢出错误
         */
        buf.clear();
        /**
         * 5.指定通道的当前位置为文件末尾
         * 默认当前位置为0，即同一个通道，每次都会从下标0开始写入，即从头开始覆盖已经写入的字节
         * 此处需要追加写入，需指定通道位置为文件末尾
         */
        fileChannel.position(file.length());
    }

}
