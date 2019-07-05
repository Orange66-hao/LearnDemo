/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.AdminDao;
import net.boocu.web.entity.AdminEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 管理员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("adminDaoImpl")
public class AdminDaoImpl extends BaseDaoImpl<AdminEntity, Long> implements AdminDao {

}