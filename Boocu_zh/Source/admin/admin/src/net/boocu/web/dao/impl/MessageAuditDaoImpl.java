package net.boocu.web.dao.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.dao.MessageAuditDao;

import org.springframework.stereotype.Repository;

@Repository("messageAuditDaoImpl")
public class MessageAuditDaoImpl extends BaseDaoImpl<ProductEntity, Long> implements MessageAuditDao {
	@Override
	public Page<ProductEntity> findMessageAuditPage(Pageable pageable,HashMap<String,Object> map) {
        // 获取条件构造器
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        // 创建条件查询
        CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);
        
        // 设置查询ROOT
        Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);
        //criteriaQuery = criteriaQuery.orderBy(criteriaBuilder.asc(root.get("proMarketPriceType")));      

        String keyword = (String) map.get("keyword");
        // 设置限制条件
        Predicate restrictions = criteriaBuilder.conjunction();
        
        //商品类型要大于6
        restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.gt(root.get("productType").<BigInteger>get("id"), 6));
        if(!keyword.isEmpty()){
        	restrictions = criteriaBuilder.and(restrictions,
            		criteriaBuilder.or( 
                    		criteriaBuilder.like(root.get("memberEntity").<String>get("username"),"%" + keyword + "%"),
                    		//criteriaBuilder.like(root.get("productEntity").<String>get("prodNumber"),"%" + keyword + "%"),
                    		criteriaBuilder.like(root.<String>get("proNo"),"%" + keyword + "%")
                            )
                    );
        }
        
        // 筛选日期
        Date date = (Date) map.get("date");
        String times = (String) map.get("time");
       if(!times.isEmpty()){
            restrictions = criteriaBuilder.and(restrictions, 
            		criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createDate"), date));
        }
       
       // 商品类型        
       String productTypeIdS = (String) map.get("productTypeId");
       long productTypeId = Long.parseLong(productTypeIdS);
        if(productTypeId != 0l){
        restrictions = criteriaBuilder.and(restrictions, 
        		criteriaBuilder.equal(root.<Long>get("productType"), productTypeId));
        }
        
        // 查找借款分页
       return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
    }
}
