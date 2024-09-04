package com.hwq.dataloom.constants;

/**
 * @Author: Hwq
 * @Date: 2023/09/03 11:24:40
 * @Version: 1.0
 * @Description: 电子邮件常量
 */
public interface EmailConstant {

    /**
     * 电子邮件html内容路径 resources目录下
     */
    String EMAIL_HTML_CONTENT_PATH = "email.html";


    /**
     * captcha缓存键
     */
    String CAPTCHA_CACHE_KEY = "bi:captcha:";

    /**
     * 电子邮件主题
     */
    String EMAIL_SUBJECT = "验证码邮件";

    /**
     * 电子邮件标题
     */
    String EMAIL_TITLE = "DataLoom平台";

    /**
     * 电子邮件标题英语
     */
    String EMAIL_TITLE_ENGLISH = "Goat Open Interface Platform";

    /**
     * 平台负责人
     */
    String PLATFORM_RESPONSIBLE_PERSON = "老山羊";

    /**
     * 平台地址
     */
    String PLATFORM_ADDRESS = "<a href='http://hgoat.cn/'>请联系我们</a>";

    String PATH_ADDRESS = "'http://hgoat.cn/'";
}
