package com.telecom.js.noc.hxtnms.operationplan.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2019-11-08 16:42
 * @desc 定时任务示例：基于SchedulingConfigurer接口
 *
 * 注意：单线程的任务调度会导致多个执行时间相近的定时任务在调度时拥塞排队，最终每个任务被调度的时间是不确定的
 * 这种情况下，务必使用多线程定时任务
 * 定时任务应尽量错峰执行
 */
@Component
@Configuration
@Slf4j
public class ScheduleTaskByConfig implements SchedulingConfigurer {

    //每5秒钟执行一次
    private static String cron = "0/5 * * * * *";

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        log.info("执行定时任务ScheduleTaskByConfig");
                    }
                },new CronTrigger(cron)
        );
    }

}
