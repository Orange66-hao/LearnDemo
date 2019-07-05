/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

import net.boocu.web.bean.admin.MemberModifBean;
import net.boocu.web.entity.MemberEntity;

/**
 * Service - 会员修改
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface MemberModifService {

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
     * 查找会员
     * 
     * @param id
     *            ID
     * @return 会员
     */
    MemberEntity find(Long id);

    /**
     * 查找所有会员集合
     * 
     * @return 会员集合
     */
    List<MemberEntity> findAll();

    /**
     * 修改会员
     * 
     * @param modifBean
     *            修改Bean
     * @param opinion
     *            意见
     * @param member
     *            会员
     * @param operator
     *            操作员
     * @param ip
     *            IP
     * @throws 修改失败的异常
     */
    void modify(MemberModifBean modifBean, String opinion, MemberEntity member, String operator, String ip)
            throws Exception;

}