/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import java.util.List;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.ResourceEntity;

/**
 * Dao - 权限资源
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface ResourceDao extends BaseDao<ResourceEntity, Long> {

	public List<ResourceEntity> getAuthorizedResource(Long userId);
	
}