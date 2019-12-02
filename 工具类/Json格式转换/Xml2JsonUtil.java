package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author liuwei
 * @date 2019-11-22 10:47
 * @desc xml转json工具类
 * 在解析xml字符串时，允许带有标签头<?xml version="1.0" encoding="UTF-8"?>
 * 注意：解析出的json对象字段顺序并不同于xml中的顺序
 */
public class Xml2JsonUtil {

    //默认编码字符集
    private static final String DFT_CST = "UTF-8";


    /**
     * xml转json，其中xml中不含有数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，略过
     * @param xmlStr
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject ignoreEmpty(String xmlStr, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(getBytesFromXml(xmlStr),trans2Camel,"ignoreEmpty");
    }

    /**
     * 不建议直接传参byte[]
     * 除非该字节流直接返回自远程调用而没有后续经过字节流-->字符串-->字节流的编解码转换
     * 如果经过编解码转换，需保证原始解码(byte[] -> string)和再次编码(string -> byte[])使用的字符集兼容
     * 且不会出现中文乱码
     * @param xml
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject ignoreEmpty(byte[] xml, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(xml,trans2Camel,"ignoreEmpty");
    }

    /**
     * xml转json，其中xml中不含有数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，也被解析为""
     * @param xmlStr
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject includeEmpty(String xmlStr, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(getBytesFromXml(xmlStr),trans2Camel,"includeEmpty");
    }

    public static JSONObject includeEmpty(byte[] xml, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(xml,trans2Camel,"includeEmpty");
    }

    private static byte[] getBytesFromXml(String xmlStr) throws UnsupportedEncodingException {
        byte[] xml;
        if (xmlStr.startsWith("<?")) {
            String firstLine = xmlStr.substring(0,xmlStr.indexOf(">")+1);
            String charSet = firstLine.split("encoding=\"")[1].split("\"")[0];
            xml = xmlStr.getBytes(charSet);
        }else{
            xml = xmlStr.getBytes(DFT_CST);
        }
        return xml;
    }

    private static JSONObject entrance(byte[] xml,boolean trans2Camel,String type) throws JDOMException, IOException {
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml);
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(is);
        Element root = doc.getRootElement();
        if (type.equals("ignoreEmpty")) {
            json.put(trans2Camel?getCamelName(root.getName()):root.getName(), iterateElementIgnore(root,trans2Camel));
        }else{
            json.put(trans2Camel?getCamelName(root.getName()):root.getName(), iterateElementInclude(root,trans2Camel));
        }
        is.close();
        return json;
    }

    /**
     * 迭代解析：无序，忽略空值
     * @param element
     * @return
     */
    private static JSONObject iterateElementIgnore(Element element,boolean trans2Camel) {
        List node = element.getChildren();
        Element et;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < node.size(); i++) {
            et = (Element) node.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0)
                    continue;
                obj.put(trans2Camel?getCamelName(et.getName()):et.getName(), iterateElementIgnore(et,trans2Camel));
            } else {
                obj.put(trans2Camel?getCamelName(et.getName()):et.getName(), et.getTextTrim());
            }
        }
        return obj;
    }

    /**
     * 迭代解析：无序，解析空值
     * @param element
     * @return
     */
    private static JSONObject iterateElementInclude(Element element,boolean trans2Camel) {
        List node = element.getChildren();
        Element et;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < node.size(); i++) {
            et = (Element) node.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0){
                    obj.put(trans2Camel?getCamelName(et.getName()):et.getName(), et.getTextTrim());
                }else{
                    obj.put(trans2Camel?getCamelName(et.getName()):et.getName(), iterateElementInclude(et,trans2Camel));
                }
            } else {
                obj.put(trans2Camel?getCamelName(et.getName()):et.getName(), et.getTextTrim());
            }
        }
        return obj;
    }

    //下划线名称转为驼峰名称
    private static String getCamelName(String underlineName){
        if (underlineName.contains("_")) {
            String[] udArr = underlineName.split("_");
            if (udArr.length > 3) {
                StringBuilder sb = new StringBuilder(udArr[0]);
                for (int i = 1; i < udArr.length; i++) {
                    sb.append(StringUtils.capitalize(udArr[i]));
                }
                return sb.toString();
            }else{
                String str = udArr[0];
                for (int i = 1; i < udArr.length; i++) {
                    str+=StringUtils.capitalize(udArr[i]);
                }
                return str;
            }
        }
        return underlineName;
    }

}
