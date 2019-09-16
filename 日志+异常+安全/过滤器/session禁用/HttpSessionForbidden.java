package com.telecom.js.noc.hxtnms.operationplan.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * @author liuwei
 * @date 2019-09-16 14:40
 * @desc 重写Tomcat的getSession()方法，禁止生成Session
 * 对getSession()返回null即可
 * 关于session禁用参见https://blog.csdn.net/weixin_38270240/article/details/100834004
 */
public class HttpSessionForbidden extends HttpServletRequestWrapper {


    public HttpSessionForbidden(HttpServletRequest request) {
        super(request);
    }


    @Override
    public HttpSession getSession() {
        return null;
    }


    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }


}
