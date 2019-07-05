/**
 * 
 */
package net.boocu.project.dao.impl;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.InvoiceDao;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Repository
public class InvoiceDaoImpl extends BaseDaoImpl<InvoiceEntity, Long>  implements 
InvoiceDao{
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.InvoiceDao#addInvoice(net.boocu.project.entity.InvoiceEntity)
	 */
	@Override
	public int addInvoice(InvoiceEntity ie) {
		StringBuilder sb = new StringBuilder("INSERT INTO t_invoice(order_number,brand_name,");
		sb.append("pro_no,pro_name,item,invoice_amount,type,user_id,pay_complete_time,pro_ids");
		sb.append(") VALUES(?,?,?,?,?,?,?,?,?,?)");
		return jdbcTemplate.update(sb.toString(), new Object[]{ie.getOrderNumber(),ie.getBrandName(),
		ie.getProNo(),ie.getProName(),ie.getItem(),ie.getInvoiceAmount(),ie.getType(),ie.getUserId()
		,ie.getPayCompleteTime(),ie.getProIds()}, new int[]{Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,
		Types.VARCHAR,Types.VARCHAR,Types.INTEGER,Types.BIGINT,Types.VARCHAR,Types.VARCHAR});
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.InvoiceDao#updateInvoiceStat(java.lang.Long, int)
	 */
	@Override
	public int updateInvoiceStat(Long id, int stat) {
		InvoiceEntity ie = this.findById(id);
		
		String sql = "UPDATE t_invoice SET status = ? WHERE order_number = ?";
		return jdbcTemplate.update(sql,stat,ie.getOrderNumber());
	}
	
	@Override
	public int updateInvoice(Long voice_id, Long info_id){
		InvoiceEntity ie = this.findById(voice_id);
		//存在发票和地址，把两者关联起来
		String sql = "UPDATE t_invoice SET status = 1 , invoice_info_id = ?,apply_time = NOW() WHERE order_number = ?";
		return jdbcTemplate.update(sql, info_id,ie.getOrderNumber());
	}
	
	@Override
	public InvoiceEntity findById(Long id){
		return super.find(id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.InvoiceDao#getInvoiceInfo(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<InvoiceEntity> getInvoiceInfo(Pageable pageable, Map<String, Object> params) throws ParseException {
		Long userId = Long.parseLong(params.get("userId").toString());
		String startTime = params.get("startTime")!=null?params.get("startTime").toString():"";
		String endTime = params.get("endTime")!=null?params.get("endTime").toString():"";
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceEntity> query = builder.createQuery(InvoiceEntity.class);
		Root<InvoiceEntity> root = query.from(InvoiceEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		restrictions = builder.and(restrictions,builder.equal(root.<Long>get("userId"), userId));
		
		if(!"".equals(startTime) && !"".equals(endTime)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			restrictions = builder.and(restrictions, 
	        		builder.between(root.<Date>get("payCompleteTime"), sdf.parse(startTime), sdf.parse(endTime)));
		}
		
		return findPage(builder, query, restrictions, root, pageable);
	}
	
	@Override
	public Page<InvoiceEntity> invoiceDataJson(Pageable pageable, Map<String, Object> params){
		int status = Integer.parseInt(params.get("status").toString());
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<InvoiceEntity> query = builder.createQuery(InvoiceEntity.class);
		Root<InvoiceEntity> root = query.from(InvoiceEntity.class);
		
		Predicate restrictions = builder.conjunction();
		
		if(status >= 0)
		restrictions = builder.and(restrictions,builder.equal(root.<Integer>get("status"), status));
		
		return findPage(builder, query, restrictions, root, pageable);
	}

}
