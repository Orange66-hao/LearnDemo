package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.McMajorDao;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McModelEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

@Repository("mcMajorDaoImpl")
public class McMajorDaoImpl extends BaseDaoImpl<McMajorEntity, Long> implements McMajorDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_major ic where ic.id in ("+rootId+")");
	}

	@Override
	public List<Map<String, Object>> queryall(String id) {
		return JdbcTemplate.queryForList(" select * from mc_industry_class where id= "+ id);
	}

	@Override
	public List<Map<String, Object>> getMcModelGradeNames(String mc_industry_id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_major ic where ic.mc_industryclass in ("+mc_industry_id+")");
	}

	@Override
	public List<Map<String, Object>> queryallid() {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_major ic");
	}

	@Override
	public List<Map<String, Object>> queryallinid(String mc_industry_id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_major ic where ic.mc_industryclass in ("+mc_industry_id+")");
	}
}
