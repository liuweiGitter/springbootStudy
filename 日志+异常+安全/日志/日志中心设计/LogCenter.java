package com.jshx.zq.p2p.log;

import com.alibaba.fastjson.JSONObject;
import com.jshx.zq.p2p.collection.BatchArrayBlockingQueue;
import com.jshx.zq.p2p.service.LogService;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author liuwei
 * @date 2019-12-24 19:20
 * @desc 日志记录中心
 * 记录外部接口调用、用户登入登出等关键信息的可延迟日志到数据库
 * 只有少部分日志需要实时入库，大部分日志可在队列中延迟入库
 *
 * 日志中心私有维持一个生产者多例线程池和一个消费者单例线程池
 * 日志生产和消费完全私有化(即对用户透明)，中心只对外开放提交日志的方法
 *
 * 采用基于"队列长度/刷库长度"比率rate的动态休眠时间的轮询算法
 * 峰期加快轮询，减少队列拥堵
 * 谷期减慢轮询，减少线程切换
 */
@Slf4j
public class LogCenter {

    private static final int KILO = 1000;

    //固定线程池大小
    private static final int POOL_SIZE = 10;

    //阻塞式消息队列长度
    private static final int QUEUE_SIZE = 10*KILO;

    //批量刷库大小
    private static final int FRESH_DB_SIZE = 200;

    //有界任务队列长度：20w≈100MB
    private static final int LOG_TASK_SIZE = 200*KILO;

    /**
     * getDynamicSleepTime()毫秒动态轮询一次
     *      超出FRESH_DB_SIZE条数据时获得一次刷库机会
     *      少于FRESH_DB_SIZE条数据时轮询计数加一，封顶到CYCLE_COUNT
     * CYCLE_COUNT次轮询只要有数据就获得一次刷库机会
     * 一次刷库机会中，强制进行一次刷库，刷库后
     *      如果队列剩余超过FRESH_DB_SIZE条数据，继续循环刷库，直到队列长度小于FRESH_DB_SIZE
     */
    private static final int POLLING_CYCLE = 5*KILO;
    private static final int FORCE_CYCLE = 60*KILO;
    private static final int CYCLE_COUNT = FORCE_CYCLE/POLLING_CYCLE;

    private static boolean LOG_MAN_START_FLAG = false;

    private static LogService LOG_SERVICE;

    /**
     * 生产者：为提高请求响应速率，日志操作全部异步线程，使用固定线程池提交日志对象
     * 消费者：为降低数据库入库频率，入库时批量操作，使用单例线程批量刷库
     * 消息队列：生产者将日志消息提交到阻塞的消息队列，消费者不需要按照顺序消费消息
     */

    /**
     * 生产者线程池：固定POOL_SIZE大小线程数，无界任务缓存队列
     *
     * 线程池任何时候都保有固定数量的线程，如果有任务导致线程挂掉，会生成一个新线程补进来
     * 总之，线程池会确保自己有固定数量的线程，区别在于它们是空闲的还是活动的(即正被占用的)
     *
     * 提交任务时
     *      如果有空闲的线程可用，则占用一个空闲线程
     *      如果所有线程都繁忙，则排队到任务缓存队列，该队列长度是没有限制的
     *      注：考虑到系统容量和日志量，目前是不需要对队列限界的(不会内存溢出)
     *          1 log data==0.2kb ==》 1 log runnable task==0.5kb
     *          ==》 100MB==20w log runnable task of 20w log data
     *          10*POLLING_CYCLE==50s ==》 速率阈值==4k data/s
     *      目前也就只有大量的动态任务可能并发调接口产生峰期日志
     *      将来如果峰期接口日志量过大，如超过4k log data/s，可以选择采用有界任务队列
     *      有界任务队列会阻塞后续任务的入列，以保护系统免于内存溢出
     */
    //private static final ExecutorService LOG_POOL = Executors.newFixedThreadPool(POOL_SIZE);

    /**
     * 一个有界队列的可变大小线程池
     * 在池线程至少POOL_SIZE/2，最多POOL_SIZE，更能适应峰谷变化
     * 任务队列最长为LOG_TASK_SIZE
     */
    private static final ExecutorService LOG_POOL = new ThreadPoolExecutor(POOL_SIZE/2, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(LOG_TASK_SIZE));

    /**
     * 消费者线程：单例线程，无界任务缓存队列
     * 由于始终只运行一个任务，因此不需要再队列中缓存任务
     */
    private static final ExecutorService LOG_MAN = Executors.newSingleThreadExecutor();

