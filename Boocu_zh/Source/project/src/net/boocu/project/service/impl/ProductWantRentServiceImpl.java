package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductWantRentDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productWantRentServiceImpl")
public class ProductWantRentServiceImpl extends BaseServiceImpl<ProductWantRentEntity, Long> implements ProductWantRentService {
	@Resource(name = "productWantRentDaoImpl")
    private ProductWantRentDao productWantRentDao;
    
    @Resource(name = "productWantRentDaoImpl")
    public void setBaseDao(ProductWantRentDao productWantRentDao) {
        super.setBaseDao(productWantRentDao);
    }

    @Resource
    private ProductService productService;
    
	@Override
	public Page<ProductWantRentEntity> getProductRentPage(Pageable pageable, Map<String, Object> map) {
		return productWantRentDao.getProductRentPage(pageable, map);
	}
    
	@Override
	public Page<ProductWantRentEntity> findProductWantRentPage(
			Pageable pageable, HashMap<String, Object> map) {
		return productWantRentDao.findProductWantRentPage(pageable, map);
	}

	@Override
	public ProductWantRentEntity save(ProductWantRentEntity productWantRentEntity,
			ProductEntity productEntity) {
		BigDecimal price = productWantRentEntity.getMagdebrugBudget();
		if(productWantRentEntity.getMagdebrugBudgetLimit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		productWantRentEntity.setProductEntity(productEntityNew);
		return save(productWantRentEntity);
	}
	
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		for(long id : ids){
			ProductWantRentEntity productWantRentEntity = find(id);
			productWantRentEntity.getProductEntity().setIsDel(1);
			update(productWantRentEntity);
		}
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductWantRentService#getWantRentInfo(java.util.Map)
	 */
	@Override
	public List<ProductWantRentEntity> getWantRentInfo(Map<String, Object> map) {
		return productWantRentDao.getWantRentInfo(map);
	}
}
