package net.boocu.project.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProducWantRepairDao;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.stereotype.Repository;

@Repository("producWantRepairDaoImpl")
public class ProducWantRepairDaoImpl extends BaseDaoImpl<ProducWantRepairEntity, Long> implements ProducWantRepairDao {

	@Override
	public Page<ProducWantRepairEntity> getProductWantRepairPage(Pageable pageable, Map<String, Object> map) {
		
		String index = map.get("index").toString();
		
		 // 获取条件构造器
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProducWantRepairEntity> query = 
        		builder.createQuery(ProducWantRepairEntity.class);
        
        Root<ProducWantRepairEntity> root = query.from(ProducWantRepairEntity.class);
        
        Predicate restrictions = builder.conjunction();
        
        if(!"0".equals(index)){
        	long brandId = Long.parseLong(map.get("brandId").toString());
        	String proNo = map.get("proNo").toString();
            restrictions = builder.and(restrictions,
            		builder.and(
            			builder.equal(root.get("productEntity").<String>get("proNo"), proNo),
            			builder.equal(root.get("productEntity").get("productBrandEntity").<String>get("id"), brandId)
            		));
        }
        
        return findPage(builder,query,restrictions,root,pageable);
	}
	

	/* (non-Javadoc)
	 * @see net.boocu.project.dao.ProducWantRepairDao#getWantRepair(java.util.Map)
	 */
	@Override
	public List<ProducWantRepairEntity> getWantRepair(Map<String, Object> map) {
		String kw = map.get("kw").toString();
		
		 // 获取条件构造器
     CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     
     CriteriaQuery<ProducWantRepairEntity> query = 
     		builder.createQuery(ProducWantRepairEntity.class);
     
     Root<ProducWantRepairEntity> root = query.from(ProducWantRepairEntity.class);
     Predicate restrictions = builder.conjunction();
     
     if(!"".equals(kw)){
     	//带有参数
         restrictions = builder.and(restrictions,
         		builder.or(
         			builder.like(root.get("productEntity").<String>get("proNo"), "%" + kw + "%"),
         			builder.like(root.get("productEntity").get("productBrandEntity").<String>get("name"), "%" + kw + "%")
         		));        	
     }
     
     // 筛选日期
     Date date = (Date) map.get("date");
     if(date != null){
         restrictions = builder.and(restrictions, 
         		builder.greaterThanOrEqualTo(root.<Date>get("createDate"), date));
     }
    
     return findList(builder, query, restrictions, root, 0, 100000, null, null);
	}
	
	@Override
	public Page<ProducWantRepairEntity> findProductWantRepairPage(Pageable pageable, HashMap<String, Object> htMap) {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProducWantRepairEntity> criteriaQuery = criteriaBuilder.createQuery(ProducWantRepairEntity.class);

		// 设置查询ROOT
		Root<ProducWantRepairEntity> root = criteriaQuery.from(ProducWantRepairEntity.class);
		// criteriaQuery =
		// criteriaQuery.orderBy(criteriaBuilder.asc(root.get("proMarketPriceType")));

		// criteriaQuery =
		// criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("proMarketPrice")));

		String keyword = (String) htMap.get("keyword");
		long brandId = (long) htMap.get("brandId");
		int proClass = (int) htMap.get("proClass");
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions,
				criteriaBuilder.equal(root.get("productEntity").<Integer> get("isDel"), 0));
		restrictions = criteriaBuilder.and(restrictions,
				criteriaBuilder.equal(root.get("productEntity").<Integer> get("webzh"), 1));
		// 商品类型:快捷发布,客户发布,还是自营
		restrictions = criteriaBuilder.and(restrictions,
				criteriaBuilder.equal(root.get("productEntity").<Integer> get("proClass"), proClass));
		if (!keyword.isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(
					criteriaBuilder.like(root.get("productEntity").<String> get("proName"), "%" + keyword + "%"),
					// criteriaBuilder.like(root.get("productEntity").<String>get("prodNumber"),"%"
					// + keyword + "%"),
					criteriaBuilder.like(root.get("productEntity").<String> get("proNo"), "%" + keyword + "%")));
		}
		if (brandId != 0l) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(
							root.get("productEntity").<ProductBrandEntity> get("productBrandEntity").<Long> get("id"),
							brandId));
		}
		// 查找借款分页
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
}
