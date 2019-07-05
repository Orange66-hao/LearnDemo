/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.DictDao;
import net.boocu.web.entity.DictEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 词典
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("dictDaoImpl")
public class DictDaoImpl extends BaseDaoImpl<DictEntity, Long> implements DictDao {

    @Override
    public boolean nameExists(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        String jpql = "select count(*) from DictEntity dicts where lower(dicts.name) = lower(:name)";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("name", name).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean identExists(String ident) {
        if (StringUtils.isBlank(ident)) {
            return false;
        }
        String jpql = "select count(*) from DictEntity dicts where lower(dicts.ident) = lower(:ident)";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("ident", ident).getSingleResult();
        return count > 0;
    }

    @Override
    public DictEntity findByIdent(String ident) {
        if (StringUtils.isBlank(ident)) {
            return null;
        }
        try {
            String jpql = "select dicts from DictEntity dicts where lower(dicts.ident) = lower(:ident)";
            return entityManager.createQuery(jpql, DictEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("ident", ident).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}