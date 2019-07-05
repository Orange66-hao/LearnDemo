package net.boocu.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.ProductDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("productServiceImpl")
public class ProductServiceImpl extends BaseServiceImpl<ProductEntity, Long> implements ProductService {
	@Resource(name = "productDaoImpl")
    private ProductDao productDao;
    
    @Resource(name = "productDaoImpl")
    public void setBaseDao(ProductDao productDao) {
        super.setBaseDao(productDao);
    }

	@Override
	@Transactional
	public boolean reversePro(Long[] id,String msg) {
		for(long iditem : id){
			ProductEntity productEntity = find(iditem);
			if(productEntity != null){
				System.out.println("serviceImp里面:"+iditem);
				productEntity.setIsDel(0);
				update(productEntity);
			}else{
				msg = "商品恢复过程中遇见错误,请重试!";
				return false;
			}
		}
		return true;
	}

	@Override
	public Page<ProductEntity> findFrontProductPage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		Page<ProductEntity> pages = productDao.findFrontProductPage(pageable, map);
		System.out.println("页数:--:"+pages.getTotalPages());
		return pages;
	}
	
	@Override
	public Page<ProductEntity> findListProductPage(Pageable pageable, HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		Page<ProductEntity> pages = productDao.findListProductPage(pageable, map);
		System.out.println("页数:--:"+pages.getTotalPages());
		return pages;
	}

	@Override
	public Page<ProductEntity> findSubscriptInfo(Pageable pageable, Map<String, Object> htMap) {
		// TODO Auto-generated method stub
		return productDao.findSubscriptInfo(pageable, htMap);
	}

	@Override
	public List<ProductEntity> searchList(String keyword) {
		return productDao.searchList(keyword);
	}

	@Override
	public List<ProductEntity> findListByNameAndProNo(int i, boolean b, String[] mcProductClass) {
		
		return productDao.findListByNameAndProNo(i,b,mcProductClass);
	}

	@Override
	public List<ProductEntity> findByIndustryClass(int i, Long id, String proType, String c1, String c2) {

		return productDao.findByIndustryClass(i,id,proType,c1,c2);
	}

}
