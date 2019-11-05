package com.telecom.js.noc.hxtnms.operationplan.controller;

import com.alibaba.fastjson.JSONObject;
import com.telecom.js.noc.hxtnms.operationplan.entity.SomeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author liuwei
 * @date 2019-11-05 10:54
 * @desc 测试用控制类
 */
@RestController
@RequestMapping(value = "/api",produces = "application/json;charset=UTF-8")
@Slf4j
public class TestController {

    /**
     * 请求的方法中入参实体类对象添加注解@Valid以切入校验
     * @param entity
     * @return
     */
    @RequestMapping("/on")
    public String testOn(@RequestBody @Valid SomeEntity entity){
        /**
         * 使用注解后免去方法中的参数校验
         * 如果切面中参数校验失败，控制方法不会执行，而是返回错误信息
         * 如果配置了全局异常处理类，错误信息将通过全局类处理后再返回调用者
         * 可以在异常处理类中针对参数校验进行自定义的处理
         * 参见《GlobalExceptionHandler》
         */
        return "{\"msg\":\"校验通过!\"}";
    }

    /**
     * 不加入校验的对比
     * @param entity
     * @return
     */
    @RequestMapping("/off")
    public String testOff(@RequestBody SomeEntity entity){
        return JSONObject.toJSONString(entity);
    }

}
