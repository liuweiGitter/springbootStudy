package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        return classpath+pathRelativeClassPath;
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
        try {
            return new FileInputStream(classpath+pathRelativeClassPath);
        } catch (FileNotFoundException e) {
            return logIOException(e);
        }
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
    // 读取json文件
    //*******************************************************

    public static List<Map> getListMapFromJson(String pathRelativeClassPath) {
        return getListFromJson(pathRelativeClassPath,Map.class);
    }

    public static <T> List<T> getListFromJson(String pathRelativeClassPath,Class<T> entityClz) {
        //解析json文件为数组
        InputStream inputStream = LocalFileReader.getFileInputStream(pathRelativeClassPath);
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
        InputStream inputStream = LocalFileReader.getFileInputStream(pathRelativeClassPath);
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

    public static <T> T getObjectFromJson(String pathRelativeClassPath,Class<T> entityClz) {
        //解析json文件为对象
        InputStream inputStream = LocalFileReader.getFileInputStream(pathRelativeClassPath);
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
        InputStream inputStream = LocalFileReader.getFileInputStream(pathRelativeClassPath);
        try {
            String text = IOUtils.toString(inputStream,"utf8");
            return JSON.parseObject(text);
        } catch (IOException e) {
            return logIOException(e);
        } finally {
            closeJsonStream(inputStream);
        }
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
    // 读取properties文件
    //*******************************************************

    //方式1：通过spring方法
    public static Properties getPropertiesBySpring(String pathRelativeClassPath){
        try {
            return PropertiesLoaderUtils.loadAllProperties(pathRelativeClassPath);
        } catch (IOException e) {
            return logIOException(e);
        }
    }

    //方式2：通过jdk方法
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

}

