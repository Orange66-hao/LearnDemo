package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductWantRentService extends BaseService<ProductWantRentEntity, Long> {
	public Page<ProductWantRentEntity> findProductWantRentPage(Pageable pageable,HashMap< String, Object> map);
	public ProductWantRentEntity save(ProductWantRentEntity productBugEntity,ProductEntity productEntity);
	
	/**
	 * 查询求购信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductWantRentEntity> getProductRentPage(Pageable pageable,Map<String,Object> map);
	
	/**
	 * 查询所有的求租
	 * @param map
	 * @return
	 */
	public List<ProductWantRentEntity> getWantRentInfo(Map<String,Object> map);
}
