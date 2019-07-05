package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.BasedataDao;
import net.boocu.project.entity.BasedataEntity;
import net.boocu.project.service.BasedataService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

@Service("basedataServiceImpl")
public class BasedataServiceImpl extends BaseServiceImpl<BasedataEntity, Long> implements BasedataService {
	@Resource(name = "basedataDaoImpl")
    private BasedataDao basedataDao;
    
    @Resource(name = "basedataDaoImpl")
    public void setBaseDao(BasedataDao basedataDao) {
    	super.setBaseDao(basedataDao);
    }
}
