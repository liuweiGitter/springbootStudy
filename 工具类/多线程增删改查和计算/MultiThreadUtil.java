package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: liuwei
 * Date: 2019-07-01 00:55
 * Desc: 多线程批量增删改操作以及查询操作封装
 * 对于增删改，封装内部支持同步、异步的多线程，对于查询，使用同步的多线程
 * 外部调用增删改，可以和外部线程同步或者异步，调用传参，可以指定内部方法为同步或者异步
 * 至于外部整体或者内部调用是否事务，完全由外部调用者决定和处理
 * 内部调用不需要支持直接的事务(静态方法不允许事务)，应由服务层处理
 */
@Slf4j
public class MultiThreadUtil<T> {
    //默认线程池固定线程数最大值
    private static final int DEFAULT_THREAD_SIZE = 5;
    //允许线程池最大线程数
    private static final int MAX_THREAD_SIZE = 20;
    //默认同步线程
    private static final boolean SYS_THREAD_TRUE = true;
    //增删改查最大允许批量数目
    private static final int MAX_DB_IO_SIZE = 10000;
    //一次请求允许查询的最大结果集数量
    private static final int MAX_QUERY_SIZE = 100000;
    //一次数据计算最大的批数量
    private static final int MAX_Calculate_SIZE = 20000;

    public enum CalculateType {
        SUM("求和"),
        MIN("最小值"),
        MAX("最大值"),
        AVG("平均值"),
        VAR("方差");
        private String name;
        public String getName() {
            return name;
        }
        CalculateType(String name){
            this.name = name;
        }
    };

    //同步的默认线程数量的多线程增删改：推荐
    public static int modifyData(List<?> list,int batchSize,Object mapper, String mapperMethodName){
        return modifyData(list,batchSize,"["+mapper.getClass().getSimpleName()+"."+mapperMethodName+"]",
                mapper,mapperMethodName,DEFAULT_THREAD_SIZE,SYS_THREAD_TRUE);
    }

    //同步的默认线程数量的自定义线程异常信息的多线程增删改
    public static int modifyData(List<?> list,int batchSize,String threadMsg, Object mapper, String mapperMethodName){
        return modifyData(list,batchSize,threadMsg,mapper,mapperMethodName,DEFAULT_THREAD_SIZE,SYS_THREAD_TRUE);
    }

