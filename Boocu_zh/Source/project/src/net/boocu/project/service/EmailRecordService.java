package net.boocu.project.service;

import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.web.service.BaseService;

public interface EmailRecordService  extends BaseService<EmailRecordEntity, Long>{

    long getCount(String stime, String etime);
}
