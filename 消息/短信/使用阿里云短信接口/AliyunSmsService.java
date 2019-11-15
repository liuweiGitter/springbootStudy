package com.jshx.zq.p2p.notice.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author liuwei
 * @date 2019-11-12 12:34
 * @desc 短信服务类：通过阿里云接口
 */
@Slf4j
public class AliyunSmsService {

    //产品名称，云通信短信API产品，开发者无需替换
    private static final String product = "Dysmsapi";

    //产品域名，开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 阿里云申请的accessKeyId和accessKeySecret
     */
    private static final String accessKeyId = "马赛克";
    private static final String accessKeySecret = "马赛克";

    /**
     * 阿里云申请的短信模版CODE和发送者签名
     */
    private static final String templateCode = "马赛克";
    private static final String signName = "马赛克";


    //短信发送客户端
    private static IAcsClient acsClient;

    static{
        //初始化acsClient，暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            acsClient = new DefaultAcsClient(profile);
        } catch (ClientException e) {
            log.error("初始化AcsClient失败：{}",e.getMessage());
        }
    }

    public static SendSmsResponse sendSms(String toPhoneNum, String msgParamJson){

        SendSmsRequest request = new SendSmsRequest();
        //必填，对端手机号
        request.setPhoneNumbers(toPhoneNum);
        //必填，发送者短信签名
        request.setSignName(signName);
        //必填，短信模板CODE
        request.setTemplateCode(templateCode);
        /**
         * 可选，短信模板中的变量替换JSON串
         * 如模板内容为"亲爱的${name}，您的验证码为${code}"时，此处的值为"{\"name\":\"Tom\", \"code\":\"123\"}"
         */
        request.setTemplateParam(msgParamJson);

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            log.error("短信发送失败：{}",e.getMessage());
        }
        return sendSmsResponse;
    }

    //获取随机的6位验证码
    public static String getRandomVerCode(){
        Random random = new Random();
        StringBuffer result= new StringBuffer();
        for (int i=0;i<6;i++){
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

}
