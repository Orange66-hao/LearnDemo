/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.RoleDao;
import net.boocu.web.entity.RoleEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 角色
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<RoleEntity, Long> implements RoleDao {

}