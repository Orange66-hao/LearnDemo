/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import net.boocu.web.entity.LogEntity;

/**
 * Service - 日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface LogService extends BaseService<LogEntity, Long> {

    /**
     * 清空日志
     */
    void clear();

}