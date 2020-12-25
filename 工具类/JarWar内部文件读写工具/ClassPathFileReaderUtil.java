package com.chinaunicom.bbss.cust.datashare.util;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.bbss.cust.datashare.cache.JarOrWarFileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author: liuw510
 * @date: 2020/12/9 19:24
 * @desc: class路径文件读取工具类
 */
public class ClassPathFileReaderUtil {

    private static final Logger log = LoggerFactory.getLogger(ClassPathFileReaderUtil.class);

    public static JSONObject jsonRead(String path) {
        return JSONObject.parseObject(stringReadFilter(path));
    }

    public static Map<String, Object> mapRead(String path) {
        return mapReadFilter(path);
    }

    public static Properties propertiesRead(String path) {
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
    public static String stringReadFilter(String path) {
        //path支持多层路径，但不能以/开头，即必须为相对路径
        if (!StringUtils.isEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        log.info("init >> " + path);
        StringBuilder builder = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(getReader(path));
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
            errorQuit(path,e);
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
        BufferedReader br = null;
        try {
            br = new BufferedReader(getReader(path));
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
            errorQuit(path,e);
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

    //*******************************************************
    // 读取文件流：字节流或字符流
    //*******************************************************

    private static InputStream getFileInputStream(String pathRelativeClassPath) {
        //获取目标文本文件输入字节流
        JarOrWarFileHelper.Type whoAmI = JarOrWarFileHelper.whoAmI();
        if (whoAmI.equals(JarOrWarFileHelper.Type.JAR)) {
            return JarOrWarFileHelper.jarInnerFileInputStream(pathRelativeClassPath);
        } else {
            return ClassPathFileReaderUtil.class.getClassLoader().getResourceAsStream(pathRelativeClassPath);
        }
    }

    private static File getFile(String pathRelativeClassPath) {
        JarOrWarFileHelper.checkTypeNot(JarOrWarFileHelper.Type.JAR);
        //获取目标文件
        return JarOrWarFileHelper.warInnerFile(pathRelativeClassPath);
    }

    private static Reader getReader(String pathRelativeClassPath) {
        Reader reader = null;
        JarOrWarFileHelper.Type whoAmI = JarOrWarFileHelper.whoAmI();
        if (whoAmI.equals(JarOrWarFileHelper.Type.JAR)) {
            InputStream inputStream = JarOrWarFileHelper.jarInnerFileInputStream(pathRelativeClassPath);
            reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        } else {
            try {
                reader = new FileReader(JarOrWarFileHelper.warInnerFile(pathRelativeClassPath));
            } catch (FileNotFoundException e) {
                errorQuit(pathRelativeClassPath,e);
            }
        }
        return reader;
    }

    private static void errorQuit(String pathRelativeClassPath, IOException e) {
        log.error(pathRelativeClassPath + " 路径文件读取失败！", e);
        System.exit(400);
    }
}
