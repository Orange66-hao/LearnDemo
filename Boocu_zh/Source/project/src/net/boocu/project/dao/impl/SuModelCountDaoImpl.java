package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.SuModelCountDao;
import net.boocu.project.entity.SuModelCountEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("suModelCountDaoImpl")
public class SuModelCountDaoImpl extends BaseDaoImpl<SuModelCountEntity, Long> implements SuModelCountDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}
}
