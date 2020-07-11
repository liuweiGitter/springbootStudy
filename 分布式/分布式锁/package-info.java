/**
 * @author liuwei
 * @date 2020-04-30 13:08
 * @desc 切面
 */
package com.jshx.zq.p2p.aspect;
/**
 spring切面要求被注解的方法必须是public的
 只能注解于spring直接代理的方法上，如控制层A的入口、服务层B的入口、定时任务C的入口
 不能注解于直接代理方法内部调用的其它方法X
 */