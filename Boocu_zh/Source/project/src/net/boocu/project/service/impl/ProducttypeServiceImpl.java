package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.ProducttypeDao;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 使用方法:如UserEntity 1.将 producttype replaceAll 成 user (注意大小写敏感,将Case
 * sensitive勾选) 2.将 Producttype replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 */

@Service("producttypeServiceImpl")
public class ProducttypeServiceImpl extends BaseServiceImpl<ProducttypeEntity, Long> implements ProducttypeService {
	@Resource(name = "producttypeDaoImpl")
	private ProducttypeDao producttypeDao;

	@Resource(name = "producttypeDaoImpl")
	public void setBaseDao(ProducttypeDao producttypeDao) {
		super.setBaseDao(producttypeDao);
	}
}
