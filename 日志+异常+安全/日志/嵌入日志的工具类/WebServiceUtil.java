package com.jshx.zq.p2p.util;

import com.jshx.zq.p2p.exception.BaseException;
import com.jshx.zq.p2p.log.LogCenter;
import com.jshx.zq.p2p.log.LogThreadPioneer;
import com.jshx.zq.p2p.log.OutCallLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @date 2019-11-19 14:33
 * @desc WebService请求工具类
 * 嵌入了日志埋点
 */
@Slf4j
public class WebServiceUtil {

    private WebServiceUtil() {
        throw new BaseException("this is util class, you should not create an object!");
    }

    private static final int SECOND = 1000;

    private static final String CHARSET = "UTF-8";

    private static final String CONN_ERROR = "连接异常";

    private static final String RESPONSE_ERROR = "响应异常";

    private static final String CALL_SUCCESS = "正常";

    private static final String LOG_CONN_MSG = "WebService服务连接异常!";

    private static final String LOG_RESPONSE_MSG = "WebService服务响应异常!";

    private static final String PROTOCOL = "soap";


    /**
     * post请求
     * @param wsdlUrl wsdl地址
     * @param xmlRequestParam 请求完整报文内容
     * @return 请求的原始响应数据
     */
    private static String postRequest(String wsdlUrl, String xmlRequestParam) {
        BufferedReader in = null;
        HttpURLConnection conn = null;
        OutputStream output = null;

        //日志埋点
        OutCallLog outCallLog = LogThreadPioneer.getOutCallLog(wsdlUrl,PROTOCOL);
        long startTime = System.currentTimeMillis();
        long connTime;
        long endTime;

        //连接埋点
        try {
            conn = getConn(wsdlUrl,xmlRequestParam);
            conn.connect();
        } catch (IOException e) {
            ResourceClose.disconnect(conn);
            endTime = connTime = System.currentTimeMillis();
            LogCenter.submitOutCallLog(outCallLog.addCallResult(connTime-startTime,endTime-startTime,false,CONN_ERROR));
            return LogAndThrowException.returnString(LOG_CONN_MSG,e);
        }
        connTime = System.currentTimeMillis();
        boolean success = true;
        String response = "";

        //响应埋点
        try {
            response = getResponse(in,conn,output,xmlRequestParam);
        } catch (Exception e) {
            success = false;
            LogAndThrowException.error(LOG_RESPONSE_MSG,e);
        } finally {
            ResourceClose.close(in,output);
            ResourceClose.disconnect(conn);
            endTime = System.currentTimeMillis();
            LogCenter.submitOutCallLog(outCallLog.addCallResult(connTime-startTime,endTime-startTime,success,success?CALL_SUCCESS:RESPONSE_ERROR));
        }
        return response;
    }

    private static HttpURLConnection getConn(String wsdlUrl, String xmlRequestParam){
        HttpURLConnection conn = null;
        try {
            URL url = new URL(wsdlUrl);
            conn = (HttpURLConnection) url.openConnection();
            //报文头
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("Content-Length", String.valueOf(xmlRequestParam.length()));
            conn.setRequestProperty("SOAPAction", "");
            conn.setRequestMethod("POST");
            //连接超时时间
            conn.setConnectTimeout(20 * SECOND);
            conn.setReadTimeout(20 * SECOND);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return conn;
    }

    private static String getResponse(BufferedReader in,HttpURLConnection conn,OutputStream output,String xmlRequestParam) throws IOException {
        //定义客户端输出流：输出请求消息到服务端
        output = conn.getOutputStream();
        if (null != xmlRequestParam) {
            byte[] soapRequest = xmlRequestParam.getBytes(CHARSET);
            //发送soap请求报文
            output.write(soapRequest, 0, soapRequest.length);
        }
        output.flush();
        StringBuilder sb = new StringBuilder();
        //读取响应报文
        in = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET));
        String str;
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * post批量请求
     * @param wsdlUrl
     * @param xmlRequestParams
     * @param continueNext 每一次请求异常时，是否继续下一次请求
     * @return
     */
    private static List<String> postRequest(String wsdlUrl, List<String> xmlRequestParams, boolean continueNext) {
        List<String> list = new ArrayList<>();
        for (String xmlRequestParam : xmlRequestParams) {
            try{
                list.add(postRequest(wsdlUrl, xmlRequestParam));
            }catch (Exception e){
                if (continueNext) {
                    continue;
                }else {
                    throw e;
                }
            }
        }
        return list;
    }

