/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberLogEntity;
import net.boocu.web.enums.MemberLogTypeEnum;

/**
 * Service - 会员日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface MemberLogService extends BaseService<MemberLogEntity, Long> {

    /**
     * 生成会员日志
     * 
     * @param type
     *            类型
     * @param cont
     *            内容
     * @param approved
     *            是否通过
     * @param operator
     *            操作员
     * @param ip
     *            IP
     * @param member
     *            会员
     * @throws 生成失败的异常
     */
    void build(MemberLogTypeEnum type, String cont, Boolean approved, String operator, String ip, MemberEntity member)
            throws Exception;

}