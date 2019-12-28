package com.jshx.zq.p2p.data;

import com.jshx.zq.p2p.util.DateUtils;
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

    private static final String STYLE2_START = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
            "xmlns:urn=\"urn:SmsWBS\"><soapenv:Header/><soapenv:Body>";

    private static final String STYLE_END = "</soapenv:Body></soapenv:Envelope>";

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

        return roundByStyle(STYLE1_START,xmlBody);
    }

    public static String getCheckPingInfo(String cityId,String userName,String userIp,String pkgNum,String opCode){
        String dataBody = "<msg_head>\n" +
                "    <encrypt>123456</encrypt>\n" +
                "    <arearid>"+cityId+"</arearid>\n" +
                "    <sendtime>20190722153714</sendtime>\n" +
                "    <opcode>"+opCode+"</opcode>\n" +
                "    <serialno>0810dd14-d331-45b3-872e-9fce7ddb5e7620190722153714</serialno>\n" +
                "</msg_head>\n" +
                "<msg_body>\n" +
                "    <username>"+userName+"</username>\n" +
                "    <userip>"+userIp+"</userip>\n" +
                "    <pagenum>"+pkgNum+"</pagenum>\n" +
                "</msg_body>";
        String xmlBody = "<urn:SmsRequest soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "    <strIn xsi:type=\"xsd:string\">\n" +
                roundByCDATA(dataBody)+
                "    </strIn>\n" +
                "</urn:SmsRequest>";

        return roundByStyle(STYLE2_START,xmlBody);
    }

    private static String roundByCDATA(String dataBody){
        return "<![CDATA["+dataBody+"]]>\n";
    }

    private static String roundByStyle(String style,String xmlBody){
        return style+xmlBody+STYLE_END;
    }

    public static String getZdDistribute(String faultCode,String areaId,String complaintInfo){
        return "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:AsigAxisService\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <urn:call soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "         <strIn xsi:type=\"xsd:string\"><![CDATA[<?xml version=\"1.0\" encoding=\"GBK\" ?>\n" +
                "<root>\n" +
                "\t<functionCode>userFaultAutoStartFlow</functionCode>\n" +
                "\t<paramDoc>\n" +
                "        <faultCode>"+faultCode+"</faultCode>\n" +
                "        <businessCode>00023</businessCode>\n" +
                "        <areaID>"+areaId+"</areaID>\n" +
                "        <complaintSrc>07</complaintSrc>\n" +
                "        <complaintCause>10415</complaintCause>\n" +
                "        <complaintInfo>"+complaintInfo+"</complaintInfo>\n" +
                "        <s_EmployeeID>-2</s_EmployeeID>\n" +
                "        <opMachine>127.0.0.1</opMachine>\n" +
                "        <opTime>"+ DateUtils.now(DateUtils.STANDARD)+"</opTime>\n" +
                "        <startMode>maintenance</startMode>\n" +
                "        <isAuto>1</isAuto>\n" +
                "        <hasData>0</hasData>\n" +
                "    </paramDoc>\n" +
                "</root>\n" +
                "]]></strIn>\n" +
                "      </urn:call>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
}
