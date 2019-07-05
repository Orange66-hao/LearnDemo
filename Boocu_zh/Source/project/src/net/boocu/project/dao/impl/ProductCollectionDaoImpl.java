package net.boocu.project.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProductCollectionDao;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductCollectionEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

@Repository("collectionDaoImpl")
public class ProductCollectionDaoImpl extends BaseDaoImpl<ProductCollectionEntity, Long>
		implements ProductCollectionDao {
	@Resource
	private ProductBrandService productBrandService;
	@Resource
	private ProducttypeService producttypeService;
	@Resource
	private MemberService memberService;

	@Override
	public Page<ProductCollectionEntity> findListProductPage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductCollectionEntity> criteriaQuery = criteriaBuilder
				.createQuery(ProductCollectionEntity.class);
		Root<ProductCollectionEntity> root = criteriaQuery.from(ProductCollectionEntity.class);

		criteriaQuery.select(root);
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("isDel"), 0));
		restrictions = criteriaBuilder.and(restrictions,
				criteriaBuilder.equal(root.<MemberEntity> get("memberEntity"), memberService.getCurrent()));
		// 获取当前日期
		Long selOType = (Long) map.get("selOType");
		Long proBrand = (Long) map.get("proBrand");
		Long selOProNo = (Long) map.get("selOProNo");

		if (selOType != null && selOType != 0) {
			ProducttypeEntity typeEntity = producttypeService.find(selOType);
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(
					root.<ProductEntity> get("productEntity").<ProducttypeEntity> get("productType"), typeEntity));
			if (proBrand != null && proBrand != 0) {
				ProductBrandEntity brandEntity = productBrandService.find(proBrand);
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(
						root.<ProductEntity> get("productEntity").<ProductBrandEntity> get("productBrandEntity"),
						brandEntity));
			}
			if (selOProNo != null && selOProNo != 0) {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
						.equal(root.<ProductEntity> get("productEntity").<ProductBrandEntity> get("proNo"), selOProNo));
			}
		}
		// 关键字
		String keyword = (String) map.get("keyword");
		if (keyword != null && !keyword.trim().isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder
					.like(root.<ProductEntity> get("productEntity").<String> get("proName"), "%" + keyword + "%"));
			restrictions = criteriaBuilder.or(restrictions, criteriaBuilder
					.like(root.<ProductEntity> get("productEntity").<String> get("proNo"), "%" + keyword + "%"));
			/* flist.add(Filter.like("proNo", "%" + keyword + "%")); */
		}
		List<Sequencer> orderList = new ArrayList<Sequencer>();
		/*
		 * // 人气排序 String popuOrder = (String) map.get("popuOrder"); if
		 * (popuOrder != null && !popuOrder.isEmpty()) {
		 * orderList.add("asc".equals(popuOrder) ?
		 * Sequencer.asc("collectionCount") :
		 * Sequencer.desc("collectionCount")); } // 价格排序todo String priceOrder =
		 * (String) map.get("priceOrder"); if (priceOrder != null &&
		 * !priceOrder.isEmpty()) { orderList.add("asc".equals(popuOrder) ?
		 * Sequencer.asc("price") : Sequencer.desc("price")); }
		 */
		// 时间排序
		String dateOrder = (String) map.get("dateOrder");
		orderList.add(Sequencer.desc("createDate"));
		pageable.setSequencers(orderList);
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}

}
