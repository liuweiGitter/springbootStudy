package com.telecom.js.noc.hxtnms.operationplan.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 登录认证Filter
 * 1.校验用户登录状态
 * 2.过期策略处理：活跃请求刷新redis过期时间
 * </p>
 *
 * @author Dingpeng
 * @since 2019-06-26
 */
//@Order(1)
//@WebFilter(filterName = "authFilter", urlPatterns = {"/*"})
//@WebFilter可以注册过滤器，但过滤器将先于Bean被Spring实例化，因此无法注入其它Bean
//可以在配置类中注册过滤器，解决Bean注入问题，详参FilterRegistrationConfig
public class AuthFilter implements Filter {
    @Autowired
    private Environment environment;
    @Autowired
    private RedisTemplate redisTemplateAuth;

    private final String AUTH_FAILED_DISPATCH = "/authFailed";
    //默认超时10min
    private Long expireTime = 600L;

    @PostConstruct
    private void initParam(){
        String expireTimeLogin = environment.getProperty("expireTimeLogin");
        if (!StringUtils.isEmpty(expireTimeLogin)){
            expireTime = Long.valueOf(expireTimeLogin);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //从请求的header中获取登录凭证token
        String token = ((HttpServletRequest) servletRequest).getHeader("token");
        //不存在的token
        if (StringUtils.isEmpty(token)) {
            servletRequest.getRequestDispatcher(AUTH_FAILED_DISPATCH+"/null").forward(servletRequest, servletResponse);
        }else {
            //校验token：登录信息缓存在redis服务器中
            String key = "cas:"+ token.toUpperCase();
            if (redisTemplateAuth.hasKey(key)) {
                //有效请求，刷新redis过期时间
                redisTemplateAuth.expire(key,expireTime,TimeUnit.SECONDS);
                filterChain.doFilter(servletRequest, servletResponse);
            }else {
                //无效的token：token虚假或者redis信息已过期
                servletRequest.getRequestDispatcher(AUTH_FAILED_DISPATCH+"/expire").forward(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {
    }

}
