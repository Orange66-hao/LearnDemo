package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.McBrandDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McBrandEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("mcBrandDaoImpl")
public	 class McBrandDaoImpl extends BaseDaoImpl<McBrandEntity, Long> implements McBrandDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> queryall(String id) {
		return JdbcTemplate.queryForList(" select * from mc_productclass where id= "+ id);
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_brand ic where ic.id in ("+id+")");
	}

	@Override
	public List<Map<String, Object>> queryall_braid() {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_brand ic");
	}

	@Override
	public List<Map<String, Object>> queryall_brainid(String id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_brand ic where ic.mc_productclass in ("+id+")");
	}
}
