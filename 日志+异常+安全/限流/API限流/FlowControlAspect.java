package com.telecom.js.noc.hxtnms.operationplan.aspect;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.telecom.js.noc.hxtnms.operationplan.annotation.ServiceLimit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwei
 * @date 2019-11-06 17:13
 * @desc API限流控制切面类
 * 一个完整的限流方案，可能分布在系统的每一环节
 * 从硬件到软件，从nginx、tomcat到api，从mycat到mysql、redis、mongodb等
 * 限流目标从所有请求总流量到单用户请求频率等
 * 本文只演示API接口针对单用户请求频率限流
 */
@Aspect
@Component
@Slf4j
public class FlowControlAspect {

    /**
     * 使用谷歌的限流工具类RateLimiter进行API限流
     */
    private static final long MAX_KEY = 1000;
    private static final long EXPIRE = 1;
    private static final TimeUnit UNIT = TimeUnit.DAYS;
    private static double permitsPerSecond = 1;

    private static LoadingCache<String, RateLimiter> cache =
            CacheBuilder.newBuilder().maximumSize(MAX_KEY).
                    expireAfterWrite(EXPIRE,UNIT).build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) throws Exception {
                    return RateLimiter.create(permitsPerSecond);
                }
            });

    @Pointcut("execution(* com.telecom.js.noc.hxtnms.operationplan.controller.*.*(..))")
    public void allController() {

    }

    /**
     * 环绕通知：匹配方法注解，接口限流控制
     */
    @Around("@annotation(com.telecom.js.noc.hxtnms.operationplan.annotation.ServiceLimit)")
    public Object flow(ProceedingJoinPoint point) throws Throwable {
        log.info("flow环绕前通知");
        //获取注解
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        ServiceLimit annotation = method.getAnnotation(ServiceLimit.class);
        //获取限流方式
        ServiceLimit.LimitType limitType = annotation.limitType();
        //获取限流的key
        String key = limitType.equals(ServiceLimit.LimitType.IP)?getRequestIp():method.toString();
        //限流判断
        RateLimiter rateLimiter = cache.get(key);
        Boolean flag = rateLimiter.tryAcquire();
        if (flag) {
            Object result = point.proceed();
            return result;
        }else{
            return "{\"msg\":\"流量超出限制调用!\",\"code\":400,\"success\":false}";
        }
    }

    /**
     * 获取客户端ip
     * @return
     */
    private String getRequestIp(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getRemoteAddr();
    }


}

