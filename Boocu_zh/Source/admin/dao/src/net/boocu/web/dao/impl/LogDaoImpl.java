/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.LogDao;
import net.boocu.web.entity.LogEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("logDaoImpl")
public class LogDaoImpl extends BaseDaoImpl<LogEntity, Long> implements LogDao {

    @Override
    public void removeAll() {
        String jpql = "delete from LogEntity logs";
        entityManager.createQuery(jpql).setFlushMode(FlushModeType.COMMIT).executeUpdate();
    }

}