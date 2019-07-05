/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ShopCartDao;
import net.boocu.project.entity.ShopCart;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Repository
public class ShopCartDaoImpl extends BaseDaoImpl<ShopCart, Long> implements ShopCartDao{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#insert(net.boocu.project.entity.ShopCart)
	 */
	@Override
	public int insert(ShopCart sc) {
		//默认添加数量是1
		StringBuilder sb = new StringBuilder("INSERT INTO t_shop_cart(member_id,pro_id,price,url,product_entity)");
		sb.append(" VALUES(?,?,?,?,?)");
		return jdbcTemplate.update(sb.toString(), new Object[]{sc.getMemberEntity().getId()
		,sc.getProductEntity().getId(),sc.getPrice(),sc.getUrl(),sc.getProductEntity().getProOriginal1()},new int[]{
		Types.INTEGER,Types.VARCHAR,Types.FLOAT,Types.VARCHAR,Types.VARCHAR} );
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#update(net.boocu.project.entity.ShopCart)
	 */
	@Override
	public int update(ShopCart sc) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#updateCount(java.lang.Long, int)
	 */
	@Override
	public int updateCount(Long id, int count) {
		String sql = "UPDATE t_shop_cart SET count = count+? WHERE id = ?";
		return jdbcTemplate.update(sql,count,id);
	}
	
	@Override
	public int addCountOne(Long userId,Long proId){
		String sql = "UPDATE t_shop_cart SET count = count+1 WHERE member_id = ? AND pro_id = ?";
		return jdbcTemplate.update(sql, userId,proId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long id) {
		String sql = "DELETE FROM t_shop_cart WHERE id = ?";
		return jdbcTemplate.update(sql,id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#findShopCartByUserId(net.boocu.project.entity.ShopCart)
	 */
	@Override
	public Page<ShopCart> findShopCartByUserId(Pageable pageable, Map<String, Object> params) {
		Long userId = Long.parseLong(params.get("userId").toString());
		String proName = params.get("proName").toString();
		Long selOType = Long.parseLong(params.get("selOType").toString());
		String selProNo = params.get("selProNo").toString();
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ShopCart> query = builder.createQuery(ShopCart.class);
		Root<ShopCart> root = query.from(ShopCart.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("memberEntity").<Long>get("id"), userId));
		
		if(selOType > 0){
			restrictions = builder.and(restrictions,
	        		builder.equal(root.get("productEntity").get("productBrandEntity").<Long>get("id"), selOType));
		}
		
		if(!selProNo.equals("0")){
			restrictions = builder.and(restrictions,
	        		builder.equal(root.get("productEntity").<String>get("proNo"), selProNo));
		}
		
		if(proName!=null && !"".equals(proName)){
			restrictions = builder.and(restrictions,
	        		builder.like(root.get("productEntity").<String>get("proName"), "%" + proName + "%"));
		}
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("status"), 1));
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public List<ShopCart> shopCartList(Map<String, Object> params){
		Long userId = Long.parseLong(params.get("userId").toString());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ShopCart> query = builder.createQuery(ShopCart.class);
		Root<ShopCart> root = query.from(ShopCart.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("memberEntity").<Long>get("id"), userId));
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("status"), 1));
		return findList(builder, query, restrictions, root, 0, 10000, null, null);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#batchDelete(java.lang.String)
	 */
	@Override
	public int batchDelete(String ids) {
		String sql = "DELETE FROM t_shop_cart WHERE id IN ("+ids+")";
		return jdbcTemplate.update(sql);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#getProductByUserIdAndProductId(java.lang.Long, java.lang.Long)
	 */
	@Override
	public int getProductByUserIdAndProductId(Long userId, Long proId) {
		String sql = "SELECT COUNT(*) FROM t_shop_cart WHERE member_id = ? AND pro_id = ?";
		return jdbcTemplate.queryForInt(sql, userId,proId);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ShopCartDao#findById(java.lang.Long)
	 */
	@Override
	public ShopCart findById(Long id) {
		
		return super.find(id);
	}

}