    /**
     * 同步或异步的多线程增删改
     * @param list 需要多线程操作的list数据
     * @param batchSizeCalled 每批次数量，实际最大数量不能超过MAX_DB_IO_SIZE
     * @param threadMsg 线程内容说明，抛出异常用
     * @param mapper 反射需要调用的mapper对象
     * @param mapperMethodName 反射需要调用的mapper方法名
     * @param threadCount 允许创建的最大线程数量，实际线程数量大小为min(circleSize,threadCount,MAX_THREAD_SIZE)
     * @param sysThread 是否需要对增删改数据条数进行计数
     * @return 成功操作的数据总数，对于异步线程，返回0
     */
    public static int modifyData(List<?> list,int batchSizeCalled,String threadMsg,
                                 Object mapper, String mapperMethodName, int threadCount, boolean sysThread){
        int success = 0;
        if (null==list || list.size()==0){
            return success;
        }
        //反射创建mapper方法
        Method mapperMethod = null;
        try {
            mapperMethod = mapper.getClass().getMethod(mapperMethodName, List.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return success;
        }
        int listLength = list.size();
        //循环批量
        final int batchSize = Math.min(batchSizeCalled,MAX_DB_IO_SIZE);
        int circleSize = listLength/batchSize;
        if (0==circleSize){
            //如果一批次可处理完，则不需要多线程
            try {
                return (int) mapperMethod.invoke(mapper,list);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //多线程中必须引用final或final等效的对象
        Method finalMapperMethod = mapperMethod;
        //是否有余量
        boolean remain = listLength % batchSize != 0;
        //循环中操作成功计数
        final int[] successArr = new int[circleSize+(remain?1:0)];
        //动态的固定大小线程池，最大取到MAX_THREAD_SIZE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(circleSize <= threadCount ? Math.min(circleSize,MAX_THREAD_SIZE) : Math.min(threadCount,MAX_THREAD_SIZE));
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //反射调用mapper方法，批量增删改
                        successArr[k-1] = (int) finalMapperMethod.invoke(mapper,list.subList((k - 1) * batchSize, k * batchSize));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (sysThread){//同步的多线程
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //余量
        if (remain) {
            try {
                successArr[circleSize] = (int) mapperMethod.invoke(mapper,list.subList(listLength - listLength % batchSize, listLength));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (sysThread){//同步的多线程
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.info("["+threadMsg+"]线程等待异常",e);
                Thread.currentThread().interrupt();
            }
            for (int result:successArr) {
                success+=result;
            }
        }
        return success;
    }

    //同步的默认线程数量的多线程查询：分页条件查询
    public static List queryData(int totalCount, int batchSizeCalled,String threadMsg,
                             Object mapper, String mapperMethodName, final QueryHomeBox<?> queryHomeBox){
        return queryData(totalCount,batchSizeCalled,threadMsg,mapper,mapperMethodName,DEFAULT_THREAD_SIZE,queryHomeBox);
    }

    /**
     * 同步的多线程查询：分页条件查询
     * @param totalCount 查询总数
     * @param batchSizeCalled 每批次数量，实际最大数量不能超过MAX_DB_IO_SIZE
     * @param threadMsg 线程内容说明，抛出异常用
     * @param mapper 反射需要调用的mapper对象
     * @param mapperMethodName 反射需要调用的mapper方法名
     * @param threadCount 允许创建的最大线程数量，实际线程数量大小为min(circleSize,threadCount,MAX_THREAD_SIZE)
     * @param queryHomeBox 查询条件
     * @return 查询结果集list
     */
    public static List queryData(int totalCount, int batchSizeCalled,String threadMsg,
                                 Object mapper, String mapperMethodName, int threadCount,
                                final QueryHomeBox<?> queryHomeBox){
        if (totalCount==0){
            return new ArrayList();
        }
        //反射创建mapper方法
        Method mapperMethod = null;
        try {
            mapperMethod = mapper.getClass().getMethod(mapperMethodName, QueryHomeBox.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new ArrayList();
        }
        //循环批量
        final int batchSize = Math.min(batchSizeCalled,MAX_DB_IO_SIZE);
        totalCount = Math.min(totalCount,MAX_QUERY_SIZE);
        int circleSize = totalCount/batchSize;
        if (0==circleSize){
            //如果一批次可处理完，则不需要多线程
            try {
                return (List) mapperMethod.invoke(mapper,queryHomeBox);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //线程安全list
        List result = Collections.synchronizedList(new ArrayList<>());
        //多线程中必须引用final或final等效的对象
        Method finalMapperMethod = mapperMethod;
        //是否有余量
        boolean remain = totalCount % batchSize != 0;
        //动态的固定大小线程池，最大取到MAX_THREAD_SIZE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(circleSize <= threadCount ? Math.min(circleSize,MAX_THREAD_SIZE) : Math.min(threadCount,MAX_THREAD_SIZE));
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        QueryHomeBox<?> queryHomeBoxK = getQueryHomeBoxK(queryHomeBox, batchSize,k);
                        //反射调用mapper方法，批量查询
                        result.addAll((List) finalMapperMethod.invoke(mapper,queryHomeBoxK));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //余量
        if (remain) {
            try {
                QueryHomeBox<?> queryHomeBoxLast = getQueryHomeBoxK(queryHomeBox, batchSize,circleSize+1);
                //反射调用mapper方法，批量查询
                result.addAll((List) finalMapperMethod.invoke(mapper,queryHomeBoxLast));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.info("["+threadMsg+"]线程等待异常",e);
            Thread.currentThread().interrupt();
        }
        return result;
    }

    /**
     * 获取线程k的分页查询条件
     * @param queryHomeBox 不分页的查询条件
     * @param pageDataCount 每页数量
     * @param queryPageNum 要查询的页码
     * @return
     */
    private static QueryHomeBox getQueryHomeBoxK(QueryHomeBox queryHomeBox,int pageDataCount,int queryPageNum) {
        QueryHomeBox queryHomeBoxK = new QueryHomeBox<>();
        queryHomeBoxK.setEqual(queryHomeBox.getEqual());
        queryHomeBoxK.setEqualMap(queryHomeBox.getEqualMap());
        queryHomeBoxK.setLike(queryHomeBox.getLike());
        queryHomeBoxK.setLikeMap(queryHomeBox.getLikeMap());
        queryHomeBoxK.setRelationMap(queryHomeBox.getRelationMap());
        queryHomeBoxK.setOrder(queryHomeBox.getOrder());
        queryHomeBoxK.setInverse(queryHomeBox.isInverse());
        queryHomeBoxK.setPageDataCount(pageDataCount);
        queryHomeBoxK.setQueryPageNum(queryPageNum);
        return queryHomeBoxK;
    }

    //同步的默认线程数量的多线程查询：单条件(如id)列表查询
    public static List queryDataListByList(int batchSizeCalled, String threadMsg,
                                       Object mapper, String mapperMethodName, final List<?> list){
        return queryDataByList(batchSizeCalled,threadMsg,mapper,mapperMethodName,DEFAULT_THREAD_SIZE,list);
    }

    //同步的默认线程数量的多线程查询：单条件(如id)列表查询
    public static Map queryDataMapByList(int batchSizeCalled, String threadMsg,
                                      Object mapper, String mapperMethodName, final List<?> list){
        return queryDataMapByList(batchSizeCalled,threadMsg,mapper,mapperMethodName,DEFAULT_THREAD_SIZE,list);
    }

    private static List queryDataByList(int batchSizeCalled, String threadMsg, Object mapper, String mapperMethodName,
                                        int threadCount, List<?> list) {
        if (null==list){
            return null;
        }
        //反射创建mapper方法
        Method mapperMethod = null;
        try {
            mapperMethod = mapper.getClass().getMethod(mapperMethodName, List.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new ArrayList();
        }
        //循环批量
        final int batchSize = Math.min(batchSizeCalled,MAX_DB_IO_SIZE);
        int totalCount = list.size();
        int circleSize = totalCount/batchSize;
        if (0==circleSize){
            //如果一批次可处理完，则不需要多线程
            try {
                return (List) mapperMethod.invoke(mapper,list);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //线程安全list
        List result = Collections.synchronizedList(new ArrayList<>());
        //多线程中必须引用final或final等效的对象
        Method finalMapperMethod = mapperMethod;
        //是否有余量
        boolean remain = totalCount % batchSize != 0;
        //动态的固定大小线程池，最大取到MAX_THREAD_SIZE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(circleSize <= threadCount ? Math.min(circleSize,MAX_THREAD_SIZE) : Math.min(threadCount,MAX_THREAD_SIZE));
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //反射调用mapper方法，批量查询
                        result.addAll((List) finalMapperMethod.invoke(mapper,list.subList((k - 1) * batchSize, k * batchSize)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //余量
        if (remain) {
            try {
                //反射调用mapper方法，批量查询
                result.addAll((List) finalMapperMethod.invoke(mapper,list.subList(totalCount - totalCount % batchSize, totalCount)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.info("["+threadMsg+"]线程等待异常",e);
            Thread.currentThread().interrupt();
        }
        return result;

    }

    private static Map queryDataMapByList(int batchSizeCalled, String threadMsg, Object mapper, String mapperMethodName,
                                        int threadCount, List<?> list) {
        if (null==list){
            return null;
        }
        //反射创建mapper方法
        Method mapperMethod = null;
        try {
            mapperMethod = mapper.getClass().getMethod(mapperMethodName, List.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return new HashMap();
        }
        //循环批量
        final int batchSize = Math.min(batchSizeCalled,MAX_DB_IO_SIZE);
        int totalCount = list.size();
        int circleSize = totalCount/batchSize;
        if (0==circleSize){
            //如果一批次可处理完，则不需要多线程
            try {
                return (Map) mapperMethod.invoke(mapper,list);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        Map result = new HashMap();
        //多线程中必须引用final或final等效的对象
        Method finalMapperMethod = mapperMethod;
        //是否有余量
        boolean remain = totalCount % batchSize != 0;
        //动态的固定大小线程池，最大取到MAX_THREAD_SIZE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(circleSize <= threadCount ? Math.min(circleSize,MAX_THREAD_SIZE) : Math.min(threadCount,MAX_THREAD_SIZE));
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //反射调用mapper方法，批量查询
                        result.putAll((Map) finalMapperMethod.invoke(mapper,list.subList((k - 1) * batchSize, k * batchSize)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //余量
        if (remain) {
            try {
                //反射调用mapper方法，批量查询
                result.putAll((Map) finalMapperMethod.invoke(mapper,list.subList(totalCount - totalCount % batchSize, totalCount)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.info("["+threadMsg+"]线程等待异常",e);
            Thread.currentThread().interrupt();
        }
        return result;

    }

    /**
     * 同步的多线程计算：数字的聚合运算(如带或不带条件的求最值、求和、平均值、方差等)
     * @param dataList 数据列表
     * @param batchSizeCalled 每批次数量，实际最大数量不能超过MAX_Calculate_SIZE
     * @param threadMsg 线程内容说明，抛出异常用
     * @param caculator 反射需要调用的计算类对象
     * @param calculateMethodName 反射需要调用的计算方法名，如果是方差计算，需传参"平均值方法名,方差方法名"
     * @param threadCount 允许创建的最大线程数量，实际线程数量大小为min(circleSize,threadCount,MAX_THREAD_SIZE)
     * @param calculateType 计算类型，支持最值、求和、平均值和方差
     * @return
     */
    public static Double calculateData(List dataList, int batchSizeCalled,String threadMsg,
                                 Object caculator, String calculateMethodName, int threadCount,
                                      CalculateType calculateType){
        if (null==dataList || dataList.size()==0){
            return null;
        }
        //反射创建calculateMethod方法
        Method calculateMethod = null;
        try {
            switch (calculateType){
                case SUM:case MIN:case MAX:case AVG://求和、最值、平均值
                    calculateMethod = caculator.getClass().getMethod(calculateMethodName, List.class);
                    break;
                case VAR://方差
                    //获取求平均值方法
                    calculateMethod = caculator.getClass().getMethod(calculateMethodName.split(",")[0], List.class);
                    break;
                default:
                    return null;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //循环批量
        final int batchSize = Math.min(batchSizeCalled,MAX_Calculate_SIZE);
        int listLength = dataList.size();
        int circleSize = listLength/batchSize;
        if (0==circleSize){
            //如果一批次可处理完，则不需要多线程
            try {
                switch (calculateType){
                    case SUM:case MIN:case MAX:case AVG://求和、最值、平均值
                        return (Double) calculateMethod.invoke(caculator,dataList);
                    case VAR://方差
                        //1.求平均值
                        double avg = (Double) calculateMethod.invoke(caculator,dataList);
                        //2.求方差
                        calculateMethod = caculator.getClass().getMethod(calculateMethodName.split(",")[1], List.class, Double.class, Integer.class);
                        return (Double) calculateMethod.invoke(caculator,dataList,avg,listLength);
                    default:
                        return null;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        Double result = null;
        //多线程中必须引用final或final等效的对象
        Method finalCalculateMethod = calculateMethod;
        //是否有余量
        boolean remain = listLength % batchSize != 0;
        //循环中操作成功计数
        final Double[] caculArr = new Double[circleSize+(remain?1:0)];
        //动态的固定大小线程池，最大取到MAX_THREAD_SIZE
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(circleSize <= threadCount ? Math.min(circleSize,MAX_THREAD_SIZE) : Math.min(threadCount,MAX_THREAD_SIZE));
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //反射调用calculateMethod方法，批量计算
                        caculArr[k-1] = (Double) finalCalculateMethod.invoke(caculator,dataList.subList((k - 1) * batchSize, k * batchSize));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }
            });
        }
        //余量
        if (remain) {
            try {
                caculArr[circleSize] = (Double) finalCalculateMethod.invoke(caculator,dataList.subList(listLength - listLength % batchSize, listLength));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.info("["+threadMsg+"]线程等待异常",e);
            Thread.currentThread().interrupt();
        }
        //result初始化
        result = caculArr[0];
        for (int i = 1;i<caculArr.length;i++) {
            switch (calculateType){
                case SUM://求和
                    result+=caculArr[i];
                    break;
                case MIN://求最小值
                    result=Math.min(caculArr[i],result);
                    break;
                case MAX://求最大值
                    result=Math.max(caculArr[i],result);
                    break;
                case AVG:case VAR://平均值、方差
                    //平均值和方差都是先求平均值
                    if (i!=caculArr.length-1){
                        result+=caculArr[i]*batchSize/listLength;
                    }else{
                        result+=caculArr[i]*(listLength % batchSize)/listLength;
                    }
                    break;
                default:
                    return null;
            }
        }
        //求方差，已经得到平均值
        if (calculateType.equals(CalculateType.VAR)){
            try {
                calculateMethod = caculator.getClass().getMethod(calculateMethodName.split(",")[1], List.class, Double.class, Integer.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
            return getVariance(dataList,result,calculateMethod,batchSize,fixedThreadPool,caculator);
        }
        return result;
    }

    /**
     * 多线程计算方差
     * @param dataList 数据列表
     * @param avg 平均值
     * @param varianceMethod 方差计算Method对象
     * @param batchSize 每批次数量
     * @param fixedThreadPool 线程池
     * @param caculator 方差计算类对象
     * @return
     */
    private static Double getVariance(List dataList,double avg,final Method varianceMethod,
                                      int batchSize,ExecutorService fixedThreadPool,Object caculator){
        Double result = null;
        int listLength = dataList.size();
        int circleSize = listLength/batchSize;
        //是否有余量
        boolean remain = listLength % batchSize != 0;
        //循环中操作成功计数
        final Double[] caculArr = new Double[circleSize+(remain?1:0)];
        //线程等待计数
        final CountDownLatch countDownLatch = new CountDownLatch(circleSize);
        //循环批量
        for (int i = 1; i <= circleSize ; i++) {
            final int k = i;
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //反射调用calculateMethod方法，批量计算
                        caculArr[k-1] = (Double) varianceMethod.invoke(caculator,dataList.subList((k - 1) * batchSize, k * batchSize),
                                avg,listLength);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }
            });
        }
        //余量
        if (remain) {
            try {
                caculArr[circleSize] = (Double) varianceMethod.invoke(caculator,dataList.subList(listLength - listLength % batchSize, listLength),
                        avg,listLength);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.info("",e);
            Thread.currentThread().interrupt();
        }
        for (Double arr:caculArr) {
            result+=arr;
        }
        return result;
    }

}
