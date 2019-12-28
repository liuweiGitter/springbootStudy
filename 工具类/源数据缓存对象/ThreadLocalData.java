package com.jshx.zq.p2p.data;

/**
 * @author liuwei
 * @date 2019-12-24 17:15
 * @desc 线程本地对象工具类
 * 存储线程级别的用户全局变量
 * 如token的redis key、远程主机ip地址、用户名、埋点位置等等
 * 这些都是系统中多数业务会用到的参数
 *
 * 线程本地对象是比session更轻量级的会话级对象
 */
public class ThreadLocalData {

    public static final ThreadLocal<String> REDIS_KEY = new ThreadLocal<String>();

    public static final ThreadLocal<String> CLIENT_IP = new ThreadLocal<String>();

    public static final ThreadLocal<String> ACCOUNT_NAME = new ThreadLocal<String>();

    public static final ThreadLocal<String> ACCOUNT_ID = new ThreadLocal<String>();

    public static final ThreadLocal<String> POINTER = new ThreadLocal<String>();

}
