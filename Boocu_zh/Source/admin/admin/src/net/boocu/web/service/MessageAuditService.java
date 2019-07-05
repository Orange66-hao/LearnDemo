package net.boocu.web.service;

import java.util.HashMap;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface MessageAuditService extends BaseService<ProductEntity, Long> {
	public Page<ProductEntity> findMessageAuditPage(Pageable pageable,HashMap< String, Object> map);
}
