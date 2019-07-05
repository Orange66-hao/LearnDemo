package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import net.boocu.project.entity.McModelCountEntity;
import net.boocu.web.service.BaseService;

public interface McModelCountService extends BaseService<McModelCountEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McModelCountEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
}
