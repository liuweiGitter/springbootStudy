package com.telecom.js.noc.hxtnms.operationplan.configure;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Author: liuwei
 * Date: 2019-05-08 08:40
 * Desc: nosql多数据源时，数据库template类等自定义bean位置
 *      application配置文件配置单数据源，其它数据源定义在此
 */
@Slf4j
@Configuration
@PropertySource(value = "classpath:/application.properties")
public class NosqlDbConfigure {

    @Autowired
    private Environment environment;

    @Bean(name="otmsMongoTemplate")
    public MongoTemplate getOtmsMongoTemplate(){
        String host = environment.getProperty("data.mongodb.otms.host");
        int port = Integer.valueOf(environment.getProperty("data.mongodb.otms.port"));
        String database = environment.getProperty("data.mongodb.otms.database");
        String username = environment.getProperty("data.mongodb.otms.username");
        String password = environment.getProperty("data.mongodb.otms.password");
        String database4Authen = environment.getProperty("data.mongodb.otms.database4Authen");

        //服务地址
        ServerAddress serverAddress = new ServerAddress(host,port);
        //鉴权
        MongoCredential credential = MongoCredential.createCredential(username,database4Authen,password.toCharArray());
        //连接选项(最大连接数，超时时间等，使用默认即可)
        MongoClientOptions mongoClientOptions = new MongoClientOptions.Builder().build();

        MongoClient mongoClient = new MongoClient(serverAddress,credential,mongoClientOptions);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,database);
        log.info("Get Otms mongoTemplate successfully");
        return mongoTemplate;
    }

	/**
     * springboot会自动创建名为redisTemplate的bean
     * 为避免冲突，项目中创建不同名的bean即可
     */
    //@Bean(name="redisTemplateData")
    public RedisTemplate getRedisTemplate01(){
        String host = environment.getProperty("data.redis.host");
        int port = Integer.valueOf(environment.getProperty("data.redis.port"));
        int database = Integer.valueOf(environment.getProperty("data.redis.database"));
        String password = environment.getProperty("data.redis.password");//password为空时没有鉴权密码
        return getRedisTemplateCommon(host,port,database,password);
    }

    @Bean(name="redisTemplateAuth")
    public RedisTemplate getRedisTemplate02(){
        String host = environment.getProperty("auth.redis.host");
        int port = Integer.valueOf(environment.getProperty("auth.redis.port"));
        int database = Integer.valueOf(environment.getProperty("auth.redis.database"));
        String password = environment.getProperty("auth.redis.password");//password为空时没有鉴权密码
        return getRedisTemplateCommon(host,port,database,password);
    }

    private RedisTemplate getRedisTemplateCommon(String host,int port,int database, String password){
        //redis单节点配置，集群需配置RedisClusterConfiguration
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);

        /**
         * 为LocalDateTime设置统一的redis序列化和反序列化格式
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //设置非空值才进行序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置反序列化时忽略JSON字符串中存在而Java对象实际没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置返回json格式数据，否则会默认hashMap从而反序列化错误
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //LocalDateTime系列序列化和反序列化模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.STANDARD));
        javaTimeModule.addSerializer(LocalDate.class,new LocalDateSerializer(DateUtils.STANDARD_DATE));
        javaTimeModule.addSerializer(LocalTime.class,new LocalTimeSerializer(DateUtils.STANDARD_TIME));
        javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateUtils.STANDARD));
        javaTimeModule.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateUtils.STANDARD_DATE));
        javaTimeModule.addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateUtils.STANDARD_TIME));
        objectMapper.registerModule(javaTimeModule);

        JedisConnectionFactory conn = new JedisConnectionFactory(redisStandaloneConfiguration);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(conn);
        redisTemplate.afterPropertiesSet();
        //设置key、value的序列化方式，在afterPropertiesSet后设置
        /**
         * Spring提供以下序列化方式：
         * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、
         * JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer
         * RedisTemplate默认使用的是JdkSerializationRedisSerializer序列化，但默认值并不是最优的
         * key值通常是String类型，选择StringRedisSerializer
         * value值则可能是各种数据类型，选择Jackson2JsonRedisSerializer
         * value为java类时不必实现序列化，Jackson在存储类对象时会在@class字段存储类全路径，读取解析时会代理反序列化
         * 字段@class如果被人为删除或更改，将无法反序列化(即使类自己实现了序列化)
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        /**
         * 开启事务支持(默认是不支持的，需手动开启)，使得一个方法中的所有redis操作在同一个 Connection 中执行命令
         * 注意，此处仅仅是事务支持，不代表实际开启了事务，同一个 Connection 不代表进行了批命令异常的回滚处理
         * 在开启事务支持后，有2种方法开启实际的事务：
         * 1.对使用redis操作的方法，在操作前添加redisTemplate.multi()，操作后添加redisTemplate.exec()
         {
         redisTemplate.multi();//开始事务
         一系列redis操作...
         redisTemplate.exec();//结束事务
         }
         * 2.对使用redis操作的方法添加@Transactional注解，操作后添加redisTemplate.exec()
         @Transactional //开始事务
         {
         一系列redis操作...
         redisTemplate.exec();//结束事务
         }
          * 3.使用redis回调(推荐，首选，会自动开启事务和处理连接的开关，回调中管道效率最高，推荐)
         {
         SessionCallback<Object> callback = new SessionCallback<Object>() {
         @Override
         public Object execute(RedisOperations operations) throws DataAccessException {
         operations.multi();
         operations.opsForValue().xxx;
         operations.opsForList().xxx;
         ...
         return operations.exec();
         }
         };
         //普通回调
         redisTemplate.execute(callback);
         //管道回调
         redisTemplate.executePipelined(callback);
         }
         */
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }
}
