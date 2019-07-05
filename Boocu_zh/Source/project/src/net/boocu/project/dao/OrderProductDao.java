/**
 * 
 */
package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.OrderProduct;

/**
 * @author Administrator
 *
 */
public interface OrderProductDao extends BaseDao<OrderProduct, Long>{

	public OrderProduct findById(Long id);

	/**
	 * @param ids
	 * @return
	 */
	public int batchDel(String ids);

	/**
	 * @param id
	 * @return
	 */
	public int delById(Long id);
	
}
