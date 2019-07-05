package net.boocu.project.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.McMemberDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("mcMemberDaoImpl")
public class McMemberDaoImpl extends BaseDaoImpl<McMemberEntity, Long> implements McMemberDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> register(String edit_username, String username, HttpServletRequest request,
			HttpSession session) {
		return JdbcTemplate.queryForList("select * from sys_admin where username='"+username+"'");
	}

	@Override
	public List<Map<String, Object>> getNodeData2(String rootId) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from sys_admin ic where ic.id in ("+rootId+")");
	}

	@Override
	public List<Map<String, Object>> quaryblame(String blame_id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name from sys_admin ic where ic.id in ("+blame_id+")");
	}
}
