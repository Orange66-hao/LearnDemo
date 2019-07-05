package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;

public interface McCompanyNameDao extends BaseDao<McCompanyNameEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	/**
	 * 查询公司名称
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> quarymccompanyname(String id);
	
	/**
	 * 查询公司名称是否存在
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> registermccompanyname(String name);
	
	/**
	 * 根据公司名的id，查询对应name（名称）
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> queryallids(String id);
}
