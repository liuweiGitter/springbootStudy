package com.jshx.zq.p2p.notice.mail;

import com.jshx.zq.p2p.vo.MailBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import java.util.Date;

/**
 * @author liuwei
 * @date 2019-11-11 14:51
 * @desc 邮件服务类
 */
@Slf4j
public class MailService {

    /**
     * 注入spring mail邮件发送工具类的内置bean依赖
     */
    private static JavaMailSenderImpl mailSender;

    public static void iocInit(JavaMailSenderImpl javaMailSender){
        if (mailSender == null) {
            mailSender = javaMailSender;
        }
    }

    /**
     * 发送邮件入口
     * @param mailBean
     * @return
     */
    public static MailBean sendMail(MailBean mailBean) {
        try {
            checkMail(mailBean); //1.检测邮件
            sendMimeMail(mailBean); //2.发送邮件
            return saveMail(mailBean); //3.保存邮件
        } catch (MessagingException e) {
            log.error("发送邮件失败:", e);//打印错误信息
            mailBean.setStatus("fail");
            mailBean.setError(e.getMessage());
            return mailBean;
        }
    }

    //检测邮件非空信息
    private static void checkMail(MailBean mailBean) {
        if (StringUtils.isEmpty(mailBean.getTo())) {
            throw new RuntimeException("邮件收信人不能为空");
        }
        if (StringUtils.isEmpty(mailBean.getSubject())) {
            throw new RuntimeException("邮件主题不能为空");
        }
        if (StringUtils.isEmpty(mailBean.getText())) {
            throw new RuntimeException("邮件内容不能为空");
        }
    }

    //构建复杂邮件信息
    private static void sendMimeMail(MailBean mailBean) throws MessagingException {
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true);//true表示支持复杂类型
        mailBean.setFrom(getMailSendFrom());//邮件发信人从配置项读取
        messageHelper.setFrom(mailBean.getFrom());//邮件发信人
        messageHelper.setTo(mailBean.getTo().split(","));//邮件收信人
        messageHelper.setSubject(mailBean.getSubject());//邮件主题
        messageHelper.setText(mailBean.getText());//邮件内容
        if (!StringUtils.isEmpty(mailBean.getCc())) {//抄送
            messageHelper.setCc(mailBean.getCc().split(","));
        }
        if (!StringUtils.isEmpty(mailBean.getBcc())) {//密送
            messageHelper.setCc(mailBean.getBcc().split(","));
        }
        if (mailBean.getMultipartFiles() != null) {//添加邮件附件
            for (MultipartFile multipartFile : mailBean.getMultipartFiles()) {
                messageHelper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
            }
        }
        if (null==mailBean.getSentDate()) {//发送时间
            mailBean.setSentDate(new Date());
            messageHelper.setSentDate(mailBean.getSentDate());
        }
        mailSender.send(messageHelper.getMimeMessage());//正式发送邮件
        mailBean.setStatus("ok");
        log.info("发送邮件成功：{}->{}", mailBean.getFrom(), mailBean.getTo());
    }

    //保存邮件
    private static MailBean saveMail(MailBean mailBean) {
        //将邮件保存到数据库：此处仅示例
        return mailBean;
    }

    //获取邮件发信人
    private static String getMailSendFrom() {
        return mailSender.getJavaMailProperties().getProperty("from");
    }

}
