/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.texting.zucp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 凌凯短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class ZucpTextingBean {

    /** 合作编号 */
    private String partner;

    /** 密匙 */
    private String key;

    /** 描述 */
    private String description;

    /** 排序 */
    private Integer order;

    /** 是否启用 */
    private Boolean enabled;

    @NotBlank
    @Length(max = 200)
    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    @NotBlank
    @Length(max = 200)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Length(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Min(0)
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @NotNull
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}