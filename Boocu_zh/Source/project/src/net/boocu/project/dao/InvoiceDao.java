/**
 * 
 */
package net.boocu.project.dao;

import java.text.ParseException;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
public interface InvoiceDao extends BaseDao<InvoiceEntity, Long>{
	
	public int addInvoice(InvoiceEntity ie);
	
	public int updateInvoiceStat(Long id,int stat);
	
	public Page<InvoiceEntity> getInvoiceInfo(Pageable pageable,Map<String,Object> params) throws ParseException;

	/**
	 * @param id
	 * @param infoId
	 * @return
	 */
	public int updateInvoice(Long voice_id, Long info_id);

	/**
	 * @param pageable
	 * @param params
	 * @return
	 */
	public Page<InvoiceEntity> invoiceDataJson(Pageable pageable, Map<String, Object> params);

	/**
	 * @param id
	 * @return
	 */
	public InvoiceEntity findById(Long id);
}
