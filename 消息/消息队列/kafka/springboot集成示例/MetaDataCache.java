package com.ping.job.master.boot;

import com.ping.job.master.exception.BaseException;
import com.ping.job.master.util.LocalFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
        throw new BaseException("this is an util class, you should not create an object!");
    }

    private static final String PROP = "data/properties/";


    //==============================================//
    //================文件对象缓存区================//
    //==============================================//

    //kafka配置文件
    public static final Properties KAFKA_PROPERTIES  = new Properties();

    public static Environment ENV;

    static void boot(Environment environment){
        initKafka();
        ENV = environment;
    }

    //==============================================//
    //=================文件初始化区=================//
    //==============================================//

    private static void initKafka() {
        String pathRelativeClassPath = PROP+"kafka_topic.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,KAFKA_PROPERTIES);
        log.info("KAFKA_PROPERTIES init");
    }

}
