/**
 * 
 */
package net.boocu.project.service;

import java.text.ParseException;
import java.util.Map;

import net.boocu.project.entity.InvoiceEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface InvoiceService extends BaseService<InvoiceEntity, Long> {
	
	public int addInvoice(InvoiceEntity ie);
	
	public int updateInvoiceStat(Long id,int stat);
	
	public Page<InvoiceEntity> getInvoiceInfo(Pageable pageable,Map<String,Object> params) throws ParseException;
	
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
