package com.jshx.zq.p2p.boot;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-10-08 11:55
 * @desc JVM关闭和ShutdownHook
 * 非守护线程的自然结束会导致JVM进程结束
 * 操作系统可以kill进程，如kill命令和Ctrl+C命令等可以主动结束JVM进程
 * 除此之外，JDK提供了一些API可以在代码中主动关闭JVM
 * 以下操作会触发钩子线程的执行
 * 1. 程序正常退出
 * 2. 程序中主动调用了System.exit()
 * 3. 终端使用Ctrl+C触发的中断
 * 4. 操作系统关闭
 * 5. 使用kill pid命令杀掉进程(注意，不是-9强杀，强杀不会触发钩子)
 */
@Slf4j
public class JVMShutdownHook {

    static void hooks() {
        log.info(">>>JVM标记钩子线程注册<<<");
        quitFlagHook();
        log.info("钩子注册完成");
        //quitTest();
    }

    //测试：模拟主动退出程序
    private static void quitTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("测试模拟退出程序");
                System.exit(666);
            }
        }).start();
    }

    /**
     * JVM退出标记钩子
     * 钩子线程可用以对线程池、文件句柄等资源进行销毁操作
     * 资源回收操作也可以通过Spring容器上下文监听来实现
     * 此处钩子仅用以追踪程序是否在关闭时触发了钩子
     * 如果触发钩子，则会触发容器监听，最终能够正确回收资源和结束进程
     * 如果未触发钩子，则通常是因为操作不当，如硬杀、硬关机等操作导致
     */
    private static void quitFlagHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.info(">>>JVM钩子正常启动！程序即将关闭！<<<");
            }
        }));
    }

}

