package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.util.*;

/**
 * @author liuwei
 * @date 2019-07-16 10:03
 * @desc 读取本地classpath目录下文件
 */
@Slf4j
public class LocalFileReader {

    private LocalFileReader(){throw new IllegalStateException("Utility class");}

    private static String classpath;
    static{
        //获取项目编译后的classpath路径
        try {
            classpath = ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException e) {
            log.error("classpath路径读取异常",e);
        }
    }

    public static String getClassPath() {
        //获取classpath路径，以/结尾
        return classpath;
    }

    public static String getFileAbsolutePath(String pathRelativeClassPath) {
        //获取目标文件绝对路径：classpath以/结尾，相对路径可以以/打头，也可以去掉/，建议去掉/
        return classpath+(pathRelativeClassPath.startsWith("/")?pathRelativeClassPath.substring(1):pathRelativeClassPath);
    }

    public static File getFile(String pathRelativeClassPath) {
        //获取目标文件
        return new File(classpath+pathRelativeClassPath);
    }

    //*******************************************************
    // 读取文件流：字节流或字符流
    //*******************************************************

    public static InputStream getFileInputStream(String pathRelativeClassPath) {
        //获取目标文本文件输入字节流
        /*try {
            //对于war包，绝对路径是可取的，但是jar包只能取相对路径
            return new FileInputStream(classpath+pathRelativeClassPath);
        } catch (FileNotFoundException e) {
            return logIOException(e);
        }*/
        return LocalFileReader.class.getClassLoader().getResourceAsStream(pathRelativeClassPath);
    }

    public static InputStream getDataInputStream(String pathRelativeClassPath) {
        //获取目标二进制文件输入字节流
        try {
            return new DataInputStream(new FileInputStream(classpath+pathRelativeClassPath));
        } catch (FileNotFoundException e) {
            return logIOException(e);
        }
    }

    public static InputStreamReader getFileInputStreamReader(String pathRelativeClassPath) {
        //获取目标文件输入字符流
        try {
            return new FileReader(classpath+pathRelativeClassPath);
        } catch (FileNotFoundException e) {
            return logIOException(e);
        }
    }

    public static Reader getBufferedReader(String pathRelativeClassPath) {
        //获取目标文件输入字符流
        try {
            return new BufferedReader(new FileReader(classpath+pathRelativeClassPath));
        } catch (FileNotFoundException e) {
            return logIOException(e);
        }
    }

    //*******************************************************
    // 读取json文件：读取的文件不允许出现空行、注释行等，否则在解析为JSONObject等对象时会报错
    //*******************************************************

    public static List<Map> getListMapFromJson(String pathRelativeClassPath) {
        return getListFromJson(pathRelativeClassPath,Map.class);
    }

    public static <T> List<T> getListFromJson(String pathRelativeClassPath,Class<T> entityClz) {
        //解析json文件为数组
        InputStream inputStream = getFileInputStream(pathRelativeClassPath);
        try {
            String text = IOUtils.toString(inputStream,"utf8");
            return JSON.parseArray(text, entityClz);
        } catch (IOException e) {
            return logIOException(e);
        } finally {
            closeJsonStream(inputStream);
        }
    }

    public static JSONArray getJSONArray(String pathRelativeClassPath) {
        //解析json文件为数组
        InputStream inputStream = getFileInputStream(pathRelativeClassPath);
        try {
            String text = IOUtils.toString(inputStream,"utf8");
            return JSON.parseArray(text);
        } catch (IOException e) {
            return logIOException(e);
        } finally {
            closeJsonStream(inputStream);
        }
    }

    public static Map getMapFromJson(String pathRelativeClassPath) {
        return getObjectFromJson(pathRelativeClassPath,Map.class);
    }

    public static Map getOrderMapFromJson(String pathRelativeClassPath) {
        return getObjectFromJson(pathRelativeClassPath,LinkedHashMap.class);
    }

    public static <T> T getObjectFromJson(String pathRelativeClassPath,Class<T> entityClz) {
        //解析json文件为对象
        InputStream inputStream = getFileInputStream(pathRelativeClassPath);
        try {
            String text = IOUtils.toString(inputStream,"utf8");
            return JSON.parseObject(text, entityClz);
        } catch (IOException e) {
            return logIOException(e);
        } finally {
            closeJsonStream(inputStream);
        }
    }

    public static JSONObject getJSONObject(String pathRelativeClassPath) {
        //解析json文件为对象
        InputStream inputStream = getFileInputStream(pathRelativeClassPath);
        try {
            String text = IOUtils.toString(inputStream,"utf8");
            return JSON.parseObject(text);
        } catch (IOException e) {
            return logIOException(e);
        } finally {
            closeJsonStream(inputStream);
        }
    }

    /**
     * 读取映射型json文件，如keyA1=keyB1、keyA2=keyB2、...
     * @param pathRelativeClassPath 相对路径
     * @param isOrder 是否按序生成数组
     * @return
     */
    public static String[][] getKeyTransArray(String pathRelativeClassPath,boolean isOrder) {
        Map<String,String> map;
        if (isOrder) {
            map = getObjectFromJson(pathRelativeClassPath,LinkedHashMap.class);
        }else{
            map = getMapFromJson(pathRelativeClassPath);
        }
        return getKeyTransArray(map);
    }

