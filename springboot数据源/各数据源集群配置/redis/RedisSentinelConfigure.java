package com.jshx.zq.p2p.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jshx.zq.p2p.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Author: liuwei
 * Date: 2019-12-17 08:40
 * Desc: redis集群配置：主从+哨兵模式
 * 在开发和测试环境中，redis很有可能是单节点的，在生产环境基本都是多节点
 * 在多节点环境启用本配置，在单节点环境忽略本配置
 * spring将根据application配置文件的实际配置判断@ConditionalOnExpression的结果
 * redis_struct=sentinel时扫描本类
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${redis_struct}'.equals('sentinel')")
@PropertySource(value = "classpath:/application.properties")
public class RedisSentinelConfigure {

    @Autowired
    private Environment environment;

    /**
     * springboot会自动创建名为redisTemplate的bean
     * 为避免冲突，项目中创建不同名的bean即可
     * 实际上，通过在启动类注解中排除以下2个类可排除内置的redisTemplate
     * RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class
     */
    @Bean(name="redisTemplateData")
    @Autowired
    public RedisTemplate getRedisTemplate01(@Qualifier("factoryData") JedisConnectionFactory factoryData){
        RedisTemplate redis = getRedisTemplateCommon(factoryData);
        log.info("Redis哨兵模式》业务数据 redisTemplateData 注册成功");
        return redis;
    }

    @Bean(name="redisTemplateAuth")
    @Autowired
    public RedisTemplate getRedisTemplate02(@Qualifier("factoryAuth") JedisConnectionFactory factoryAuth){
        RedisTemplate redis = getRedisTemplateCommon(factoryAuth);
        log.info("Redis哨兵模式》鉴权数据 redisTemplateAuth 注册成功");
        return redis;
    }

    private RedisTemplate getRedisTemplateCommon(JedisConnectionFactory jedisConnectionFactory){
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
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
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

    //业务redis工厂
    @Bean("factoryData")
    public JedisConnectionFactory factoryData(){
        String[] host = environment.getProperty("spring.redis.sentinel.nodes").split(",");
        String master = environment.getProperty("spring.redis.sentinel.master");
        String password = environment.getProperty("spring.redis.password");
        int database = Integer.parseInt(environment.getProperty("data.redis.database"));
        return jedisConnectionFactory(host,master,password,database);
    }

    //鉴权redis工厂
    @Bean("factoryAuth")
    public JedisConnectionFactory factoryAuth(){
        String[] host = environment.getProperty("spring.redis.sentinel.nodes").split(",");
        String master = environment.getProperty("spring.redis.sentinel.master");
        String password = environment.getProperty("spring.redis.password");
        int database = Integer.parseInt(environment.getProperty("auth.redis.database"));
        return jedisConnectionFactory(host,master,password,database);
    }

    //redis哨兵和连接工厂配置
    private JedisConnectionFactory jedisConnectionFactory(String[] host,String master,
                                                         String password,int database){
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        for(String redisHost : host){
            String[] item = redisHost.split(":");
            String ip = item[0];
            String port = item[1];
            configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
        }
        log.info("redis哨兵节点："+ JSONObject.toJSONString(host));
        configuration.setMaster(master);
        //redis主从服务登录密码，默认为空
        configuration.setPassword(password);
        //redis主从服务db下标，默认为0
        configuration.setDatabase(database);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        String prefix = "spring.redis.jedis.pool";
        /*
        在空闲时以及获取连接时检查连接的有效性
        检查到无效连接时，会清理掉无效连接并重新获取新的连接
        此项配置意在防止因防火墙策略、redis服务宕机等原因导致出现持久的redis坏连接
         */
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnCreate(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setMaxTotal(getParamInt(prefix,"max-total"));
        poolConfig.setMaxIdle(getParamInt(prefix,"max-idle"));
        poolConfig.setMaxWaitMillis(getParamInt(prefix,"max-wait"));
        poolConfig.setMinIdle(getParamInt(prefix,"min-idle"));
        return new JedisConnectionFactory(configuration,poolConfig);
    }

    private int getParamInt(String prefix, String key){
        return Integer.parseInt(environment.getProperty(prefix+"."+key));
    }

}