    //日志消息队列：调用外部接口
    private static final String OUT_CALL_KEY = "OUT_CALL_KEY";
    private static final BatchArrayBlockingQueue OUT_CALL_QUEUE = new BatchArrayBlockingQueue(QUEUE_SIZE);

    //日志记录
    private static Map<String,Integer> LOG_POLLING_COUNT;
    static {
        LOG_POLLING_COUNT = new HashMap<>();
        LOG_POLLING_COUNT.put(OUT_CALL_KEY,0);
    }

    /**
     * 启动消费者线程
     */
    public static void startThread(LogService logService){
        if (LOG_MAN_START_FLAG) {
            return;
        }
        LOG_SERVICE = logService;
        LOG_MAN_START_FLAG = true;
        log.info("-->>-->>日志消费者线程启动<<--<<--");
        LOG_MAN.submit(new Runnable() {
            @Override
            public void run() {
                log.info("-->>-->>LogMan Running Body<<--<<--");
                while (true){
                    //休眠轮询
                    try {
                        Thread.sleep(getDynamicSleepTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //日志入库
                    freshOutCallLog();
                }
            }
        });
    }

    /**
     * 获取动态轮询时间(毫秒)
     *
     * 比率rate=队列长度queueSize/批量入库长度FRESH_DB_SIZE
     * rate==0 10*POLLING_CYCLE
     * rate<0.1 5*POLLING_CYCLE
     * rate<0.5 1*POLLING_CYCLE
     * rate<1.0 0.5*POLLING_CYCLE
     * rate<5.0 0.1*POLLING_CYCLE
     * rate<10.0 0.05*POLLING_CYCLE
     * rate>=10.0 0.01*POLLING_CYCLE
     */
    private static int getDynamicSleepTime(){
        float rate = 1.0f*OUT_CALL_QUEUE.size()/FRESH_DB_SIZE;
        if (rate==0) {
            return 10*POLLING_CYCLE;
        }else if(rate<0.1){
            return 5*POLLING_CYCLE;
        }else if(rate<0.5){
            return POLLING_CYCLE;
        }else if(rate<1.0){
            return POLLING_CYCLE/2;
        }else if(rate<5.0){
            return POLLING_CYCLE/10;
        }else if(rate<10.0){
            return POLLING_CYCLE/20;
        }else {
            return POLLING_CYCLE/100;
        }
    }

    //========================日志提交========================//

    //外部接口日志
    public static void submitOutCallLog(OutCallLog log){
        LOG_POOL.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * 日志提交到消息队列
                     * 如果队列已满，阻塞式等待
                     * 阻塞等待期间，该线程被占用
                     */
                    OUT_CALL_QUEUE.put(log);
                } catch (InterruptedException e) {
                    lossLog(e,log);
                }
            }
        });
    }

    private static void lossLog(Exception e, Object logBody){
        log.error(e.getMessage());
        log.error("日志提交失败："+ JSONObject.toJSONString(logBody));
    }


    //========================日志入库========================//

    //外部接口日志
    private static void freshOutCallLog(){
        try {
            int queueSize = OUT_CALL_QUEUE.size();
            int currentCount = LOG_POLLING_COUNT.get(OUT_CALL_KEY);
            boolean freshFlag = queueSize>0 && (currentCount==CYCLE_COUNT || queueSize >= FRESH_DB_SIZE);
            log.info("============队列大小:"+queueSize);
            log.info("============轮询计数:"+currentCount);
            log.info("============刷新判断:"+freshFlag);
            if (freshFlag) {
                log.info("≈常规刷库≈");
                //计数清零
                LOG_POLLING_COUNT.put(OUT_CALL_KEY,0);
                //批量入库
                List<OutCallLog> list = OUT_CALL_QUEUE.take(FRESH_DB_SIZE);
                LOG_SERVICE.insertOutCallLog(list);
                /**
                 * 防止峰期日志流量积累，获准一次入库机会时，清空长队列
                 */
                while (OUT_CALL_QUEUE.size() >= FRESH_DB_SIZE) {
                    log.info("⚡⚡⚡峰期刷库⚡⚡⚡");
                    list.clear();
                    list = OUT_CALL_QUEUE.take(FRESH_DB_SIZE);
                    LOG_SERVICE.insertOutCallLog(list);
                }
            }else{
                //计数递增：为避免一直没有queueSize导致计数膨胀，计数封顶到CYCLE_COUNT
                if(currentCount < CYCLE_COUNT){
                    LOG_POLLING_COUNT.put(OUT_CALL_KEY,currentCount+1);
                }
            }
        } catch (InterruptedException e) {
            log.error("日志读取失败："+e.getMessage());
        }
    }


}
