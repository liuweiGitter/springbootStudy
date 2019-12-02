package com.jshx.zq.p2p.util;

import com.jshx.zq.p2p.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;


import java.io.*;
import java.net.*;

/**
 * @author liuwei
 * @date 2019-11-19 14:33
 * @desc webservice请求工具类
 */
@Slf4j
public class WebServiceUtil {

    private WebServiceUtil() {
        throw new BaseException("this is util class, you should not create an object!");
    }

    private static final int SECOND = 1000;

    private static URLConnection getConnection(String wsdlUrl, int requestLength) {
        HttpURLConnection conn;
        try {
            URL url = new URL(wsdlUrl);
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            LogAndThrowException.error("webservice url or connection create exception !", e);
            return null;
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setDefaultUseCaches(false);
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", "" + requestLength);
        conn.setRequestProperty("SOAPAction", "");
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            LogAndThrowException.error("webservice request method [" + conn.getRequestMethod() + "] not supported !", e);
            return null;
        }
        //连接超时时间10秒
        conn.setConnectTimeout(10 * SECOND);
        conn.setReadTimeout(10 * SECOND);
        return conn;
    }

    private static void closeStream(Closeable... closeables){
        for (Closeable closeable : closeables) {
            if (null!=closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    log.error("closeStream failed "+e.getMessage());
                }
            }
        }
    }

    /**
     * webservice post请求
     *
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postRequest(String wsdlUrl, String xmlRequestParam) {
        BufferedReader in = null;
        HttpURLConnection conn = (HttpURLConnection) getConnection(wsdlUrl, xmlRequestParam.length());
        OutputStream output = null;
        try {
            conn.connect();
            //定义客户端输出流：输出请求消息到服务端
            output = conn.getOutputStream();
            if (null != xmlRequestParam) {
                byte[] soapRequest = xmlRequestParam.getBytes("UTF-8");
                //发送soap请求报文
                output.write(soapRequest, 0, soapRequest.length);
            }
            output.flush();
            //获取响应报文
            output = conn.getOutputStream();
            if (null != xmlRequestParam) {
                byte[] soapRequest = xmlRequestParam.getBytes("UTF-8");
                //发送soap请求报文
                output.write(soapRequest, 0, soapRequest.length);
            }
            output.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            LogAndThrowException.error("远程webservice服务异常!", e);
            return null;
        } finally {
            //资源引用关闭，防止资源句柄和连接池计数等泄露
            closeStream(in,output);
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    /**
     * 返回的数据在CDATA时
     *
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postAndGetCDATA(String wsdlUrl, String xmlRequestParam) {
        return getCDATAContent(postRequest(wsdlUrl, xmlRequestParam));
    }

    /**
     * 从xml字符串中提取<!CDATA[xxx]]>数据，并返回xxx
     */
    public static String getCDATAContent(String xmlStr) {
        if (!xmlStr.contains("CDATA")) {
            LogAndThrowException.error("数据结构错误!", xmlStr);
        }
        int startIndex = xmlStr.indexOf("CDATA") + 6;
        int endIndex = xmlStr.lastIndexOf("]]>");
        return xmlStr.substring(startIndex, endIndex);
    }

    /**
     * 返回的数据在CDATA，但CDATA被转义时
     * 使用HttpURLConnection时，CDATA会被转义
     * 具体来说
     * < -- &lt;
     * > -- &gt;
     * " -- &quto;
     * 等等
     *
     * @param wsdlUrl
     * @param xmlRequestParam
     * @return
     */
    public static String postAndGetXmlLabelResponse(String wsdlUrl, String xmlRequestParam) {
        String originResponse = postRequest(wsdlUrl, xmlRequestParam);
        //获取CDATA中的数据：即被转义的数据
        return getXmlLabelContent(originResponse);
    }

    private static String getXmlLabelContent(String originResponse) {
        int startIndex = originResponse.indexOf("&lt;");
        int endIndex = originResponse.lastIndexOf("&gt;") + 4;
        return StringEscapeUtils.unescapeXml(originResponse.substring(startIndex, endIndex));
    }


}
