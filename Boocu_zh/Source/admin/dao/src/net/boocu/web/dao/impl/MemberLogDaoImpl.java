/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.MemberLogDao;
import net.boocu.web.entity.MemberLogEntity;

import org.springframework.stereotype.Repository;

/**
 * Dao - 会员日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("memberLogDaoImpl")
public class MemberLogDaoImpl extends BaseDaoImpl<MemberLogEntity, Long> implements MemberLogDao {

}
