package com.chinaunicom.bbss.cust.datashare.boot;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.bbss.cust.datashare.cache.JarOrWarFileHelper;
import com.chinaunicom.bbss.cust.datashare.util.ClassPathFileReaderUtil;
import com.chinaunicom.bbss.cust.datashare.util.YamlPropertySourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author: liuw510
 * @date: 2020/12/24 19:43
 * @desc: 程序启动的前置性校验
 * 校验程序内部文件读取是否正确
 */
public class PreCheckBeforeStart {

    //校验文件
    private static final String CHECK_FILE_PATH = "check-file.liuw510.yml";

    private static final Logger log = LoggerFactory.getLogger(JarOrWarFileHelper.class);

    public static void check() {
        printSysInfo();
        log.info("====前置性校验开始====");
        JarOrWarFileHelper.Type type = JarOrWarFileHelper.whoAmI();
        log.info("程序启动方式：" + type.name());
        log.info(">>ClassPathFileReaderUtil工具校验");
        log.info(ClassPathFileReaderUtil.stringReadFilter(CHECK_FILE_PATH));
        log.info(">>YamlPropertySourceUtil工具校验");
        log.info(JSONObject.toJSONString(YamlPropertySourceUtil.loadRelativePath(CHECK_FILE_PATH)));
        log.info("====前置性校验成功====");
    }

    public static void printSysInfo() {
        log.info("***运行环境信息***");
        Properties props = System.getProperties();
        log.info("[1] OS Info");
        log.info("  OS Name(操作系统名称)：" + props.getProperty("os.name"));
        log.info("  OS Arch(操作系统架构)：" + props.getProperty("os.arch"));
        log.info("  OS Version(操作系统版本)：" + props.getProperty("os.version"));
        log.info("  File Separator(系统文件路径分隔符)：" + props.getProperty("file.separator"));
        log.info("  Path Separator(依赖路径分隔符)：" + props.getProperty("path.separator"));
        log.info("  Line Separator(换行符)：" + props.getProperty("line.separator"));
        log.info("[2] User Info");
        log.info("  User Name(用户名)：" + props.getProperty("user.name"));
        log.info("  User Home(用户主目录)：" + props.getProperty("user.home"));
        log.info("  User WorkPath(用户当前工作目录)：" + props.getProperty("user.dir"));
        log.info("[3] Java Info");
        log.info("  Java Home：" + props.getProperty("java.home"));
        log.info("  Java Version：" + props.getProperty("java.version"));
        log.info("  JVM Vendor：" + props.getProperty("java.vm.vendor"));
        log.info("  JVM Name：" + props.getProperty("java.vm.name"));
        log.info("  JVM Version：" + props.getProperty("java.vm.version"));
    }


}
