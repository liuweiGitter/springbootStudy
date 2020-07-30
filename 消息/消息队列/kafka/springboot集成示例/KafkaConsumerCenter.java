package com.ping.job.manager.consumer;

import com.ping.job.manager.cache.MasterCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author liuwei
 * @date 2020-07-07 15:50
 * @desc 消费者服务中心
 */
@Component
@ConditionalOnExpression("${kafka_start_flag:true}")
@DependsOn("kafkaBeanCreatedFlag")
@PropertySource(value = "classpath:/data/properties/kafka_topic.properties")
@Slf4j
public class KafkaConsumerCenter {

    @PostConstruct
    private void init(){
        log.info(">>>KafkaConsumerCenter bean init success!");
    }

    @KafkaListener(topics = "${taskMasterInterface}", containerFactory = KafkaConsumerFacType.autoCommitDynamic)
    public void taskMasterRegister (ConsumerRecord<String, String> record) {
        String url = record.value();
        if (StringUtils.isEmpty(url) || !url.startsWith("http")) {
            return;
        }
        MasterCache.addMaster(url);
    }

}


