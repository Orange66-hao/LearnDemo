/**
 * 
 */
package net.boocu.project.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.project.dao.InvoiceDao;
import net.boocu.project.dao.ProductDao;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.InvoiceService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

/**
 * @author Administrator
 *
 */
@Service
public class InvoiceServiceImpl extends BaseServiceImpl<InvoiceEntity, Long> implements
InvoiceService{
	
	@Autowired
	private InvoiceDao invoiceDao;
	
	private ProductDao productDao;

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#addInvoice(net.boocu.project.entity.InvoiceEntity)
	 */
	@Override
	public int addInvoice(InvoiceEntity ie) {
		return invoiceDao.addInvoice(ie);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#updateInvoiceStat(java.lang.Long, int)
	 */
	@Override
	public int updateInvoiceStat(Long id, int stat) {
		return invoiceDao.updateInvoiceStat(id, stat);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#getInvoiceInfo(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<InvoiceEntity> getInvoiceInfo(Pageable pageable, Map<String, Object> params) throws ParseException {
		return invoiceDao.getInvoiceInfo(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#updateInvoice(int, int)
	 */
	@Override
	public int updateInvoice(Long voice_id, Long info_id) {
		return invoiceDao.updateInvoice(voice_id, info_id);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#invoiceDataJson(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public Page<InvoiceEntity> invoiceDataJson(Pageable pageable, Map<String, Object> params) {
		return invoiceDao.invoiceDataJson(pageable, params);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.InvoiceService#findById(java.lang.Long)
	 */
	@Override
	public InvoiceEntity findById(Long id) {
		return invoiceDao.findById(id);
	}

}
