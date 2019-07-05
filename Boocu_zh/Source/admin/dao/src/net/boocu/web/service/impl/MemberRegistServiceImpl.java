/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.boocu.web.bean.admin.MemberRegistBean;
import net.boocu.web.dao.MemberDao;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberInfoEntity;
import net.boocu.web.enums.MemberLogTypeEnum;
import net.boocu.web.service.MemberInfoService;
import net.boocu.web.service.MemberLogService;
import net.boocu.web.service.MemberRegistService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 会员注册
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("memberRegistServiceImpl")
public class MemberRegistServiceImpl implements MemberRegistService {

    @Resource(name = "memberDaoImpl")
    private MemberDao memberDao;

    @Resource(name = "memberInfoServiceImpl")
    private MemberInfoService memberInfoService;
 
    @Resource(name = "memberLogServiceImpl")
    private MemberLogService memberLogService;

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return memberDao.usernameExists(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean idNoExists(String idNo) {
        return memberDao.idNoExists(idNo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberDao.emailExists(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean mobileExists(String mobile) {
        return memberDao.mobileExists(mobile);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity find(Long id) {
    	
        return memberDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberEntity> findAll() {
        return memberDao.findList(null, null, null, null);
    }

    @Override
    @Transactional
    public void regist(MemberRegistBean registBean, String opinion, String operator, String ip) throws Exception {

        // 解析身份证号码
        if (registBean.getGender() == null || registBean.getBirth() == null) {
            registBean.parseIdNo();
        }

        // TODO 注册会员
        MemberEntity member = new MemberEntity();
        member.setUsername(registBean.getUsername());
        member.setPassword(registBean.getPassword());
    /*    member.setName(registBean.getName());
        member.setIdNo(registBean.getIdNo());
        member.setGender(registBean.getGender());
        member.setBirth(registBean.getBirth());
        member.setEmail(registBean.getEmail());
        member.setMobile(registBean.getMobile());
        member.setCreditScore(0);
        member.setEnabled(registBean.getEnabled());
        member.setLocked(false);
        member.setLoginFailureCount(0);
        member.setRegistIp(ip);*/
        memberDao.persist(member);

        // TODO 补充会员信息
        MemberInfoEntity memberInfo = new MemberInfoEntity();
        memberInfo.setMember(member);
        memberInfoService.save(memberInfo);
  
        // TODO 生成会员日志
        memberLogService.build(MemberLogTypeEnum.regist, opinion, true, operator, ip, member);
    }

}