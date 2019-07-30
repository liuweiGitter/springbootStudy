package design.pattern.structural_patterns.facade.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 14:16
 * @desc 子系统1
 */
@Slf4j
public class Entity1 {

    public void system1Method1(){
        log.info("在系统1做事情1");
    }

    public void system1Method2(){
        log.info("在系统1做事情2");
    }
}
