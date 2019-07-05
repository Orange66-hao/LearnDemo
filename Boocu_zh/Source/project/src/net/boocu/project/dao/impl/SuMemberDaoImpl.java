package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.SuMemberDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.SuMemberEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("suMemberDaoImpl")
public class SuMemberDaoImpl extends BaseDaoImpl<SuMemberEntity, Long> implements SuMemberDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from sys_admin ic where ic.id in ("+rootId+")");
	}

	@Override
	public List<Map<String, Object>> register(String name) {
		return JdbcTemplate.queryForList("select * from sys_admin where username='"+name+"'");
	}
}
