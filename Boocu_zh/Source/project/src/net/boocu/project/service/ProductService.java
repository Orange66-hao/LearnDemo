package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProductService extends BaseService<ProductEntity, Long> {

	boolean reversePro(Long[] id,String msg);
	
	public Page<ProductEntity> findFrontProductPage(Pageable pageable,
			Map map) ;
	public Page<ProductEntity> findListProductPage(Pageable pageable,
			HashMap<String, Object> map) ;

	public Page<ProductEntity> findSubscriptInfo(Pageable pageable, Map<String, Object> htMap) ;

	List<ProductEntity> searchList(String keyword);

	List<ProductEntity> findListByNameAndProNo(int i, boolean b, String[] mcProductClassList);

    List<ProductEntity> findByIndustryClass(int i, Long id, String proType, String c1, String c2);
}
