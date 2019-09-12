package com.telecom.js.noc.hxtnms.operationplan.configure;

import com.telecom.js.noc.hxtnms.operationplan.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author liuwei
 * @date 2019-07-26 15:16
 * @desc 过滤器注册：解决过滤器中Bean无法注入问题
 */
@Configuration
public class FilterRegistrationConfig {

    /**
     * 注册过滤器
     */
    @Bean
    public FilterRegistrationBean authFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(authFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("author", "liuwei");
        registration.setName("authFilter");
		//设置过滤器顺序
        registration.setOrder(1);
        return registration;
    }

    /**
     * 对过滤器创建Bean对象
     * 注意：过滤器类本身作为一个普通Bean而不是Filter注册，如此可以在过滤器中注入Bean
     * 可以使用任何普通Bean的注册方式
     * 本例使用配置类注册过滤器为一个普通Bean
     * 也可以在过滤器类头添加@Component注解来注册
     */
    @Bean
    public Filter authFilter() {
        return new AuthFilter();
    }
}
