package com.jshx.zq.p2p.util;

import com.jshx.zq.p2p.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-11-29 12:58
 * @desc 记录日志并抛出异常工具类
 */
@Slf4j
public class LogAndThrowException {

    public static void error(String errorMsg,Object obj){
        log.error(errorMsg,obj);
        throw new BaseException(errorMsg);
    }

}
