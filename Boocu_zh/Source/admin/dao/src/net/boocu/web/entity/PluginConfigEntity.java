/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.boocu.framework.entity.BaseOrderEntity;

/**
 * Entity - 插件配置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Entity
@Table(name = "sys_plugin_config")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_plugin_config_sequence")
public class PluginConfigEntity extends BaseOrderEntity {

    /** serialVersionUID */
    private static final long serialVersionUID = -4281267831939221806L;

    /** 插件 */
    private String plugin;

    /** 是否启用 */
    private Boolean enabled;

    /** 属性 */
    private Map<String, String> attributes = new HashMap<String, String>();

    @Column(nullable = false, updatable = false, unique = true)
    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    @Column(nullable = false)
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sys_plugin_config_attribute", joinColumns = @JoinColumn(name = "plugin_config"))
    @MapKeyColumn(name = "name")
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * 获取属性值
     * 
     * @param name
     *            属性名称
     * @return 属性值
     */
    @Transient
    public String getAttribute(String name) {
        if (getAttributes() != null && name != null) {
            return getAttributes().get(name);
        } else {
            return null;
        }
    }

    /**
     * 设置属性值
     * 
     * @param name
     *            属性名称
     * @param value
     *            属性值
     */
    @Transient
    public void setAttribute(String name, String value) {
        if (getAttributes() != null && name != null) {
            getAttributes().put(name, value);
        }
    }

}