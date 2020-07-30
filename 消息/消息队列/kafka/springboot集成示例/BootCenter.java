package com.ping.job.manager.boot;

import com.alibaba.fastjson.JSONObject;
import com.ping.job.manager.controller.ManagerInterfaceController;
import com.ping.job.manager.controller.TaskManagerController;
import com.ping.job.manager.producer.KafkaProduceCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * @author liuwei
 * @date 2019-11-25 14:25
 * @desc 启动中心类
 * 在项目启动后，部分任务需要立即被执行(如初始化redis、同步本地文件等)，一些方法因此需要被调用
 * 所有需要被调用的任务统一在调用中心注册执行
 */
@Component
@Slf4j
public class BootCenter {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private TaskManagerController taskManagerController;

    private volatile static boolean WEB_START_FLAG = false;

    public static void startWebNotice(){
        WEB_START_FLAG = true;
    }

    @PostConstruct
    private void boot(){
        log.info(">>>BootCenter running");
        //1.依赖注入
        iocInit();
        //2.内部数据初始化：json文件、properties文件等元数据初始化
        innerDataInit();
        //3.静态线程启动：消费者线程等启动
        staticThreadStart();
        //4.外部数据初始化：数据库等
        outerDataInit();
        //5.项目启动后的操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (WEB_START_FLAG) {
                        break;
                    }
                }
                afterProcessStart();
            }
        }).start();
    }

    //项目启动后的操作
    private void afterProcessStart() {
        log.info(">>>BootCenter afterProcessStart");
        //对外消息发布
        publishInit();
        //数据库任务加载
        taskInit();
    }

    //依赖注入组
    private void iocInit(){
        log.info(">>>BootCenter iocInit");
    }

    //内部数据初始化组
    private void innerDataInit(){
        log.info(">>>BootCenter innerDataInit");
        log.info("===MetaDataCache initData");
        MetaDataCache.boot(environment);
        KafkaProduceCenter.initParam(kafkaTemplate);
    }

    //静态线程启动组
    private void staticThreadStart() {
        log.info(">>>BootCenter staticThreadStart");
    }

    //外部数据初始化组
    private void outerDataInit(){
        log.info(">>>BootCenter outerDataInit");
    }

    //对外消息发布
    private void publishInit() {
        log.info(">>>BootCenter publishInit info");
        //延迟10秒钟发布
        try {
            Thread.sleep(10000);
            log.info(">>>Current Process ip:port "
                    + JSONObject.toJSONString(new ManagerInterfaceController().publish()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void taskInit() {
        log.info(">>>BootCenter taskInit info");
        //延迟60秒钟加载任务
        try {
            Thread.sleep(60000);
            taskManagerController.taskInit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
