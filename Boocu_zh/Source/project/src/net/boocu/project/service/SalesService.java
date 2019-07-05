package net.boocu.project.service;

import java.util.HashMap;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface SalesService extends BaseService<ProductEntity, Long> {
	public Page<ProductEntity> findFrontSalesPage(Pageable pageable,Map map) ;
	public Page<ProductEntity> findSalesPage(Pageable pageable,HashMap< String, Object> map);
}
