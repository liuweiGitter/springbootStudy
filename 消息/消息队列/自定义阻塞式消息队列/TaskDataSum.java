package com.jshx.zq.p2p.task.batch;

import com.alibaba.fastjson.JSONObject;
import com.jshx.zq.p2p.collection.BatchArrayBlockingQueue;
import com.jshx.zq.p2p.task.DataServiceCenter;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author liuwei
 * @date 2020-02-20 11:29
 * @desc 任务数据汇总批处理
 * 集群部署时，任务只能由一台服务器处理，nginx可实现
 * 待处理：当服务宕机时，由于延迟，部分数据不能入库，永久性丢失
 */
@Slf4j
public class TaskDataSum {

    //生产者类型数量：每个类型的生产者给予1个生产队列和1个消费者线程
    private static final int PRODUCE_NUM = 2;

    private static final int KILO = 1000;

    //固定线程池大小
    private static final int POOL_SIZE = 20;

    //阻塞式消息队列长度
    private static final int QUEUE_SIZE = 10*KILO;

    //批量刷库大小
    private static final int FRESH_DB_SIZE = 500;

    //有界任务队列长度：1w≈5MB
    private static final int TASK_SIZE = 10*KILO;

    private static final int POLLING_CYCLE = 3*KILO;
    private static final int FORCE_CYCLE = 60*KILO;
    private static final int CYCLE_COUNT = FORCE_CYCLE/POLLING_CYCLE;

    private static volatile boolean START_FLAG = false;

    //生产者线程池
    private static final ExecutorService DATA_COMMIT_POOL = new ThreadPoolExecutor(POOL_SIZE/2, POOL_SIZE,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(TASK_SIZE));

    //消费者线程池
    private static final ExecutorService DATA_FRESH_MAN = Executors.newFixedThreadPool(PRODUCE_NUM);

    private static final String PING_TASK_KEY = "PING_TASK";
    private static final String DIS_LOG_KEY = "DIS_LOG";
    private static final BatchArrayBlockingQueue PING_TASK_QUEUE = new BatchArrayBlockingQueue(QUEUE_SIZE);
    private static final BatchArrayBlockingQueue DIS_LOG_QUEUE = new BatchArrayBlockingQueue(QUEUE_SIZE);

    private static Map<String,Integer> TASK_POLLING_COUNT;
    static {
        TASK_POLLING_COUNT = new HashMap<>();
        TASK_POLLING_COUNT.put(PING_TASK_KEY,0);
        TASK_POLLING_COUNT.put(DIS_LOG_KEY,0);
    }

    /**
     * 启动消费者线程
     */
    public static void startThread(){
        if (START_FLAG) {
            return;
        }
        START_FLAG = true;
        log.info("-->>-->>消费者线程A启动<<--<<--");
        consumer("A1：ping测数据统计和日志记录",PING_TASK_QUEUE,PING_TASK_KEY);
        consumer("A2：派单失败日志记录",DIS_LOG_QUEUE,DIS_LOG_KEY);
    }

    private static void consumer(String theme, BatchArrayBlockingQueue queue, String key){
        DATA_FRESH_MAN.submit(new Runnable() {
            @Override
            public void run() {
                log.info(theme);
                while (true){
                    //休眠轮询
                    try {
                        Thread.sleep(getDynamicSleepTime(queue));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    freshData(queue,key);
                    if (destroyFlag) {
                        //销毁时退出
                        return;
                    }
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
    private static int getDynamicSleepTime(BatchArrayBlockingQueue queue){
        if (destroyFlag) {
            //如果触发了钩子，1ms每轮快速休眠，尽快刷库
            return 1;
        }
        float rate = 1.0f*queue.size()/FRESH_DB_SIZE;
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

    //提交ping测任务数据统计
    public static void submitTaskData(Map<String, Object> map){
        DATA_COMMIT_POOL.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    PING_TASK_QUEUE.put(map);
                } catch (InterruptedException e) {
                    lossLog(e,map,"ping测统计");
                }
            }
        });
    }

    //提交派单失败日志
    public static void submitDisFailed(Map<String, Object> map){
        DATA_COMMIT_POOL.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DIS_LOG_QUEUE.put(map);
                } catch (InterruptedException e) {
                    lossLog(e,map,"派单失败日志");
                }
            }
        });
    }

    private static void lossLog(Exception e, Object logBody, String flag){
        log.error(e.getMessage());
        log.error(flag+"数据提交失败："+ JSONObject.toJSONString(logBody));
    }


    //批量刷库
    private static void freshData(BatchArrayBlockingQueue queue,String key){
        try {
            int queueSize = queue.size();
            int currentCount = TASK_POLLING_COUNT.get(key);
            boolean freshFlag = queueSize>0 && (currentCount==CYCLE_COUNT || queueSize >= FRESH_DB_SIZE);
            /*log.info("============"+key+"队列大小:"+queueSize);
            log.info("============"+key+"轮询计数:"+currentCount);
            log.info("============"+key+"刷新判断:"+freshFlag);*/
            if (freshFlag) {
                log.info("≈"+key+"常规刷库≈");
                //计数清零
                TASK_POLLING_COUNT.put(key,0);
                //批量入库
                List<Map<String, Object>> list = queue.take(FRESH_DB_SIZE);
                batchFresh(list,key);
                /**
                 * 防止峰期日志流量积累，获准一次入库机会时，清空长队列
                 */
                while (queue.size() >= FRESH_DB_SIZE) {
                    log.info("⚡⚡⚡"+key+"峰期刷库⚡⚡⚡");
                    list.clear();
                    list = queue.take(FRESH_DB_SIZE);
                    batchFresh(list,key);
                }
            }else{
                //计数递增：为避免一直没有queueSize导致计数膨胀，计数封顶到CYCLE_COUNT
                if(currentCount < CYCLE_COUNT){
                    TASK_POLLING_COUNT.put(key,currentCount+1);
                }
            }
        } catch (InterruptedException e) {
            log.error("消费者线程A数据读取失败："+e.getMessage());
        }
    }

    private static void batchFresh(List<Map<String, Object>> list, String key){
        if (PING_TASK_KEY.equals(key)) {
            DataServiceCenter.afterWatchTaskList(list);
        }else {
            DataServiceCenter.disFailedLogList(list);
        }
    }

    //钩子接口--优雅销毁线程池
    private static boolean destroyFlag = false;

    public static void destroyPool(long waitTime){
        log.warn("》》》TaskDataSum线程池销毁，休眠"+waitTime+"ms等待任务后处理");
        destroyFlag = true;
        DATA_COMMIT_POOL.shutdown();
        DATA_FRESH_MAN.shutdown();
        //休眠一段时间，等待线程池执行完任务或者超时
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("DATA_COMMIT_POOL是否完成所有任务："+DATA_COMMIT_POOL.isTerminated());
        log.info("DATA_FRESH_MAN是否完成所有任务："+DATA_FRESH_MAN.isTerminated());
    }


}

