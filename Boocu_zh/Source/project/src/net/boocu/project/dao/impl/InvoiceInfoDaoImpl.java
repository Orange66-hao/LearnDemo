/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.InvoiceInfoDao;
import net.boocu.project.entity.InvoiceInfoEntity;
/**
 * @author Administrator
 *
 */
@Repository
public class InvoiceInfoDaoImpl extends BaseDaoImpl<InvoiceInfoEntity, Long>
implements InvoiceInfoDao{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addInvoiceInfo(InvoiceInfoEntity iie) {
		StringBuilder sql = new StringBuilder("INSERT INTO t_invoice_info(user_id,type,");
		sql.append("unit_name,record_code,reg_address,reg_phone,bank,bank_code,");
		sql.append("revc_addr_id) VALUES(?,?,?,?,?,?,?,?,?)");
		return jdbcTemplate.update(sql.toString(),new Object[]{iie.getUserId(),iie.getType()
		,iie.getUnitName(),iie.getRecordCode(),iie.getRegAddress(),iie.getRegPhone(),
		iie.getBank(),iie.getBankCode(),iie.getAddressEntity().getId()},new int[]{Types.BIGINT
		,Types.INTEGER,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.BIGINT});
	}
	
	@Override
	public int addSimpleInvoiceInfo(InvoiceInfoEntity iie){
		StringBuilder sb = new StringBuilder("INSERT INTO t_invoice_info(user_id,type,");
		sb.append("unit_name,details,recv_addr_id) VALUES(?,?,?,?,?)");
		return jdbcTemplate.update(sb.toString(),new Object[]{iie.getUserId(),iie.getType()
		,iie.getUnitName(),iie.getDetails(),iie.getAddressEntity().getId()},new int[]{
		Types.BIGINT,Types.BIGINT,Types.VARCHAR,Types.VARCHAR,Types.BIGINT});
	}
	
	@Override
	public Long addSimpleII(InvoiceInfoEntity iie) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		if(iie.getType() == 0){
			//普通发票
			String sql = "INSERT INTO t_invoice_info(user_id,type,unit_name,recv_addr_id) VALUES(?,?,?,?)";
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connnection) throws SQLException {
					PreparedStatement ps=connnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);  
					ps.setLong(1, iie.getUserId());
					ps.setInt(2, iie.getType());
					ps.setString(3, iie.getUnitName());
					ps.setLong(4, iie.getAddressEntity().getId());
					return ps;
				}},
			keyHolder);
		}else{
			//增值发票
			String sql = "INSERT INTO t_invoice_info(user_id,type,unit_name,record_code,reg_address,reg_phone,bank,bank_code,recv_addr_id) VALUES(?,?,?,?,?,?,?,?,?)";
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connnection) throws SQLException {
					PreparedStatement ps=connnection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);  
					ps.setLong(1, iie.getUserId());
					ps.setInt(2, iie.getType());
					ps.setString(3, iie.getUnitName());
					ps.setString(4, iie.getRecordCode());
					ps.setString(5, iie.getRegAddress());
					ps.setString(6, iie.getRegPhone());
					ps.setString(7, iie.getBank());
					ps.setString(8, iie.getBankCode());
					ps.setLong(9, iie.getAddressEntity().getId());
					return ps;
				}},
			keyHolder);
		}
		return keyHolder.getKey().longValue();
	}

	@Override
	public InvoiceInfoEntity findById(Long id,int type) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceInfoEntity> query = builder.createQuery(InvoiceInfoEntity.class);
		Root<InvoiceInfoEntity> root = query.from(InvoiceInfoEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,builder.equal(root.<Integer>get("type"), type));
		restrictions = builder.and(restrictions,builder.equal(root.<Long>get("userId"), id));
		
		List<InvoiceInfoEntity> lists = findList(builder, query, restrictions, root, 0, 10000, null, null);
		if(lists.size() > 0){
			return lists.get(0);
		}else{
			return null;
		}
	}
}
