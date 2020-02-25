package com.jshx.zq.p2p.util;

import com.jshx.zq.p2p.log.LogCenter;
import com.jshx.zq.p2p.log.LogThreadPioneer;
import com.jshx.zq.p2p.log.OutCallLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-06-06 08:46
 * Desc: http post/get请求工具类
 * 所有post和get传参为json格式，编码字符集默认为UTF-8，也可以根据需要指定特定字符集
 * 接收响应字节流后默认解析为UTF-8字符串，也可以根据需要指定特定字符集
 *
 * 嵌入了日志埋点
 */
@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String DFT_CT_TYPE = "application/json;charset=";

    private static final String UTF8 = "UTF-8";

    private static final String CT_TYPE = "Content-Type";

    private static final int KILO = 1000;

    private static final String PROTOCOL = "http";

    private static final String CONN_ERROR = "连接异常";

    private static final String RESPONSE_ERROR = "响应异常";

    private static final String CALL_SUCCESS = "正常";

    private static final String LOG_CONN_MSG = "HttpService服务连接异常!";

    private static final String LOG_RESPONSE_MSG = "HttpService服务响应异常!";

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(10*KILO).setSocketTimeout(10*KILO).build();

    //==============获取post请求对象==============//

    /**
     * 获取一个HttpPost对象，定义了header和json参数
     * 编码字符集UTF-8
     *
     * @param uri
     * @param jsonPost
     * @param headerMap
     * @return
     */
    public static HttpPost getHttpPost(String uri, String jsonPost, Map<String, String> headerMap) {
        return getHttpPost(uri, jsonPost, headerMap, UTF8);
    }

    public static HttpPost getHttpPost(String uri, String jsonPost, Map<String, String> headerMap, String charsetName) {
        HttpPost httpPost = new HttpPost(uri);
        //设置请求的json参数
        StringEntity se = new StringEntity(jsonPost,charsetName);
        se.setContentType("text/json");
        httpPost.setEntity(se);
        //设置请求header
        setHeaderMap(httpPost, headerMap);
        //设置请求连接超时
        httpPost.setConfig(REQUEST_CONFIG);
        httpPost.setHeader(CT_TYPE, DFT_CT_TYPE + charsetName);
        return httpPost;
    }

    /**
     * 获取一个HttpPost对象，定义了header和param参数(将被转为json格式)
     * 编码字符集UTF-8
     *
     * @param uri
     * @param paramPost
     * @param headerMap
     * @return
     */
    public static HttpPost getHttpPost(String uri, Map<String, Object> paramPost, Map<String, String> headerMap) {
        return getHttpPost(uri, paramPost, headerMap, DFT_CT_TYPE + UTF8);
    }

    public static HttpPost getHttpPost(String uri, Map<String, Object> paramPost, Map<String, String> headerMap, String charsetName) {
        //设置请求的param参数
        URIBuilder uriBuilder = getURIBuilder(uri,paramPost);
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(uriBuilder.build());
        } catch (URISyntaxException e) {
            log.error("uri syntax error!", e);
            return null;
        }
        //设置请求header
        setHeaderMap(httpPost, headerMap);
        //设置请求连接超时
        httpPost.setConfig(REQUEST_CONFIG);
        httpPost.setHeader(CT_TYPE, DFT_CT_TYPE + charsetName);
        return httpPost;
    }


    //==============获取get请求对象==============//


    public static HttpGet getHttpGet(String uri) {
        return getHttpGet(uri, null, null, UTF8);
    }

    public static HttpGet getHttpGet(String uri, Map<String, Object> paramGet, Map<String, String> headerMap) {
        return getHttpGet(uri, paramGet, headerMap, UTF8);
    }

    public static HttpGet getHttpGet(String uri, Map<String, Object> paramGet, Map<String, String> headerMap, String charsetName) {
        //设置请求的param参数
        URIBuilder uriBuilder = getURIBuilder(uri,paramGet);
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            log.error("uri syntax error!", e);
            return null;
        }
        //设置请求header
        setHeaderMap(httpGet, headerMap);
        //设置请求连接超时
        httpGet.setConfig(REQUEST_CONFIG);
        httpGet.setHeader(CT_TYPE, DFT_CT_TYPE + charsetName);
        return httpGet;
    }



    //==============发起post或get请求并得到响应结果==============//

    /**
     * 发送一个httpPost或httpGet请求，获取响应的String类型对象(解码字符集UTF-8)
     * 后续可根据需要转换为json对象、map对象或者其它类型对象
     *
     * @param httpUriRequest
     * @return
     */
    public static String executeAnHttpRequest(HttpUriRequest httpUriRequest) {
        return executeAnHttpRequest(httpUriRequest, UTF8);
    }

    public static String executeAnHttpRequest(HttpUriRequest httpUriRequest, String decodeCharsetName) {
        String body = null;
        CloseableHttpResponse httpResponse = null;

        //日志埋点
        OutCallLog outCallLog = LogThreadPioneer.getOutCallLog(httpUriRequest.getURI().getPath(),PROTOCOL);
        long startTime = System.currentTimeMillis();
        long connTime;
        long endTime;

        //连接埋点
        CloseableHttpClient httpClient;
        try{
            //获取httpClient：创建连接
            httpClient = HttpClients.custom().build();
        }catch (Exception e){
            endTime = connTime = System.currentTimeMillis();
            LogCenter.submitOutCallLog(outCallLog.addCallResult(connTime-startTime,endTime-startTime,false,CONN_ERROR));
            return LogAndThrowException.returnString(LOG_CONN_MSG,e);
        }
        connTime = System.currentTimeMillis();
        boolean success = true;

        //响应埋点
        try {
            //获取响应
            httpResponse = httpClient.execute(httpUriRequest);
            //获取返回值的String类型
            HttpEntity entity = httpResponse.getEntity();
            body = EntityUtils.toString(entity, decodeCharsetName);
        } catch (IOException e) {
            success = false;
            LogAndThrowException.error(LOG_RESPONSE_MSG,e);
        } finally {
            ResourceClose.close(httpResponse,httpClient);
            endTime = System.currentTimeMillis();
            LogCenter.submitOutCallLog(outCallLog.addCallResult(connTime-startTime,endTime-startTime,success,success?CALL_SUCCESS:RESPONSE_ERROR));
        }
        return body;
    }


    //设置header
    private static void setHeaderMap(HttpUriRequest httpUriRequest, Map<String, String> headerMap) {
        if (null != headerMap) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpUriRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }
    //获取URIBuilder
    private static URIBuilder getURIBuilder(String uri,Map<String, Object> paramMap){
        //设置请求的param参数
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(uri);
        } catch (URISyntaxException e) {
            log.error("uri syntax error!", e);
            return null;
        }
        if (null != paramMap && paramMap.size()>0) {
            List<NameValuePair> list = new LinkedList<>();
            for (Map.Entry<String, Object> map : paramMap.entrySet()) {
                list.add(new BasicNameValuePair(map.getKey(), map.getValue().toString()));
            }
            uriBuilder.setParameters(list);
        }
        return uriBuilder;
    }

}
