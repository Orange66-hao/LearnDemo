/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.AuthorityDao;
import net.boocu.web.entity.AuthorityEntity;
import net.boocu.web.service.AuthorityService;

import org.springframework.stereotype.Service;

/**
 * Service - 角色
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("authorityServiceImpl")
public class AuthorityServiceImpl extends BaseServiceImpl<AuthorityEntity, Long> implements AuthorityService {

    @Resource(name = "authorityDaoImpl")
    public void setBaseDao(AuthorityDao roleDao) {
        super.setBaseDao(roleDao);
    }

   
}