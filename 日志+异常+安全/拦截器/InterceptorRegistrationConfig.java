package com.telecom.js.noc.hxtnms.operationplan.configure;

import com.telecom.js.noc.hxtnms.operationplan.interceptor.AuthorityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuwei
 * @date 2019-11-04 14:22
 * @desc 注册拦截器
 */
@Configuration
public class InterceptorRegistrationConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorityInterceptor authorityInterceptor;

    /**
     * 按顺序注册多个拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/**");
    }

}
