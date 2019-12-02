package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSONObject;
import com.jshx.zq.p2p.exception.BaseException;

import java.util.Map;

/**
 * @author liuwei
 * @date 2019-12-02 10:19
 * @desc Json转Map工具类
 * 主要针对特定的key值映射
 */
public class Json2MapUtil {

    /**
     * JSONObject转Map<String, Object>
     * 进行key值映射
     * @param keys ["jsonKey"]["mapKey"]
     * @param jsonObject
     * @param resultMap
     */
    public static void keyTrans(String[][] keys, JSONObject jsonObject, Map<String, Object> resultMap){
        if (null==keys) {
            throw new BaseException("Json2MapUtil keys cant be null");
        }
        for (String[] key : keys) {
            resultMap.put(key[1],jsonObject.get(key[0]));
        }
    }

}
