/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.LogDao;
import net.boocu.web.entity.LogEntity;
import net.boocu.web.service.LogService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 日志
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("logServiceImpl")
public class LogServiceImpl extends BaseServiceImpl<LogEntity, Long> implements LogService {

    @Resource(name = "logDaoImpl")
    private LogDao logDao;

    @Resource(name = "logDaoImpl")
    public void setBaseDao(LogDao logDao) {
        super.setBaseDao(logDao);
    }

    @Override
    @Transactional
    public void clear() {
        logDao.removeAll();
    }

}