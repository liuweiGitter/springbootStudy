package cn.js189.cloud.data;

import cn.js189.cloud.util.LocalFileReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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

    //某业务类型映射表：<third,zd>
    public static final Map<String,String> BZ_MAP = new HashMap<>();

    //url.properties属性文件
    public static final Properties URL_PROPERTIES = new Properties();

    static {
        initBzMap();
        initUrlProperties();
    }

    private static void initBzMap() {
        String pathRelativeClassPath = "data/bzTypeMap.json";
        List<Map> list= LocalFileReader.getListMapFromJson(pathRelativeClassPath);
        for (Map map:list) {
            BZ_MAP.put(map.get("third").toString(),map.get("zd").toString());
        }
        log.info("BZ_MAP init");
    }

    private static void initUrlProperties() {
        String pathRelativeClassPath = "url.properties";
        InputStream is = MetaDataCache.class.getClassLoader().getResourceAsStream(pathRelativeClassPath);
        try {
            URL_PROPERTIES.load(is);
        } catch (IOException e) {
            log.info(""+e);
        }
        log.info("URL_PROPERTIES init");
    }

}
