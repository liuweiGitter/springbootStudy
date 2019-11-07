package com.telecom.js.noc.hxtnms.operationplan.annotation;

import java.lang.annotation.*;

/**
 * @author liuwei
 * @date 2019-11-07 11:32
 * @desc 自定义方法级注解：流量控制注解类
 * 默认根据IP地址限流
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceLimit {

    LimitType limitType() default LimitType.IP;

    int maxRate() default 10;

    enum LimitType{
        //方法名
        METHOD_NAME,
        //请求源IP地址
        IP
    }

}
