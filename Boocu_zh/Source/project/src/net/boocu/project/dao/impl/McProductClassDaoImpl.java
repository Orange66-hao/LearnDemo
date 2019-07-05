package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.McProductClassDao;
import net.boocu.project.dao.ProductclassDao;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProductclassEntity;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository("mcproductclassDaoImpl")
public class McProductClassDaoImpl extends BaseDaoImpl<McProductClassEntity, Long> implements McProductClassDao {
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getNodeData2(String productclass_id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.name name,ic.menuid menuid from mc_productclass ic where ic.id in ("+productclass_id+")");
	}

	@Override
	public List<Map<String, Object>> queryall_pro(String id) {
		return JdbcTemplate.queryForList(" select * from mc_major where id= "+ id);
	}

	@Override
	public List<Map<String, Object>> queryall_id() {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_productclass ic");
	}

	@Override
	public List<Map<String, Object>> queryall_inid(String id) {
		return JdbcTemplate.queryForList(" select ic.id id,ic.ids ids,ic.name name from mc_productclass ic where ic.mc_major in ("+id+")");
	}
	
}
