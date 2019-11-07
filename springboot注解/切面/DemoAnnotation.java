package com.telecom.js.noc.hxtnms.operationplan.annotation;

import java.lang.annotation.*;

/**
 * @author liuwei
 * @date 2019-11-07 11:32
 * @desc 自定义方法级注解：仅为示例
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DemoAnnotation {
    boolean value() default true;
    int max() default 10;
}
