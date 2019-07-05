package net.boocu.project.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.SalesDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Repository;


@Repository("salesDaoImpl")
public class SalesDaoImpl extends BaseDaoImpl<ProductEntity, Long> implements SalesDao {
	@Resource
	private IndustryClassService industryClassService;
	@Resource
	private ProductclassService productclassService;
	@Resource
	private ProductBrandService productBrandService;
	@Resource
	private ProducttypeService producttypeService;
	
	@Override
	public Page<ProductEntity> findFrontSalesPage(Pageable pageable, Map map) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);
		Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);
		criteriaQuery.select(root);
		//设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		
		pageable.getFilters().add(Filter.eq("isPromSale", 1));
		pageable.getFilters().add(Filter.eq("display", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		//关键字
		String keyword = (String)map.get("keyword");
		if(keyword != null && !keyword.trim().isEmpty()){
			System.out.println("keyword:"+keyword);
			restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.or(
					//商品名称
					criteriaBuilder.like(root.<String>get("proName"), "%"+keyword+"%"),
					//商品型号
					criteriaBuilder.like(root.<String>get("proNo"), "%"+keyword+"%")
					));
		}
		//是否自营
		String isSelf = (String)map.get("isSelf");
		if(isSelf !=null && !isSelf.isEmpty()){
			System.out.println("isSelf:"+isSelf);
			Filter filter = "1".equals(isSelf)?Filter.eq("proClass",0):Filter.ne("proClass", 0);
			pageable.getFilters().add(filter);
		}
		//二手还是新品
		String isNew = (String)map.get("isNew");
		if(isNew != null && !isNew.isEmpty()){
			Filter filter = "1".equals(isNew)?Filter.eq("qualityStatus",  QualityStatusEnum.all):Filter.ne("qualityStatus",  QualityStatusEnum.all);
			pageable.getFilters().add(filter);
		}
		//排序集合
		List<Sequencer> orderList = new ArrayList<Sequencer>();
		//人气排序 
		String popuOrder = (String)map.get("popuOrder");
		
		if(popuOrder != null && !popuOrder.isEmpty()){
			orderList.add("asc".equals(popuOrder)?Sequencer.asc("lookTime"):Sequencer.desc("lookTime"));
		}
		//价格排序
		String priceOrder = (String)map.get("priceOrder");
		if(priceOrder != null && !priceOrder.isEmpty()){
			//pageable.getSequencers().add("asc".equals(priceOrder)?Sequencer.asc("salesPrice"):Sequencer.desc("salesPrice"));
			orderList.add("asc".equals(priceOrder)?Sequencer.asc("salesRealPrice"):Sequencer.desc("salesRealPrice"));
		}
		//更新时间排序
		String timeOrder = (String)map.get("timeOrder");
		if(timeOrder != null && !timeOrder.isEmpty()){
			//pageable.getSequencers().add("asc".equals(timeOrder)?Sequencer.asc("modifyDate"):Sequencer.desc("modifyDate"));
			orderList.add("asc".equals(timeOrder)?Sequencer.asc("modifyDate"):Sequencer.desc("modifyDate"));
		}
		pageable.setSequencers(orderList);
		/*		//是否自营 
		String isSelf = (String)map.get("isSelf");
		if(isSelf != null && !isSelf.isEmpty()){
			System.out.println("isNew:"+isSelf);
			Predicate isSelfPredicate = null;
			if("1".equals(isSelf)){
				isSelfPredicate = criteriaBuilder.equal(root.<Integer>get("proClass"), 0);
			}else{
				isSelfPredicate = criteriaBuilder.notEqual(root.<Integer>get("proClass"), 0);
				isSelfPredicate = criteriaBuilder.or(criteriaBuilder.notEqual(root.<Integer>get("proClass"), 0),
						criteriaBuilder.isNull((root.<Integer>get("proClass"))));
			}
			restrictions = criteriaBuilder.and(restrictions,isSelfPredicate);
		}
		
		//二手还是新品
		String isNew = (String)map.get("isNew");
		if(isNew != null && !isNew.isEmpty()){
			Predicate isNewPredicate = null;
			System.out.println("isNew:"+isNew);
			if("1".equals(isNew)){
				isNewPredicate =criteriaBuilder.or(criteriaBuilder.equal(root.<QualityStatusEnum>get("qualityStatus"), QualityStatusEnum.all)
						,criteriaBuilder.isNull(root.<QualityStatusEnum>get("qualityStatus"))) ;
			}else{
				isNewPredicate =criteriaBuilder.notEqual(root.<QualityStatusEnum>get("qualityStatus"), QualityStatusEnum.all) ;
			}
			restrictions = criteriaBuilder.and(restrictions,isNewPredicate);
			
		}
		//排序集合
		List<Order> orderList = new ArrayList<Order>();
		//人气排序 todo
		String popuOrder = (String)map.get("popuOrder");
		//todo
		if(popuOrder != null && !popuOrder.isEmpty()){
			orderList.add("asc".equals(popuOrder)?criteriaBuilder.asc(root.<Integer>get("lookTime")):criteriaBuilder.desc(root.<Integer>get("lookTime")));
		}
		//价格排序
		String priceOrder = (String)map.get("priceOrder");
		if(priceOrder != null && !priceOrder.isEmpty()){
			//pageable.getSequencers().add("asc".equals(priceOrder)?Sequencer.asc("salesPrice"):Sequencer.desc("salesPrice"));
			orderList.add("asc".equals(priceOrder)?criteriaBuilder.asc(root.<BigDecimal>get("realPrice")):criteriaBuilder.desc(root.<BigDecimal>get("realPrice")));
		}
		//更新时间排序
		String timeOrder = (String)map.get("timeOrder");
		if(timeOrder != null && !timeOrder.isEmpty()){
			//pageable.getSequencers().add("asc".equals(timeOrder)?Sequencer.asc("modifyDate"):Sequencer.desc("modifyDate"));
			orderList.add("asc".equals(timeOrder)?criteriaBuilder.asc(root.<Date>get("modifyDate")):criteriaBuilder.desc(root.<Date>get("modifyDate")));
		}
		criteriaQuery = criteriaQuery.orderBy(orderList).where(restrictions);
		TypedQuery query = this.entityManager.createQuery(criteriaQuery)
				.setFlushMode(FlushModeType.COMMIT);
		query.setFirstResult((pageable.getPageNumber().intValue() - 1)
				* pageable.getPageSize().intValue());
		query.setMaxResults(pageable.getPageSize().intValue());

		List results = query.getResultList();*/

/*		Long total = counts(criteriaBuilder, criteriaQuery, root);
		return new Page(pageable, results, total);*/
        // 查找借款分页
       return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
/*	public Long counts(CriteriaBuilder criteriaBuilder,
			CriteriaQuery<ProductEntity> criteriaQuery, Root<ProductEntity> root) {
		CriteriaQuery countCriteriaQuery = criteriaBuilder
				.createQuery(Long.class);
		for (Iterator iterator = criteriaQuery.getRoots().iterator(); iterator
				.hasNext();) {
			Root countRoot = (Root) iterator.next();
			Root destCountRoot = countCriteriaQuery.from(countRoot
					.getJavaType());
			destCountRoot.alias(getAlias(countRoot));

			copyJoins(countRoot, destCountRoot);
		}

		countCriteriaQuery.select(criteriaBuilder.count(root));

		if (criteriaQuery.getGroupList() != null) {
			countCriteriaQuery.groupBy(criteriaQuery.getGroupList());
		}

		if (criteriaQuery.getGroupRestriction() != null) {
			countCriteriaQuery.having(criteriaQuery.getGroupRestriction());
		}

		if (criteriaQuery.getRestriction() != null) {
			countCriteriaQuery.where(criteriaQuery.getRestriction());
		}

		return ((Long) this.entityManager.createQuery(countCriteriaQuery)
				.setFlushMode(FlushModeType.COMMIT).getSingleResult());
	}
	private void copyJoins(From<?, ?> from, From<?, ?> to) {
		for (Iterator iterator = from.getJoins().iterator(); iterator.hasNext();) {
			Join join = (Join) iterator.next();
			Join toJoin = to.join(join.getAttribute().getName(),
					join.getJoinType());

			toJoin.alias(getAlias(join));
			copyJoins(join, toJoin);
		}
		for (Iterator iterator = from.getFetches().iterator(); iterator
				.hasNext();) {
			Fetch fetch = (Fetch) iterator.next();
			Fetch toFetch = to.fetch(fetch.getAttribute().getName());

			copyFetches(fetch, toFetch);
		}
	}
	private void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
		for (Iterator iterator = from.getFetches().iterator(); iterator
				.hasNext();) {
			Fetch fetch = (Fetch) iterator.next();
			Fetch toFetch = to.fetch(fetch.getAttribute().getName());
			copyFetches(fetch, toFetch);
		}
	}
	private synchronized String getAlias(Selection<?> selection) {
		if (selection == null) {
			return null;
		}
		String alias = selection.getAlias();
		if (alias == null) {
			if (aliasCount >= 1000L) {
				aliasCount = 0L;
			}
			alias = "ibsGeneratedAlias" + (aliasCount++);
			selection.alias(alias);
		}
		return alias;
	}*/
	@Override
	public Page<ProductEntity> findSalesPage(Pageable pageable,HashMap<String, Object> map){
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
		if(keyword != null && !keyword.trim().isEmpty()){
			restrictions = criteriaBuilder.and(restrictions,criteriaBuilder.or(
					//会员名和型号
					criteriaBuilder.like(root.get("memberEntity").<String>get("username"),"%" + keyword + "%"),
					criteriaBuilder.like(root.<String>get("proNo"), "%"+keyword+"%")		
					));
		}
        
        // 查找借款分页
       return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
    }
	
}
