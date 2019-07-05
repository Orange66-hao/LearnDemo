package net.boocu.project.dao;

import java.util.HashMap;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductAuditDao extends BaseDao<ProductEntity,Long> {
	public Page<ProductEntity> findProductAuditPage(Pageable pageable,HashMap<String, Object> map);
}
