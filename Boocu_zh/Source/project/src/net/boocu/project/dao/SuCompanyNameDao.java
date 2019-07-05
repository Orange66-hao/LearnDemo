package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.SuCompanyNameEntity;

public interface SuCompanyNameDao extends BaseDao<SuCompanyNameEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param nameid
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String nameid);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param nameid
	 * @return
	 */
	public List<Map<String, Object>> queryall(String id);
	/**
	 * 查询该名是否存在
	 * @param nameid
	 * @return
	 */
	public List<Map<String, Object>> register(String name);
}
