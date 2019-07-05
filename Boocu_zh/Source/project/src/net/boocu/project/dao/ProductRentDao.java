package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductRentDao extends BaseDao<ProductRentEntity,Long> {
	public Page<ProductRentEntity> findProductRentPage(Pageable pageable,HashMap<String,Object> htMap) ;
	
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
