/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.password;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 短信方式重置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class TextingResetBean {

    /** 手机号码 */
    private String mobile;

    /** 验证码 */
    private String captcha;

    /** 新密码 */
    private String password;

    @NotBlank
    @Pattern(regexp = "^1[3,5,8]\\d{9}$")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @NotBlank
    @Length(max = 200)
    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @NotBlank
    @Pattern(regexp = "^[^\\s&\"<>]+$")
    @Length(min = 4, max = 20)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}