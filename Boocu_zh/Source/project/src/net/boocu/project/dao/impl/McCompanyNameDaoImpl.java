package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.McCompanyNameDao;
import net.boocu.project.dao.McContactsDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McContactsEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("mcCompanyNameDaoImpl")
public class McCompanyNameDaoImpl extends BaseDaoImpl<McCompanyNameEntity, Long> implements McCompanyNameDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> quarymccompanyname(String id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_company_name ic where ic.id= ("+id+")");
	}

	@Override
	public List<Map<String, Object>> registermccompanyname(String name) {
		return JdbcTemplate.queryForList("select * from mc_company_name where name='"+name+"'");
	}

	@Override
	public List<Map<String, Object>> queryallids(String id) {
		return JdbcTemplate.queryForList(" select * from mc_company where ids= "+ id);
	}
}
