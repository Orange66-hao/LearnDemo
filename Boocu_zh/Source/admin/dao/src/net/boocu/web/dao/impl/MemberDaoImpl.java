/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.dao.MemberDao;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.entity.ResourceEntity;
import net.boocu.web.service.MemberGradeService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 会员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("memberDaoImpl")
public class MemberDaoImpl extends BaseDaoImpl<MemberEntity, Long> implements MemberDao {
	
	@Resource
	MemberGradeService memberGradeService;
	
	@Override
	public Page<MemberEntity> findMemberAuditPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		 // 获取条件构造器
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // 创建条件查询
        CriteriaQuery<MemberEntity> criteriaQuery = criteriaBuilder.createQuery(MemberEntity.class);
        
        // 设置查询ROOT
        Root<MemberEntity> root = criteriaQuery.from(MemberEntity.class);
        //criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(root.get("proMarketPriceType")));
        
        //criteriaQuery = criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("proMarketPrice")));
        
        String keyword = (String) htMap.get("keyword");
        String gride = (String) htMap.get("gride");
        String invite=String.valueOf( htMap.get("invite"));
        // 设置限制条件
        Predicate restrictions = criteriaBuilder.conjunction();
        if(!keyword.isEmpty()){
        	restrictions = criteriaBuilder.and(restrictions,
            		criteriaBuilder.or( 
                    		criteriaBuilder.like(root.<String>get("username"),"%" + keyword + "%")
                            )
                    );
        }
        
        //用户等级
        if(gride!=null && !gride.isEmpty()){
        	MemberGradeEntity memberGradeEntity = memberGradeService.find(Filter.eq("name", gride));
        	if(memberGradeEntity !=null){
            	restrictions = criteriaBuilder.and(restrictions,
                		criteriaBuilder.or( 	
                        		criteriaBuilder.equal(root.<MemberGradeEntity>get("memberGradeEntity").<String>get("name"), gride)
                                )
                        );	
        	}
        }
        //是否为会员邀请
        if(invite!=null && !invite.isEmpty()){
        		restrictions = criteriaBuilder.and(restrictions,
        				criteriaBuilder.or( 	
        						criteriaBuilder.equal(root.<String>get("invite"), invite)
        						)
        				);	
        }
        
        
        // 查找借款分页
       return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
    @Override
    public boolean usernameExists(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        String jpql = "select count(*) from MemberEntity members where lower(members.username) = lower(:username) or lower(members.email) = lower(:email) or members.mobile = :mobile";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("username", username).setParameter("email", username)
                .setParameter("mobile", username).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean idNoExists(String idNo) {
        if (StringUtils.isBlank(idNo)) {
            return false;
        }
        String jpql = "select count(*) from MemberEntity members where lower(members.idNo) = lower(:idNo) or members.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("idNo", idNo).setParameter("username", idNo).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean emailExists(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        String jpql = "select count(*) from MemberEntity members where lower(members.email) = lower(:email) or lower(members.username) = lower(:username)";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("email", email).setParameter("username", email).getSingleResult();
        return count > 0;
    }

    @Override
    public boolean mobileExists(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        String jpql = "select count(*) from MemberEntity members where members.mobile = :mobile or members.username = :username";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("mobile", mobile).setParameter("username", mobile).getSingleResult();
        return count > 0;
    }

    @Override
    public MemberEntity findByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        try {
            String jpql = "select members from MemberEntity members where lower(members.username) = lower(:username)";
            return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public MemberEntity findByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return null;
        }
        try {
            String jpql = "select members from MemberEntity members where lower(members.email) = lower(:email)";
            return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public MemberEntity findByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        try {
            String jpql = "select members from MemberEntity members where members.mobile = :mobile";
            return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("mobile", mobile).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public MemberEntity findById(Long id){
    	if (id <= 0) {
            return null;
        }
    	try {
            String jpql = "select members from MemberEntity members where members.id = :id";
            return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    //查询qq的openid是否已经存在
    @Override
	public MemberEntity showopenid(String openid) {
     	 if (StringUtils.isBlank(openid)) {
             return null;
         }
         try {
             String jpql = "select members from MemberEntity members where members.openid = :openid";
             return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
                     .setParameter("openid", openid).getSingleResult();
         } catch (NoResultException e) {
             return null;
         }
	}
    
    //查询微信的openid是否已经存在
    @Override
    public MemberEntity showwxopenid(String wxopenid) {
    	if (StringUtils.isBlank(wxopenid)) {
    		return null;
    	}
    	try {
    		String jpql = "select members from MemberEntity members where members.wxopenid = :wxopenid";
    		return entityManager.createQuery(jpql, MemberEntity.class).setFlushMode(FlushModeType.COMMIT)
    				.setParameter("wxopenid", wxopenid).getSingleResult();
    	} catch (NoResultException e) {
    		return null;
    	}
    }

}