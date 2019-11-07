package com.telecom.js.noc.hxtnms.operationplan.aspect;

import com.telecom.js.noc.hxtnms.operationplan.annotation.DemoAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liuwei
 * @date 2019-11-06 17:13
 * @desc 接口调用控制切面类
 * 添加@Aspect和@Component注解注册切面类
 * 作用在控制类方法上的切面，在拦截器放行后才会执行，如果拦截器阻止了控制方法的调用，切面不会执行
 * 切点可以选择匹配方法签名，也可以选择匹配方法注解
 *
 * 切面可以实现很多功能，常见的除了审计还可以进行API接口限流
 * 关于API限流，见另外相关章节
 *
 * 本文演示环绕通知，AspectJ共定义了5种通知类型
 * Before：目标方法调用前执行
 * After：目标方法返回后或抛出异常后执行
 * AfterReturning：目标方法返回后执行
 * AfterThrowing：目标方法抛出异常后执行
 */
@Aspect
@Component
@Slf4j
public class AccessControlAspect {

    //时区
    private final static ZoneOffset TIME_ZONE = ZoneOffset.of("+8");

    /**
     * 调用数量统计对象
     * 注意同步保证线程并发的安全性
     */
    private final static Map<String, AtomicLong> COUNT_MAP = new ConcurrentHashMap();


    /**
     * 定义命名的切点：作为切点变量被通知引用
     * 本例匹配cn.js189.cloud.controller包下的所有类的所有方法
     * 1、execution()：表达式主体。
     * 2、第一个*号：表示返回类型，*号表示所有的类型。
     * 3、包名：表示需要拦截的包名，后面的一个句点表示当前包，两个句点表示当前包和当前包的所有子包。
     * 4、第二个*号：表示类名，*号表示所有的类。
     * 5、*(..)：最后这个星号表示方法名，*号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数
     */
    @Pointcut("execution(* com.telecom.js.noc.hxtnms.operationplan.controller.*.*(..))")
    public void allController() {

    }

    /**
     * 环绕通知：匹配方法签名
     * 可根据需要在目标方法执行前后环绕执行代码逻辑
     * 可以选择拒绝执行目标方法(如超量限流)，选择执行时间(如优先级排队)，执行时替换方法参数，执行完毕替换返回值等
     *
     * 接口访问日志，可写入log文件，也可写入数据库，注意，需要异步实现
     * log日志系统通常是业务线程同步提交日志内容到消息队列，然后快速返回业务线程，继续执行业务逻辑
     * log的独立的消费者线程会另外处理日志消息队列的IO写入操作
     * 如果需要将操作信息写入数据库，也应该使用同步提交异步消费的消息队列，并定时批量刷新日志入库 TODO
     */
    @Around("allController()")
    public Object count(ProceedingJoinPoint point) {
        log.info("count环绕前通知");
        String method = getMethodName(point);
        log.info("申请调用{}方法，入参{}",method, Arrays.toString(point.getArgs()));
        try {
            long start = LocalDateTime.now().toInstant(TIME_ZONE).toEpochMilli();
            //执行目标方法
            Object obj = point.proceed();
            long over = LocalDateTime.now().toInstant(TIME_ZONE).toEpochMilli();
            log.info("count环绕后通知");
            log.info("方法执行时间{}秒",(over-start)/1000+"."+(over-start)%1000);
            log.info("方法调用次数{}",getMethodCalledAddCount(method));
            return obj;
        } catch (Throwable throwable) {
            log.debug(throwable.getMessage());
        }
        return null;
    }

    /**
     * 环绕通知：匹配方法注解，本例中自定义了注解类
     *
     * 模拟接口限流控制
     * 需要切面的接口方法上添加@DemoAnnotation注解即可
     */
    @Around("@annotation(com.telecom.js.noc.hxtnms.operationplan.annotation.DemoAnnotation)")
    public Object flow(ProceedingJoinPoint point) throws Throwable {
        log.info("flow环绕前通知");
        //获取注解
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        DemoAnnotation demoAnnotation = method.getAnnotation(DemoAnnotation.class);
        //获取方法计数对象
        AtomicLong methodCount = getMethodFromMap(getMethodName(point));
        //同步接口计数对象，以模拟限流
        synchronized (methodCount){
            //获取方法的调用次数
            long count = methodCount.get();
            if (count < demoAnnotation.max()){
                methodCount.incrementAndGet();
                Object result = point.proceed();
                return result;
            }else{
                return "{\"msg\":\"流量超出限制调用!\",\"code\":400,\"success\":false}";
            }
        }
    }


    //获取方法名
    private String getMethodName(ProceedingJoinPoint point){
        return point.getTarget().getClass().getSimpleName() + "." + point.getSignature().getName() + "(..)";
    }

    //初始化方法map
    private void initMap(String method){
        if (!COUNT_MAP.containsKey(method)){
            //首次调用某个方法时需要同步
            synchronized (COUNT_MAP){
                if (!COUNT_MAP.containsKey(method)){
                    COUNT_MAP.put(method,new AtomicLong(0L));
                }
            }
        }
    }

    //获取方法map中的指定method的计数对象：用于上锁以同步限流
    private AtomicLong getMethodFromMap(String method){
        initMap(method);
        return COUNT_MAP.get(method);
    }

    //获取方法调用加1后的次数
    private long getMethodCalledAddCount(String method){
        initMap(method);
        return COUNT_MAP.get(method).incrementAndGet();
    }

}

