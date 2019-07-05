package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductWantRentDao extends BaseDao<ProductWantRentEntity,Long> {
	public Page<ProductWantRentEntity> findProductWantRentPage(Pageable pageable,HashMap<String,Object> htMap) ;
	
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
