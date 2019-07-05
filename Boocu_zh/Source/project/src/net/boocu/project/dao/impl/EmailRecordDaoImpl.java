package net.boocu.project.dao.impl;

import org.springframework.stereotype.Service;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.EmailRecordDao;
import net.boocu.project.entity.EmailRecordEntity;

import javax.persistence.Query;
import java.math.BigInteger;

@Service("emailRecordDaoImpl")
public class EmailRecordDaoImpl extends BaseDaoImpl<EmailRecordEntity, Long> implements EmailRecordDao{

    @Override
    public long getCount(String stime, String etime) {
        Query query
                = entityManager.createNativeQuery("select count(1) from sys_email_log where modify_date>='"+stime+"' and modify_date<='"+etime+"'");

        return ((BigInteger)query.getResultList().get(0)).intValue();
    }
}
