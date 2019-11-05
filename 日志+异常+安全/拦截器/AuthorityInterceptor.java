package com.telecom.js.noc.hxtnms.operationplan.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwei
 * @date 2019-11-03 23:23:35
 * @desc 鉴权拦截器：内部请求的通用鉴权拦截
 * 拦截器属于spring组件，调用于DispatchServlet之后，controller方法之前后
 * (当preHandle返回true时)一个拦截器的执行顺序是preHandle--controller--postHandle--afterCompletion
 * (当所有preHandle返回true时)多个拦截器的执行顺序是preHandle1--preHandle2--controller--postHandle2--postHandle1--afterCompletion2--afterCompletion1
 * (当过滤通过时)一个过滤器的执行顺序是doFilterBefore--controller--doFilterAfter
 * (当所有过滤通过时)多个过滤器的执行顺序是doFilterBefore1--doFilterBefore2--controller--doFilterAfter2--doFilterAfter1
 * 一个过滤器+一个拦截器的执行顺序是doFilterBefore--preHandle--controller--postHandle--afterCompletion--doFilterAfter
 * 多个过滤器+多个拦截器的执行顺序是doFilterBefore1--doFilterBefore2--preHandle1--preHandle2--controller--postHandle2--postHandle1--afterCompletion2--afterCompletion1--doFilterAfter2--doFilterAfter1
 */
@Slf4j
@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private Environment environment;
    
    @Autowired
    private RedisTemplate redisTemplateAuth;

    private static final String AUTH_FAILED_DISPATCH = "/authFailed";
    //默认超时10min
    private Long expireTime = 600L;

    //免拦截的like路径set
    private static final Set<String> FREE_INTER_LIKE = new HashSet<String>();

    static {
        //对外接口、单点登录等开放接口：进入自定义的鉴权方式
    	FREE_INTER_LIKE.add("/api");
    	FREE_INTER_LIKE.add("/sso");
    	//请求转发、内部错误等：放开鉴权，防止死循环false
    	FREE_INTER_LIKE.add("/error");
    	FREE_INTER_LIKE.add(AUTH_FAILED_DISPATCH);
    }
    
	/**
	 * @desc 请求的前处理，在controller方法执行之前被调用
	 * @return
	 * true，放行controller方法
	 * false，不再放行controller方法，直接返回
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("AuthorityInterceptor调用前处理");
        //免拦截的url路径直接放行
        String requestPath = request.getServletPath();
        for (String likePath : FREE_INTER_LIKE) {
            if (requestPath.startsWith(likePath)){
                return true;
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
            request.getRequestDispatcher(AUTH_FAILED_DISPATCH+"/null").forward(request, response);
        }else {
            //校验token：登录信息缓存在redis服务器中
            String key = "AUTH:"+ token.toUpperCase();
            if (redisTemplateAuth.hasKey(key)) {
                //有效请求，刷新redis过期时间
                redisTemplateAuth.expire(key,expireTime,TimeUnit.SECONDS);
                return true;
            }else {
                //无效的token：token虚假或者redis信息已过期
                request.getRequestDispatcher(AUTH_FAILED_DISPATCH+"/expire").forward(request, response);
            }
        }
		return false;
	}

	/**
	 * @desc 请求的后处理，在controller方法执行完成后调用
     * 如果返回视图，在返回响应结果之前被调用
     * 如果返回数据对象，在返回响应结果之后被调用
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
		log.info("AuthorityInterceptor视图响应前或数据对象响应后处理");
		
	}

	/**
	 * @desc 请求完成后的后处理，返回响应结果之后被调用
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("AuthorityInterceptor响应完成后处理");
		
	}

}
