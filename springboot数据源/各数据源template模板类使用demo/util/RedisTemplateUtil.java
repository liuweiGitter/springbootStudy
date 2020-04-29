package util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Author: liuwei
 * Date: 2019-05-08 15:45
 * Desc: redisTemplate工具测试类，在普通测试类中使用
 */
@Slf4j
public class RedisTemplateUtil {

    //获取鉴权的RedisTemplate对象
    public RedisTemplate getRedisTemplate(){
        String host = "马赛克";
        int port = 马赛克;
        int database = 马赛克;
        String password = "";//本例没有鉴权密码
        //redis单节点配置，集群需配置RedisClusterConfiguration
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);

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
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
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
