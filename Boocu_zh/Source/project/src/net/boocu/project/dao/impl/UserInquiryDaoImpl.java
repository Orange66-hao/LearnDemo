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
import net.boocu.project.dao.UserInquiryDao;
import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Repository("userInquiryDaoImpl")
public class UserInquiryDaoImpl extends BaseDaoImpl<UserInquiryEntity,Long> implements UserInquiryDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Page<UserInquiryEntity> findInquiryByPage(Pageable pageable, Map<String, Object> params) {
		Long id = Long.parseLong(params.get("userId").toString());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserInquiryEntity> query = builder.createQuery(UserInquiryEntity.class);
		Root<UserInquiryEntity> root = query.from(UserInquiryEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions, 
        		builder.equal(root.get("memberEntity").<Long>get("id"), id));
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public Page<UserInquiryEntity> inquiryData(Pageable pageable, Map<String, Object> params) {
		
		//默认获取待报价的询价单
		Integer status = Integer.parseInt(params.get("status")==null?"0":params.get("status").toString());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserInquiryEntity> query = builder.createQuery(UserInquiryEntity.class);
		Root<UserInquiryEntity> root = query.from(UserInquiryEntity.class);
		
		Predicate restrictions = builder.conjunction();
		if(status == 0){
			restrictions = builder.and(restrictions, 
	        		builder.or(
	        				builder.equal(root.get("status"), status),
	        				builder.equal(root.get("status"), 1)
	        		));
		}else{
			restrictions = builder.and(restrictions, 
	        		builder.equal(root.get("status"), status));
		}
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public int insertInquiry(UserInquiryEntity uie){
		String sql = "INSERT INTO jhj_user_inquiry(pro_name,member_id,pro_id,privilege_price,"
				+ "one_price,status,url) VALUES(?,?,?,?,?,?,?);";
		return jdbcTemplate.update(sql, new Object[]{uie.getProName(),uie.getMemberEntity().getId()
				,uie.getProductEntity().getId(),uie.getPrivilegePrice(),uie.getOnePrice(),
				uie.getStatus(),uie.getUrl()}, new int[]{Types.VARCHAR,Types.INTEGER,Types.INTEGER,
				Types.FLOAT,Types.FLOAT,Types.INTEGER,Types.VARCHAR});
	}
	
	@Override
	public int inquiry(Long id){
		String sql = "UPDATE jhj_user_inquiry SET status = 1 WHERE id = ?";
		return jdbcTemplate.update(sql, id);
	}
	
	@Override
	public int give(int id,float price){
		String sql = "UPDATE jhj_user_inquiry SET status = 2,privilege_price = ? WHERE id = ?";
		return jdbcTemplate.update(sql, price,id);
	}

}
