package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.SuMemberEntity;

public interface SuMemberDao extends BaseDao<SuMemberEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String rootId);
	
	/**
	 * 查询是否存在
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> register(String name);
}
