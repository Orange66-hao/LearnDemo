/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.MemberInfoDao;
import net.boocu.web.entity.MemberInfoEntity;
import net.boocu.web.service.MemberInfoService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 会员信息
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("memberInfoServiceImpl")
public class MemberInfoServiceImpl extends BaseServiceImpl<MemberInfoEntity, Long> implements MemberInfoService {

    @Resource(name = "memberInfoDaoImpl")
    private MemberInfoDao memberInfoDao;

    @Resource(name = "memberInfoDaoImpl")
    public void setBaseDao(MemberInfoDao memberInfoDao) {
        super.setBaseDao(memberInfoDao);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberInfoEntity find(Long id) {
        return memberInfoDao.find(id);
    }

}