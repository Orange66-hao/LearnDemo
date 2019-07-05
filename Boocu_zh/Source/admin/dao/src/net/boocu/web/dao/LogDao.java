/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.LogEntity;

/**
 * Dao - 日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface LogDao extends BaseDao<LogEntity, Long> {

    /**
     * 删除所有日志
     */
    void removeAll();

}