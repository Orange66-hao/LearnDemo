/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import java.util.HashMap;
import java.util.List;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.ResourceEntity;

/**
 * Dao - 会员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface MemberDao extends BaseDao<MemberEntity, Long> {
	public Page<MemberEntity> findMemberAuditPage(Pageable pageable,HashMap<String,Object> htMap) ;

    /**
     * 判断用户名是否存在（忽略大小写，同时对比身份证号码、邮箱地址、手机号码）
     * 
     * @param username
     *            用户名
     * @return 用户名是否存在
     */
    boolean usernameExists(String username);

    /**
     * 判断身份证号码是否存在（忽略大小写，同时对比用户名）
     * 
     * @param idNo
     *            身份证号码
     * @return 身份证号码是否存在
     */
    boolean idNoExists(String idNo);

    /**
     * 判断邮箱地址是否存在（忽略大小写，同时对比用户名）
     * 
     * @param email
     *            邮箱地址
     * @return 邮箱地址是否存在
     */
    boolean emailExists(String email);

    /**
     * 判断手机号码是否存在（忽略大小写，同时对比用户名）
     * 
     * @param mobile
     *            手机号码
     * @return 手机号码是否存在
     */
    boolean mobileExists(String mobile);

    /**
     * 根据用户名查找会员（忽略大小写）
     * 
     * @param username
     *            用户名
     * @return 会员，若不存在则返回null
     */
    MemberEntity findByUsername(String username);

    /**
     * 根据邮箱地址查找会员（忽略大小写）
     * 
     * @param email
     *            邮箱地址
     * @return 会员，若不存在则返回null
     */
    MemberEntity findByEmail(String email);

    /**
     * 根据手机号码查找会员
     * 
     * @param mobile
     *            手机号码
     * @return 会员，若不存在则返回null
     */
    MemberEntity findByMobile(String mobile);

	/**
	 * @param id
	 * @return
	 */
	MemberEntity findById(Long id);

	/**
	 * qq登录查询openid是否已经存在
	 * @return
	 */
	MemberEntity showopenid(String openid);

	/**
	 * 微信登录查询openid是否已经存在
	 * @return
	 */
	MemberEntity showwxopenid(String wxopenid);

}