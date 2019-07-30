package design.pattern.structural_patterns.facade.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 14:16
 * @desc 子系统3
 */
@Slf4j
public class Entity3 {

    public void system3Method1(){
        log.info("在系统3做事情1");
    }

    public void system3Method2(){
        log.info("在系统3做事情2");
    }

    public void system3Method3(){
        log.info("在系统3做事情3");
    }
}
