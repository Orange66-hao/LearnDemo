package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.SuMemberEntity;
import net.boocu.web.service.BaseService;

public interface SuMemberService extends BaseService<SuMemberEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<SuMemberEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param rootId
	 * @return
	 */
	public Map getNodeData2(String rootId);
	/**
	 * 查询是否存在
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> register(String name);
}
