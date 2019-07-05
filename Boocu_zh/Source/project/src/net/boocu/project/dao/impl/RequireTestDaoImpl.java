package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.RequireTestDao;
import net.boocu.project.entity.RequireTestEntity;

import org.springframework.stereotype.Repository;

/**
 * 需求测试   add by  deng 20160118
 * */
@Repository("requireTestDaoImpl")
public class RequireTestDaoImpl extends BaseDaoImpl<RequireTestEntity, Long> implements RequireTestDao {
}
