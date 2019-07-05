package net.boocu.project.service.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.InstrumentDao;
import net.boocu.project.dao.ModuleDao;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.InstrumentService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("instrumentServiceImpl")
public class InstrumentServiceImpl extends BaseServiceImpl<InstrumentEntity, Long> implements InstrumentService {
	@Resource(name = "instrumentDaoImpl")
    private InstrumentDao instrumentDao;
    
    @Resource(name = "instrumentDaoImpl")
    public void setBaseDao(InstrumentDao instrumentDao) {
    	super.setBaseDao(instrumentDao);
    }
}