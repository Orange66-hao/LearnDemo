/**
 * 
 */
package net.boocu.project.service;

import net.boocu.project.entity.InvoiceInfoEntity;
import net.boocu.web.service.BaseService;

/**
 * @author Administrator
 *
 */
public interface InvoiceInfoService extends BaseService<InvoiceInfoEntity, Long>
{
	
	public int addInvoiceInfo(InvoiceInfoEntity iie);
	
	public InvoiceInfoEntity findById(Long id,int type);
	
	public int addSimpleInvoiceInfo(InvoiceInfoEntity iie);
	
	/**
	 * 新增发票地址，并返回主键id
	 * @param iie
	 * @return
	 */
	public Long addSimpleII(InvoiceInfoEntity iie);
	
}
