/**
 * 
 */
package net.boocu.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.InvoiceInfoDao;
import net.boocu.project.entity.InvoiceInfoEntity;
import net.boocu.project.service.InvoiceInfoService;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class InvoiceInfoServiceImpl extends BaseServiceImpl<InvoiceInfoEntity,Long> 
implements InvoiceInfoService {
	
	@Autowired
	private InvoiceInfoDao invoiceInfoDao; 
	
	@Override
	public int addInvoiceInfo(InvoiceInfoEntity iie) {
		return invoiceInfoDao.addInvoiceInfo(iie);
	}

	@Override
	public InvoiceInfoEntity findById(Long id,int type) {
		return invoiceInfoDao.findById(id,type);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceInfoService#addSimpleInvoiceInfo(net.boocu.project.entity.InvoiceInfoEntity)
	 */
	@Override
	public int addSimpleInvoiceInfo(InvoiceInfoEntity iie) {
		return invoiceInfoDao.addSimpleInvoiceInfo(iie);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceInfoService#addSimpleII(net.boocu.project.entity.InvoiceInfoEntity)
	 */
	@Override
	public Long addSimpleII(InvoiceInfoEntity iie) {
		return invoiceInfoDao.addSimpleII(iie);
	}

}
