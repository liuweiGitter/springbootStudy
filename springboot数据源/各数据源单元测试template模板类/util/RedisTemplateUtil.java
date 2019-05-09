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
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(password);
        JedisConnectionFactory conn = new JedisConnectionFactory(redisStandaloneConfiguration);
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(conn);
        redisTemplate.afterPropertiesSet();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
