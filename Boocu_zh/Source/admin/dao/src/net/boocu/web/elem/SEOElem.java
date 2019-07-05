/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.elem;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Elem - SEO
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Embeddable
public class SEOElem implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 2316422582584680336L;

    /** 页面标题 */
    private String title;

    /** 页面关键词 */
    private String keywords;

    /** 页面描述 */
    private String description;

    @Column(name = "seo_title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "seo_keywords")
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Column(name = "seo_description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}