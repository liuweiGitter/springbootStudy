package com.chinaunicom.bbss.cust.datashare.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: liuw510
 * @date: 2020/12/24 17:10
 * @desc: 代码内部文件读写辅助工具类
 */
public class JarOrWarFileHelper {

    private static final Logger log = LoggerFactory.getLogger(JarOrWarFileHelper.class);

    public enum Type {
        JAR, WAR, IDEA
    }

    /**
     * 类文件路径
     * 代码中经常需要读取配置文件
     * 在打包时，几乎所有的配置文件都应该编译(拷贝)到类路径下
     * 当代码在idea中运行时，或者打成war包在服务器中运行时，代码会以操作系统中文件夹的形式存在
     * 此时，在读取一个类路径下的配置文件时，首先，需要获取类文件路径，其次，指定该配置文件的相对类路径，然后，可以通过io或nio接口读取该文件
     */
    private static final String CLASS_PATH = JarOrWarFileHelper.class.getResource("/").getPath();


    /**
     * jar文件路径
     * 当代码打成jar包在服务器中运行时，代码会以jar文件的形式存在
     * jar文件内部的文件读取，首先，需要获取jar文件的路径和classes路径前缀，其次，指定该文件的相对类路径，然后，使用jar接口读取该文件
     */
    private static String JAR_PATH = null;
    private static String JAR_CLASS_PREFIX = null;

    private static String getJarPath() {
        if (null == JAR_PATH) {
            initJarPaths();
        }
        return JAR_PATH;
    }

    private static String getJarClassPath() {
        if (null == JAR_CLASS_PREFIX) {
            initJarPaths();
        }
        return JAR_CLASS_PREFIX;
    }

    private static void initJarPaths() {
        String jarClassPath = JarOrWarFileHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        /**
         * 路径格式为
         * win系统(file:\xxx\xxx\xxx.jar!\BOOT-INF\classes!\)
         * unix系统(file:/xxx/xxx/xxx.jar!/BOOT-INF/classes!/)
         */
        log.info("jar文件类绝对路径初始化 >> {}", jarClassPath);
        if (jarClassPath.startsWith("file:")) {
            jarClassPath = jarClassPath.substring(5);
        }
        if (jarClassPath.contains("jar!") && jarClassPath.contains("classes!")) {
            String[] pathSeg = jarClassPath.split("!");
            JAR_PATH = pathSeg[0];
            //当在windows系统中运行jar文件时，文件路径分隔符直接拼接可能会出现一种windows+unix混合风格，形如：xxx/xxx\
            //JAR_CLASS_PREFIX = pathSeg[1].substring(1) + File.separator;
            JAR_CLASS_PREFIX = pathSeg[1].substring(1) + JAR_PATH.charAt(0);
            log.info("jar文件路径提取 JAR_PATH={} JAR_CLASS_PREFIX={}", JAR_PATH, JAR_CLASS_PREFIX);
        } else {
            log.error("jar文件类路径错误！正确格式为：");
            log.info("win系统(file:\\xxx\\xxx\\xxx.jar!\\BOOT-INF\\classes!\\)");
            log.info("unix系统(file:/xxx/xxx/xxx.jar!/BOOT-INF/classes!/)");
            typeErrorQuit();
        }
    }

    private static Type whoAmI = null;

    public static Type whoAmI() {
        if (null != whoAmI) {
            return whoAmI;
        }
        if (CLASS_PATH.contains(".jar")) {
            whoAmI = Type.JAR;
        } else if (CLASS_PATH.contains(".war")) {
            whoAmI = Type.WAR;
        } else {
            whoAmI = Type.IDEA;
        }
        return whoAmI;
    }

    public static void checkTypeIs(Type expectedType) {
        if (!expectedType.equals(whoAmI())) {
            log.error("checkTypeIs类型校验错误！程序实际启动类型为{}，期望校验类型为{}", whoAmI(), expectedType);
            typeErrorQuit();
        }
    }

    public static void checkTypeNot(Type expectedNotType) {
        if (expectedNotType.equals(whoAmI())) {
            log.error("checkTypeNot类型校验错误！程序实际启动类型为{}", whoAmI());
            typeErrorQuit();
        }
    }

    private static void typeErrorQuit() {
        System.exit(400);
    }

    public static InputStream jarInnerFileInputStream(String jarInnerPath) {
        checkTypeIs(Type.JAR);
        //path支持多层路径，但不能以/开头，即必须为相对路径
        if (null != jarInnerPath && jarInnerPath.startsWith("/")) {
            jarInnerPath = jarInnerPath.substring(1);
        }
        try {
            //获取当前class文件打成的jar文件
            JarFile jarFile = new JarFile(getJarPath());
            //获取jar文件中指定资源文件的实体类对象
            JarEntry entry = jarFile.getJarEntry(getJarClassPath() + jarInnerPath);
            //读取资源文件为输入流
            return jarFile.getInputStream(entry);
        } catch (IOException e) {
            log.error(jarInnerPath + " 路径文件读取失败！", e);
            typeErrorQuit();
            return null;
        }
    }


    public static Resource warInnerFileResource(String warInnerPath) {
        checkTypeNot(Type.JAR);
        //path支持多层路径，允许以/开头，但会作为相对路径拼接到class路径
        if (null != warInnerPath && warInnerPath.startsWith("/")) {
            warInnerPath = warInnerPath.substring(1);
        }
        return new FileSystemResource(warInnerPath);
    }

    public static File warInnerFile(String warInnerPath) {
        checkTypeNot(Type.JAR);
        //path支持多层路径，允许以/开头，但会作为相对路径拼接到class路径
        if (null != warInnerPath && warInnerPath.startsWith("/")) {
            warInnerPath = warInnerPath.substring(1);
        }
        return new File(CLASS_PATH + warInnerPath);
    }

}
