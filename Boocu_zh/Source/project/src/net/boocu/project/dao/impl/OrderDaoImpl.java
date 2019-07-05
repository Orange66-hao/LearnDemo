/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.OrderDao;
import net.boocu.project.entity.Order;
import net.boocu.project.entity.ShopCart;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Repository
public class OrderDaoImpl extends BaseDaoImpl<Order, Long> implements OrderDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Order findById(Long id){
		return super.find(id);
	}
	
	/**
	 * 获取所有等待支付，并且超出支付时间的订单
	 * @return
	 */
	@Override
	public List<Order> getWaitPayList(){
		String sql = "SELECT id AS id FROM t_order WHERE DATE_ADD(create_date,INTERVAL 24 HOUR) < NOW() AND status = 0;";
		List<Order> list = new ArrayList<Order>();
		jdbcTemplate.query(sql, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Order order = new Order();
				order.setId(rs.getLong("id"));
				list.add(order);
			}
		});
		return list;
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.OrderDao#addOrder(net.boocu.project.entity.Order)
	 */
	@Override
	public Long addOrder(Order order,List<ShopCart> pros) {
		/*
		Order o = super.merge(order);
		Long orderId = o.getId();
		*/
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		final String sql = "INSERT INTO t_order(order_number,freight,buyer_id,seller_id,delivery_address,sum_price,recv_addr_id) VALUES(?,?,?,?,?,?,?)";
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);  
				ps.setString(1, order.getOrderNumber());
				ps.setFloat(2, order.getFreight());
				ps.setLong(3, order.getBuyMember().getId());
				ps.setLong(4, order.getSellMember().getId());
				ps.setString(5, order.getDeliveryAddress()==null?"":order.getDeliveryAddress());
				ps.setFloat(6, order.getSumPrice());
				if(order.getAddressEntity() != null && order.getAddressEntity().getId() > 0){
					ps.setLong(7, order.getAddressEntity().getId());
				}else{
					ps.setLong(7,0);
				}
				
				return ps;
			}
		}, keyHolder);
		Long orderId = keyHolder.getKey().longValue();
		
		/*
		 * 这里真特么日了狗，还要拿到orderProductId反插入到order表,煞笔框架不能用表连接。搭框架的人我也是呵呵了，猪一样的队友
		int orderId = jdbcTemplate.update(sql, new Object[]{order.getOrderNumber(),order.getFreight(),
				order.getBuyMember().getId(),order.getSellMember().getId()}, new int[]{
				Types.VARCHAR,Types.FLOAT,Types.INTEGER,Types.INTEGER,});
		*/
		StringBuilder odIds = new StringBuilder();
		String opSql = "INSERT INTO t_order_product(order_id,product_id,count,price) VALUES(?,?,?,?)";
		for(ShopCart sc : pros){
			jdbcTemplate.update(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(opSql,Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, orderId);
					ps.setLong(2, sc.getProductEntity().getId());
					ps.setLong(3, sc.getCount());
					ps.setFloat(4, sc.getPrice());
					return ps;
				}
			}, keyHolder);
			odIds.append(keyHolder.getKey().longValue()).append(",");
		}
		//把末尾的“，”去掉
		odIds = odIds.deleteCharAt(odIds.lastIndexOf(","));
		
		String updateSql = "UPDATE t_order SET order_product_ids = ? WHERE id = ?";
		jdbcTemplate.update(updateSql, odIds.toString(),orderId);
		return orderId;
	}
	
	/* (non-Javadoc)
	 * @see net.boocu.project.dao.OrderDao#delOrder(java.lang.Long)
	 */
	@Override
	public void delOrder(Long id) {
		
	}
	
	@Override
	public int updatePayCompleteTime(Long id){
		String sql = "UPDATE t_order SET pay_complete_time = NOW() WHERE id = ?";
		return jdbcTemplate.update(sql,id);
	}
	

	@Override
	public int updateTradeCompleteTime(Long id){
		String sql = "UPDATE t_order SET trade_complete_time = NOW() WHERE id = ?";
		return jdbcTemplate.update(sql,id);
	}
	
	@Override
	public int batchDel(String ids){
		String sql = "DELETE FROM t_order WHERE id IN("+ids+")";
		return jdbcTemplate.update(sql);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.OrderDao#updateStatus(java.lang.Long, int)
	 */
	@Override
	public int updateStatus(Long id, int status) {
		String sql = "UPDATE t_order SET status = ? WHERE id = ?";
		return jdbcTemplate.update(sql,status,id);
	}
	
	@Override
	public int updateWhenPay(Order o){
		if(o.getWxPayNonceStr()==null){
			o.setWxPayNonceStr("");
		}
		if(o.getBody() == null){
			o.setBody("");
		}
		String sql = "UPDATE t_order SET pay_type = ? , pay_amount = ?,wx_pay_nonce_str = ?,body = ? WHERE id = ?";
		return jdbcTemplate.update(sql,o.getPayType(),o.getPayAmount(),o.getWxPayNonceStr(),o.getBody(),o.getId());
	}
	
	@Override
	public int updateOrderAddr(Long id,String addr,Long addrId){
		String sql = "UPDATE t_order SET delivery_address = ?,recv_addr_id = ? WHERE id = ?";
		return jdbcTemplate.update(sql, addr,addrId,id);
	}
	
	@Override
	public int updateDisCount(Long id,float price){
		String sql = "UPDATE t_order SET discount_price = ? WHERE id = ?";
		return jdbcTemplate.update(sql, price,id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.OrderDao#findOrderByPage(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<Order> findOrderByPage(Pageable pageable, Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("userId").toString());
		String orderNum = params.get("orderNumber").toString();
		String status = params.get("status").toString();
		//String txtName = params.get("txtName").toString();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> query = builder.createQuery(Order.class);
		Root<Order> root = query.from(Order.class);
		
		Predicate restrictions = builder.conjunction();
		
		if(!"".equals(status) && !status.equals("-2")){
			if(status.equals("1234")){
				restrictions = builder.and(restrictions, 
	        		builder.or(
	        				builder.equal(root.get("status"), 1),
	        				builder.equal(root.get("status"), 2),
	        				builder.equal(root.get("status"), 3),
	        				builder.equal(root.get("status"), 4)
	        		));
			}else if(status.equals("510")){
				restrictions = builder.and(restrictions, 
		        		builder.or(
		        				builder.equal(root.get("status"), 5),
		        				builder.equal(root.get("status"), 10)
		        		));
			}else if(status.equals("1112")){
				restrictions = builder.and(restrictions, 
		        		builder.or(
		        				builder.equal(root.get("status"), 11),
		        				builder.equal(root.get("status"), 12)
		        		));
			}else{
				restrictions = builder.and(restrictions,
		        		builder.equal(root.<Integer>get("status"), status));	
			}
		}
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("buyMember").<Long>get("id"), userId));
		if(orderNum != null && !"".equals(orderNum)){
			restrictions = builder.and(restrictions,
		       		builder.like(root.<String>get("orderNumber"), "%" + orderNum + "%"));
		}
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public Order findByOrderNum(String orderNum){
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> query = builder.createQuery(Order.class);
		Root<Order> root = query.from(Order.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,builder.equal(root.<String>get("orderNumber"), orderNum));
		
		List<Order> lists = findList(builder, query, restrictions, root, 0, 10000, null, null);
		if(lists.size() > 0){
			return lists.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public void insertPayLog(Map<String,String> map){
		String sql = "INSERT INTO t_pay_log(pro_type,pay_type,pro_info,user_name,trade_no,";
		sql += "pay_amount,pay_complete_time,status,buyer_email) VALUES(?,?,?,?,?,?,NOW(),?,?)";
		jdbcTemplate.update(sql, new Object[]{map.get("pro_type").toString(),map.get("pay_type").toString(),
		map.get("pro_info").toString(),map.get("user_name").toString(),map.get("trade_no").toString(),
		map.get("pay_amount").toString(),map.get("status").toString(),map.get("buyer_email").toString()},new int[]{
		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR});
	}
	
	@Override
	public Page<Order> findOrderJson(Pageable pageable,Map<String,Object> params) throws ParseException{
		
		String orderNumber = params.get("orderNumber")!=null?params.get("orderNumber").toString():"";
		String email = params.get("email")!=null?params.get("email").toString():"";
		String buyName = params.get("buyName")!=null?params.get("buyName").toString():"";
		String recvName = params.get("recvName")!=null?params.get("recvName").toString():"";
		String mobile = params.get("mobile")!=null?params.get("mobile").toString():"";
		String status = params.get("status")!=null?params.get("status").toString():"";
		String payType = params.get("payType")!=null?params.get("payType").toString():"";
		String startTime = params.get("startTime")!=null?params.get("startTime").toString():"";
		String endTime = params.get("endTime")!=null?params.get("endTime").toString():"";
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> query = builder.createQuery(Order.class);
		Root<Order> root = query.from(Order.class);
		
		Predicate restrictions = builder.conjunction();
		
		if(!"".equals(orderNumber)){
			restrictions = builder.and(restrictions, 
	        		builder.like(root.<String>get("orderNumber"), "%" + orderNumber + "%"));
		}
		if(!"".equals(email)){
			restrictions = builder.and(restrictions, 
	        		builder.like(root.get("buyMember").<String>get("email"), "%" + email + "%"));
		}
		if(!"".equals(buyName)){
			restrictions = builder.and(restrictions, 
	        		builder.like(root.get("buyMember").<String>get("realName"),  "%" + buyName + "%"));
		}
		if(!"".equals(recvName)){
			restrictions = builder.and(restrictions, 
	        		builder.like(root.get("buyMember").get("addressEntity").<String>get("recvName"), "%" + recvName + "%"));
			
			restrictions = builder.and(restrictions, 
	        		builder.equal(root.get("buyMember").get("addressEntity").<Integer>get("isDefault"), 1));
		}
		if(!"".equals(mobile)){
			restrictions = builder.and(restrictions, 
	        		builder.like(root.get("buyMember").get("addressEntity").<String>get("mobilePhone"), "%" + mobile + "%"));
			
			restrictions = builder.and(restrictions, 
	        		builder.equal(root.get("buyMember").get("addressEntity").<Integer>get("isDefault"), 1));
		}
		if(!"".equals(status) && !"-100".equals(status)){
			restrictions = builder.and(restrictions, 
	        		builder.equal(root.<Integer>get("status"), status));
		}
		if(!"".equals(payType) && !"-1".equals(payType)){
			restrictions = builder.and(restrictions, 
	        		builder.equal(root.<Integer>get("payType"), payType));
		}
		
		if(!"".equals(startTime) && !"".equals(endTime)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			restrictions = builder.and(restrictions, 
	        		builder.between(root.<Date>get("createDate"), sdf.parse(startTime), sdf.parse(endTime)));
		}
        
		return findPage(builder, query, restrictions, root, pageable);
	}
	
}
