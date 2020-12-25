package com.chinaunicom.bbss.cust.datashare.util;

import com.chinaunicom.bbss.cust.datashare.cache.JarOrWarFileHelper;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import java.util.Properties;

/**
 * @author: liuw510
 * @date: 2020/12/16 11:25
 * @desc: yaml配置文件读取工具类
 */
public class YamlPropertySourceUtil {

    //读取绝对路径：只允许非jar包读取绝对路径
    public static Properties loadAbsolutePath(String path) {
        JarOrWarFileHelper.checkTypeIs(JarOrWarFileHelper.Type.JAR);
        return loadStream(new FileSystemResource(path));
    }

    //读取流：允许jar、war、idea等所有包类型
    private static Properties loadStream(Resource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    //读取相对classes的路径：允许jar、war、idea等所有包类型
    public static Properties loadRelativePath(String path) {
        JarOrWarFileHelper.Type whoAmI = JarOrWarFileHelper.whoAmI();
        Resource resource = null;
        if (whoAmI.equals(JarOrWarFileHelper.Type.JAR)) {
            resource = new InputStreamResource(JarOrWarFileHelper.jarInnerFileInputStream(path));
        } else {
            resource = JarOrWarFileHelper.warInnerFileResource(path);
        }
        return loadStream(resource);
    }


}
