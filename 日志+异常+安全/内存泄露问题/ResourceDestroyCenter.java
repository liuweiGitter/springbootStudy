package com.jshx.zq.p2p.boot;

import com.jshx.zq.p2p.log.LogCenter;
import com.jshx.zq.p2p.task.batch.TaskDataSum;
import org.springframework.context.ApplicationContext;

/**
 * @author liuwei
 * @date 2020-03-03 10:32
 * @desc 资源销毁中心
 * 线程池、文件句柄等资源需要在程序退出时销毁，否则会导致JVM进程引用不能清除从而
 *      1.资源句柄被占用，系统启动前不可再用
 *      2.资源消耗内存，内存泄露，长期积累内存溢出
 *      3.JVM进程后台残留(后台线程耗内存占cpu，定时任务还会抢夺数据库连接等资源)，虽然tomcat服务已经杀掉
 * 即使资源被terminated，kill进程时也可能因为其它引用不能清除或者程序bug导致JVM进程残留
 * 资源销毁很大程度上降低了残留进程的可能性
 * 服务器杀掉web进程时需要软杀(参见JVMShutdownHook.java)
 * 并且为了保证残留进程彻底清除，必须手动或脚本定时搜索和清除
 *
 * springboot提供了专门的程序关闭方法，通过spring-boot-starter-actuator来实现
 * 这种方法相对是非常安全的(内部能找到并且会释放资源)，进程会被逐渐退出
 * 但更广泛的进程退出方式，还是采用监听事件的形式主动回收资源，退出方式为：cmd软杀+等待boot回收+cmd补强杀
 */
public class ResourceDestroyCenter {

    //销毁等待时间：允许一定时间回收资源，超时强制退出
    static final long DEAD_WAIT = 10*1000;

    private static ApplicationContext applicationContext;

    //同包保护方法，只允许包内调用
    static void run(){
        //1.自定义线程池销毁
        new Thread(()->{
            TaskDataSum.destroyPool(DEAD_WAIT);
        }).start();
        new Thread(()->{
            LogCenter.destroyPool(DEAD_WAIT);
        }).start();
        //2.数据库连接池销毁
        /**
         * 实际上，连接池引用找不到，不能主动释放连接！！！
         * 只能依靠springboot内部注销，有些时候会注销失败
         */

        //3.文件句柄和网络句柄销毁
        /**
         * 也找不到引用！！！
         */
    }

}
