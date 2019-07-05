package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProducWantRepairDao extends BaseDao<ProducWantRepairEntity,Long> {
	public Page<ProducWantRepairEntity> findProductWantRepairPage(Pageable pageable,HashMap<String,Object> htMap) ;
	
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
