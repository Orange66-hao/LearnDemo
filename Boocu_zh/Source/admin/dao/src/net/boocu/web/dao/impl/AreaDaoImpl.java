/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.AreaDao;
import net.boocu.web.entity.AreaEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 地区
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("areaDaoImpl")
public class AreaDaoImpl extends BaseDaoImpl<AreaEntity, Long> implements AreaDao {

    @Override
    public boolean nameExists(String parentId, String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        String jpql = null;
        Long count = null;

        if (parentId != null) {
            jpql = "select count(*) from AreaEntity areas where areas.parent.id = :parentId and lower(areas.name) = lower(:name)";
            count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("parentId", parentId).setParameter("name", name).getSingleResult();
        } else {
            jpql = "select count(*) from AreaEntity areas where areas.parent is null and lower(areas.name) = lower(:name)";
            count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("name", name).getSingleResult();
        }

        return count > 0;
    }

}