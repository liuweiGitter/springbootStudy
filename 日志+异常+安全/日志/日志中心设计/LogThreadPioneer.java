package com.jshx.zq.p2p.log;

import com.jshx.zq.p2p.data.MetaDataCache;
import com.jshx.zq.p2p.data.ThreadLocalData;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-12-25 20:42
 * @desc 获取日志的线程级初始化对象
 * 日志对象包含固定的用户信息，这些用户信息存储在ThreadLocalData中
 * 各类型日志对象提供自身所需的有参构造，本类将用以获取这些初始化对象
 */
public class LogThreadPioneer {

    /**
     * 后台自动调用外部接口时，一些日志参数可能会是null，转换为服务器本地参数
     */
    private static String getCaller(){
        String caller = ThreadLocalData.ACCOUNT_NAME.get();
        if (StringUtils.isEmpty(caller)) {
            caller = MetaDataCache.SERVER_PROPERTIES.getProperty("local_name");
        }
        return caller;
    }

    private static String getClientIp(){
        String clientIp = ThreadLocalData.CLIENT_IP.get();
        if (StringUtils.isEmpty(clientIp)) {
            clientIp = MetaDataCache.SERVER_PROPERTIES.getProperty("local_ip");
        }
        return clientIp;
    }

    //获取外部接口调用日志的初始化对象
    public static OutCallLog getOutCallLog(String url,String protocol){
        String caller = getCaller();
        LocalDateTime callTime = LocalDateTime.now();
        String pointer = ThreadLocalData.POINTER.get()+"";
        String clientIp = getClientIp();
        return new OutCallLog(caller, callTime, url, pointer, clientIp, protocol);
    }
}
