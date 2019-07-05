/**
 * 
 */
package net.boocu.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.OrderProductDao;
import net.boocu.project.entity.OrderProduct;
import net.boocu.project.service.OrderProductService;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class OrderProductServiceImpl extends BaseServiceImpl<OrderProduct, Long>
	implements OrderProductService{

	@Autowired
	private OrderProductDao orderProductDao;
	
	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderProductService#findById(java.lang.Long)
	 */
	@Override
	public OrderProduct findById(Long id) {
		return orderProductDao.findById(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderProductService#batchDel(java.lang.String)
	 */
	@Override
	public int batchDel(String ids) {
		return orderProductDao.batchDel(ids);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderProductService#delById(java.lang.Long)
	 */
	@Override
	public int delById(Long id) {
		return orderProductDao.delById(id);
	}

}
