/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.framework.enums.SnTypeEnum;

/**
 * Entity - 序列号
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Entity
@Table(name = "sys_sn")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_sn_sequence")
public class SnEntity extends BaseEntity {

    /** serialVersionUID */
    private static final long serialVersionUID = -2886999225655427618L;

    /** 类型 */
    private SnTypeEnum type;

    /** 末值 */
    private Long lastValue;

    @Column(nullable = false, updatable = false, unique = true)
    public SnTypeEnum getType() {
        return type;
    }

    public void setType(SnTypeEnum type) {
        this.type = type;
    }

    @Column(nullable = false)
    public Long getLastValue() {
        return lastValue;
    }

    public void setLastValue(Long lastValue) {
        this.lastValue = lastValue;
    }

}