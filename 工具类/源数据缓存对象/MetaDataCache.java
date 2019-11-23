package com.jshx.zq.p2p.data;

import com.jshx.zq.p2p.exception.BaseException;
import com.jshx.zq.p2p.util.LocalFileReader;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author liuwei
 * @date 2019-10-28 16:48
 * @desc 元数据缓存工具类
 * 读取来自properties、json等文件的数据，并存储到相应对象中以供调用
 */
@Slf4j
public class MetaDataCache {

    private MetaDataCache(){
        throw new BaseException("this is util class, you should not create an object!");
    }


    //某业务类型映射表
    public static final Map<String,String> BILL_MAP = new HashMap<>();

    //url.properties属性文件
    public static final Properties URL_PROPERTIES = new Properties();

    static {
        initBzMap();
        initUrlProperties();
    }

    private static void initBzMap() {
        String pathRelativeClassPath = "data/billOperType.json";
        BILL_MAP.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("BZ_MAP init");
    }

    private static void initUrlProperties() {
        String pathRelativeClassPath = "url.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,URL_PROPERTIES);
        log.info("URL_PROPERTIES init");
    }

}
