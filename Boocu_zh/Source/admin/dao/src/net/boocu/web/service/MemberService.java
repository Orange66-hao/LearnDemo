/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.HashMap;
import java.util.List;

import net.boocu.project.entity.AutoTestEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.shiro.ShiroPrincipal;

/**
 * Service - 会员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface MemberService extends BaseService<MemberEntity, Long> {
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
     * 判断用户名是否唯一（忽略大小写，同时对比身份证号码、邮箱地址、手机号码）
     * 
     * @param previousUsername
     *            修改前用户名
     * @param currentUsername
     *            当前用户名
     * @return 用户名是否唯一
     */
    boolean usernameUnique(String previousUsername, String currentUsername);

    /**
     * 判断身份证号码是否存在（忽略大小写，同时对比用户名）
     * 
     * @param idNo
     *            身份证号码
     * @return 身份证号码是否存在
     */
    boolean idNoExists(String idNo);

    /**
     * 判断身份证号码是否唯一（忽略大小写，同时对比用户名）
     * 
     * @param previousIdNo
     *            修改前身份证号码
     * @param currentIdNo
     *            当前身份证号码
     * @return 身份证号码是否唯一
     */
    boolean idNoUnique(String previousIdNo, String currentIdNo);

    /**
     * 判断邮箱地址是否存在（忽略大小写，同时对比用户名）
     * 
     * @param email
     *            邮箱地址
     * @return 邮箱地址是否存在
     */
    boolean emailExists(String email);

    /**
     * 判断邮箱地址是否唯一（忽略大小写，同时对比用户名）
     * 
     * @param previousEmail
     *            修改前邮箱地址
     * @param currentEmail
     *            当前邮箱地址
     * @return 邮箱地址是否唯一
     */
    boolean emailUnique(String previousEmail, String currentEmail);

    /**
     * 判断手机号码是否存在（忽略大小写，同时对比用户名）
     * 
     * @param mobile
     *            手机号码
     * @return 手机号码是否存在
     */
    boolean mobileExists(String mobile);

    /**
     * 判断手机号码是否唯一（忽略大小写，同时对比用户名）
     * 
     * @param previousMobile
     *            修改前手机号码
     * @param currentMobile
     *            当前手机号码
     * @return 手机号码是否唯一
     */
    boolean mobileUnique(String previousMobile, String currentMobile);

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
     * 查找有身份的会员集合
     * 
     * @return 有身份的会员集合
     */
    List<MemberEntity> findHasIdentityList();

    /**
     * 判断会员是否登录
     * 
     * @return 会员是否登录
     */
    boolean authorized();

    /**
     * 获取当前会员
     * 
     * @return 当前会员，不存在时返回NULL
     */
    MemberEntity getCurrent();

    /**
     * 获取当前身份信息
     * 
     * @return 身份信息，不存在时返回NULL
     */
    ShiroPrincipal getCurrentPrincipal();

    /**
     * 获取当前会员ID
     * 
     * @return 当前会员ID，不存在时返回NULL
     */
    Long getCurrentId();

    /**
     * 获取当前会员用户名
     * 
     * @return 当前会员用户名，不存在时返回NULL
     */
    String getCurrentUsername();
    
    /**
	 * @param id
	 * @return
	 */
	MemberEntity findById(Long id);

	/**
	 * @param qq快捷登录时，查询openid是否已经存在
	 * @return
	 */
	MemberEntity showopenid(String openid);

	/**
	 * @param 微信快捷登录时，查询openid是否已经存在
	 * @return
	 */
	MemberEntity showwxopenid(String wxopenid);

}