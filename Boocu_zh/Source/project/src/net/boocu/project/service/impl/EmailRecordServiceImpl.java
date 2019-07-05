package net.boocu.project.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.EmailRecordDao;
import net.boocu.project.dao.FriendsDao;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.service.EmailRecordService;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.impl.BaseServiceImpl;
@Service("emailRecordServiceImpl")
public class EmailRecordServiceImpl extends BaseServiceImpl<EmailRecordEntity, Long> implements EmailRecordService {
	@Resource
    private EmailRecordDao dao;
    
    @Resource 
    public void setBaseDao(EmailRecordDao dao) {
        super.setBaseDao(dao);
    }

    @Override
    public long getCount(String stime, String etime) {
        return dao.getCount(stime,etime);
    }
}
