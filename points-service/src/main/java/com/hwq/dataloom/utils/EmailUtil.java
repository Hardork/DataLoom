package com.hwq.dataloom.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import static com.hwq.dataloom.constants.EmailConstant.*;

@Slf4j
public class EmailUtil {


    /**
     * 生成预约优惠券提示邮件
     * @param emailHtmlPath 邮件模版地址
     * @param couponTemplateName 优惠券名称
     * @param leftTime 剩余时间
     * @param link 链接
     * @return 邮件字符串
     */
    public static String buildEmailContent(String emailHtmlPath, String couponTemplateName, String leftTime, String link) {
        // 加载邮件html模板
        ClassPathResource resource = new ClassPathResource(emailHtmlPath);
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line;
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.info("发送邮件读取模板失败{}", e.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 替换html模板中的参数
        return MessageFormat.format(buffer.toString(), EMAIL_TITLE, couponTemplateName, leftTime, link, PLATFORM_RESPONSIBLE_PERSON, PLATFORM_ADDRESS);
    }


}
