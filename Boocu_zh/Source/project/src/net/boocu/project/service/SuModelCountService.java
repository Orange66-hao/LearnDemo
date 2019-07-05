package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.SuModelCountEntity;
import net.boocu.web.service.BaseService;

public interface SuModelCountService extends BaseService<SuModelCountEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<SuModelCountEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
}
