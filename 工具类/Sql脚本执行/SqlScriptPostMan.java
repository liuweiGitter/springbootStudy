package com.ping.job.cover.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwei
 * @date 2019-07-17 15:48
 * @desc sql脚本执行中心
 * <p>
 * 需引入ant和c3p0的jar依赖，如
 * <!-- https://mvnrepository.com/artifact/org.apache.ant/ant -->
 * <dependency>
 * <groupId>org.apache.ant</groupId>
 * <artifactId>ant</artifactId>
 * <version>1.10.7</version>
 * </dependency>
 * <!-- https://mvnrepository.com/artifact/com.mchange/c3p0 -->
 * <dependency>
 * <groupId>com.mchange</groupId>
 * <artifactId>c3p0</artifactId>
 * <version>0.9.5.4</version>
 * </dependency>
 */
@Slf4j
public class SqlScriptPostMan {

    private static final String ERROR_LOG_PATH = "logs/sql/error/";

    private static final DateTimeFormatter DFT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    //获取当前时间
    public static String nowFormatter() {
        return LocalDateTime.now().format(DFT_FORMATTER);
    }


    /**
     * 执行sql脚本，支持多sql脚本执行，支持指定数据源
     * 使用mybatis ScriptRunner执行sql脚本，sql脚本一行出错会忽略而继续执行下一行，sql脚本一个出错会忽略而继续执行下一个脚本
     * 最终的执行结果：允许存在数据丢失，错误不支持回滚
     * >>>>>>保存错误日志，记录执行错误的sql语句，日志文件如果不存在会自动创建，如果存在，会覆盖写入，即使没有错误抛出也会覆盖写入空字符(文件长度为0)
     *
     * @param dataSource 数据源
     * @param scriptPath sql脚本文件路径，支持绝对路径和相对路径
     * @return
     */
    public static boolean executeScriptsSkipErrors(DataSource dataSource, String... scriptPath) {
        boolean withoutException = true;
        Connection conn;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("数据库连接异常：", e);
            return false;
        }
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setLogWriter(null);//不输出运行日志，只输出错误日志
        //如果拼接整个脚本为一条sql，无论是否true，遇到错误时都停止，但不影响出错前没有问题的sql语句段的执行和提交
        //如果不拼接整个脚本，true时遇到错误停止，但不影响出错前没有问题的sql语句的执行和提交，false时则会跳过所有错误而执行所有可以执行的sql
        runner.setStopOnError(false);
        //是否提交，为true时无论是否在拼接整个脚本为一条sql，都会在最后统一提交，为false时会执行connection.rollback()，但实际也并没有回滚
        runner.setAutoCommit(true);
        //拼接整个脚本为一条sql执行
        //与不拼接的区别在于，前者是一条条执行，后者是拼接后一次性执行
        //如果想要忽略所有错误sql而执行所有可执行的sql，就不能拼接
        //即使sql语句没有语法错误也没有逻辑错误，大量执行sql的话也可能会有部分错误，因此，最好不要拼接
        runner.setSendFullScript(false);
        int success = scriptPath.length;
        String errorList = "";
        File file = new File(ERROR_LOG_PATH);
        if (!file.exists()) {
            file.mkdirs();//创建多层次不存在的目录
        }
        for (String pathX : scriptPath) {
            PrintWriter errorWriter = null;
            Reader pathXReader = null;
            String errorLogFileName = null;
            try {
                //输出错误日志：日志路径(到目录)必须存在
                errorLogFileName = ERROR_LOG_PATH + pathX.substring(pathX.lastIndexOf(File.separator) + 1 ) + "_"+ nowFormatter();
                errorWriter = new PrintWriter(errorLogFileName);
                runner.setErrorLogWriter(errorWriter);
                //执行脚本文件
                log.info(">>>>>开始执行脚本 {}",pathX);
                pathXReader = new FileReader(pathX);
                runner.runScript(pathXReader);
                log.info(">>>>>脚本 {} 执行完成",pathX);
            } catch (FileNotFoundException e) {
                log.error("错误日志输出文件未找到：", e);
            } finally {
                //关闭文件：否则将持续占用引用而无法删除
                if (null != errorWriter) {
                    errorWriter.close();
                }
                if (null != errorWriter) {
                    try {
                        pathXReader.close();
                    } catch (IOException e) {
                        log.error("脚本文件{}关闭失败：", pathX, e);
                    }
                }
                //删除脚本文件和空日志文件
                new File(pathX).delete();
                if (new File(errorLogFileName).length() > 0) {
                    errorList += "\n\t" + errorLogFileName;
                    success--;
                }else{
                    new File(errorLogFileName).delete();
                }
            }
        }
        //关闭runner和conn
        runner.closeConnection();
        try {
            conn.close();
        } catch (SQLException e) {
            withoutException = false;
            log.error("sql连接关闭异常：", e);
        }
        log.info(">>>>>脚本执行完成<<<<<");
        log.info("脚本总数：" + scriptPath.length + " 成功执行总数：" + success);
        if (!errorList.equals("")) {
            withoutException = false;
            log.info("错误日志列表：" + errorList);
        }
        return withoutException;
    }

    /**
     * 执行sql脚本，仅支持c3p0类型数据源
     * 使用ant SQLExec执行sql脚本，sql脚本一行出错会整个脚本回滚
     * 最终的执行结果：不存在数据丢失，要么全部执行，要么全部回滚
     *
     * @param dataSource 数据源
     * @param scriptPath sql脚本文件路径，支持绝对路径和相对路径
     * @return
     */
    public static boolean executeScriptWithTransaction(DataSource dataSource, String scriptPath) {
        ComboPooledDataSource dataSourceCp = (ComboPooledDataSource) dataSource;
        Map<String, String> dbMap = new HashMap<>();
        //设置数据库参数
        dbMap.put("driverClass", dataSourceCp.getDriverClass());
        dbMap.put("url", dataSourceCp.getJdbcUrl());
        dbMap.put("user", dataSourceCp.getUser());
        dbMap.put("password", dataSourceCp.getPassword());
        return executeScriptWithTransaction(dbMap, scriptPath);
    }

    /**
     * 执行sql脚本，支持指定db配置
     * 使用ant SQLExec执行sql脚本，sql脚本一行出错会整个脚本回滚
     * 最终的执行结果：不存在数据丢失，要么全部执行，要么全部回滚
     *
     * @param dbMap      数据源配置，key值：driverClass、url、user、password
     * @param scriptPath sql脚本文件路径，支持绝对路径和相对路径
     * @return
     */
    public static boolean executeScriptWithTransaction(Map<String, String> dbMap, String scriptPath) {
        SQLExec sqlExec = new SQLExec();
        //设置数据库参数
        sqlExec.setDriver(dbMap.get("driverClass"));
        sqlExec.setUrl(dbMap.get("url"));
        sqlExec.setUserid(dbMap.get("user"));
        sqlExec.setPassword(dbMap.get("password"));
        //要执行的脚本
        sqlExec.setSrc(new File(scriptPath));
        //有出错的语句该如何处理：支持"continue", "stop", "abort"，默认abort
        //abort会抛出异常，返回码为1，stop不会抛出异常，返回码为0
        //continue则会忽略错误而继续运行，会执行和提交所有可正确执行的sql语句
        sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
        //输出执行日志到文件中，默认输出到控制台
        sqlExec.setPrint(true);//必须设置为true才会输出到文件
        File file = new File(ERROR_LOG_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File logFile = new File(file, scriptPath.substring(scriptPath.lastIndexOf(File.separator) + 1) + "_"+ nowFormatter());
        sqlExec.setOutput(logFile);
        //必须初始化project属性，否则空指针
        sqlExec.setProject(new Project());
        try {
            //执行脚本文件
            log.info(">>>>>开始执行脚本 {}",scriptPath);
            sqlExec.execute();
            log.info(">>>>>脚本 {} 成功执行<<<<<",scriptPath);
            //删除日志文件
            logFile.delete();
        } catch (BuildException e) {
            log.error(">>>>>脚本 {} SQL执行异常，全部回滚<<<<<",scriptPath);
            log.error(e.getMessage());
            /**
             * 比较坑爹的是，异常错误不会记录到日志里，需要自己捕获来写入
             * 而且在错误时不能打印完整的sql语句，但会有错误的明显提示，如：
             * com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Incorrect datetime value: 'test' for column 'createTime' at row 100
             */
            appendMsgToFile(logFile,e.getMessage());
            log.error("错误日志路径：" + logFile.getAbsolutePath());
            return false;
        } finally {
            //删除脚本文件
            new File(scriptPath).delete();
        }
        return true;
    }

    /**
     * 向文件追加写入消息
     * @param file
     * @param msg
     */
    private static void appendMsgToFile(File file, String msg) {
        try {
            OutputStreamWriter writer = new FileWriter(file,true);
            writer.write(msg);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("文件 {} 写入失败！",file.getAbsolutePath());
        }
    }


}
