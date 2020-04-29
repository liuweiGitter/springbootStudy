package com.jshx.zq.p2p.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author liuwei
 * @date 2020-03-03 09:50
 * @desc 容器事件监听器
 */
@Slf4j
@Component
public class SpringContainerListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            log.info(">>>Spring Container已初始化，容器名："
                    +((ContextRefreshedEvent) event).getApplicationContext().getApplicationName());
        }else if(event instanceof ContextClosedEvent){
            log.info(">>>Spring Container已关闭，等待资源销毁");
            ResourceDestroyCenter.run();
        }
    }

}
