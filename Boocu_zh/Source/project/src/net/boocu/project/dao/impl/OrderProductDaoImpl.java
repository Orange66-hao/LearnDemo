/**
 * 
 */
package net.boocu.project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.OrderProductDao;
import net.boocu.project.entity.OrderProduct;

/**
 * @author Administrator
 *
 */
@Repository
public class OrderProductDaoImpl extends BaseDaoImpl<OrderProduct, Long>
	implements OrderProductDao{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.OrderProductDao#findById(java.lang.Long)
	 */
	@Override
	public OrderProduct findById(Long id) {
		return super.find(id);
	}
	
	@Override
	public int delById(Long id){
		String sql = "DELETE FROM t_order_product WHERE id = ?";
		return jdbcTemplate.update(sql,id);
	}
	
	@Override
	public int batchDel(String ids){
		String sql = "DELETE FROM t_order_product WHERE id IN("+ids+")";
		return jdbcTemplate.update(sql);
	}

}
