package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProducttypeDao;
import net.boocu.project.entity.ProducttypeEntity;

import org.springframework.stereotype.Repository;


@Repository("producttypeDaoImpl")
public class ProducttypeDaoImpl extends BaseDaoImpl<ProducttypeEntity, Long> implements ProducttypeDao {
	
}
