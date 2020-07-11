package com.jshx.zq.p2p.aspect;

import com.jshx.zq.p2p.annotation.DistributeTask;
import com.jshx.zq.p2p.util.RedisDistributeLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2020-04-29 21:14
 * @desc 分布式任务切面
 */
@Component
@Aspect
@Slf4j
public class DistributeAspect {

    @Autowired
    private RedisTemplate redisTemplateData;

    @Pointcut("@annotation(com.jshx.zq.p2p.annotation.DistributeTask)")
    private void pointcut() {
    }

    @Around("pointcut() && @annotation(distributeTask)")
    public Object around(ProceedingJoinPoint point, DistributeTask distributeTask) {
        String key = distributeTask.key();
        int expireTime = distributeTask.expireTime();
        int value = RedisDistributeLockUtil.getALockValue();
        Object obj = null;
        boolean getLock;
        //1.线程尝试获取锁
        if (distributeTask.type() == DistributeTask.TaskFrequency.NORMAL) {
            getLock = RedisDistributeLockUtil.tryGetLock(redisTemplateData, key, value, expireTime);
        }else{
            getLock = RedisDistributeLockUtil.tryGetLockFast(redisTemplateData, key, value, expireTime);
        }
        if (getLock) {
            log.info("√√线程 " + Thread.currentThread().getName() + " 成功获取锁 " + key);
            try {
                //2.任务方法调用
                obj = point.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            //3.任务运行结束后释放锁
            boolean release = RedisDistributeLockUtil.tryReleaseLock(redisTemplateData, key, value);
            log.info((release ? "√√" : "××") + "线程 " + Thread.currentThread().getName() + (release ? " 成功" : " 失败")+ "释放锁 " + key);
        } else {
            log.info("××线程 " + Thread.currentThread().getName() + " 失败获取锁 " + key);
        }
        return obj;
    }

}
