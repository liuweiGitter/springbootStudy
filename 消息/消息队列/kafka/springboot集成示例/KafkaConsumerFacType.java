package com.ping.job.master.consumer;

/**
 * @author liuwei
 * @date 2020-07-12 14:16
 * @desc kafka消费者工厂类型
 */
public class KafkaConsumerFacType {
    public static final String autoCommit = "kfkListenerFacAutoCommit";
    public static final String manualCommit = "kfkListenerFacManualCommit";
    public static final String autoCommitDynamic = "kfkListenerFacAutoCommitDy";
    public static final String manualCommitDynamic = "kfkListenerFacManualCommitDy";
}
