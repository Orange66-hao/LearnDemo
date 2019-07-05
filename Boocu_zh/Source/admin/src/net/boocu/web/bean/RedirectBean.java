/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean;

import org.hibernate.validator.constraints.URL;

/**
 * Bean - 重定向
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class RedirectBean {

    /** 重定向URL */
    private String redirectUrl;

    @URL
    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

}