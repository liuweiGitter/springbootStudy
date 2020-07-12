	package com.jshx.zq.p2p.mq.producer;

	import com.jshx.zq.p2p.data.MetaDataCache;
	import lombok.extern.slf4j.Slf4j;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
	import org.springframework.kafka.core.KafkaTemplate;
	import org.springframework.stereotype.Component;

	import java.time.LocalDateTime;
	import java.time.format.DateTimeFormatter;

	/**
	 * @author liuwei
	 * @date 2020-03-10 02:06
	 * @desc 生产者测试
	 */
	@Component
	@ConditionalOnExpression("${kafka_start_flag:true}")
	@Slf4j
	public class TestProducer {

		@Autowired
		private KafkaTemplate kafkaTemplate;

		public void sendMsg(String msg){
			//如果生产者发送的主题尚未创建，kafka会自动创建该主题
			kafkaTemplate.send(MetaDataCache.KAFKA_TOPICS.getProperty("test"), msg);
		}

		public void test(){
			sendMsg("Time = "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		}

	}