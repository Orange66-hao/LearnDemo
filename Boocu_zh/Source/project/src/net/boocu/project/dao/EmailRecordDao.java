package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.EmailRecordEntity;

public interface EmailRecordDao extends BaseDao<EmailRecordEntity,Long>{

    long getCount(String stime, String etime);
}
