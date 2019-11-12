package com.telecom.js.noc.hxtnms.operationplan.controller;

import com.telecom.js.noc.hxtnms.operationplan.entity.MailBean;
import com.telecom.js.noc.hxtnms.operationplan.notice.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author liuwei
 * @date 2019-11-05 10:54
 * @desc 邮件发送控制类：测试
 */
@RestController
@RequestMapping(value = "/mail",produces = "application/json;charset=UTF-8")
@Slf4j
public class MailController {

    @Autowired
    private MailService mailService;

    @RequestMapping("/send")
    public String sendMail(@RequestBody @Valid MailBean mailBean){
        mailService.sendMail(mailBean);
        return "{\"msg\":\"邮件发送成功!\"}";
    }

}
