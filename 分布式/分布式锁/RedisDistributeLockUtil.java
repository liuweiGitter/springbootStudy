package com.jshx.zq.p2p.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwei
 * @date 2020-04-29 09:57
 * @desc redis分布式锁工具类
 */
public class RedisDistributeLockUtil {

    /**
     * 为避免死锁，过期时间需要有特别的规定
     * 1.不允许出现负值(即无失效期)
     * 2.不允许太长(长期不能解锁和死锁没多大区别)
     * 3.不允许太短
     * 3.1.防止应用服务集群因各主机时差大于锁定时间而造成的同一任务重复执行
     * 3.2.防止redis集群或主从(单机不存在)因同步时差大于锁定时间而造成的同一任务重复执行
     *
     * 无论锁定的任务是否允许应用服务集群多进程并发，都应该遵从3.1、3.2规则，在一定程度上消除时差影响
     * 限制锁过期时间不低于600秒，不高于12小时
     * 要求应用集群各主机时差保持在10min之内，锁定任务执行周期大于10min
     *
     * 对于高频执行的任务，限制锁过期时间不低于10秒，使用tryGetLockFast方法
     */
    private static int MIN_EXPIRE_TIME_FAST = 10;
    private static int MIN_EXPIRE_TIME = 600;
    private static int MAX_EXPIRE_TIME = 12*3600;

    /**
     * 尝试获取redis分布式锁：限制锁过期时间不低于600秒，不高于12小时
     * @param redisTemplate
     * @param lockKey 锁key，多进程同key
     * @param lockValue 锁value，多进程不同value，确保解锁时只有上锁者可自解
     *                  使用整型是为了便于比较，(通过redisTemplate操作时)lua脚本可能并不支持字符串的比较
     *                  使用低16字节即可，value的主要目的是防止其它线程解锁，鉴于解锁代码可控，它解可能性可以认为不存在
     * @param expireTime 过期时间，确保在上锁者因为bug、网络等异常不能解锁时，锁key能到期自动释放以避免死锁
     * @return 如果key不存在，设置key，成功后返回true，其余情况返回false
     */
    public static boolean tryGetLock(RedisTemplate redisTemplate, String lockKey, Integer lockValue, int expireTime) {
        if (expireTime < MIN_EXPIRE_TIME){
            expireTime = MIN_EXPIRE_TIME;
        }else if (expireTime > MAX_EXPIRE_TIME){
            expireTime = MAX_EXPIRE_TIME;
        }
        return redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取redis分布式锁：限制锁过期时间不低于10秒，不高于12小时
     * @param redisTemplate
     * @param lockKey
     * @param lockValue
     * @param expireTime
     * @return
     */
    public static boolean tryGetLockFast(RedisTemplate redisTemplate, String lockKey, Integer lockValue, int expireTime) {
        if (expireTime < MIN_EXPIRE_TIME_FAST){
            expireTime = MIN_EXPIRE_TIME_FAST;
        }else if (expireTime > MAX_EXPIRE_TIME){
            expireTime = MAX_EXPIRE_TIME;
        }
        return redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 尝试释放redis分布式锁
     * @param redisTemplate
     * @param lockKey
     * @param lockValue
     * @return
     */
    public static boolean tryReleaseLock(RedisTemplate redisTemplate, String lockKey, Integer lockValue) {

        //此处lua脚本不能识别字符串或整数的==比较运算，需比较差值
        //String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //String script = "if redis.call('get', KEYS[1]) == "+lockValue+" then return redis.call('del', KEYS[1]) else return 0 end";
        String script = "if (redis.call('get', KEYS[1]) - "+lockValue+" == 0) then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = RedisScript.of(script,Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
        return (1L == (Long) result);

    }

    //获取一个随机的整型value，取低16位
    public static Integer getALockValue() {
        return (int)(Math.random()*65535);
    }

}

/**
 * 示例
 * private void lockTest(){
 *         log.info(">>>redis分布式锁测试");
 *         String key = "disLockKey";
 *         int value1 = RedisDistributeLockUtil.getALockValue();
 *         int value2 = RedisDistributeLockUtil.getALockValue();
 *         testLock(key,value1);
 *         testLock(key,value2);
 *     }
 *
 * private void testLock(String key, Integer value){
 *         new Thread(new Runnable() {
 *             @Override
 *             public void run() {
 *                 log.info(">>>线程 "+Thread.currentThread().getName()+" 启动");
 *                 //1.线程尝试获取锁
 *                 if (RedisDistributeLockUtil.tryGetLockFast(redisTemplateData,key,value,30)) {
 *                     log.info("√√线程 "+Thread.currentThread().getName()+" 成功获取锁 "+key);
 *                     //2.休眠模拟锁内运行任务
 *                     try {
 *                         Thread.sleep(3000);
 *                     } catch (InterruptedException e) {
 *                         e.printStackTrace();
 *                     }
 *                     //3.任务运行结束后释放锁
 *                     boolean release = RedisDistributeLockUtil.tryReleaseLock(redisTemplateData,key,value);
 *                     log.info((release?"√√":"××")+"线程 "+Thread.currentThread().getName()+" 释放锁 "+key+(release?"成功":"失败"));
 *                 }else{
 *                     log.info("××线程 "+Thread.currentThread().getName()+" 失败获取锁 "+key);
 *                 }
 *                 log.info(">>>线程 "+Thread.currentThread().getName()+" 结束");
 *             }
 *         }).start();
 *     }
 */
