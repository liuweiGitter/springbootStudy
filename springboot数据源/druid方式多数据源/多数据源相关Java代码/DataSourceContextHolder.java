package com.jshx.zq.p2p.datasource;

/**
 * Author: ZhangXD
 * Description：线程本地环境，用以存储当前线程数据源对象
 */

public class DataSourceContextHolder {

    private DataSourceContextHolder(){
        throw new IllegalStateException("Utility class");
    }


    private static final ThreadLocal<DataSourceEnum> contextHolder = new InheritableThreadLocal<>();

    /**
     *  设置数据源
     * @param db
     */
    public static void setDataSource(DataSourceEnum db){
        contextHolder.set(db);
    }

    /**
     * 取得当前数据源
     * @return
     */
    public static DataSourceEnum getDataSource(){
        return contextHolder.get();
    }

    /**
     * 清除线程上下文数据
     */
    public static void clear(){
        contextHolder.remove();
    }
}
