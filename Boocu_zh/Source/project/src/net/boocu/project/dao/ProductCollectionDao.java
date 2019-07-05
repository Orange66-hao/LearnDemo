package net.boocu.project.dao;

import java.util.HashMap;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductCollectionEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductCollectionDao extends BaseDao<ProductCollectionEntity, Long> {

	/**
	 * 查找分页实体
	 * 
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductCollectionEntity> findListProductPage(Pageable pageable, Map map);
}
