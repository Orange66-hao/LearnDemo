package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.SuCompanyNameEntity;
import net.boocu.project.entity.SuContactsEntity;
import net.boocu.project.entity.SuMemberEntity;
import net.boocu.web.service.BaseService;

public interface SuCompanyNameService extends BaseService<SuCompanyNameEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<SuCompanyNameEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
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
