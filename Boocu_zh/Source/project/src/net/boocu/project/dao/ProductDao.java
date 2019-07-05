package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface ProductDao extends BaseDao<ProductEntity,Long> {
	/**
	 * 列表页面获取数据的公共方法 add by fang 20150910
	 * */
	public Page<ProductEntity> findFrontProductPage(Pageable pageable,
			Map map) ; 
	public Page<ProductEntity> findListProductPage(Pageable pageable,
			HashMap<String, Object> map) ; 
	
	public Page<ProductEntity> findSubscriptInfo(Pageable pageable, Map<String, Object> htMap);
	public List<ProductEntity> searchList(String keyword);
	public List<ProductEntity> findListByNameAndProNo(int i, boolean b, String[] mcProductClass);

    List<ProductEntity> findByIndustryClass(int i, Long id, String proType, String c1, String c2);
}
