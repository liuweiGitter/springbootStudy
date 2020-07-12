package com.ping.job.master.config;

import com.ping.job.master.boot.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwei
 * @date 2020-07-12 09:20
 * @desc kafka生产者和消费者配置
 */
@Configuration
@ConditionalOnExpression("${kafka_start_flag:true}")
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap_servers;

    /**
     * 生产者配置
     */
    @Value("${spring.kafka.producer.retries}")
    private String retries;
    @Value("${spring.kafka.producer.batch-size}")
    private String batch_size;
    @Value("${spring.kafka.producer.buffer-memory}")
    private String buffer_memory;
    @Value("${spring.kafka.producer.acks}")
    private String acks;

    /**
     * 消费者配置
     */
    @Value("${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    private final Integer dftMaxPollIntervalMs = 300*6000;
    @Value("${spring.kafka.consumer.auto-commit-interval}")
    private String autoCommitIntervalMs;



    //生产者KafkaTemplate注册
    @Bean
    public KafkaTemplate kafkaTemplate(){
        log.info("KafkaTemplate注册");
        HashMap<String, Object> configs = new HashMap<>();
        //kafka服务器地址
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrap_servers);
        //发送失败时的重试次数
        configs.put(ProducerConfig.RETRIES_CONFIG,retries);
        //批量生产(向服务器发送消息)的最大字节数
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG,batch_size);
        //生产者消息过多时需要缓冲消息等待发送，设置缓冲区内存总字节数
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG,buffer_memory);
        //生产消息的key和value值的序列化
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        /**
         * 生产者要求kafka集群leader在什么时候确认消息生产完成，取值all, -1, 0, 1
         * 0：生产者无需leader确认
         *      发送后即单方面认为已经成功，即使leader并没有收到消息
         * 1：生产者需要leader确认，但leader不需任何副本确认
         *      如果leader在确认后、同步到副本前立即挂掉，副本将永久丢失这些数据
         * -1或all：生产者需要leader确认，并且leader需要等待所有副本确认
         *      除非所有副本都确认了消息，否则leader会认为此消息没有被生产完成
         */
        configs.put(ProducerConfig.ACKS_CONFIG,acks);

        DefaultKafkaProducerFactory producerFactory = new DefaultKafkaProducerFactory(configs);
        return new KafkaTemplate(producerFactory);
    }

    //消费者监听工厂注册：固定groupId + 自动提交消息
    @Bean(name = "kfkListenerFacAutoCommit")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kfkListenerFacAutoCommit() {
        return getKafkaListenerContainerFactory(true,false);
    }

    //消费者监听工厂注册：固定groupId + 手动提交消息
    @Bean(name = "kfkListenerFacManualCommit")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kfkListenerFacManualCommit() {
        return getKafkaListenerContainerFactory(false,false);
    }

    //消费者监听工厂注册：动态groupId + 自动提交消息
    @Bean(name = "kfkListenerFacAutoCommitDy")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kfkListenerFacAutoCommitDy() {
        return getKafkaListenerContainerFactory(true,true);
    }

    //消费者监听工厂注册：动态groupId + 手动提交消息
    @Bean(name = "kfkListenerFacManualCommitDy")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kfkListenerFacManualCommitDy() {
        return getKafkaListenerContainerFactory(false,true);
    }


    private KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
            getKafkaListenerContainerFactory(boolean enableAutoCommit, boolean isDynamicGroupId) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        Map<String, Object> props = new HashMap<>();
        //kafka服务器地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        //是否自动提交消息以及提交消息的延迟时间
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        //消息的key和value值的反序列化
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        /**
         * 设置每次poll的最大数据个数以及最大间隔时间(默认300秒)
         * kafka允许消费者每次poll拉取一批消息，消费者在处理完每一条消息后需要确认(无论自动还是手动)
         * 假设一次poll了10条，则在这10条消息都被确认后，才被允许进行下一轮poll
         * 每一轮poll之间有时间间隔限制，如果消费者处理消息时业务逻辑很重或者其它原因导致一轮确认超时
         * 则消费者会被kafka服务端移除消费组，此后，消费者不再能监听到新消息，且老消息的确认也不会再被接受
         * 因此，需合理评估一次poll的最大数量和poll间隔时间，避免消费者被强制下线，下线后会有以下报错：
         * This member will leave the group because consumer poll timeout has expired.
         * This means the time between subsequent calls to poll() was longer than the configured max.poll.interval.ms,
         * which typically implies that the poll loop is spending too much time processing messages.
         *
         * You can address this either by increasing max.poll.interval.ms
         * or by reducing the maximum size of batches returned in poll() with max.poll.records.
         *
         * Member consumer-1-75d006f2-ee82-477a-a8be-00f229d9763b sending LeaveGroup request
         * to coordinator 47.96.170.25:19092 (id: 2147483647 rack: null)
         */
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, dftMaxPollIntervalMs);
        /**
         * 设置groupId
         * 对于同一个topic的监听，集群中的消费者可以设置是否保持同一个groupId
         * 一些情况下，集群消费者们不允许重复消费消息，比如消息日志的数据库持久化，此时要求这些消费者保持同一个groupId
         * 另一些情况下，集群消费者们需要重复消费消息，比如广播消息的响应，此时要求这些消费者保持不同的groupId
         *
         * 要求同一个groupId时，此处传参false，@KafkaListener中填写固定的groupId即可
         *      @KafkaListener(topics = "xxx", groupId = "xxx", containerFactory = "xxx")
         * 要求不同的groupId时，此处传参true，@KafkaListener中不能填写groupId
         * 此处动态groupId选择为ip地址(端口在spring容器启动后才能获取到)
         *      @KafkaListener(topics = "xxx", containerFactory = "xxx")
         * 注意，一般情况下，集群中服务器私网ip都是不同的，因此可以保证groupId的唯一性
         * 对于伪集群或者容器集群或者跨局域网的集群，或许会存在ip相同的情况，此时添加随机数即可
         */
        if (isDynamicGroupId) {
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka_"+ServerInfo.getIpAddr());
        }

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return factory;
    }


}
