package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductRentDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productRentServiceImpl")
public class ProductRentServiceImpl extends BaseServiceImpl<ProductRentEntity, Long> implements ProductRentService {
	@Resource(name = "productRentDaoImpl")
    private ProductRentDao productRentDao;
	
	@Resource
    private ProductService productService;
    
    @Resource(name = "productRentDaoImpl")
    public void setBaseDao(ProductRentDao productRentDao) {
        super.setBaseDao(productRentDao);
    }
    
	@Override
	public Page<ProductRentEntity> getProductRentPage(Pageable pageable, Map<String, Object> map) {
		return productRentDao.getProductRentPage(pageable, map);
	}

	@Override
	public Page<ProductRentEntity> findProductRentPage(Pageable pageable,
			HashMap<String, Object> map) {
		return productRentDao.findProductRentPage(pageable, map);
	}

	@Override
	public ProductRentEntity save(ProductRentEntity productRentEntity,
			ProductEntity productEntity) {
		BigDecimal price = productRentEntity.getRentPrice();
		if(productRentEntity.getRentPriceUnit()== PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		productRentEntity.setProductEntity(productEntityNew);
		return save(productRentEntity);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		//super.deleteList(ids);
		for(long id : ids){
			ProductRentEntity productRentEntity = find(id);
			productRentEntity.getProductEntity().setIsDel(1);
			update(productRentEntity);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductRentService#getRentInfo(java.util.Map)
	 */
	@Override
	public List<ProductRentEntity> getRentInfo(Map<String, Object> map) {
		return productRentDao.getRentInfo(map);
	}

}
