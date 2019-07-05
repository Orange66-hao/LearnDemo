/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 登录
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class LoginBean {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 验证码ID */
    private String captchaId;

    /** 验证码 */
    private String captcha;

    /** 记住我 */
    private int remember;
    @NotBlank
    @Length(max = 200)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Length(max = 200)
    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    @Length(max = 200)
    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

	public int getRemember() {
		return remember;
	}

	public void setRemember(int remember) {
		this.remember = remember;
	}

}