package com.jshx.zq.p2p.annotation;

import java.lang.annotation.*;

/**
 * @author liuwei
 * @date 2020-04-29 21:10
 * @desc 分布式任务注解
 * 注解于方法，带有本注解的方法将添加分布式锁切面
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface DistributeTask {

    //redis key
    String key();
    //key expireTime
    int expireTime() default 600;
    //task frequency
    TaskFrequency type() default TaskFrequency.NORMAL;

    enum TaskFrequency{
        FAST,NORMAL
    }
}
