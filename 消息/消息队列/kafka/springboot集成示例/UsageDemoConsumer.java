package com.liuwei.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2020-07-07 15:50
 * @desc 消费者demo
 * 动态/固定groupId、自动/手动确认消息，消费者用法示例
 */
@Component
@ConditionalOnExpression("${kafka_start_flag:true}")
@PropertySource(value = "classpath:/data/properties/kafka_topic.properties")
@Slf4j
public class UsageDemoConsumer {

    /**
     * 动态groupId + 自动确认消息
     */
    @KafkaListener(topics = "${taskMasterInterface}", containerFactory = "kfkListenerFacAutoCommitDy")
    public void kfkListenerFacAutoCommitDy (ConsumerRecord<String, String> record) {
        //业务处理
        log.info("<<<kfkListenerFacAutoCommitDy>>>"+record.value());
    }

    /**
     * 动态groupId + 手动确认消息
     */
    @KafkaListener(topics = "${taskMasterInterface}", containerFactory = "kfkListenerFacManualCommitDy")
    public void kfkListenerFacManualCommitDy (KafkaConsumer<String, String> consumer, ConsumerRecord<String, String> record) {
        //业务处理
        log.info("<<<kfkListenerFacManualCommitDy>>>"+record.value());
        //手动确认消息：异步确认
        consumer.commitSync();
    }

    //======================================================================//

    /**
     * 固定groupId + 自动确认消息
     * groupId自定义固定值
     */
    @KafkaListener(topics = "${taskMasterInterface}", groupId = "kfkListenerFacAutoCommit", containerFactory = "kfkListenerFacAutoCommit")
    public void kfkListenerFacAutoCommit (ConsumerRecord<String, String> record) {
        //业务处理
        log.info("<<<kfkListenerFacAutoCommit>>>"+record.value());
    }

    /**
     * 固定groupId + 手动确认消息
     * groupId自定义固定值
     */
    @KafkaListener(topics = "${taskMasterInterface}", groupId = "kfkListenerFacManualCommit", containerFactory = "kfkListenerFacManualCommit")
    public void kfkListenerFacManualCommit (KafkaConsumer<String, String> consumer, ConsumerRecord<String, String> record) {
        //业务处理
        log.info("<<<kfkListenerFacManualCommit>>>"+record.value());
        //手动确认消息：异步确认
        consumer.commitSync();
    }

}


