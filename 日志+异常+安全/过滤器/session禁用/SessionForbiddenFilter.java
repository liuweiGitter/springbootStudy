package com.telecom.js.noc.hxtnms.operationplan.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author liuwei
 * @date 2019-09-16 14:46
 * @desc session禁用过滤器
 */
@Slf4j
public class SessionForbiddenFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("SessionForbiddenFilter");
        filterChain.doFilter(new HttpSessionForbidden((HttpServletRequest) servletRequest), servletResponse);
    }

    @Override
    public void destroy() {

    }
}
