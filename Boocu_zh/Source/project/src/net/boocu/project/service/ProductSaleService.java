package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductSaleService extends BaseService<ProductSaleEntity, Long> {
	public ProductSaleEntity save(ProductSaleEntity productSaleEntity,ProductEntity productEntity);
	public Page<ProductSaleEntity> findProductSalePage(Pageable pageable,HashMap< String, Object> map);
	public Page<ProductSaleEntity> findFrontProductSalePage(Pageable pageable,Map map);
	
	/**
	 * 查询销售信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductSaleEntity> getProductSalePage(Pageable pageable,HashMap< String, Object> map);
	
	/**
	 * 查询所有的销售
	 * @param pageable
	 * @param map
	 * @return
	 */
	public List<ProductSaleEntity> getSaleInfo(HashMap<String, Object> map);
}
