/**
 * 
 */
package net.boocu.project.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.Order;
import net.boocu.project.entity.ShopCart;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface OrderService extends BaseService<Order, Long>{
	
	public Long addOrder(Order order , List<ShopCart> pros);
	
	public void delOrder(Long id);
	
	public void updateStatus(Long id,int status);
	
	public Page<Order> findOrderByPage(Pageable pageable,Map<String, Object> params);
	
	public Order findById(Long id);
	
	/**
	 * @return
	 */
	public List<Order> getWaitPayList();
	
	/**
	 * @param id
	 * @param addr
	 * @return
	 */
	public int updateOrderAddr(Long id, String addr,Long addrId);
	
	/**
	 * @param ids
	 * @return
	 */
	public int batchDel(String ids);

	/**
	 * @param ids
	 * @return
	 */
	public int batchComplete(String ids);
	
	/**
	 * @param o
	 * @return
	 */
	public int updateWhenPay(Order o);
	
	/**
	 * 根据订单编号查询订单
	 * @param orderNum
	 * @return
	 */
	public Order findByOrderNum(String orderNum);
	
	/**
	 * 更新支付完成时间
	 * @param id
	 * @return
	 */
	public int updatePayCompleteTime(Long id);
	
	/** 
	 * 更新交易完成时间
	 * @param id
	 * @return
	 */
	public int updateTradeCompleteTime(Long id);
	
	/**
	 * 添加支付日志
	 * @param map
	 */
	public void insertPayLog(Map<String, String> map);
	
	/**
	 * @param pageable
	 * @param params
	 * @return
	 * @throws ParseException 
	 */
	public Page<Order> findOrderJson(Pageable pageable, Map<String, Object> params) throws ParseException;
	
	/**
	 * 修改订单的折扣金额
	 * @param id
	 * @param price
	 * @return
	 */
	public int updateDisCount(Long id, float price);
}
