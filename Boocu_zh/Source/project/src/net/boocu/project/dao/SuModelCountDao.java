package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.McModelCountEntity;
import net.boocu.project.entity.SuModelCountEntity;

public interface SuModelCountDao extends BaseDao<SuModelCountEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);

}
