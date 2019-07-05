package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductRepairDao extends BaseDao<ProductRepairEntity,Long> {
	public Page<ProductRepairEntity> findProductRepairPage(Pageable pageable,HashMap<String,Object> htMap) ;
	
	/**
	 * 查询求购信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductRepairEntity> getProductRepairPage(Pageable pageable,Map<String,Object> map);
	
	/**
	 *  查询所有的维修
	 * @param map
	 * @return
	 */
	public List<ProductRepairEntity> getRepairInfo(Map<String,Object> map);
}
