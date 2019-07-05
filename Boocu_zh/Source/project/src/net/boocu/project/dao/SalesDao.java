package net.boocu.project.dao;

import java.util.HashMap;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.SalesEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface SalesDao extends BaseDao<ProductEntity,Long> {
	/**
	 * 列表页面获取数据的公共方法 add by deng 20151013
	 * */
	public Page<ProductEntity> findFrontSalesPage(Pageable pageable,Map map) ; 
	public Page<ProductEntity> findSalesPage(Pageable pageable,HashMap<String, Object> map);
}
