package com.jshx.zq.p2p.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * @author liuwei
 * @date 2019-11-15 21:02
 * @desc 阿里云短信发送测试
 */

@RestController
@RequestMapping("/aliyun")
public class AliyunSmsController {

    @RequestMapping(value = "/sendSms", method = RequestMethod.POST, produces = "application/json")
    public SendSmsResponse getWorkOrder(@RequestBody Map<String, String> param) {
        String msgParamJson = "{\"code\":\"" + AliyunSmsService.getRandomVerCode() + "\"}";
        return AliyunSmsService.sendSms(param.get("toPhoneNum"), msgParamJson);
    }

}
