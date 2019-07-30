package design.pattern.structural_patterns.facade.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-30 14:16
 * @desc 子系统2
 */
@Slf4j
public class Entity2 {

    public void system2Method1(){
        log.info("在系统2做事情1");
    }

    public void system2Method2(){
        log.info("在系统2做事情2");
    }
}
