package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSONArray;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liuwei
 * @date 2019-11-22 10:47
 * @desc xml转json工具类
 * 在解析xml字符串时，允许带有标签头<?xml version="1.0" encoding="UTF-8"?>
 * 注意1：xml字符串必须有根节点，如果没有根节点，自动拼接一个root根节点
 * 注意2：xml字符串中允许单闭合标签，如<br/>
 * 注意3：xml字符串中允许多次多层嵌套数组元素，如<list><arr><b>11</b><b>12</b></arr><arr><b>21</b><b>22</b></arr></list>
 * 注意4：解析出的json对象字段顺序并不同于xml中的顺序，但数组是按顺序解析
 */
public class Xml2JsonUtil {

    //默认编码字符集
    private static final String DFT_CST = "UTF-8";


    /**
     * xml转json，其中xml中不含有数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，略过
     * @param xmlStr xml字符串
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject ignoreEmpty(String xmlStr, boolean trans2Camel) throws JDOMException, IOException {
        return ignoreEmpty(xmlStr,trans2Camel,null);
    }

    /**
     * xml转json，其中xml中可以含有嵌套多层的数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，略过
     * @param xmlStr xml字符串
     * @param trans2Camel 是否转为驼峰命名
     * @param arrayTags 数组元素标签名列表，例<list><data>123</data><data>456</data></list>中list为数组元素
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject ignoreEmpty(String xmlStr, boolean trans2Camel, String[] arrayTags) throws JDOMException, IOException {
        return entrance(getBytesFromXml(xmlStr),trans2Camel,"ignoreEmpty",arrayTags);
    }

    /**
     * 不建议直接传参byte[]
     * 除非该字节流直接返回自远程调用而没有后续经过字节流-->字符串-->字节流的编解码转换
     * 如果经过编解码转换，需保证原始解码(byte[] -> string)和再次编码(string -> byte[])使用的字符集兼容
     * 且不会出现中文乱码
     * 如果元素值为空，略过
     * 不解析数组元素
     * @param xml xml字节数组
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject ignoreEmpty(byte[] xml, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(xml,trans2Camel,"ignoreEmpty",null);
    }

    /**
     * xml转json，其中xml中不含有数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，也被解析为""
     * @param xmlStr xml字符串
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject includeEmpty(String xmlStr, boolean trans2Camel) throws JDOMException, IOException {
        return includeEmptyAndArray(xmlStr,trans2Camel,null);
    }

    /**
     * xml转json，其中xml中可以含有嵌套多层的数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，也被解析为""
     * @param xmlStr xml字符串
     * @param trans2Camel 是否转为驼峰命名
     * @param arrayTags 数组元素标签名列表，例<list><data>123</data><data>456</data></list>中list为数组元素
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject includeEmptyAndArray(String xmlStr, boolean trans2Camel, String[] arrayTags) throws JDOMException, IOException {
        return entrance(getBytesFromXml(xmlStr),trans2Camel,"includeEmpty",
                (null==arrayTags || 0 == arrayTags.length)?null:arrayTags);
    }

    /**
     * xml转json，其中xml中不含有数组元素
     * 解析结果的顺序不同于xml顺序
     * 如果元素值为空，也被解析为""
     * @param xml xml字节数组
     * @param trans2Camel 是否转为驼峰命名
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject includeEmpty(byte[] xml, boolean trans2Camel) throws JDOMException, IOException {
        return entrance(xml,trans2Camel,"includeEmpty",null);
    }

    /**
     * 获取字符集编码，并去除首行<?xml version="1.0" encoding="UTF-8"?>
     * 如果标签没有根节点，自动拼接root根节点
     */
    private static byte[] getBytesFromXml(String xmlStr) throws UnsupportedEncodingException {
        byte[] xml;
        if (xmlStr.startsWith("<?")) {
            String firstLine = xmlStr.substring(0,xmlStr.indexOf(">")+1);
            String charSet = firstLine.split("encoding=\"")[1].split("\"")[0];
            xml = addRootIfNeeded(xmlStr.substring(xmlStr.indexOf(">")+1)).getBytes(charSet);
        }else{
            xml = addRootIfNeeded(xmlStr).getBytes(DFT_CST);
        }
        return xml;
    }

    /**
     * 如果标签没有根节点，自动拼接root根节点
     */
    private static String addRootIfNeeded(String xml){
        String startTag = xml.substring(1,xml.indexOf(">"));
        if (!xml.endsWith(startTag+">")) {
            return "<root>"+xml+"</root>";
        }
        return xml;
    }

    private static JSONObject entrance(byte[] xml,boolean trans2Camel,String type, String[] arrayTags) throws JDOMException, IOException {
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml);
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(is);
        Element root = doc.getRootElement();
        Set<String> arrayTagSet = new HashSet<>();
        if (null != arrayTags) {
            arrayTagSet.addAll(Arrays.asList(arrayTags));
        }
        try{
            if (type.equals("ignoreEmpty")) {
                json.put(trans2Camel?getCamelName(root.getName()):root.getName(), iterateElement(root,trans2Camel,arrayTagSet,false));
            }else{
                json.put(trans2Camel?getCamelName(root.getName()):root.getName(), iterateElement(root,trans2Camel,arrayTagSet,true));
            }
        } catch (Exception e){
            throw e;
        } finally {
            ResourceClose.close(is);
        }
        return json;
    }

    /**
     * 迭代解析：无序，解析嵌套多层数组
     * 对于某个节点，需要判断是否数组，而且可能会嵌套多层数组
     * @param element 当前节点元素
     * @param trans2Camel 是否驼峰转换
     * @param arrayTagSet 数组标签集合
     * @param nullParse 是否解析空值
     * @return 当前节点对应的JSONArray或JSONObject对象
     */
    private static Object iterateElement(Element element,boolean trans2Camel, Set<String> arrayTagSet, boolean nullParse) {
        List<Element> node = element.getChildren();
        /**
         * 当前节点为数组节点
         */
        if (arrayTagSet.contains(element.getName())) {
            JSONArray children = new JSONArray();
            for (Element child : node) {
                JSONObject childJson = new JSONObject();
                fillJsonObject(childJson,child,trans2Camel,arrayTagSet,nullParse);
                if (childJson.size()>0) {
                    children.add(childJson);
                }
            }
            return children;
        }
        /**
         * 当前节点为非数组节点
         */
        Element et;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < node.size(); i++) {
            et = node.get(i);
            fillJsonObject(obj,et,trans2Camel,arrayTagSet,nullParse);
        }
        return obj;
    }

    //json对象填充
    private static void fillJsonObject(JSONObject obj, Element et,boolean trans2Camel, Set<String> arrayTagSet, boolean nullParse){
        String elName = et.getName();
        if (et.getTextTrim().equals("")) {
            if (et.getChildren().size() == 0){
                if (!nullParse) {
                    return;
                }
                obj.put(trans2Camel?getCamelName(elName):elName, "");
            }else{
                obj.put(trans2Camel?getCamelName(elName):elName, iterateElement(et,trans2Camel,arrayTagSet,nullParse));
            }
        } else {
            obj.put(trans2Camel?getCamelName(elName):elName, et.getTextTrim());
        }
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
