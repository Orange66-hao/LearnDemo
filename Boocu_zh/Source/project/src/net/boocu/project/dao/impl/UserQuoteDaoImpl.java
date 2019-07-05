/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.Types;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.UserQuoteDao;
import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Repository
public class UserQuoteDaoImpl extends BaseDaoImpl
<UserQuoteEntity, Long> implements UserQuoteDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page<UserQuoteEntity> findQuoteByPage
	(Pageable pageable, Map<String, Object> params) {
		Long id = Long.parseLong(params.get("userId").toString());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserQuoteEntity> query = builder.createQuery(UserQuoteEntity.class);
		Root<UserQuoteEntity> root = query.from(UserQuoteEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions, 
        		builder.equal(root.get("memberEntity").<Long>get("id"), id));
		
		return findPage(builder, query, restrictions, root, pageable);
	}

	@Override
	public int insertQuote(UserQuoteEntity uqe) {
		String sql = "INSERT INTO jhj_user_quote(pro_name,member_id,pro_id,original_price,type)";
		sql += " VALUES(?,?,?,?,?)";
		return jdbcTemplate.update(sql, new Object[]{uqe.getProName(),uqe.getMemberEntity().getId()
				,uqe.getProductEntity().getId(),uqe.getOriginalPrice(),uqe.getType()},new int[]{
				Types.VARCHAR,Types.LONGVARCHAR,Types.LONGVARCHAR,Types.FLOAT,Types.VARCHAR});
	}

	@Override
	public int quote(Long id) {
		String sql = "UPDATE jhj_user_quote SET status = 2 WHERE id = ?";
		return jdbcTemplate.update(sql,id);
	}

	@Override
	public Page<UserQuoteEntity> quoteData(Pageable pageable, Map<String, Object> params) {
		Integer status = Integer.parseInt(params.get("status").toString());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserQuoteEntity> query = builder.createQuery(UserQuoteEntity.class);
		Root<UserQuoteEntity> root = query.from(UserQuoteEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions, 
        		builder.equal(root.get("status"), status));
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public int findById(Long id){
		String sql = "SELECT COUNT(*) FROM jhj_user_quote WHERE pro_id = ?";
		return jdbcTemplate.queryForInt(sql, id);
	}

	@Override
	public int give(Long id, float price) {
		String sql = "UPDATE jhj_user_quote SET privilege_price = ? WHERE id = ?";
		return jdbcTemplate.update(sql, price,id);
	}


}
