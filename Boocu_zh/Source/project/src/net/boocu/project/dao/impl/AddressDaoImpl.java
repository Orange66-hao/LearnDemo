/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.AddressDao;
import net.boocu.project.entity.AddressEntity;

/**
 * @author Administrator
 *
 */
@Repository
public class AddressDaoImpl extends BaseDaoImpl<AddressEntity, Long> implements AddressDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate; 

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.AddressDao#addAddress(net.boocu.project.entity.AddressEntity)
	 */
	@Override
	public long addAddress(AddressEntity ae) {
		if(ae.getIsDefault() == 1){
			//取消这个用户的默认收货地址
			this.cancelAddr(ae.getMemberEntity().getId());
		}
		//16个参数
		String sql = "INSERT INTO t_delivery_address(member_id,continent,country,province,";
		sql += "city,area,detail,post_code,recv_name,codem,mobile_phone,code_tel,telqh,";
		sql +=  "tel_number,tel_fj,is_default) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return jdbcTemplate.update(sql, new Object[]{ae.getMemberEntity().getId(),ae.getContinent(),
		ae.getCountry(),ae.getProvince(),ae.getCity(),ae.getArea(),ae.getDetail(),ae.getPostCode(),
		ae.getRecvName(),ae.getCodeM(),ae.getMobilePhone(),ae.getCodeTel(),ae.getTelQH(),ae.getTelNumber(),
		ae.getTelFj(),ae.getIsDefault()}, new int[]{Types.BIGINT,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER});
	}
	
	@Override
	public int updateAddr(AddressEntity ae){
		String sql = "UPDATE t_delivery_address SET continent = ?,country = ?,province = ?,city = ?,";
		sql += "area = ?,detail = ?,post_code = ?,recv_name = ?,codem = ?,mobile_phone = ?,code_tel = ?";
		sql += ",telqh = ?,tel_number = ?,tel_fj = ?,is_default = ? WHERE id = ?";
		return jdbcTemplate.update(sql, new Object[]{ae.getContinent(),ae.getCountry(),ae.getProvince(),
		ae.getCity(),ae.getArea(),ae.getDetail(),ae.getPostCode(),ae.getRecvName(),ae.getCodeM(),
		ae.getMobilePhone(),ae.getCodeTel(),ae.getTelQH(),ae.getTelNumber(),ae.getTelFj(),ae.getIsDefault(),
		ae.getId()}, new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.INTEGER});
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.AddressDao#delAddress(java.lang.Long)
	 */
	@Override
	public void delAddress(Long id) {
		String sql = "DELETE FROM t_delivery_address WHERE id = ?";
		jdbcTemplate.update(sql,id);
	}
	
	@Override
	public List<AddressEntity> getAddrLists(AddressEntity ae){
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AddressEntity> query = builder.createQuery(AddressEntity.class);
		Root<AddressEntity> root = query.from(AddressEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,
        		builder.equal(root.get("memberEntity").<Long>get("id"), ae.getMemberEntity().getId()));
		
		return findList(builder, query, restrictions, root, 0,1000,null, null);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.AddressDao#updateToDefault(java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateToDefault(Long userId, Long defId) {
		this.cancelAddr(userId);
		String defSql = "UPDATE t_delivery_address SET is_default = 1 WHERE id = ?";
		jdbcTemplate.update(defSql,defId);
	}
	
	//取消这个用户的默认收货地址
	@Override
	public void cancelAddr(Long userId){
		String sql = "UPDATE t_delivery_address SET is_default = 0 WHERE member_id = ?";
		jdbcTemplate.update(sql,userId);
	}
	
	@Override
	public void updateMemberAddr(Long userId,Long addrId){
		String sql = "UPDATE sys_member SET default_del_addr = ? WHERE id = ?";
		jdbcTemplate.update(sql,addrId,userId);
	}
	
	@Override
	public AddressEntity findDefAddr(Long userId){
		String sql = "SELECT t.id AS id FROM t_delivery_address t WHERE t.is_default = 1"
		+ " AND t.member_id = "+userId;
		AddressEntity ae = new AddressEntity();
		jdbcTemplate.query(sql, new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				ae.setId(rs.getLong("id"));
			}
		});
		return ae;
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.AddressDao#findById(java.lang.Long)
	 */
	@Override
	public AddressEntity findById(Long id) {
		return super.find(id);
	}
	
}
