package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.McContactsDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McContactsEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("mcContactsDaoImpl")
public class McContactsDaoImpl extends BaseDaoImpl<McContactsEntity, Long> implements McContactsDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> quarycontacts(String id) {
		List<Map<String, Object>> maps = JdbcTemplate.queryForList(" select id,name,teacher,resume,major from mc_contacts where id in (" + id + ")");
		return maps;
	}
}
