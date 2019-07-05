package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductRepairDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productRepairServiceImpl")
public class ProductRepairServiceImpl extends BaseServiceImpl<ProductRepairEntity, Long> implements ProductRepairService {
	@Resource(name = "productRepairDaoImpl")
    private ProductRepairDao productRepairDao;
    
    @Resource(name = "productRepairDaoImpl")
    public void setBaseDao(ProductRepairDao productRepairDao) {
        super.setBaseDao(productRepairDao);
    }

    @Resource
    private ProductService productService;
    
	@Override
	public Page<ProductRepairEntity> getProductRepairPage(Pageable pageable, Map<String, Object> map) {
		return productRepairDao.getProductRepairPage(pageable, map);
	}
    
	@Override
	public Page<ProductRepairEntity> findProductRepairPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		return productRepairDao.findProductRepairPage(pageable, htMap);
	}

	@Override
	@Transactional
	public ProductRepairEntity save(ProductRepairEntity productRepairEntity,
			ProductEntity productEntity) {
		BigDecimal price = productRepairEntity.getRepairPrice();
		if(productRepairEntity.getRepairPriceUnit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		productRepairEntity.setProductEntity(productEntityNew);
		return save(productRepairEntity);
	}
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		for(long id : ids){
			ProductRepairEntity productRepairEntity = find(id);
			productRepairEntity.getProductEntity().setIsDel(1);
			update(productRepairEntity);
		}
		
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductRepairService#getRepairInfo(java.util.Map)
	 */
	@Override
	public List<ProductRepairEntity> getRepairInfo(Map<String, Object> map) {
		return productRepairDao.getRepairInfo(map);
	}
}
