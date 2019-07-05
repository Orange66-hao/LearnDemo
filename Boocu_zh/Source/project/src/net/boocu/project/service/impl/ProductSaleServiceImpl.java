package net.boocu.project.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductSaleDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productSaleServiceImpl")
public class ProductSaleServiceImpl extends BaseServiceImpl<ProductSaleEntity, Long> implements ProductSaleService {
	@Resource(name = "productSaleDaoImpl")
    private ProductSaleDao productSaleDao;
    
	@Resource
	private ProductService productService;
	
	@Resource
	private IndustryClassService industryClassService;
	
    @Resource(name = "productSaleDaoImpl")
    public void setBaseDao(ProductSaleDao productSaleDao) {
        super.setBaseDao(productSaleDao);
    }

	@Override
	@Transactional
	public ProductSaleEntity save(ProductSaleEntity productSaleEntity,
			ProductEntity productEntity) {
		//add by fang 20151021
		BigDecimal price = productSaleEntity.getProShopPrice();
		if(productSaleEntity.getPriceUnit() == PriceUnitEnum.millionyuan){
			price = price.multiply(new BigDecimal("10000"));
		}
		productEntity.setPrice(price);
		
		//add by fang 20150910 增加关联
		productEntity.setProductSaleEntity(productSaleEntity);
		ProductEntity prodEntityNew = productService.save(productEntity);
		productSaleEntity.setProductEntity(prodEntityNew);
		return save(productSaleEntity);
	}
	@Override
	public Page<ProductSaleEntity> findProductSalePage(Pageable pageable,HashMap<String, Object> map){
		return productSaleDao.findProductSalePage(pageable,map);
	}

	@Override
	@Transactional
	public void deleteList(Long... ids) {
		//super.deleteList(ids);
		for(long id : ids){
			ProductSaleEntity productSaleEntity = find(id);
			productSaleEntity.getProductEntity().setIsDel(1);
			update(productSaleEntity);
		}
	}

	@Override
	public Page<ProductSaleEntity> findFrontProductSalePage(Pageable pageable, Map map) {
		
		return productSaleDao.findFrontProductSalePage(pageable, map);
		//return productService.findFrontProductPage(pageable, map);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductSaleService#getProductSalePage(net.boocu.web.Pageable, java.util.HashMap)
	 */
	@Override
	public Page<ProductSaleEntity> getProductSalePage(Pageable pageable, HashMap<String, Object> map) {
		return productSaleDao.getProductSalePage(pageable,map);
	}

	/* (non-Javadoc)
	 * @see net.boocu.project.service.ProductSaleService#getSaleInfo(net.boocu.web.Pageable, java.util.HashMap)
	 */
	@Override
	public List<ProductSaleEntity> getSaleInfo(HashMap<String, Object> map) {
		return productSaleDao.getSaleInfo(map);
	}

    
	
   
}
