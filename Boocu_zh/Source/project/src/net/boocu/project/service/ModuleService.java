package net.boocu.project.service;

import net.boocu.project.entity.ModuleEntity;
import net.boocu.web.service.BaseService;

/**
 * 使用方法:如UserEntity
 * 1.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface ModuleService extends BaseService<ModuleEntity, Long> {
	
}
