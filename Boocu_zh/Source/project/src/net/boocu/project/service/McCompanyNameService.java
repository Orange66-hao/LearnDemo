package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.web.service.BaseService;

public interface McCompanyNameService extends BaseService<McCompanyNameEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McCompanyNameEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	
	/**
	 * 查询联系人名称
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
