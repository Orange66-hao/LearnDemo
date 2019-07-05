package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.SuContactsEntity;
import net.boocu.web.service.BaseService;

public interface SuContactsService extends BaseService<SuContactsEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<SuContactsEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	
	public List<Map<String, Object>> getNodeData2(String rootId);

	public List<Map<String, Object>> queryall(String id);
}
