/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 邮件令牌
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class MailTokenBean {

    /** 邮箱地址 */
    private String email;

    /** 令牌代码 */
    private String code;

    @NotBlank
    @Email
    @Length(max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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