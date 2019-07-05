package net.boocu.project.dao.impl;

import java.util.HashMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.AutoTestDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.stereotype.Repository;

@Repository("autoTestDaoImpl")
public class AutoTestDaoImpl extends BaseDaoImpl<AutoTestEntity, Long> implements AutoTestDao {

	@Override
	public Page<AutoTestEntity> findAutoTestPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		 // 获取条件构造器
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // 创建条件查询
        CriteriaQuery<AutoTestEntity> criteriaQuery = criteriaBuilder.createQuery(AutoTestEntity.class);
        
        // 设置查询ROOT
        Root<AutoTestEntity> root = criteriaQuery.from(AutoTestEntity.class);
        //criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(root.get("proMarketPriceType")));
        
        //criteriaQuery = criteriaQuery.select(root).orderBy(criteriaBuilder.desc(root.get("proMarketPrice")));
        
        String keyword = (String) htMap.get("keyword");
        long brandId = (long) htMap.get("brandId");
        int proClass = (int)htMap.get("proClass");
        // 设置限制条件
        Predicate restrictions = criteriaBuilder.conjunction();
        restrictions = criteriaBuilder.and(restrictions, 
        		criteriaBuilder.equal(root.get("productEntity").<Integer>get("isDel"), 0 ));
        //商品类型:快捷发布,客户发布,还是自营
        restrictions = criteriaBuilder.and(restrictions, 
        		criteriaBuilder.equal(root.get("productEntity").<Integer>get("proClass"), proClass ));
        if(!keyword.isEmpty()){
        	restrictions = criteriaBuilder.and(restrictions,
            		criteriaBuilder.or( 
                    		criteriaBuilder.like(root.get("productEntity").<String>get("proName"),"%" + keyword + "%"),
                    		//criteriaBuilder.like(root.get("productEntity").<String>get("prodNumber"),"%" + keyword + "%"),
                    		criteriaBuilder.like(root.get("productEntity").<String>get("proNo"),"%" + keyword + "%")
                            )
                    );
        }
        if(brandId != 0l){
        	 restrictions = criteriaBuilder.and(restrictions, 
        			 criteriaBuilder.equal(root.get("productEntity").<ProductBrandEntity>get("productBrandEntity").<Long>get("id"), brandId ));
        }
        // 查找借款分页
       return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
	
}
