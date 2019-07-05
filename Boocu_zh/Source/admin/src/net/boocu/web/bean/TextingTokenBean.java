/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 短信令牌
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class TextingTokenBean {

    /** 手机号码 */
    private String mobile;

    /** 令牌代码 */
    private String code;

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
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}