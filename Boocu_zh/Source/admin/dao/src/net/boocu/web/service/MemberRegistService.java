/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

import net.boocu.web.bean.admin.MemberRegistBean;
import net.boocu.web.entity.MemberEntity;

/**
 * Service - 用户注册
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface MemberRegistService {

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
     * 查找账户
     * 
     * @param id
     *            ID
     * @return 账户
     */
    MemberEntity find(Long id);

    /**
     * 查找所有账户集合
     * 
     * @return 账户集合
     */
    List<MemberEntity> findAll();

    /**
     * 注册会员
     * 
     * @param registBean
     *            注册Bean
     * @param opinion
     *            意见
     * @param operator
     *            操作员
     * @param ip
     *            IP
     * @throws 注册失败的异常
     */
    void regist(MemberRegistBean registBean, String opinion, String operator, String ip) throws Exception;

}