package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProducWantRepairService extends BaseService<ProducWantRepairEntity, Long> {
	public Page<ProducWantRepairEntity> findProductWantRepairPage(Pageable pageable,HashMap<String,Object> htMap) ;
	public ProducWantRepairEntity save(ProducWantRepairEntity producWantRepairEntity,ProductEntity productEntity);
	
	/**
	 * 查询求购信息
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProducWantRepairEntity> getProductWantRepairPage(Pageable pageable,Map<String,Object> map);
	
	/**
	 * 查询所有的求修
	 * @param map
	 * @return
	 */
	public List<ProducWantRepairEntity> getWantRepair(Map<String,Object> map);
}
