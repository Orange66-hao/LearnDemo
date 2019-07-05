package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.CompanymessageDao;
import net.boocu.project.entity.CompanymessageEntity;
import net.boocu.project.service.CompanymessageService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("companymessageServiceImpl")
public class CompanymessageServiceImpl extends BaseServiceImpl<CompanymessageEntity, Long> implements CompanymessageService {
	@Resource(name = "companymessageDaoImpl")
    private CompanymessageDao companymessageDao;
    
    @Resource(name = "companymessageDaoImpl")
    public void setBaseDao(CompanymessageDao companymessageDao) {
        super.setBaseDao(companymessageDao);
    }
}
