/**
 * 
 */
package net.boocu.web.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.SysConfigDao;
import net.boocu.web.entity.SysConfigEntity;
import net.boocu.web.util.Constants;

/**
 * 所有系统参数配置都写到这里
 * @author Administrator
 *
 */
@Repository
public class SysConfigDaoImpl extends BaseDaoImpl<SysConfigEntity, Long> implements SysConfigDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* (non-Javadoc)
	 * @see net.boocu.web.dao.SysConfigDao#updateConfig(net.boocu.web.entity.SysConfigEntity)
	 */
	@Override
	public int updateConfig(SysConfigEntity sce) {
		String sql = "UPDATE t_sys_config SET value = ? WHERE id = ?";
		return jdbcTemplate.update(sql, sce.getValue(),sce.getId());
	}
	
	@Override
	public String getConfigByKey(String key){
		//SpringJdbc 存在查询不到抛出异常的问题，此处捕获异常，return null；
		try {
			String sql = "SELECT value FROM t_sys_config WHERE `key` = ?";
			return jdbcTemplate.queryForObject(sql,String.class, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.boocu.web.dao.SysConfigDao#updateInitConfig()
	 */
	@Override
	public void updateInitConfig() {
		String delSql = "DELETE FROM t_sys_config";
		//清空系统配置
		jdbcTemplate.update(delSql);
		//插入货币转换比例配置参数
		String insSql = "INSERT INTO t_sys_config(`key`,value,name) VALUES(?,?,?)";
		jdbcTemplate.update(insSql,Constants.RMBCHDOLLERKEY,"0.17","人民币对美元的转换汇率");
		jdbcTemplate.update(insSql,Constants.DOLLERCHRMBKEY,"6","美元对人民币的转换汇率");
		jdbcTemplate.update(insSql,Constants.FREIGHTKEY,"30","运费");
	}
	
	@Override
	public List<SysConfigEntity> getSysConfigLists(){
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<SysConfigEntity> query = builder.createQuery(SysConfigEntity.class);
		Root<SysConfigEntity> root = query.from(SysConfigEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		return findList(builder, query, restrictions, root, 0, 1000, null, null);
	}
	

}
