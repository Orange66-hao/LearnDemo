package net.boocu.project.dao;

import java.util.HashMap;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface AutoTestDao extends BaseDao<AutoTestEntity,Long> {
	public Page<AutoTestEntity> findAutoTestPage(Pageable pageable,HashMap<String,Object> htMap) ;
}
