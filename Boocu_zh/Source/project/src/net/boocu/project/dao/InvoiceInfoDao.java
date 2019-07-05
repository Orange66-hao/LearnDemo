/**
 * 
 */
package net.boocu.project.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.InvoiceInfoEntity;

/**
 * @author Administrator
 *
 */
public interface InvoiceInfoDao extends BaseDao<InvoiceInfoEntity, Long> {
	
	public int addInvoiceInfo(InvoiceInfoEntity iie);
	
	public InvoiceInfoEntity findById(Long id,int type);

	/**
	 * @param iie
	 * @return
	 */
	public int addSimpleInvoiceInfo(InvoiceInfoEntity iie);
	
	/**
	 * 新增发票地址，并返回主键id
	 * @param iie
	 * @return
	 */
	public Long addSimpleII(InvoiceInfoEntity iie);
	
}
