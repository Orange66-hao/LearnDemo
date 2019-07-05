package net.boocu.project.dao;

import java.util.HashMap;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProjectNeedDao extends BaseDao<ProjectNeedEntity,Long> {
	public Page<ProjectNeedEntity> findProjectNeedPage(Pageable pageable,HashMap<String,Object> htMap) ;
}
