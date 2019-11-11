package cn.js189.cloud.service;

import cn.js189.cloud.aspect.OpreationLog;
import cn.js189.cloud.dao.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwei
 * @date 2019-11-01 17:40
 * @desc 日志服务类：异步线程日志服务
 * 提供异步线程的日志审计服务，可根据需要在切面中调用，或者在业务方法中调用
 * 进一步地，以后可以将日志服务拓展为同步线程提交消息队列，异步线程消费批量入库操作
 *
 * 为了避免线程泛滥以及会话内请求过度刷新引起程序崩溃，请求需要做限流
 * 限流至少应在nginx、tomcat上进行，必要情况下，还需要对API接口进行限流
 *
 * 上层做到限流以及黑白名单、防火墙等安全防护以后，应用程序可以比较简单地处理操作日志
 *
 */
@Service
public class LogService {

    private static OperateLogMapper operateLogMapper;

    private final static ExecutorService SINGLE_LOG_THREAD = Executors.newCachedThreadPool();

    private final static ScheduledExecutorService BATCH_LOG_THREAD = Executors.newScheduledThreadPool(1);

    //日志缓存队列：同步提交，最早每30s批量刷库一次，超出时不能阻塞等待，而是直接异步单条刷库
    private final static int QUEUE_SIZE = 2000;
    private static List<OpreationLog> queue = new ArrayList<>(QUEUE_SIZE);
    private int cycleCount;

    @Autowired
    private void init(OperateLogMapper operateLogMapper){
        LogService.operateLogMapper = operateLogMapper;
        //批量刷库
        batchLog();
    }

    /**
     * 异步记录日志：单条日志记录
     * @param functionName 方法名
     * @param inputParam 入参
     * @param outputParam 出参
     */
    public static void log(String functionName, String inputParam, String outputParam) {
        OpreationLog log = new OpreationLog(functionName,inputParam,outputParam);
        log(log);
    }

    /**
     * 异步记录日志：单条日志记录
     * @param log 日志对象
     */
    public static void log(OpreationLog log) {
        SINGLE_LOG_THREAD.submit(new Runnable() {
            @Override
            public void run() {
                operateLogMapper.insert(log);
            }
        });
    }


    /**
     * 异步提交或记录日志：队未满且获得锁时仍未满时提交队列，其余情况记录日志
     */
    public static void submit(String functionName, String inputParam, String outputParam) {
        OpreationLog log = new OpreationLog(functionName,inputParam,outputParam);
        submit(log);
    }

    /**
     * 异步提交或记录日志：队未满且获得锁时仍未满时提交队列，其余情况记录日志
     */
    public static void submit(OpreationLog log) {
        if (queue.size() < QUEUE_SIZE) {
            SINGLE_LOG_THREAD.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (queue){
                        if (queue.size() < QUEUE_SIZE) {
                            //异步提交日志：避免同步等待queue锁
                            queue.add(log);
                            return;
                        }
                    }
                }
            });
        }
        //异步单条刷库
        log(log);
    }

    /**
     * 定时批量刷库
     */
    private void batchLog(){
        cycleCount = 0;
        BATCH_LOG_THREAD.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cycleCount++;
                //2次调度周期且缓存队列不为空 或者 1次调度周期且缓存队列已超过75% 刷库一次
                if ((cycleCount >= 2 && queue.size() > 0) || queue.size() >= 0.75*QUEUE_SIZE) {
                    synchronized (queue){
                        if (queue.size() == 0) {
                            return;
                        }
                        boolean result = operateLogMapper.batchInsert(queue);
                        if (result) {
                            cycleCount = 0;
                            queue.clear();
                        }
                    }
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

}
