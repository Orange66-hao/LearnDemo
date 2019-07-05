/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

import net.boocu.web.LogConfig;

/**
 * Service - 日志配置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface LogConfigService {

    /**
     * 获取所有日志配置
     * 
     * @return 所有日志配置
     */
    List<LogConfig> getAll();

}