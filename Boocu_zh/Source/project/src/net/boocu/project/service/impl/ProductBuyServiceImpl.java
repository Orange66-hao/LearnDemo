package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductBuyDao;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productBuyServiceImpl")
public class ProductBuyServiceImpl extends BaseServiceImpl<ProductBuyEntity, Long> implements ProductBuyService {
	@Resource
    private ProductService productService;
	
	@Resource(name = "productBuyDaoImpl")
    private ProductBuyDao productBuyDao;
    
    @Resource(name = "productBuyDaoImpl")
    public void setBaseDao(ProductBuyDao productBuyDao) {
        super.setBaseDao(productBuyDao);
    }

    
    
	@Override
	public Page<ProductBuyEntity> findProductBuyPage(Pageable pageable,
			HashMap<String, Object> map) {
		return productBuyDao.findProductBuyPage(pageable,map);
	}

	@Override
	@Transactional
	public ProductBuyEntity save(ProductBuyEntity productBugEntity,
			ProductEntity productEntity) {
		BigDecimal price = productBugEntity.getProMarketPrice();
		if(productBugEntity.getProMarketPriceLimit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		ProductEntity productEntityNew = productService.save(productEntity);
		productBugEntity.setProductEntity(productEntityNew);
		return save(productBugEntity);
	}
	
	@Override
	@Transactional
	public void deleteList(Long... ids) {
		//super.deleteList(ids);
		for(long id : ids){
			ProductBuyEntity productBuyEntity = find(id);
			productBuyEntity.getProductEntity().setIsDel(1);
			update(productBuyEntity);
		}
		
	}



	@Override
	public Object findFrontProductBuyPage(Pageable pageable, Map con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<ProductBuyEntity> getProductBuyPage(Pageable pageable, Map<String, Object> map) {
		return productBuyDao.getProductBuyPage(pageable, map);
	}



	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductBuyService#getBuyInfo(net.boocu.web.Pageable, java.util.Map)
	 */
	@Override
	public List<ProductBuyEntity> getBuyInfo(Map<String, Object> map) {
		return productBuyDao.getBuyInfo(map);
	}
}
