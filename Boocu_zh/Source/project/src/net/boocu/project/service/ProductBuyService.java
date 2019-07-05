package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductBuyService extends BaseService<ProductBuyEntity, Long> {
	public Page<ProductBuyEntity> findProductBuyPage(Pageable pageable,HashMap< String, Object> map);
	public ProductBuyEntity save(ProductBuyEntity productBugEntity,ProductEntity productEntity);
	public Object findFrontProductBuyPage(Pageable pageable, Map con);
	
	/**
	 * 查询求购信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductBuyEntity> getProductBuyPage(Pageable pageable,Map<String,Object> map);
	
	/**
	 * 查询求购的菜单
	 * @param pageable
	 * @param map
	 * @return
	 */
	public List<ProductBuyEntity> getBuyInfo(Map<String, Object> map);
}
