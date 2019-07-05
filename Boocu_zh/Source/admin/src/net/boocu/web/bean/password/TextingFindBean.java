/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.password;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 短信方式找回
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class TextingFindBean {

    /** 手机号码 */
    private String mobile;

//    /** 验证码ID */
//    private String captchaId;
//
//    /** 验证码 */
//    private String captcha;

    @NotBlank
    @Pattern(regexp = "^1[3,5,8]\\d{9}$")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // @Length(max = 200)
    // public String getCaptchaId() {
    // return captchaId;
    // }
    //
    // public void setCaptchaId(String captchaId) {
    // this.captchaId = captchaId;
    // }
    //
    // @Length(max = 200)
    // public String getCaptcha() {
    // return captcha;
    // }
    //
    // public void setCaptcha(String captcha) {
    // this.captcha = captcha;
    // }

}