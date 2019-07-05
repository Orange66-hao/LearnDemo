package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductSaleDao extends BaseDao<ProductSaleEntity,Long> {
	/**
	 * 
	 * 概述:后台分页
	 * 传入:分页对象pageable,分页参数map
	 */
	public Page<ProductSaleEntity> findProductSalePage(Pageable pageable,HashMap<String, Object> map);

	/**
	 * 
	 * 概述:前台分页方法
	 * 传入:分页对象pageable,分页参数map
	 */
	public Page<ProductSaleEntity> findFrontProductSalePage(Pageable pageable,
			Map map);
	
	
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
