package net.boocu.project.service;

import net.boocu.project.entity.ProductTestEntity;
import net.boocu.web.service.BaseService;

/**
 * 产品测试     add by deng  20160118
 * */
public interface ProductTestService extends BaseService<ProductTestEntity, Long> {
	public void saveWithTest(ProductTestEntity productTestEntity, String[] brandName);
	
}
