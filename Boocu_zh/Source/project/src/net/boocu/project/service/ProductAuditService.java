package net.boocu.project.service;

import java.util.HashMap;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductAuditService extends BaseService<ProductEntity, Long> {
	public Page<ProductEntity> findProductAuditPage(Pageable pageable,HashMap< String, Object> map);
}