    public static List<String> postAndGetXmlLabelResponse(String wsdlUrl, List<String> xmlRequestParams, boolean continueNext) {
        List<String> originResponses = postRequest(wsdlUrl, xmlRequestParams, continueNext);
        //获取CDATA中的数据：即被转义的数据
        List<String> responses = new ArrayList<>();
        for (String originResponse : originResponses) {
            responses.add(getXmlLabelContent(originResponse));
        }
        return responses;
    }

    /**
     * 返回一个json格式的数据
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postAndGetJsonResponse(String wsdlUrl, String xmlRequestParam) {
        return getJsonContent(postRequest(wsdlUrl, xmlRequestParam));
    }

    private static String getJsonContent(String xmlStr) {
        int startIndex = xmlStr.indexOf("{");
        int endIndex = xmlStr.lastIndexOf("}");
        return xmlStr.substring(startIndex,endIndex+1);
    }

    /**
     * 返回的数据在CDATA时
     * 此方法要求响应数据带有根元素，形如<root><a></a><b></b></root>
     * 如果没有根元素，使用Xml2JsonUtil解析数据为JSONObject时会报错
     * 本方法判断响应没有根元素时，会自动拼接root元素
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postAndGetCDATAResponse(String wsdlUrl, String xmlRequestParam) {
        return getCDATAContent(postRequest(wsdlUrl, xmlRequestParam));
    }

    /**
     * 从xml字符串中提取<!CDATA[xxx]]>数据，并返回xxx
     */
    private static String getCDATAContent(String xmlStr){
        if (!xmlStr.contains("CDATA")) {
            LogAndThrowException.error("数据结构错误!",xmlStr);
        }
        int startIndex = xmlStr.indexOf("CDATA")+6;
        int endIndex = xmlStr.lastIndexOf("]]>");
        String content = xmlStr.substring(startIndex,endIndex);;
        if (!hasRootElement(content)) {
            content = "<root>"+content+"</root>";
        }
        return content;
    }

    /**
     * 返回的数据在CDATA，但CDATA被转义时
     * 使用HttpURLConnection时，CDATA会被转义
     * 具体来说
     * < -- &lt;
     * > -- &gt;
     * " -- &quto;
     * 等等
     * 此方法要求响应数据带有根元素，形如<root><a></a><b></b></root>
     * 如果没有根元素，使用Xml2JsonUtil解析数据为JSONObject时会报错
     * 本方法判断响应没有根元素时，会自动拼接root元素
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postAndGetXmlLabelResponse(String wsdlUrl, String xmlRequestParam) {
        String originResponse = postRequest(wsdlUrl, xmlRequestParam);
        //获取CDATA中的数据：即被转义的数据
        return getXmlLabelContent(originResponse);
    }

    /**
     * 从xml字符串中提取&lt;***&gt;数据，并反转义后返回
     */
    private static String getXmlLabelContent(String originResponse) {
        int startIndex = originResponse.indexOf("&lt;");
        int endIndex = originResponse.lastIndexOf("&gt;")+4;
        String unescapeXml = StringEscapeUtils.unescapeXml(originResponse.substring(startIndex,endIndex));
        if (!hasRootElement(unescapeXml)) {
            unescapeXml = "<root>"+unescapeXml+"</root>";
        }
        return unescapeXml;
    }

    /**
     * 是否带有<?xml标头，如果带，一定会有根元素，否则报文本身就是错误的
     * 不带标头的报文，判断是否有根元素
     */
    private static boolean hasRootElement(String xmlStr){
        if (!xmlStr.startsWith("<?")) {
            int firstRight = xmlStr.indexOf(">");
            String firstLabel = xmlStr.substring(1,firstRight);
            int lastLeft = xmlStr.lastIndexOf("</");
            String lastLabel = xmlStr.substring(lastLeft+2,xmlStr.length()-1);
            return firstLabel.equals(lastLabel);
        }
        return true;
    }


}
