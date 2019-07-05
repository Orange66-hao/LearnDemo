package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;

public interface McBrandDao extends BaseDao<McBrandEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryall(String id);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String id);
	/**
	 * 根据id查询品牌型号信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> queryall_braid();
	/**
	 * 根据id查询品牌型号信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> queryall_brainid(String id);
}
