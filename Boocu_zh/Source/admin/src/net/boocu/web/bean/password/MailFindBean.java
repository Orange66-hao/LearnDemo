/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.password;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 邮件方式找回
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class MailFindBean {

    /** 邮箱地址 */
    private String email;

//    /** 验证码ID */
//    private String captchaId;
//
//    /** 验证码 */
//    private String captcha;

    @NotBlank
    @Email
    @Length(max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    @Length(max = 200)
//    public String getCaptchaId() {
//        return captchaId;
//    }
//
//    public void setCaptchaId(String captchaId) {
//        this.captchaId = captchaId;
//    }
//
//    @Length(max = 200)
//    public String getCaptcha() {
//        return captcha;
//    }
//
//    public void setCaptcha(String captcha) {
//        this.captcha = captcha;
//    }

}