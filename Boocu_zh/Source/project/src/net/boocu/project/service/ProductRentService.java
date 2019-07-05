package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductRentService extends BaseService<ProductRentEntity, Long> {
	public Page<ProductRentEntity> findProductRentPage(Pageable pageable,HashMap< String, Object> map);
	public ProductRentEntity save(ProductRentEntity productBugEntity,ProductEntity productEntity);
	
	/**
	 * 查询求购信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductRentEntity> getProductRentPage(Pageable pageable,Map<String,Object> map);
	
	/**
	 * 查询所有的租赁信息
	 * @param map
	 * @return
	 */
	public List<ProductRentEntity> getRentInfo(Map<String,Object> map);
}
