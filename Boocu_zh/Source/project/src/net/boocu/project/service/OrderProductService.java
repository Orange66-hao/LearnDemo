/**
 * 
 */
package net.boocu.project.service;

import net.boocu.project.entity.OrderProduct;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface OrderProductService extends BaseService<OrderProduct, Long>{

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
