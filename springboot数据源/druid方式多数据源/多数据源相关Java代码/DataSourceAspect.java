package com.jshx.zq.p2p.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Author: ZhangXD
 * Description：切换数据源切面
 */
@Aspect
@Component
public class DataSourceAspect {

    @Before("execution(* com.jshx.zq.p2p.mapper.mysql.*.*(..))")
    public void setDataSourceLocal(JoinPoint point){
        DataSourceContextHolder.setDataSource(DataSourceEnum.BIZ_MYSQL);
    }
    @After("execution(* com.jshx.zq.p2p.mapper.mysql.*.*(..))")
    public void clearDataSourceLocal(JoinPoint point){
        DataSourceContextHolder.clear();
    }

}
