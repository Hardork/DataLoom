package com.hwq.dataloom.mq.consumer;

import com.hwq.dataloom.config.EmailConfig;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.model.vo.coupon.CouponTemplateQueryVO;
import com.hwq.dataloom.mq.event.CouponRemindEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import com.hwq.dataloom.service.CouponTemplateService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.hwq.dataloom.constants.EmailConstant.*;
import static com.hwq.dataloom.utils.EmailUtil.buildEmailContent;

/**
 * @author HWQ
 * @date 2024/9/4 16:24
 * @description 优惠券预约消费者
 * 处理到时的优惠券消息
 */
@RocketMQMessageListener(
        topic = CouponMessageConstant.REMIND_COUPON_TOPIC,
        consumerGroup = CouponMessageConstant.REMIND_COUPON_CONSUMER_GROUP
)
public class CouponRemindConsumer implements RocketMQListener<MessageWrapper<CouponRemindEvent>> {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponRemindEvent> couponRemindEventMessageWrapper) {
        CouponRemindEvent message = couponRemindEventMessageWrapper.getMessage();
        String notifyContact = message.getContact();
        Long couponTemplateId = message.getCouponTemplateId();
        // 查询出对应的优惠券模版缓存
        CouponTemplateQueryVO couponTemplateQueryVO = couponTemplateService.findCouponTemplateById(couponTemplateId);
        // TODO：鉴于系统目前只有邮件通知，后期如果接入更多通知方式可以使用策略模式
        try {
            sendEmail(couponTemplateQueryVO.getName(), message.getRemindTime().toString(), "http://hgoat.cn", notifyContact);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送邮件
     * @param couponTemplateName 活动名称
     * @param leftTime 剩余时间
     * @param link 网站链接
     * @param emailAccount 用户邮箱
     * @throws MessagingException
     */
    private void sendEmail(String couponTemplateName, String leftTime, String link, String emailAccount) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // 邮箱发送内容组成
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(EMAIL_SUBJECT);
        helper.setText(buildEmailContent(EMAIL_HTML_CONTENT_PATH, couponTemplateName, leftTime, link), true);
        helper.setTo(emailAccount);
        helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
        mailSender.send(message);
    }
}
