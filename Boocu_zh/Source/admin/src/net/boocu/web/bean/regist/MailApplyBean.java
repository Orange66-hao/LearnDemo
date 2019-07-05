/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.regist;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 邮件方式申请
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class MailApplyBean {

    /** 邮箱地址 */
    private String email;

    /** 用户名 */
    private String username;

    @NotBlank
    @Email
    @Length(max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = StringUtils.lowerCase(email);
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