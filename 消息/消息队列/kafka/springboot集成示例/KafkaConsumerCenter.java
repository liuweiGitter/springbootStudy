package com.ping.job.master.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2020-07-07 15:50
 * @desc 消费者服务中心
 */
@Component
@ConditionalOnExpression("${kafka_start_flag:true}")
@PropertySource(value = "classpath:/data/properties/kafka_topic.properties")
@Slf4j
public class KafkaConsumerCenter {

    //@KafkaListener(topics = "${xxTopic主题}", containerFactory = "kfkListenerFacAutoCommitDy")
    //@KafkaListener(topics = "${xxTopic主题}", containerFactory = "kfkListenerFacManualCommitDy")
    //@KafkaListener(topics = "${xxTopic主题}", groupId = "xxGroupId", containerFactory = "kfkListenerFacAutoCommit")
    //@KafkaListener(topics = "${xxTopic主题}", groupId = "xxGroupId", containerFactory = "kfkListenerFacManualCommit")
    public void xxConsumerBusiness (ConsumerRecord<String, String> record) {
        /**
         * 业务处理在业务层执行
         * 服务中心只负责消费者监听注册
         */
        //XXConsumer.business(record.value());
    }
	
	//...

}


