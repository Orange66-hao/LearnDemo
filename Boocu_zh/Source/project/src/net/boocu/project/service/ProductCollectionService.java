package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.ProductCollectionEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductCollectionService extends BaseService<ProductCollectionEntity, Long> {
	/**
	 * 删除收藏
	 */
	boolean reverseProCollection(Long[] id, String msg);

	/**
	 * 加入收藏
	 * 
	 * @param entity
	 * @return
	 */
	void saveCollection(ProductCollectionEntity entity);

	/**
	 * 查找分页实体
	 * 
	 * @param pageable
	 * @param map
	 * @return
	 */
	public Page<ProductCollectionEntity> findListCollectionPage(Pageable pageable, Map map);
}
