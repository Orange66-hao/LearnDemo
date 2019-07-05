/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.framework.enums.SnTypeEnum;
import net.boocu.web.dao.SnDao;
import net.boocu.web.service.SnService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 序列号
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("snServiceImpl")
public class SnServiceImpl implements SnService {

    @Resource(name = "snDaoImpl")
    private SnDao snDao;

    @Transactional
    public String generate(SnTypeEnum type) {
        return snDao.generate(type);
    }

}