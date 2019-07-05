/**
 * 
 */
package net.boocu.project.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.InvoiceDao;
import net.boocu.project.dao.OrderDao;
import net.boocu.project.dao.OrderProductDao;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.project.entity.Order;
import net.boocu.project.entity.OrderProduct;
import net.boocu.project.entity.ShopCart;
import net.boocu.project.service.OrderService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, Long>
 	implements OrderService{

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderProductDao opDao;
	
	@Autowired
	private InvoiceDao invoiceDao;
	
	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#addOrder(net.boocu.project.entity.Order)
	 */
	@Override
	public Long addOrder(Order order , List<ShopCart> pros) {
		return orderDao.addOrder(order,pros);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#delOrder(java.lang.Long)
	 */
	@Override
	public void delOrder(Long id) {
		orderDao.delOrder(id);
	}

	@Override
	public void updateStatus(Long id, int status) {
		orderDao.updateStatus(id, status);
		//交易完成后，往发票表插入数据
		if(status == 9){
			Order order = orderDao.findById(id);
			if(order != null){
				//判断订单的商品是否含有税
				InvoiceEntity ie = new InvoiceEntity();
				String [] ids = order.getOrderProductIds().split(",");
				StringBuilder proIds = new StringBuilder();
				for(String i : ids){
					OrderProduct op = opDao.findById(Long.parseLong(i));
					//含税的
					if(op.getProduct().getIsTax() == 1){
						
						proIds.append(op.getProduct().getId()).append(",");
						
						ie.setOrderNumber(order.getOrderNumber());
						ie.setBrandName(op.getProduct().getProductBrandEntity().getName());
						ie.setProName(op.getProduct().getProName());
						ie.setProNo(op.getProduct().getProNo());
						ie.setItem("销售");
						ie.setInvoiceAmount(order.getPayAmount()+"");
						switch (op.getProduct().getTaxRate().ordinal()) {
						case 0:
							ie.setType(0);
							break;
						case 1:
							ie.setType(0);
							break;
						case 2:
							ie.setType(1);
							break;
						default:
							ie.setType(0);
							break;
						}
						ie.setUserId(order.getBuyMember().getId());
						ie.setPayCompleteTime(order.getPayCompleteTime());
						ie.setProIds(op.getProduct().getId()+"");
						invoiceDao.addInvoice(ie);
					}
				}
				/*
				proIds.deleteCharAt(proIds.lastIndexOf(","));
				ie.setProIds(proIds.toString());
				invoiceDao.addInvoice(ie);
				*/
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#findOrderByPage(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<Order> findOrderByPage(Pageable pageable, Map<String, Object> params) {
		return orderDao.findOrderByPage(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#findById(java.lang.Long)
	 */
	@Override
	public Order findById(Long id) {
		return orderDao.findById(id);
	}

	@Override
	public int batchComplete(String ids){
		String [] arrids = ids.split(",");
		int count = 0;
		for(String i : arrids){
			count += orderDao.updateStatus(Long.parseLong(i), 8);
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#batchDel(java.lang.String)
	 */
	@Override
	public int batchDel(String ids) {
		String arrIds [] = ids.split(",");
		//要删除订单必须先删除订单产品
		for(String i : arrIds){
			Order o = orderDao.findById(Long.parseLong(i));
			opDao.batchDel(o.getOrderProductIds());
		}
		StringBuilder sb = new StringBuilder(ids);
		sb.deleteCharAt(ids.lastIndexOf(","));
		return orderDao.batchDel(sb.toString());
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#updateWhenPay(net.boocu.project.entity.Order)
	 */
	@Override
	public int updateWhenPay(Order o) {
		return orderDao.updateWhenPay(o);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#findByOrderNum(java.lang.String)
	 */
	@Override
	public Order findByOrderNum(String orderNum) {
		return orderDao.findByOrderNum(orderNum);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#updateCompleteTime(java.lang.Long)
	 */
	@Override
	public int updatePayCompleteTime(Long id) {
		return orderDao.updatePayCompleteTime(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#updateTradeCompleteTime(java.lang.Long)
	 */
	@Override
	public int updateTradeCompleteTime(Long id) {
		return orderDao.updateTradeCompleteTime(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#insertPayLog(java.util.Map)
	 */
	@Override
	public void insertPayLog(Map<String, String> map) {
		orderDao.insertPayLog(map);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#findOrderJson(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<Order> findOrderJson(Pageable pageable, Map<String, Object> params) throws ParseException {
		return orderDao.findOrderJson(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#updateDisCount(java.lang.Long, float)
	 */
	@Override
	public int updateDisCount(Long id, float price) {
		return orderDao.updateDisCount(id, price);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#updateOrderAddr(java.lang.Long, java.lang.String)
	 */
	@Override
	public int updateOrderAddr(Long id, String addr,Long addrId) {
		return orderDao.updateOrderAddr(id, addr,addrId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.OrderService#getWaitPayList()
	 */
	@Override
	public List<Order> getWaitPayList() {
		return orderDao.getWaitPayList();
	}
}
