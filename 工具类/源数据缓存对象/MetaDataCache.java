package com.jshx.zq.p2p.data;

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
        throw new BaseException("this is util class, you should not create an object!");
    }


    //账单类型映射表
    public static final Map<String,String> BILL_MAP = new HashMap<>();

    /**
     * 动态定时任务类型映射表
     * Map<String taskGroupName,Map<String className/methodName,Object>>
     */
    public static final Map<String,Map<String,String>> DYNAMIC_TASK_MAP = new HashMap<>();
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
    //任务类的全路径：包名+"."
    private static String taskClassPath = "com.jshx.zq.p2p.task.dynamic.";

    //url.properties属性文件
    public static final Properties URL_PROPERTIES = new Properties();

    /**
     * 表单key值转换映射map
     * Map<String methodName:String["originKey"]["mapKey"]>
     */
    public static final Map<String,String[][]> TABLE_KEY_MAP = new HashMap<>();

    //静态代码块将延迟到类被调用，此处代码加入项目启动方法
    /*static {
        initTaskClassPath();
        initBzMap();
        initUrlProperties();
        initDyTaskMap();
    }*/

    public static void boot(){
        initTaskClassPath();
        initBzMap();
        initUrlProperties();
        initDyTaskMap();
        initTableKeyTransMap();
    }

    private static void initTaskClassPath() {
        log.info("dynamic task class package path is :"+taskClassPath);
    }

    private static void initBzMap() {
        String pathRelativeClassPath = "data/billOperType.json";
        BILL_MAP.putAll(LocalFileReader.getMapFromJson(pathRelativeClassPath));
        log.info("BILL_MAP init");
    }

    private static void initUrlProperties() {
        String pathRelativeClassPath = "url.properties";
        LocalFileReader.getPropertiesByJdk(pathRelativeClassPath,URL_PROPERTIES);
        log.info("URL_PROPERTIES init");
    }

    private static void initDyTaskMap() {
        String pathRelativeClassPath = "data/dynamicTaskMap.json";
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
                System.exit(404);
            } catch (NoSuchMethodException e) {
                log.error("DYNAMIC_METHOD_MAP init error!",e);
                System.exit(404);
            }
        }
        log.info("DYNAMIC_TASK_OBJECT init");
    }

    private static void initTableKeyTransMap() {
        String pathRelativeClassPath = "data/tableKeyTrans.json";
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

}
