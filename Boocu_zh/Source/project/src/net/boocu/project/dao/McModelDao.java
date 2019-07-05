package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.McModelEntity;

public interface McModelDao extends BaseDao<McModelEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);

}
