package net.boocu.project.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProductSaleDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Repository;

@Repository("productSaleDaoImpl")
public class ProductSaleDaoImpl extends BaseDaoImpl<ProductSaleEntity, Long> implements ProductSaleDao {
	@Resource
	private IndustryClassService industryClassService;
	@Resource
	private ProductclassService productclassService;
	@Resource
	private ProductBrandService productBrandService;

	@Override
	public Page<ProductSaleEntity> findProductSalePage(Pageable pageable, HashMap<String, Object> htMap) {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProductSaleEntity> criteriaQuery = criteriaBuilder.createQuery(ProductSaleEntity.class);
		// 设置查询ROOT
		Root<ProductSaleEntity> root = criteriaQuery.from(ProductSaleEntity.class);
		criteriaQuery.select(root);

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

	@Override
	public Page<ProductSaleEntity> findFrontProductSalePage(Pageable pageable, Map map) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductSaleEntity> criteriaQuery = criteriaBuilder.createQuery(ProductSaleEntity.class);
		Root<ProductSaleEntity> root = criteriaQuery.from(ProductSaleEntity.class);
		criteriaQuery.select(root);
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("isDel"), 0));
		restrictions = criteriaBuilder.and(restrictions,
				criteriaBuilder.equal(root.<ProductEntity> get("productEntity").<Integer> get("webzh"), 1));
		// 行业分类
		Long[] indClassIds = (Long[]) map.get("indClass");
		if (indClassIds != null && indClassIds.length != 0) {
			for (long id : indClassIds) {
				IndustryClassEntity industryClassEntity = industryClassService.find(id);
				if (industryClassEntity != null) {
					restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(
							criteriaBuilder.equal(root.<ProductEntity> get("productEntity")
									.<IndustryClassEntity> get("industryClassEntity"), industryClassEntity),
							criteriaBuilder.like(
									root.<ProductEntity> get("productEntity")
											.<IndustryClassEntity> get("industryClassEntity").<String> get("parentid"),
									industryClassEntity.getId().toString() + "%")));
				}
			}
		}
		// 产品分类
		Long[] proClassIds = (Long[]) map.get("proClass");
		if (proClassIds != null && proClassIds.length != 0) {
			for (long id : proClassIds) {
				ProductclassEntity productclassEntity = productclassService.find(id);
				if (productclassEntity != null) {
					restrictions = criteriaBuilder.and(restrictions,
							criteriaBuilder.or(
									criteriaBuilder.equal(root.<ProductEntity> get("productEntity")
											.<ProductclassEntity> get("productclass"), productclassEntity),
									criteriaBuilder.like(
											root.<ProductEntity> get("productEntity")
													.<ProductclassEntity> get("productclass").<String> get("parentid"),
											productclassEntity.getMenuid() + "%")));
				}
			}
		}
		// 品牌
		Long[] brands = (Long[]) map.get("brands");
		if (brands != null && brands.length != 0) {
			for (long id : brands) {
				ProductBrandEntity productBrandEntity = productBrandService.find(id);
				if (productBrandEntity != null) {
					restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(
							root.<ProductEntity> get("productEntity").<ProductBrandEntity> get("productBrandEntity"),
							productBrandEntity));
				}
			}
		}

		// 关键字
		String keyword = (String) map.get("keyword");
		if (keyword != null && !keyword.trim().isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(
							// 商品名称
							criteriaBuilder.like(root.<ProductEntity> get("productEntity").<String> get("proName"),
									"%" + keyword + "%"),
							// 商品型号
							criteriaBuilder.like(root.<ProductEntity> get("productEntity").<String> get("proNo"),
									"%" + keyword + "%")));
		}

		// 是否自营
		String isSelf = (String) map.get("isSelf");
		if (isSelf != null && !isSelf.isEmpty()) {
			Predicate isSelfPredicate = null;
			if ("1".equals(isSelf)) {
				isSelfPredicate = criteriaBuilder
						.equal(root.<ProductEntity> get("productEntity").<Integer> get("proClass"), 0);
			} else {
				isSelfPredicate = criteriaBuilder
						.notEqual(root.<ProductEntity> get("productEntity").<Integer> get("proClass"), 0);
			}
			restrictions = criteriaBuilder.and(restrictions, isSelfPredicate);
		}

		// 二手还是新品
		String isNew = (String) map.get("isNew");
		if (isNew != null && !isNew.isEmpty()) {
			Predicate isNewPredicate = null;
			if ("1".equals(isNew)) {
				isNewPredicate = criteriaBuilder.equal(
						root.<ProductEntity> get("productEntity").<QualityStatusEnum> get("qualityStatus"),
						QualityStatusEnum.all);
			} else {
				isNewPredicate = criteriaBuilder.notEqual(
						root.<ProductEntity> get("productEntity").<QualityStatusEnum> get("qualityStatus"),
						QualityStatusEnum.all);
			}
			restrictions = criteriaBuilder.and(restrictions, isNewPredicate);
		}

		// 排序集合
		List<Order> orderList = new ArrayList<Order>();
		// 人气排序 todo
		String popuOrder = (String) map.get("popuOrder");
		if (popuOrder != null && !popuOrder.isEmpty()) {
			orderList.add("asc".equals(popuOrder)
					? criteriaBuilder.asc(root.<ProductEntity> get("productEntity").<Integer> get("lookTime"))
					: criteriaBuilder.desc(root.<ProductEntity> get("productEntity").<Integer> get("lookTime")));
		}
		// 价格排序
		String priceOrder = (String) map.get("priceOrder");
		if (priceOrder != null && !priceOrder.isEmpty()) {
			pageable.getSequencers()
					.add("asc".equals(priceOrder) ? Sequencer.asc("proShopPrice") : Sequencer.desc("proShopPrice"));
		}
		criteriaQuery = criteriaQuery.orderBy(orderList);

		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}

	@Override
	public Page<ProductSaleEntity> getProductSalePage(Pageable pageable, HashMap<String, Object> map) {

		String index = map.get("index").toString();
		
		 // 获取条件构造器
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ProductSaleEntity> query = 
        		builder.createQuery(ProductSaleEntity.class);
        
        Root<ProductSaleEntity> root = query.from(ProductSaleEntity.class);
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
	
	@Override
	public List<ProductSaleEntity> getSaleInfo(HashMap<String, Object> map) {
		String kw = map.get("kw").toString();
		
		 // 获取条件构造器
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ProductSaleEntity> query = 
        		builder.createQuery(ProductSaleEntity.class);
        
        Root<ProductSaleEntity> root = query.from(ProductSaleEntity.class);
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
        
        return findList(builder,query,restrictions,root,0,100000,null, null);
	}
}
