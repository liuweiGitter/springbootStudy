package com.telecom.js.noc.hxtnms.operationplan.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwei
 * @date 2019-07-29 09:00
 * @desc 鉴权后用户信息解析
 */
@Component
public class LoginManParse {

    //RedisTemplate本类中暂未使用，故注释
    /*@Autowired
    private static RedisTemplate redisTemplateAuth;*/

    private static ValueOperations<String, String> redisValueOper;

    //初始化注入静态变量
    @Autowired
    public void initStaticVariable(RedisTemplate redisTemplateAuth) {
        //LoginManParse.redisTemplateAuth = redisTemplateAuth;
        redisValueOper = redisTemplateAuth.opsForValue();
    }

    //获取登录对象全部信息{"role":{xxx:xxx,...},"user":{xxx:xxx,...}}
    public static Map<String,Object> getWholeMap(HttpServletRequest servletRequest){
        //从请求的header中获取登录凭证token
        String token = servletRequest.getHeader("token");
        //获取token的key值
        String key = "cas:"+ token.toUpperCase();
        //解析对象
        return JSONObject.parseObject(redisValueOper.get(key),Map.class);
    }

    //获取登录对象角色信息{xxx:xxx,...}
    public static Map<String,Object> getRoleMap(HttpServletRequest servletRequest){
        //解析对象
        return (Map<String, Object>) getWholeMap(servletRequest).get("role");
    }

    //获取登录对象用户信息{xxx:xxx,...}
    public static Map<String,Object> getUserMap(HttpServletRequest servletRequest){
        //解析对象
        return (Map<String, Object>) getWholeMap(servletRequest).get("user");
    }

    //获取用户账号和中文名
    public static Map<String,String> getUserNames(HttpServletRequest servletRequest){
        //解析对象账号
        Map<String,Object> userMap = getUserMap(servletRequest);
        Map<String,String> names = new HashMap<>();
        names.put("userName",userMap.get("userName").toString());
        names.put("cnName",userMap.get("cnName").toString());
        return names;
    }

    //获取用户账号
    public static String getUserName(HttpServletRequest servletRequest){
        //解析对象账号
        return getUserMap(servletRequest).get("userName").toString();
    }

}