    /**
     * 读取映射型json文件，如name1:{keyA1=keyB1、keyA2=keyB2、...}、name2:{keyA1=keyB1、keyA2=keyB2、...}、...
     * @param pathRelativeClassPath 相对路径
     * @return 排序后的key--keyTrans对
     */
    public static Map<String,String[][]> getKeyTransArrayMap(String pathRelativeClassPath) {
        Map<String,JSONObject> map = getMapFromJson(pathRelativeClassPath);
        if (map.size()==0) {
            return new HashMap<>();
        }
        Map<String,String[][]> resultMap = new HashMap<>();
        Set<Map.Entry<String,JSONObject>> set = map.entrySet();
        Iterator<Map.Entry<String,JSONObject>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,JSONObject> row = iterator.next();
            String rowName = row.getKey();
            //LinkedHashMap无法被JSON反序列化(com.alibaba.fastjson.JSONException: can not get javaBeanDeserializer. java.util.LinkedHashMap)，需要间接转换
            //LinkedHashMap rowMap = row.getValue().toJavaObject(LinkedHashMap.class);
            LinkedHashMap rowMap = JSONObject.parseObject(row.getValue().toJSONString(),LinkedHashMap.class);
            resultMap.put(rowName,getKeyTransArray(rowMap));
        }
        return resultMap;
    }

    private static String[][] getKeyTransArray(Map<String,String> map) {
        if (map.size()==0) {
            return new String[][]{};
        }
        Set<Map.Entry<String,String>> set = map.entrySet();
        Iterator<Map.Entry<String,String>> iterator = set.iterator();
        String[] key = new String[map.size()];
        String[] keyTrans = new String[map.size()];
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String,String> keys = iterator.next();
            key[i] = keys.getKey();
            keyTrans[i] = keys.getValue();
            i++;
        }
        return new String[][]{key,keyTrans};
    }

    private static <T> T logIOException(IOException e){
        log.error("IO异常：",e);
        return null;
    }

    private static void closeJsonStream(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            log.error("json文件解析错误：",e);
        }
    }

    private static void closePropertyStream(Closeable closeable){
        try {
            closeable.close();
        } catch (IOException e) {
            log.error("properties文件解析错误：",e);
        }
    }


    //*******************************************************
    // 读取properties文件：读取的文件不允许出现空行、注释行等，否则在解析为properties时会报错
    //*******************************************************

    //方式1：通过spring方法：实际底层会调用方式2
    //注：此方法不能指定字符集，会使用默认字符集ISO 8859-1，如果文件中有其它字符集格式的中文，可能会乱码
    //规定所有源数据文件必须使用统一的UTF-8编码
    public static Properties getPropertiesBySpring(String pathRelativeClassPath){
        try {
            return PropertiesLoaderUtils.loadAllProperties(pathRelativeClassPath);
        } catch (IOException e) {
            return logIOException(e);
        }
    }

    //方式2：通过jdk方法
    //注：此方法不能指定字符集，会使用默认字符集ISO 8859-1，如果文件中有其它字符集格式的中文，可能会乱码
    //规定所有源数据文件必须使用统一的UTF-8编码
    public static Properties getPropertiesByJdk(String pathRelativeClassPath) {
        Properties properties = new Properties();
        getPropertiesByJdk(pathRelativeClassPath,properties);
        return properties;
    }

    public static void getPropertiesByJdk(String pathRelativeClassPath,Properties properties) {
        InputStream is = LocalFileReader.class.getClassLoader().getResourceAsStream(pathRelativeClassPath);
        try {
            properties.load(is);
        } catch (IOException e) {
            log.info(""+e);
        } finally {
            closePropertyStream(is);
        }
    }
	
	
	//*******************************************************
    // 读取的文件允许出现空行、注释行
    //*******************************************************
	
	private static JSONObject jsonRead(String path) {
        return JSONObject.parseObject(stringReadFilter(path));
    }

    private static Map<String, Object> mapRead(String path) {
        return mapReadFilter(path);
    }

    private static Properties propertiesRead(String path) {
        Map<String, Object> mapRead = mapReadFilter(path);
        Properties properties = new Properties();
        Iterator<Map.Entry<String, Object>> iterator = mapRead.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> keyValue = iterator.next();
            properties.setProperty(keyValue.getKey(), String.valueOf(keyValue.getValue()));
        }
        return properties;
    }

    //读取的文件允许出现空行和左起第一个非空字符为#的注释行
    private static String stringReadFilter(String path) {
        //path支持多层路径，但不能以/开头，即必须为相对路径
        if (!StringUtils.isEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        log.info("init >> " + path);
        StringBuilder builder = new StringBuilder();
        File file = getFile(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = StringUtils.trimWhitespace(line);
                //忽略空行和注释行
                if ("".equals(line) || line.startsWith("#")) {
                    continue;
                }
                builder.append(line + System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(path + " 路径文件读取失败！", e);
            System.exit(400);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    //读取的文件允许出现空行和左起第一个非空字符为#的注释行
    private static Map<String, Object> mapReadFilter(String path) {
        //path支持多层路径，但不能以/开头，即必须为相对路径
        if (!StringUtils.isEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        log.info("init >> " + path);
        Map<String, Object> map = new HashMap<>();
        File file = getFile(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            int equalIndex;
            while ((line = br.readLine()) != null) {
                line = StringUtils.trimWhitespace(line);
                //忽略空行和注释行
                if ("".equals(line) || line.startsWith("#")) {
                    continue;
                }
                equalIndex = line.indexOf("=");
                if (equalIndex == line.length() - 1) {
                    log.error(path + " 路径文件读取错误！value值不允许为空");
                    System.exit(400);
                }
                map.put(line.substring(0, equalIndex), line.substring(equalIndex + 1));
            }
        } catch (IOException e) {
            log.error(path + " 路径文件读取失败！", e);
            System.exit(400);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
	
	private static File getFile(String pathRelativeClassPath) {
        //获取目标文件
        return new File(classpath + pathRelativeClassPath);
    }
	

}

