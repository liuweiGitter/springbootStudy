package cn.js189.cloud.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: liuwei
 * Date: 2019-06-06 08:46
 * Desc: http post/get请求工具类
 */
@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取一个HttpPost对象，定义了header和json参数
     * @param uri
     * @param jsonPost
     * @param headerMap
     * @return
     */
    public static HttpPost getHttpPost(String uri, String jsonPost, Map<String,String> headerMap){
        HttpPost httpPost = new HttpPost(uri);
        //设置请求的json参数
        StringEntity se = new StringEntity(jsonPost, Charset.forName("UTF-8"));
        se.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(se);
        //设置请求header
        if (headerMap != null) {
            for (Map.Entry<String,String> entry:headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
        }
        httpPost.setHeader("Content-Type","application/json;charset=UTF-8");
        return httpPost;
    }

    /**
     * 获取一个HttpPost对象，定义了header和param参数
     * @param uri
     * @param paramPost
     * @param headerMap
     * @return
     */
    public static HttpPost getHttpPost(String uri, Map<String, String> paramPost, Map<String,String> headerMap){
        //设置请求的param参数
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(uri);
        } catch (URISyntaxException e) {
            log.error("uri syntax error!", e);
            return null;
        }
        List<NameValuePair> list = new LinkedList<>();
        for (Map.Entry<String, String> map : paramPost.entrySet()) {
            list.add(new BasicNameValuePair(map.getKey(), map.getValue()));
        }
        uriBuilder.setParameters(list);
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(uriBuilder.build());
        } catch (URISyntaxException e) {
            log.error("uri syntax error!", e);
            return null;
        }
        //设置请求header
        for (Map.Entry<String,String> entry:headerMap.entrySet()) {
            httpPost.setHeader(entry.getKey(),entry.getValue());
        }
        return httpPost;
    }

    /**
     * 发送一个httpPost请求，获取响应的String类型对象
     * 后续可根据需要转换为json对象、map对象或者其它类型对象
     * @param httpPost
     * @return
     */
    public static String executeAnHttpPost(HttpPost httpPost) {
        String body = "";
        CloseableHttpResponse httpResponse = null;
        try {
            //获取httpClient
            CloseableHttpClient httpClient = HttpClients.custom().build();
            httpResponse = httpClient.execute(httpPost);
            //获取返回值的String类型
            HttpEntity entity = httpResponse.getEntity();
            body = EntityUtils.toString(entity, "UTF-8");
            log.debug("httpPost response:" + body);
        } catch (UnsupportedEncodingException e) {
            log.error("" + e);
        } catch (ClientProtocolException e) {
            log.error("" + e);
        } catch (IOException e) {
            log.error("" + e);
        }finally {
            if (null != httpResponse){
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("httpResponse close exception:" + e);
                }
            }
        }
        return body;
    }


    /**
     * Json格式字符串转成固定格式对象
     * @param entityClz
     * @param <T>
     * @return
     */
    public static <T> T getObjectFromJson(String origin, Class<T> entityClz) {
        return JSON.parseObject(origin, entityClz);
    }

    /**
     * Object对象转为Json格式字符串
     * @param obj
     * @return
     */
    public static String getJsonFromObject(Object obj) {
        return JSON.toJSONString(obj);
    }

}
