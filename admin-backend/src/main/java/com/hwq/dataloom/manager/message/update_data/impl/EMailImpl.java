package com.hwq.dataloom.manager.message.update_data.impl;

import com.hwq.dataloom.config.EmailConfig;
import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessage;
import com.hwq.dataloom.utils.EmailUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.hwq.dataloom.constant.EmailConstant.*;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description:
 **/
@Component("update_date_email")
public class EMailImpl implements IUpdateDataMessage {
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private EmailConfig emailConfig;

    @Override
    public String mark() {
        return "email";
    }

    @Override
    public void send(String message,String account) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            // 邮箱发送内容组成
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setText(message, true);
            helper.setTo(account);
            helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String buildMessage(String userName, String updateContent) {
        return EmailUtil.buildUpdateDataEmailContent(UPDATE_DATA_EMAIL_HTML_CONTENT_PATH, userName, updateContent);
    }
}
