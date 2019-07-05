/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.MemberLogDao;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberLogEntity;
import net.boocu.web.enums.MemberLogTypeEnum;
import net.boocu.web.service.MemberLogService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 会员日志
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("memberLogServiceImpl")
public class MemberLogServiceImpl extends BaseServiceImpl<MemberLogEntity, Long> implements MemberLogService {

    @Resource(name = "memberLogDaoImpl")
    public void setBaseDao(MemberLogDao memberLogDao) {
        super.setBaseDao(memberLogDao);
    }

    @Override
    @Transactional
    public void build(MemberLogTypeEnum type, String cont, Boolean approved, String operator, String ip,
            MemberEntity member) throws Exception {

        // 生成日志
        MemberLogEntity log = new MemberLogEntity();
        log.setType(type);
        log.setCont(cont);
        log.setApproved(approved);
        log.setOperator(operator);
        log.setIp(ip);
        log.setMember(member);
        save(log);
    }

}