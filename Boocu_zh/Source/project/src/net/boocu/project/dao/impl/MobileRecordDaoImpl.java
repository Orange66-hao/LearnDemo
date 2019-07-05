package net.boocu.project.dao.impl;

import org.springframework.stereotype.Service;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.EmailRecordDao;
import net.boocu.project.dao.MobileRecordDao;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.entity.MobileRecordEntity;
@Service("mobileRecordDaoImpl")
public class MobileRecordDaoImpl extends BaseDaoImpl<MobileRecordEntity, Long> implements MobileRecordDao{

}
