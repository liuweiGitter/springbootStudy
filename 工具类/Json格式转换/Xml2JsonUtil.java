package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author liuwei
 * @date 2019-11-22 10:47
 * @desc xml转json工具类
 * 在解析xml字符串时，允许带有标签头<?xml version="1.0" encoding="UTF-8"?>
 */
public class Xml2JsonUtil {


    /**
     * xml转json，其中xml中不含有数组元素
     * @param xml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static JSONObject withoutArrEle(byte[] xml) throws JDOMException, IOException {
        JSONObject json = new JSONObject();
        InputStream is = new ByteArrayInputStream(xml);
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(is);
        Element root = doc.getRootElement();
        json.put(root.getName(), iterateElement(root));
        is.close();
        return json;
    }

    private static JSONObject iterateElement(Element element) {
        List node = element.getChildren();
        Element et;
        JSONObject obj = new JSONObject();
        for (int i = 0; i < node.size(); i++) {
            et = (Element) node.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.getChildren().size() == 0)
                    continue;
                obj.put(et.getName(), iterateElement(et));
            } else {
                obj.put(et.getName(), et.getTextTrim());
            }
        }
        return obj;
    }

}
