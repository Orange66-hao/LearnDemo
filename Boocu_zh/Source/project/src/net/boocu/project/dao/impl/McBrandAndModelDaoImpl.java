package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.McBrandAndModelDao;
import net.boocu.project.dao.McModelDao;
import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.McModelEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("mcBrandAndModelDaoImpl")
public class McBrandAndModelDaoImpl extends BaseDaoImpl<McBrandAndModelEntity, Long> implements McBrandAndModelDao {

	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getParentIds(String param) {
		System.err.println("-----------"+param);
		return JdbcTemplate.queryForList(" select DISTINCT SUBSTR(ic.id,1,3) as parentId from jhj_industry_class ic where ic.name in ("+param+")");
	}

	@Override
	public List<Map<String, Object>> addBrandAndModel(String brand, String model, String mc_productclass_name, String mc_company) {
		List<Map<String, Object>> maps = JdbcTemplate.queryForList(" select * from mc_brand_and_model where mc_brand='" + brand + "' AND mc_model='" + model + "' AND mc_productclass_name='" + mc_productclass_name + "' AND mc_company='" + mc_company + "'");
		return maps;
	}

	@Override
	public List<Map<String, Object>> quaryMcBrandAndModel(String id) {
		List<Map<String, Object>> maps = JdbcTemplate.queryForList(" select * from mc_brand_and_model where id in (" + id + ")");
		return maps;
	}
}
