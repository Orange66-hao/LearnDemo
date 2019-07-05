package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.ModuleDao;
import net.boocu.project.entity.ModuleEntity;
import net.boocu.project.service.ModuleService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */

@Service("moduleServiceImpl")
public class ModuleServiceImpl extends BaseServiceImpl<ModuleEntity, Long> implements ModuleService {
	@Resource(name = "moduleDaoImpl")
    private ModuleDao moduleDao;
    
    @Resource(name = "moduleDaoImpl")
    public void setBaseDao(ModuleDao moduleDao) {
    	super.setBaseDao(moduleDao);
    }
}
