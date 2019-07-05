/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.dao.NoticeDao;
import net.boocu.web.entity.NoticeEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 通知
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("noticeDaoImpl")
public class NoticeDaoImpl extends BaseDaoImpl<NoticeEntity, Long> implements NoticeDao {

    @Override
    public Page<NoticeEntity> findReceiverPage(String receiver, Pageable pageable) {
        // 获取条件构造器
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // 创建条件查询
        CriteriaQuery<NoticeEntity> criteriaQuery = criteriaBuilder.createQuery(NoticeEntity.class);
        // 设置查询ROOT
        Root<NoticeEntity> root = criteriaQuery.from(NoticeEntity.class);
        criteriaQuery.select(root);

        // 设置限制条件
        Predicate restrictions = criteriaBuilder.conjunction();
        restrictions = criteriaBuilder.and(
                restrictions,
                criteriaBuilder.or(root.get("receivers").isNull(),
                        criteriaBuilder.like(root.<String> get("receivers"), receiver + NoticeEntity.SEPARATOR)));

        // 查找通知分页
        return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
    }

}
