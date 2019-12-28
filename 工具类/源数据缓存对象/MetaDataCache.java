package com.jshx.zq.p2p.data;

import com.alibaba.fastjson.JSONObject;
import com.jshx.zq.p2p.exception.BaseException;
import com.jshx.zq.p2p.task.dynamic.DynamicJob;
import com.jshx.zq.p2p.util.LocalFileReader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

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

    private static final String JSON = "data/json/";

    private static final String PROP = "data/properties/";

    private static final int NOT_FOUND = 404;

    //动态任务类的全路径：包名+"."
    private static String taskClassPath = "com.jshx.zq.p2p.task.dynamic.";


    //==============================================//
    //================文件对象缓存区================//
    //==============================================//

    //云测障碍元数据
    public static final Map<String,Map<String,String>> CLOUD_BREAK_MAP = new HashMap<>();
    //城市编码映射表
    public static final Map<String,String> CITY_CODE_MAP = new HashMap<>();
    //综调派单元数据
    public static final JSONObject ZD_DIS_JSON = initZdJson();
    /**
     * 动态定时任务类型映射表
     * Map<String taskGroupName,Map<String className/methodName,Object>>
     */
    public static final Map<String,Map<String,String>> DYNAMIC_TASK_MAP = new HashMap<>();
    /**
     * ftp文件元数据
     */
    public static final Map<String,Map<String,String>> FTP_PARAMS = new HashMap<>();
    /**
     * 动态任务对应的执行对象
     * Map<String taskGroupName:className,Object>
     */
    public static final Map<String,Object> DYNAMIC_OBJECT_MAP = new HashMap<>();
    /**
     * 动态任务对应的执行方法
     * Map<String className:methodName,Method>
     */
    public static final Map<String, Method> DYNAMIC_METHOD_MAP = new HashMap<>();
    //url.properties属性文件
    public static final Properties URL_PROPERTIES = initUrlProperties();
    //公钥校验码密码等配置文件
    public static final Properties KEYS_PROPERTIES = new Properties();
    //登录鉴权配置文件
    public static final Properties LOGIN_PARAMS_PROPERTIES = new Properties();
    //area_code.properties属性文件
    public static final Properties AREA_CODE_PROPERTIES = initAreaCodeProperties();
    /**
     * 表单key值转换映射map
     * Map<String methodName:String["originKey"]["mapKey"]>
     */
    public static final Map<String,String[][]> TABLE_KEY_MAP = new HashMap<>();
    //服务器信息配置文件
    public static final Properties SERVER_PROPERTIES = new Properties();

    //热部署，动态文件的最后修改时间
    //private static final Map<String,Long> LAST_MODIFIED_TIMES = new HashMap<>();

    //非赋值的静态代码块将延迟到类被调用，此处代码加入项目启动方法boot以实现预加载
    /*static {
        initTaskClassPath();
        ...
    }*/

    public static void boot(){
        initTaskClassPath();
        initCloudBreakdownMap();
        initDyTaskMap();
        initTableKeyTransMap();
        initCityCode();
        initKeys();
        initLoginParams();
        initFtpParams();
        initServerInfo();
    }

    //==============================================//
    //=================文件初始化区=================//
    //==============================================//

    private static void initKeys() {
        String pathRelativeClassPath = PROP+"secret_key.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,KEYS_PROPERTIES);
        log.info("KEYS_PROPERTIES init");
    }

    private static void initLoginParams() {
        String pathRelativeClassPath = PROP+"login_params.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,LOGIN_PARAMS_PROPERTIES);
        log.info("LOGIN_PARAMS_PROPERTIES init");
    }

    private static void initTaskClassPath() {
        log.info("dynamic task class package path is :"+taskClassPath);
    }

    private static JSONObject initZdJson() {
        String pathRelativeClassPath = JSON+"disToZd.json";
        log.info("ZD_DIS_JSON init");
        return LocalFileReader.getJSONObject(pathRelativeClassPath);
    }

    private static void initCloudBreakdownMap() {
        String pathRelativeClassPath = JSON+"cloudBreakdown.json";
        CLOUD_BREAK_MAP.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("CLOUD_BREAK_MAP init");
    }

    private static void initCityCode() {
        String pathRelativeClassPath = JSON+"cityCode.json";
        CITY_CODE_MAP.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("CITY_CODE_MAP init");
    }

    private static Properties initUrlProperties() {
        String pathRelativeClassPath = PROP+"url.properties";
        log.info("URL_PROPERTIES init");
        return LocalFileReader.getPropertiesBySpring(pathRelativeClassPath);
    }

    private static Properties initAreaCodeProperties() {
        String pathRelativeClassPath = PROP+"area_code.properties";
        log.info("AREA_CODE_PROPERTIES init");
        return LocalFileReader.getPropertiesBySpring(pathRelativeClassPath);
    }

    private static void initDyTaskMap() {
        String pathRelativeClassPath = JSON+"dynamicTaskMap.json";
        DYNAMIC_TASK_MAP.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("DYNAMIC_TASK_MAP init");
        initDyTaskObject();
    }

    private static void initDyTaskObject() {
        Set<Map.Entry<String,Map<String,String>>> set = DYNAMIC_TASK_MAP.entrySet();
        Iterator<Map.Entry<String,Map<String,String>>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Map<String,String>> task = iterator.next();
            String key = task.getKey();
            Map<String,String> value = task.getValue();
            try {
                Class clz = Class.forName(taskClassPath+value.get("object"));
                Object object = clz.newInstance();
                Method method = clz.getMethod(value.get("method"), DynamicJob.class);
                DYNAMIC_OBJECT_MAP.put(key+":"+value.get("object"),object);
                DYNAMIC_METHOD_MAP.put(value.get("object")+":"+value.get("method"),method);
            } catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
                log.error("DYNAMIC_OBJECT_MAP init error!",e);
                //系统强退
                System.exit(NOT_FOUND);
            } catch (NoSuchMethodException e) {
                log.error("DYNAMIC_METHOD_MAP init error!",e);
                System.exit(NOT_FOUND);
            }
        }
        log.info("DYNAMIC_TASK_OBJECT init");
    }

    //ftp配置文件形式;
    private static void initFtpParams() {
        String pathRelativeClassPath = JSON+"ftp.json";
        FTP_PARAMS.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("FTP_PARAMS init");
    }

    private static void initTableKeyTransMap() {
        String pathRelativeClassPath = JSON+"tableKeyTrans.json";
        Map<String,List<String>> tempMap = new HashMap<>();
        tempMap.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        Set<Map.Entry<String,List<String>>> entrySet = tempMap.entrySet();
        Iterator<Map.Entry<String,List<String>>> iterator = entrySet.iterator();
        while (iterator.hasNext()){
            Map.Entry<String,List<String>> table = iterator.next();
            List<String> columnList = table.getValue();
            String[][] tableColumn = new String[columnList.size()][2];
            for (int i = 0; i < columnList.size(); i++) {
                tableColumn[i] = columnList.get(i).split(":");
            }
            TABLE_KEY_MAP.put(table.getKey(),tableColumn);
        }
        log.info("TABLE_KEY_MAP init");
    }

    private static void initServerInfo() {
        String pathRelativeClassPath = PROP+"server_info.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,SERVER_PROPERTIES);
        log.info("SERVER_PROPERTIES init");
    }

}
