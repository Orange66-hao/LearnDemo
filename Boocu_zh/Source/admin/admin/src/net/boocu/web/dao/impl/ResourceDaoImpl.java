/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import java.util.List;

import javax.persistence.FlushModeType;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.ResourceDao;
import net.boocu.web.entity.ResourceEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 权限资源
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("resourceDaoImpl")
public class ResourceDaoImpl extends BaseDaoImpl<ResourceEntity, Long> implements ResourceDao {

	@Override
	public List<ResourceEntity> getAuthorizedResource(Long userId) {
		String sql = " select re.id, re.name, re.source, re.menu from sec_user u " + 
				" left outer join sec_role_user ru on u.id=ru.user_id " + 
				" left outer join sec_role r on ru.role_id=r.id " + 
				" left outer join sec_role_authority ra on r.id = ra.role_id " + 
				" left outer join sec_authority a on ra.authority_id = a.id " + 
				" left outer join sec_authority_resource ar on a.id = ar.authority_id " + 
				" left outer join sec_resource re on ar.resource_id = re.id " + 
				" where u.id=? and re.menu is not null " +
				" union all " +
				" select re.id, re.name, re.source, re.menu from sec_resource re " + 
				" where re.menu is not null and not exists (select ar.authority_id from sec_authority_resource ar where ar.resource_id = re.id)";
		
     	 return entityManager.createQuery(sql, ResourceEntity.class).setFlushMode(FlushModeType.COMMIT).getResultList();
         
	}

}