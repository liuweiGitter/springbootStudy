package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-06-06 08:46
 * Desc: http post请求工具类
 */
@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
        throw new IllegalStateException("Utility class");
    }
    private static final Integer TIME_OUT = 10000;
    private static final String XXL_COOKIE = "XXL_JOB_LOGIN_IDENTITY";

    /**
     * 发起http post请求
     * @param loginUrl 登录地址
     * @param requestUrl get请求的目标地址
     * @param loginParam 登录参数：用户名、密码、是否记住登录状态
     * @param requestParam 请求参数
     * @return 空字符串，或者响应的JSON数据
     */
    public static String getResponse(HttpServletRequest request, HttpServletResponse response,
                                     String loginUrl, String requestUrl,
                                     Map<String, String> loginParam, Map<String, String> requestParam) {
        String result = "";
        CloseableHttpClient client = HttpClients.custom().build();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT).
                build();
        CloseableHttpResponse httpResponse = null;

        //1.获取登录cookie：获取失败时直接返回空字符串
        String cookie = getLoginCookie(loginUrl, loginParam, request, response);
        if ("".equals(cookie)){
            return result;
        }

        //2.携带登录cookie发起post请求
        try {
            //2.1携带cookie发起请求
            URI uri = buildPostUrl(requestUrl,requestParam);
            if (null == uri){
                return result;
            }
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Cookie",cookie);
            httpResponse = client.execute(httpPost);
            if (httpResponse == null) {
                return "";
            }
            //2.2获取请求响应
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            log.error("[request] io error!", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("[request] client close error!", e);
            }
        }
        return result;
    }

    /**
     * 获取登录cookie字符串
     * @param loginUrl 登录地址
     * @param loginParam 登录参数：用户名、密码、是否记住登录状态
     * @param request
     * @param response
     * @return
     */
    private static String getLoginCookie(String loginUrl, Map<String, String> loginParam,
                                HttpServletRequest request, HttpServletResponse response){
        String loginCookie = "";
        //0.读取浏览器cookie，如果有登录cookie，直接返回
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie:cookies) {
                if (XXL_COOKIE.equals(cookie.getName())){
                    loginCookie = XXL_COOKIE+"="+cookie.getValue()+";Max-Age="+cookie.getMaxAge();
                    return loginCookie;
                }
            }
        }
        CloseableHttpClient client = HttpClients.custom().build();
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).setConnectionRequestTimeout(TIME_OUT).
                build();
        //1.登录
        CloseableHttpResponse httpResponse = null;
        try {
            //1.1拼接登录url
            URI uri = buildPostUrl(loginUrl,loginParam);
            if (null == uri){
                return loginCookie;
            }
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);
            //1.2发起登录请求
            httpResponse = client.execute(httpPost);
        } catch (ClientProtocolException e) {
            log.error("[login] client protocol error!", e);
        } catch (IOException e) {
            log.error("[login] io error!", e);
        } finally {
            if (httpResponse == null){
                try {
                    client.close();
                } catch (IOException e) {
                    log.error("[login] client close error!", e);
                }
            }
        }

        //2.获取登录cookie
        Header cookieHeader = httpResponse.getFirstHeader("Set-Cookie");
        loginCookie = cookieHeader.getValue();
        //3.客户端添加cookie
        String[] loginCookieArr = loginCookie.split(";");
        Cookie cookie = new Cookie(XXL_COOKIE,loginCookieArr[0].substring(23));
        cookie.setMaxAge(Integer.parseInt(loginCookieArr[1].substring(9)));
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return loginCookie;
    }

    /**
     * 拼接post请求的uri
     * @param url 请求的url
     * @param postParam 请求的post参数
     * @return
     */
    private static URI buildPostUrl(String url, Map<String, String> postParam){
        //拼接url
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            List<NameValuePair> list = new LinkedList<>();
            for (Map.Entry<String, String> map : postParam.entrySet()) {
                list.add(new BasicNameValuePair(map.getKey(), map.getValue()));
            }
            uriBuilder.setParameters(list);
            return uriBuilder.build();
        }catch (URISyntaxException e) {
            log.error("url syntax error!", e);
        }
        return null;
    }

}
