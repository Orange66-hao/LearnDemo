/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.DictWordDao;
import net.boocu.web.entity.DictWordEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 词典单词
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("dictWordDaoImpl")
public class DictWordDaoImpl extends BaseDaoImpl<DictWordEntity, Long> implements DictWordDao {

    @Override
    public boolean nameExists(String dictId, String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        String jpql = null;
        Long count = null;

        if (dictId != null) {
            jpql = "select count(*) from DictWordEntity dictWords where dictWords.dict.id = :dictId and lower(dictWords.name) = lower(:name)";
            count = entityManager.createQuery(jpql.toString(), Long.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("dictId", dictId).setParameter("name", name).getSingleResult();
        } else {
            jpql = "select count(*) from DictWordEntity dictWords where dictWords.dict is null and lower(dictWords.name) = lower(:name)";
            count = entityManager.createQuery(jpql.toString(), Long.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("name", name).getSingleResult();
        }

        return count > 0;
    }

}