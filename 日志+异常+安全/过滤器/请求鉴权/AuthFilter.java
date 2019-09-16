package com.telecom.js.noc.hxtnms.operationplan.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
//此处使用FilterRegistrationConfig注册过滤器
@Slf4j
public class AuthFilter implements Filter {

    @Autowired
    private Environment environment;
    @Autowired
    private RedisTemplate redisTemplateAuth;

    private static final String AUTH_FAILED_DISPATCH = "/authFailed";
    //默认超时10min
    private Long expireTime = 600L;

    //免过滤的like路径set
    private static final Set<String> FREE_FILTER_LIKE = new HashSet<String>();

    static {
        FREE_FILTER_LIKE.add("/api");
    }

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
        log.info("AuthFilter");
        //免过滤的url路径直接放行
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        String requestPath = request.getServletPath();
        for (String likePath : FREE_FILTER_LIKE) {
            if (requestPath.startsWith(likePath)){
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        /***
         * 从请求的header中或url中获取登录凭证token
         * post请求取header
         * get请求优先从header中取，如果取不到，再从url中取
         */
        String method = request.getMethod();
        String token = request.getHeader("token");
        if ("GET".equals(method) && StringUtils.isEmpty(token)){
            token = request.getParameter("token");
        }
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
