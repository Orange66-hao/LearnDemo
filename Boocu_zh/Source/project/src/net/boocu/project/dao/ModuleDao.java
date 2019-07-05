package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ModuleEntity;
/**
 * 使用方法:如UserEntity
 * 1.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface ModuleDao extends BaseDao<ModuleEntity,Long> {

}
