package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductBuyDao extends BaseDao<ProductBuyEntity,Long> {
	public Page<ProductBuyEntity> findProductBuyPage(Pageable pageable,HashMap<String,Object> htMap) ;
	

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
