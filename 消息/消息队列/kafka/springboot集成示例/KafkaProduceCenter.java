package com.ping.job.master.producer;

import com.ping.job.master.boot.MetaDataCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author liuwei
 * @date 2020-03-08 19:09
 * @desc 生产者服务中心
 */
@Slf4j
public class KafkaProduceCenter {

    private static KafkaTemplate kafkaTemplate;

    //静态变量注入，生产者服务因此可使用静态方法调用
    public static void initParam(KafkaTemplate kafka){
        kafkaTemplate = kafka;
    }

    //获取消息对象
    private static ProducerRecord getRecord(String topic, String msg){
        return new ProducerRecord<>(topic, msg);
    }

    /**
     * 发送消息
     * 消息本身已经经过处理，如类型转换、编码、加密等等，在此直接发送
     */
    public static void sendMsg(String topic, String msg){
        kafkaTemplate.send(getRecord(MetaDataCache.KAFKA_PROPERTIES.getProperty(topic), msg));
    }

}
