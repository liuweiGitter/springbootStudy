package com.telecom.js.noc.hxtnms.operationplan.configure;

import com.telecom.js.noc.hxtnms.operationplan.filter.AuthFilter;
import com.telecom.js.noc.hxtnms.operationplan.filter.SessionForbiddenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * @author liuwei
 * @date 2019-07-26 15:16
 * @desc 过滤器注册：解决多过滤器顺序问题
 * 在配置多个过滤器时，Order注解并不能保证过滤器顺序，需要通过FilterRegistrationBean在配置类中注册过滤器
 */
@Configuration
public class FilterRegistrationConfig {

    private static final String AUTHOR_KEY = "author";
    private static final String AUTHOR_VAL = "liuwei";

    /**
     * 注册鉴权过滤器：优先级1
     */
    @Bean(name = "authFilterRegistration")
    @Autowired
    public FilterRegistrationBean authFilterRegistration(Filter authFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(authFilter);
        registration.addUrlPatterns("/*");
        registration.addInitParameter(AUTHOR_KEY, AUTHOR_VAL);
        registration.setName("authFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 对鉴权过滤器创建Bean对象：因需要进行Spring注入
     * 可以使用任何普通Bean的注册方式
     * 本例使用配置类注册过滤器为一个普通Bean
     * 也可以在过滤器类头添加@Component注解来注册
     */
    @Bean(name = "authFilter")
    public Filter authFilter() {
        return new AuthFilter();
    }


    /**
     * 注册session禁用过滤器：优先级0
     */
    @Bean(name = "sessionForbiddenFilterRegistration")
    public FilterRegistrationBean sessionForbiddenFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SessionForbiddenFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter(AUTHOR_KEY, AUTHOR_VAL);
        registration.setName("sessionForbiddenFilter");
        registration.setOrder(0);
        return registration;
    }

}
