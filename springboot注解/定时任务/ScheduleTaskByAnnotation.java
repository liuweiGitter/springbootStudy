package com.telecom.js.noc.hxtnms.operationplan.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2019-11-08 16:08
 * @desc 定时任务示例：基于@Scheduled注解
 * 关于cron表达式，参见http://cron.qqe2.com/
 * fixedDelay和fixedRate支持initialDelay
 * cron不支持initialDelay
 *
 * 注意1：
 * 所有任务的具体执行时间要看调度器排队情况
 *
 * 注意2：
 * 单线程的任务调度会导致多个执行时间相近的定时任务在调度时拥塞排队，最终每个任务被调度的时间是不确定的
 * 这种情况下，务必使用多线程定时任务
 * 定时任务应尽量错峰执行，无法错峰的任务应多线程执行，否则调度时间无法保证
 * 多线程定时任务需要在启动类添加注解@EnableAsync，并在定时任务类头或其@Scheduled方法上添加注解@Async
 * 
 * 注意3：
 * 调度时间很难保证，不建议使用本方法，推荐使用原生的quartz任务
 */
@Component
@Slf4j
public class ScheduleTaskByAnnotation {

    //异步执行，每5秒钟执行一次
    @Async
    @Scheduled(cron = "0/5 * * * * *")
    public void task1(){
        log.info("执行定时任务task1");
    }

    //异步执行，每次任务在上次任务结束之后5秒钟执
    @Async
    @Scheduled(fixedDelay = 5000)
    public void task2(){
        log.info("执行定时任务task2");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
    }

    //同步执行，每次任务在上次任务开始之后5秒钟执行，如果上次任务执行超过5秒钟，则在任务结束之后立即执行
    @Scheduled(fixedRate = 5000)
    public void task3(){
        log.info("执行定时任务task3");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
    }

    //同步执行，初始化延迟3秒钟，然后按照fixedDelay执行
    @Scheduled(initialDelay = 3000, fixedDelay = 5000)
    public void task4(){
        log.info("执行定时任务task4");
    }


    /**
     * 根据打印的结果可以看到
     * 同步的任务调度，都在scheduling-1线程中执行
     * 异步的任务调度，则在cTaskExecutor-n线程中执行，其中，n为动态递增的任务编号
     */
}
