package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.web.service.BaseService;

public interface McContactsService extends BaseService<McContactsEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McContactsEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);

	/**
	 * 查询联系人名称
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> quarycontacts(String id);
}
