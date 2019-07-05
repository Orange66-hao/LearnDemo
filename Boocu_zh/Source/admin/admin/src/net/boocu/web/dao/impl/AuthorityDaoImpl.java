/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.AuthorityDao;
import net.boocu.web.entity.AuthorityEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 权限资源
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("authorityDaoImpl")
public class AuthorityDaoImpl extends BaseDaoImpl<AuthorityEntity, Long> implements AuthorityDao {

}