package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.SuContactsEntity;

public interface SuContactsDao extends BaseDao<SuContactsEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	
	public List<Map<String, Object>> getNodeData2(String rootId);
	
	public List<Map<String, Object>> queryall(String id);
}
