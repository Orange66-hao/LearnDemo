/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.regist;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 短信方式申请
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class TextingApplyBean {

    /** 手机号码 */
    private String mobile;

    /** 用户名 */
    private String username;

    @NotBlank
    @Pattern(regexp = "^1[3,5,8]\\d{9}$")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @NotBlank
    @Pattern(regexp = "^[0-9a-z_A-Z\\u4e00-\\u9fa5]+$")
    @Length(min = 2, max = 20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.lowerCase(username);
    }

}