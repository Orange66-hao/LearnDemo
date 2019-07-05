/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.TokenDao;
import net.boocu.web.entity.TokenEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 令牌
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("tokenDaoImpl")
public class TokenDaoImpl extends BaseDaoImpl<TokenEntity, Long> implements TokenDao {

    @Override
    public TokenEntity findByAddr(String addr) {
        if (StringUtils.isBlank(addr)) {
            return null;
        }
        try {
            String jpql = "select tokens from TokenEntity tokens where lower(tokens.addr) = lower(:addr)";
            return entityManager.createQuery(jpql, TokenEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("addr", addr).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
