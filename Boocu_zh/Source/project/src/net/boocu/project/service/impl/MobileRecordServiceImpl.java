package net.boocu.project.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.EmailRecordDao;
import net.boocu.project.dao.FriendsDao;
import net.boocu.project.dao.MobileRecordDao;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.entity.MobileRecordEntity;
import net.boocu.project.service.EmailRecordService;
import net.boocu.project.service.MobileRecordService;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.impl.BaseServiceImpl;
@Service("mobileRecordServiceImpl")
public class MobileRecordServiceImpl extends BaseServiceImpl<MobileRecordEntity, Long> implements MobileRecordService {
	@Resource
    private MobileRecordDao dao;
    
    @Resource 
    public void setBaseDao(MobileRecordDao dao) {
        super.setBaseDao(dao);
    }
}
