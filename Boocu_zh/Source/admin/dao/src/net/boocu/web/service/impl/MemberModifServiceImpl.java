/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.LockModeType;

import net.boocu.web.bean.admin.MemberModifBean;
import net.boocu.web.dao.MemberDao;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.enums.MemberLogTypeEnum;
import net.boocu.web.service.MemberLogService;
import net.boocu.web.service.MemberModifService;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 会员修改
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("memberModifServiceImpl")
public class MemberModifServiceImpl implements MemberModifService {

    @Resource(name = "memberDaoImpl")
    private MemberDao memberDao;

    @Resource(name = "memberLogServiceImpl")
    private MemberLogService memberLogService;

    @Override
    @Transactional(readOnly = true)
    public boolean usernameUnique(String previousUsername, String currentUsername) {
        if (StringUtils.equalsIgnoreCase(previousUsername, currentUsername)) {
            return true;
        } else {
            if (memberDao.usernameExists(currentUsername)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean idNoUnique(String previousIdNo, String currentIdNo) {
        if (StringUtils.equalsIgnoreCase(previousIdNo, currentIdNo)) {
            return true;
        } else {
            if (memberDao.idNoExists(currentIdNo)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailUnique(String previousEmail, String currentEmail) {
        if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
            return true;
        } else {
            if (memberDao.emailExists(currentEmail)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean mobileUnique(String previousMobile, String currentMobile) {
        if (StringUtils.equals(previousMobile, currentMobile)) {
            return true;
        } else {
            if (memberDao.mobileExists(currentMobile)) {
                return false;
            } else {
                return true;
            }
        }
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
    public void modify(MemberModifBean modifBean, String opinion, MemberEntity member, String operator, String ip)
            throws Exception {

        // 解析身份证号码
        if (modifBean.getGender() == null || modifBean.getBirth() == null) {
            modifBean.parseIdNo();
        }

        memberDao.refresh(member, LockModeType.PESSIMISTIC_WRITE);
        if (StringUtils.isNotBlank(modifBean.getPassword())) {
            member.setPassword(modifBean.getPassword());
        }
        /*member.setName(modifBean.getName());
        member.setIdNo(modifBean.getIdNo());
        member.setGender(modifBean.getGender());
        member.setBirth(modifBean.getBirth());
        member.setEmail(modifBean.getEmail());
        member.setMobile(modifBean.getMobile());
        member.setEnabled(modifBean.getEnabled());
        if (member.getLocked() && BooleanUtils.isFalse(modifBean.getLocked())) {
            member.setLocked(false);
            member.setLoginFailureCount(0);
            member.setLockedDate(null);
        }*/
        memberDao.merge(member);

        // TODO 生成会员日志
        memberLogService.build(MemberLogTypeEnum.modify, opinion, true, operator, ip, member);
    }

}