package com.jshx.zq.p2p.data;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-11-21 09:40
 * @desc xml入参工具类：项目私有
 */
@Slf4j
public class XmlRequestData {

    private XmlRequestData(){
        throw new IllegalStateException("Utility class allow not to create object !");
    }

    private static final String STYLE1_START = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:def=\"http://DefaultNamespace\"><soapenv:Header/><soapenv:Body>";

    private static final String STYLE1_END = "</soapenv:Body></soapenv:Envelope>";

    /**
     * 监控--请求用户产品信息
     * @param jrNum
     * @param cityName
     * @return
     */
    public static String getUserPrdInfo(String jrNum, String cityName){
        String dataBody = "<data>\n" +
                "<jr_no>"+jrNum+"</jr_no>\n" +
                "<city_name>"+cityName+"</city_name>\n" +
                "</data>";
        String xmlBody = "<def:getGovUserInfo soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "    <xml xsi:type=\"xsd:string\">\n" +
                roundByCDATA(dataBody)+
                "    </xml>\n" +
                "</def:getGovUserInfo>";

        return roundByStyle1(xmlBody);
    }

    private static String roundByCDATA(String dataBody){
        return "<![CDATA["+dataBody+"]]>\n";
    }

    private static String roundByStyle1(String xmlBody){
        return STYLE1_START+xmlBody+STYLE1_END;
    }

}
