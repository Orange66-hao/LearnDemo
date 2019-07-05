package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.SuContactsDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.SuContactsEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("suContactsDaoImpl")
public class SuContactsDaoImpl extends BaseDaoImpl<SuContactsEntity, Long> implements SuContactsDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from su_contacts ic where ic.id in ("+rootId+")");
	}

	@Override
	public List<Map<String, Object>> queryall(String id) {
		return JdbcTemplate.queryForList(" select * from su_company_name where id= "+ id);
	}
}
