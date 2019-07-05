package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.AdminareaDao;
import net.boocu.project.entity.AdminareaEntity;
import net.boocu.project.service.AdminareaService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("adminareaServiceImpl")
public class AdminareaServiceImpl extends BaseServiceImpl<AdminareaEntity, Long> implements AdminareaService {
	@Resource(name = "adminareaDaoImpl")
    private AdminareaDao adminareaDao;
    
    @Resource(name = "adminareaDaoImpl")
    public void setBaseDao(AdminareaDao adminareaDao) {
        super.setBaseDao(adminareaDao);
    }
}
