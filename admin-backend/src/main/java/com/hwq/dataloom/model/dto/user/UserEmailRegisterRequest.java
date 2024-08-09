package com.hwq.dataloom.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Hwq
 * @Date: 2023/09/04 11:34:09
 * @Version: 1.0
 * @Description: 用户注册请求体
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String emailAccount;

    private String captcha;

    private String userName;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String invitationCode;

    private String agreeToAnAgreement;
}
