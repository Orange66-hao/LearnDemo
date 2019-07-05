package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductRepairService extends BaseService<ProductRepairEntity, Long> {
	public Page<ProductRepairEntity> findProductRepairPage(Pageable pageable,HashMap<String,Object> htMap) ;
	public ProductRepairEntity save(ProductRepairEntity productRepairEntity,ProductEntity productEntity);
	
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
