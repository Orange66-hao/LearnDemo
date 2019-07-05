package net.boocu.project.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductAuditDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.ProductAuditService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productAuditServiceImpl")
public class ProductAuditServiceImpl extends BaseServiceImpl<ProductEntity, Long> implements ProductAuditService {
	@Resource(name = "productAuditDaoImpl")
    private ProductAuditDao productAuditDao;
    	
    @Resource(name = "productAuditDaoImpl")
    public void setBaseDao(ProductAuditDao productAuditDao) {
        super.setBaseDao(productAuditDao);
    }
	@Override
	public Page<ProductEntity> findProductAuditPage(Pageable pageable,HashMap<String, Object> map){
		return productAuditDao.findProductAuditPage(pageable,map);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		//super.deleteList(ids);
		for(long id : ids){
			ProductEntity productEntity = find(id);
			productEntity.setIsDel(1);
			update(productEntity);
		}
	}
}
