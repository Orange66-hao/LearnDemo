/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.MemberInfoDao;
import net.boocu.web.entity.MemberInfoEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 会员信息
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("memberInfoDaoImpl")
public class MemberInfoDaoImpl extends BaseDaoImpl<MemberInfoEntity, Long> implements MemberInfoDao {

    @Override
    public MemberInfoEntity find(Long id) {
        if (id == null) {
            return null;
        }
        try {
            String jpql = "select memberInfos from MemberInfoEntity memberInfos where memberInfos.pers = :pers";
            return entityManager.createQuery(jpql, MemberInfoEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("pers", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
