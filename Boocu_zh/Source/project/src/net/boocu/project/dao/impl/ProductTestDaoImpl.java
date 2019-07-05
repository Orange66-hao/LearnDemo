package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProductTestDao;
import net.boocu.project.entity.ProductTestEntity;

import org.springframework.stereotype.Repository;

/**
 * 产品测试   add by  deng 20160118
 * */
@Repository("productTestDaoImpl")
public class ProductTestDaoImpl extends BaseDaoImpl<ProductTestEntity, Long> implements ProductTestDao {
}
