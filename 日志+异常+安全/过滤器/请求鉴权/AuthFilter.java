package com.telecom.js.noc.hxtnms.operationplan.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

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
//@Order(1)//设置过滤器顺序
//@WebFilter(filterName = "authFilter", urlPatterns = {"/*"})
//@Component
//本例没有使用注解，而是选择在配置类中注册过滤器，详参FilterRegistrationConfig
@Slf4j
public class AuthFilter implements Filter {

    @Autowired
    private Environment environment;
    @Autowired
    private RedisTemplate redisTemplateAuth;

    private static final String AUTH_FAILED_DISPATCH = "/authFailed";
    //默认超时10min
    private Long expireTime = 600L;
    
    //例外过滤路径
    private final Set<String> excludeEqualPaths = new HashSet<String>();
	private final Set<String> excludeLikePaths = new HashSet<String>();

    @PostConstruct
    private void initParam(){
    	//超时时间
        String expireTimeLogin = environment.getProperty("expireTimeLogin");
        if (!StringUtils.isEmpty(expireTimeLogin)){
            expireTime = Long.valueOf(expireTimeLogin);
        }
        //例外过滤路径
        String excludeEqualPaths = environment.getProperty("excludeEqualPaths");
        if (!StringUtils.isEmpty(excludeEqualPaths)){
        	for (String excludeEqualPath : excludeEqualPaths.split(",")) {
        		this.excludeEqualPaths.add(excludeEqualPath);
			}
        }
        String excludeLikePaths = environment.getProperty("excludeLikePaths");
        if (!StringUtils.isEmpty(excludeLikePaths)){
        	for (String excludeLikePath : excludeLikePaths.split(",")) {
        		this.excludeLikePaths.add(excludeLikePath);
			}
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /**
         * 例外路径不过滤，直接放行
         */
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        //以/开头的工程名(不包括)之后?之前的请求路径
		String path = request.getServletPath();
		System.out.println("path:"+path);
		if (excludeEqualPaths.contains(path)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		for (String likePath : excludeLikePaths) {
			if (path.startsWith(likePath.substring(0, likePath.length()-1))) {
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
