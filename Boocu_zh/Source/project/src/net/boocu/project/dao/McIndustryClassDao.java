package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McIndustryClassEntity;

public interface McIndustryClassDao extends BaseDao<McIndustryClassEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);

}
