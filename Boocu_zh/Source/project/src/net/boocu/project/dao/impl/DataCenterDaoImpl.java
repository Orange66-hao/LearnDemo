package net.boocu.project.dao.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.DataCenterDao;
import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.ProductBrandService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.stereotype.Repository;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Repository("dataCenterDaoImpl")
public class DataCenterDaoImpl extends BaseDaoImpl<DataCenterEntity, Long> implements DataCenterDao {
	
	@Resource
	ProductBrandService productBrandService;
	
	@Override
	public Page<DataCenterEntity> findFrontDataCenterPage(Pageable pageable,
			Map map) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DataCenterEntity> criteriaQuery = criteriaBuilder.createQuery(DataCenterEntity.class);
		Root<DataCenterEntity> root = criteriaQuery.from(DataCenterEntity.class);
		criteriaQuery.select(root);
		//设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		//品牌
		String brands = (String)map.get("brands");
		if(brands != null && !brands.isEmpty()){
			if(!brands.equals("all")){
				long id = Long.parseLong(brands);
				System.out.println("id:::"+id);
					ProductBrandEntity productBrandEntity = productBrandService.find(id);
					if(productBrandEntity != null){
						restrictions =  criteriaBuilder.and(restrictions, 
				        				criteriaBuilder.equal(root.<ProductEntity>get("productEntity").<ProductBrandEntity>get("productBrandEntity"),productBrandEntity));
			}
			
				}
		}
		//关键字
		String keyword = (String)map.get("keyword");
		if(keyword != null && !keyword.trim().isEmpty()){
			restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.or(
					//商品名称
					criteriaBuilder.like(root.<ProductEntity>get("productEntity").<String>get("proName"), "%"+keyword+"%"),
					//文件名
					criteriaBuilder.like(root.<String>get("filename"), "%"+keyword+"%")
					));
		}
		
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
}
