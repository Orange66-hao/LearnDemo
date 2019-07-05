package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.HelpDao;
import net.boocu.project.entity.HelpEntity;
import net.boocu.project.service.HelpService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

@Service("helpServiceImpl")
public class HelpServiceImpl extends BaseServiceImpl<HelpEntity, Long> implements HelpService {
	@Resource(name = "helpDaoImpl")
    private HelpDao helpDao;
    
    @Resource(name = "helpDaoImpl")
    public void setBaseDao(HelpDao helpDao) {
        super.setBaseDao(helpDao);
    }
}
