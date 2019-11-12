package com.telecom.js.noc.hxtnms.operationplan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author liuwei
 * @date 2019-11-12 09:05
 * @desc 邮件信息类
 */
@Data
public class MailBean {
    private String id;//邮件id
    private String from;//邮件发送人
    @NotEmpty
    private String to;//邮件接收人（多个邮箱则用逗号","隔开）
    @NotEmpty
    private String subject;//邮件主题
    @NotEmpty
    private String text;//邮件内容
    private Date sentDate;//发送时间
    private String cc;//抄送（多个邮箱则用逗号","隔开）
    private String bcc;//密送（多个邮箱则用逗号","隔开）
    private String status;//状态
    private String error;//报错信息
    @JsonIgnore
    private MultipartFile[] multipartFiles;//邮件附件
}
