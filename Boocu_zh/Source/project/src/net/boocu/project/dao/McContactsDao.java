package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;

public interface McContactsDao extends BaseDao<McContactsEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);

	/**
	 * 查询联系人名称
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> quarycontacts(String id);
}
