package net.boocu.project.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.ProductDao;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.CalibrationEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProductTestEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.project.entity.RequireTestEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Repository;

@Repository("productDaoImpl")
public class ProductDaoImpl extends BaseDaoImpl<ProductEntity, Long> implements ProductDao {

	@Resource
	private IndustryClassService industryClassService;
	@Resource
	private ProductclassService productclassService;
	@Resource
	private ProductBrandService productBrandService;
	@Resource
	private ProducttypeService producttypeService;

	@Override
	public Page<ProductEntity> findFrontProductPage(Pageable pageable, Map map) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);
		Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);

		// 服务类型
		Long[] serTypeIds = (Long[]) map.get("serTypeIds");

		criteriaQuery.select(root);
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("isDel"), 0));

		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("apprStatus"), 1));
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("webzh"), 1));
		// 获取当前日期
		Date date = new Date();

		// pageable.getFilters().add(Filter.eq("isDel", 0));
		// pageable.getFilters().add(Filter.eq("apprStatus", 1));

		// 有效期
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.ge("inforValidity", date));
		filters.add(Filter.eq("longTerm", 1));

		pageable.getFilters().add(Filter.or(filters));
		pageable.getFilters().add(Filter.eq("proownaudit", 1));

		if (serTypeIds != null && serTypeIds.length != 0) {
			List<Filter> fList = new ArrayList<Filter>();
			for (long id : serTypeIds) {
				ProducttypeEntity producttypeEntity = producttypeService.find(id);
				if (producttypeEntity != null) {
					fList.add(Filter.eq("productType", producttypeEntity));
				}
			}
			pageable.getFilters().add(Filter.or(fList));
		}

		// 行业分类
		Long[] indClassIds = (Long[]) map.get("indClass");
		if (indClassIds != null && indClassIds.length != 0) {
			Predicate predicate = null;
			for (int i = 0; i < indClassIds.length; i++) {
				IndustryClassEntity industryClassEntity = industryClassService.find(indClassIds[i]);
				if (industryClassEntity != null) {
					if (i == 0) {
						// 单选
						predicate = criteriaBuilder.or(criteriaBuilder.like(root.<String> get("industryClass"), "%"
								+ industryClassEntity.getName()
								+ "%")/*
										 * , criteriaBuilder.like(root.<
										 * String>get("industryClass"),
										 * industryClassEntity.getName() +"%" )
										 */);
					} else {
						// 多选
						predicate = criteriaBuilder.or(predicate, criteriaBuilder
								.like(root.<String> get("industryClass"), "%" + industryClassEntity.getName() + "%"));
						// predicate = criteriaBuilder.or(predicate,
						// criteriaBuilder.like(root.<String>get("industryClass").<String>get("parentid"),
						// industryClassEntity.getName()+"%" ));

					}
				}
			}
			restrictions = criteriaBuilder.and(restrictions, predicate);

		}
		// 产品分类
		Long[] proClassIds = (Long[]) map.get("proClass");
		if (proClassIds != null && proClassIds.length != 0) {
			Predicate predicate = null;
			for (int i = 0; i < proClassIds.length; i++) {
				ProductclassEntity productclassEntity = productclassService.find(proClassIds[i]);
				if (productclassEntity != null) {
					if (i == 0) {
						Subquery<ProductclassEntity> proClass_sq = criteriaQuery.subquery(ProductclassEntity.class);
						Root<ProductclassEntity> proClass_root = proClass_sq.from(ProductclassEntity.class);
						Predicate restrictions4 = criteriaBuilder.conjunction();
						restrictions4 = criteriaBuilder.and(restrictions4,
								criteriaBuilder.equal(root.<ProductclassEntity> get("productclass"), proClass_root));
						Predicate predicate_sb = criteriaBuilder
								.or(criteriaBuilder.equal(proClass_root, productclassEntity), criteriaBuilder.like(
										proClass_root.<String> get("parentid"), productclassEntity.getMenuid() + "%"));
						restrictions4 = criteriaBuilder.and(restrictions4, predicate_sb);
						proClass_sq.where(restrictions4).select(proClass_root);
						predicate = criteriaBuilder.exists(proClass_sq);

						// 自动化测试
						Subquery<AutoTestEntity> autoTest_sq = criteriaQuery.subquery(AutoTestEntity.class);
						Root<AutoTestEntity> autoTest_root = autoTest_sq.from(AutoTestEntity.class);
						Subquery<InstrumentEntity> instrument_sq = criteriaQuery.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> subRoot = instrument_sq.from(InstrumentEntity.class);
						Predicate restrictions2 = criteriaBuilder.conjunction();
						Predicate restrictions3 = criteriaBuilder.conjunction();
						restrictions2 = criteriaBuilder.and(restrictions2,
								criteriaBuilder.equal(subRoot.<AutoTestEntity> get("autoTest"), autoTest_root));
						restrictions2 = criteriaBuilder.and(restrictions2, criteriaBuilder.or(
								criteriaBuilder.equal(subRoot.<ProductclassEntity> get("productclass"),
										productclassEntity),
								criteriaBuilder.like(
										subRoot.<ProductclassEntity> get("productclass").<String> get("parentid"),
										productclassEntity.getMenuid() + "%")));
						instrument_sq.where(restrictions2).select(subRoot);
						restrictions3 = criteriaBuilder.and(restrictions3,
								criteriaBuilder.equal(root, autoTest_root.get("productEntity")));
						restrictions3 = criteriaBuilder.and(restrictions3, criteriaBuilder.exists(instrument_sq));
						autoTest_sq.where(restrictions3).select(autoTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(autoTest_sq));
						// 自动化测试需求
						Subquery<ProjectNeedEntity> projectNeed_sq = criteriaQuery.subquery(ProjectNeedEntity.class);
						Root<ProjectNeedEntity> projectNeed_root = projectNeed_sq.from(ProjectNeedEntity.class);
						Subquery<InstrumentEntity> projectNeed_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> projectNeed_subRoot = projectNeed_instrument_sq
								.from(InstrumentEntity.class);
						Predicate projectNeed_restrictions2 = criteriaBuilder.conjunction();
						Predicate projectNeed_restrictions3 = criteriaBuilder.conjunction();
						projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2, criteriaBuilder
								.equal(projectNeed_subRoot.<ProjectNeedEntity> get("projectNeed"), projectNeed_root));
						projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(projectNeed_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												projectNeed_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));
						projectNeed_instrument_sq.where(projectNeed_restrictions2).select(projectNeed_subRoot);
						projectNeed_restrictions3 = criteriaBuilder.and(projectNeed_restrictions3,
								criteriaBuilder.equal(root, projectNeed_root.get("productEntity")));
						projectNeed_restrictions3 = criteriaBuilder.and(projectNeed_restrictions3,
								criteriaBuilder.exists(projectNeed_instrument_sq));
						projectNeed_sq.where(projectNeed_restrictions3).select(projectNeed_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(projectNeed_sq));
						// 产品测试
						Subquery<ProductTestEntity> productTest_sq = criteriaQuery.subquery(ProductTestEntity.class);
						Root<ProductTestEntity> productTest_root = productTest_sq.from(ProductTestEntity.class);
						Subquery<InstrumentEntity> productTest_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> productTest_subRoot = productTest_instrument_sq
								.from(InstrumentEntity.class);
						Predicate productTest_restrictions2 = criteriaBuilder.conjunction();
						Predicate productTest_restrictions3 = criteriaBuilder.conjunction();
						productTest_restrictions2 = criteriaBuilder.and(productTest_restrictions2, criteriaBuilder
								.equal(productTest_subRoot.<ProductTestEntity> get("productTest"), productTest_root));
						productTest_restrictions2 = criteriaBuilder.and(productTest_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(productTest_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												productTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));
						productTest_instrument_sq.where(productTest_restrictions2).select(productTest_subRoot);
						productTest_restrictions3 = criteriaBuilder.and(productTest_restrictions3,
								criteriaBuilder.equal(root, productTest_root.get("productEntity")));
						productTest_restrictions3 = criteriaBuilder.and(productTest_restrictions3,
								criteriaBuilder.exists(productTest_instrument_sq));
						productTest_sq.where(productTest_restrictions3).select(productTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(productTest_sq));
						// 测试需求
						Subquery<RequireTestEntity> requireTest_sq = criteriaQuery.subquery(RequireTestEntity.class);
						Root<RequireTestEntity> requireTest_root = requireTest_sq.from(RequireTestEntity.class);
						Subquery<InstrumentEntity> requireTest_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> requireTest_subRoot = requireTest_instrument_sq
								.from(InstrumentEntity.class);
						Predicate requireTest_restrictions2 = criteriaBuilder.conjunction();
						Predicate requireTest_restrictions3 = criteriaBuilder.conjunction();
						requireTest_restrictions2 = criteriaBuilder.and(requireTest_restrictions2, criteriaBuilder
								.equal(requireTest_subRoot.<RequireTestEntity> get("requireTest"), requireTest_root));
						requireTest_restrictions2 = criteriaBuilder.and(requireTest_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(requireTest_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												requireTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));
						requireTest_instrument_sq.where(requireTest_restrictions2).select(requireTest_subRoot);
						requireTest_restrictions3 = criteriaBuilder.and(requireTest_restrictions3,
								criteriaBuilder.equal(root, requireTest_root.get("productEntity")));
						requireTest_restrictions3 = criteriaBuilder.and(requireTest_restrictions3,
								criteriaBuilder.exists(requireTest_instrument_sq));
						requireTest_sq.where(requireTest_restrictions3).select(requireTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(requireTest_sq));
						// 自动化测试方案
						Subquery<CalibrationEntity> calibration_sq = criteriaQuery.subquery(CalibrationEntity.class);
						Root<CalibrationEntity> calibration_root = calibration_sq.from(CalibrationEntity.class);
						Subquery<InstrumentEntity> calibration_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> calibration_subRoot = calibration_instrument_sq
								.from(InstrumentEntity.class);
						Predicate calibration_restrictions2 = criteriaBuilder.conjunction();
						Predicate calibration_restrictions3 = criteriaBuilder.conjunction();
						calibration_restrictions2 = criteriaBuilder.and(calibration_restrictions2, criteriaBuilder
								.equal(calibration_subRoot.<CalibrationEntity> get("calibration"), calibration_root));
						calibration_restrictions2 = criteriaBuilder.and(calibration_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(calibration_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												calibration_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));
						calibration_instrument_sq.where(calibration_restrictions2).select(calibration_subRoot);
						calibration_restrictions3 = criteriaBuilder.and(calibration_restrictions3,
								criteriaBuilder.equal(root, calibration_root.get("productEntity")));
						calibration_restrictions3 = criteriaBuilder.and(calibration_restrictions3,
								criteriaBuilder.exists(calibration_instrument_sq));
						calibration_sq.where(calibration_restrictions3).select(calibration_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(calibration_sq));
					} else {
						Subquery<ProductclassEntity> proClass_sq = criteriaQuery.subquery(ProductclassEntity.class);
						Root<ProductclassEntity> proClass_root = proClass_sq.from(ProductclassEntity.class);
						Predicate restrictions4 = criteriaBuilder.conjunction();
						restrictions4 = criteriaBuilder.and(restrictions4,
								criteriaBuilder.equal(root.<ProductclassEntity> get("productclass"), proClass_root));
						// Predicate predicate_sb =criteriaBuilder.or(
						// criteriaBuilder.equal(proClass_root,productclassEntity),
						// criteriaBuilder.like(proClass_root.<String>get("parentid"),
						// productclassEntity.getMenuid()+"%" ));
						Predicate predicate_sb = criteriaBuilder.or(predicate,
								criteriaBuilder.equal(proClass_root, productclassEntity));
						predicate_sb = criteriaBuilder.or(predicate, criteriaBuilder
								.like(proClass_root.<String> get("parentid"), productclassEntity.getMenuid() + "%"));
						restrictions4 = criteriaBuilder.and(restrictions4, predicate_sb);
						proClass_sq.where(restrictions4).select(proClass_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(proClass_sq));

						// predicate =criteriaBuilder.or(predicate,
						// criteriaBuilder.equal(root.<ProductclassEntity>get("productclass"),productclassEntity));
						// predicate = criteriaBuilder.or(predicate,
						// criteriaBuilder.like(root.<ProductclassEntity>get("productclass").<String>get("parentid"),
						// productclassEntity.getMenuid()+"%" ));

						// 自动化测试
						Subquery<AutoTestEntity> autoTest_sq = criteriaQuery.subquery(AutoTestEntity.class);
						Root<AutoTestEntity> autoTest_root = autoTest_sq.from(AutoTestEntity.class);
						Subquery<InstrumentEntity> instrument_sq = criteriaQuery.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> autoTest_subRoot = instrument_sq.from(InstrumentEntity.class);
						Predicate autoTest_restrictions2 = criteriaBuilder.conjunction();
						Predicate autoTest_restrictions3 = criteriaBuilder.conjunction();
						autoTest_restrictions2 = criteriaBuilder.and(autoTest_restrictions2, criteriaBuilder
								.equal(autoTest_subRoot.<AutoTestEntity> get("autoTest"), autoTest_root));
						autoTest_restrictions2 = criteriaBuilder
								.and(autoTest_restrictions2,
										criteriaBuilder
												.or(criteriaBuilder
														.equal(autoTest_subRoot
																.<ProductclassEntity> get("productclass"),
																productclassEntity),
														criteriaBuilder.like(
																autoTest_subRoot
																		.<ProductclassEntity> get("productclass")
																		.<String> get("parentid"),
																productclassEntity.getMenuid() + "%")));

						autoTest_restrictions2 = criteriaBuilder.or(autoTest_restrictions2, criteriaBuilder
								.equal(autoTest_subRoot.<ProductclassEntity> get("productclass"), productclassEntity));
						autoTest_restrictions2 = criteriaBuilder.or(autoTest_restrictions2, criteriaBuilder.like(
								autoTest_subRoot.<ProductclassEntity> get("productclass").<String> get("parentid"),
								productclassEntity.getMenuid() + "%"));
						instrument_sq.where(autoTest_restrictions2).select(autoTest_subRoot);
						autoTest_restrictions3 = criteriaBuilder.and(autoTest_restrictions3,
								criteriaBuilder.equal(root, autoTest_root.get("productEntity")));
						autoTest_restrictions3 = criteriaBuilder.and(autoTest_restrictions3,
								criteriaBuilder.exists(instrument_sq));
						autoTest_sq.where(autoTest_restrictions3).select(autoTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(autoTest_sq));
						// 测试方案
						Subquery<ProjectNeedEntity> projectNeed_sq = criteriaQuery.subquery(ProjectNeedEntity.class);
						Root<ProjectNeedEntity> projectNeed_root = projectNeed_sq.from(ProjectNeedEntity.class);
						Subquery<InstrumentEntity> projectNeed_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> projectNeed_subRoot = projectNeed_instrument_sq
								.from(InstrumentEntity.class);
						Predicate projectNeed_restrictions2 = criteriaBuilder.conjunction();
						Predicate projectNeed_restrictions3 = criteriaBuilder.conjunction();
						projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2, criteriaBuilder
								.equal(projectNeed_subRoot.<ProjectNeedEntity> get("projectNeed"), projectNeed_root));
						projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(projectNeed_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												projectNeed_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));
						projectNeed_restrictions2 = criteriaBuilder.or(projectNeed_restrictions2, criteriaBuilder.equal(
								projectNeed_subRoot.<ProductclassEntity> get("productclass"), productclassEntity));
						projectNeed_restrictions2 = criteriaBuilder
								.or(projectNeed_restrictions2,
										criteriaBuilder.like(
												projectNeed_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%"));
						projectNeed_instrument_sq.where(projectNeed_restrictions2).select(projectNeed_subRoot);
						projectNeed_restrictions3 = criteriaBuilder.and(projectNeed_restrictions3,
								criteriaBuilder.equal(root, projectNeed_root.get("productEntity")));
						projectNeed_restrictions3 = criteriaBuilder.and(projectNeed_restrictions3,
								criteriaBuilder.exists(projectNeed_instrument_sq));
						projectNeed_sq.where(projectNeed_restrictions3).select(projectNeed_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(projectNeed_sq));
						// 产品测试
						Subquery<ProductTestEntity> productTest_sq = criteriaQuery.subquery(ProductTestEntity.class);
						Root<ProductTestEntity> productTest_root = productTest_sq.from(ProductTestEntity.class);
						Subquery<InstrumentEntity> productTest_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> productTest_subRoot = productTest_instrument_sq
								.from(InstrumentEntity.class);
						Predicate productTest_restrictions2 = criteriaBuilder.conjunction();
						Predicate productTest_restrictions3 = criteriaBuilder.conjunction();
						productTest_restrictions2 = criteriaBuilder.and(productTest_restrictions2, criteriaBuilder
								.equal(productTest_subRoot.<ProductTestEntity> get("productTest"), productTest_root));
						productTest_restrictions2 = criteriaBuilder.and(productTest_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(productTest_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												productTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));

						productTest_restrictions2 = criteriaBuilder.or(productTest_restrictions2, criteriaBuilder.equal(
								productTest_subRoot.<ProductclassEntity> get("productclass"), productclassEntity));
						productTest_restrictions2 = criteriaBuilder
								.or(productTest_restrictions2,
										criteriaBuilder.like(
												productTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%"));
						productTest_instrument_sq.where(productTest_restrictions2).select(productTest_subRoot);
						productTest_restrictions3 = criteriaBuilder.and(productTest_restrictions3,
								criteriaBuilder.equal(root, productTest_root.get("productEntity")));
						productTest_restrictions3 = criteriaBuilder.and(productTest_restrictions3,
								criteriaBuilder.exists(productTest_instrument_sq));
						productTest_sq.where(productTest_restrictions3).select(productTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(productTest_sq));
						// 测试需求
						Subquery<RequireTestEntity> requireTest_sq = criteriaQuery.subquery(RequireTestEntity.class);
						Root<RequireTestEntity> requireTest_root = requireTest_sq.from(RequireTestEntity.class);
						Subquery<InstrumentEntity> requireTest_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> requireTest_subRoot = requireTest_instrument_sq
								.from(InstrumentEntity.class);
						Predicate requireTest_restrictions2 = criteriaBuilder.conjunction();
						Predicate requireTest_restrictions3 = criteriaBuilder.conjunction();
						requireTest_restrictions2 = criteriaBuilder.and(requireTest_restrictions2, criteriaBuilder
								.equal(requireTest_subRoot.<RequireTestEntity> get("requireTest"), requireTest_root));
						requireTest_restrictions2 = criteriaBuilder.and(requireTest_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(requireTest_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												requireTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));

						requireTest_restrictions2 = criteriaBuilder.or(requireTest_restrictions2, criteriaBuilder.equal(
								requireTest_subRoot.<ProductclassEntity> get("productclass"), productclassEntity));
						requireTest_restrictions2 = criteriaBuilder
								.or(requireTest_restrictions2,
										criteriaBuilder.like(
												requireTest_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%"));
						requireTest_instrument_sq.where(requireTest_restrictions2).select(requireTest_subRoot);
						requireTest_restrictions3 = criteriaBuilder.and(requireTest_restrictions3,
								criteriaBuilder.equal(root, requireTest_root.get("productEntity")));
						requireTest_restrictions3 = criteriaBuilder.and(requireTest_restrictions3,
								criteriaBuilder.exists(requireTest_instrument_sq));
						requireTest_sq.where(requireTest_restrictions3).select(requireTest_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(requireTest_sq));
						// 自动化测试方案
						Subquery<CalibrationEntity> calibration_sq = criteriaQuery.subquery(CalibrationEntity.class);
						Root<CalibrationEntity> calibration_root = calibration_sq.from(CalibrationEntity.class);
						Subquery<InstrumentEntity> calibration_instrument_sq = criteriaQuery
								.subquery(InstrumentEntity.class);
						Root<InstrumentEntity> calibration_subRoot = calibration_instrument_sq
								.from(InstrumentEntity.class);
						Predicate calibration_restrictions2 = criteriaBuilder.conjunction();
						Predicate calibration_restrictions3 = criteriaBuilder.conjunction();
						calibration_restrictions2 = criteriaBuilder.and(calibration_restrictions2, criteriaBuilder
								.equal(calibration_subRoot.<CalibrationEntity> get("calibration"), calibration_root));
						calibration_restrictions2 = criteriaBuilder.and(calibration_restrictions2,
								criteriaBuilder.or(
										criteriaBuilder
												.equal(calibration_subRoot.<ProductclassEntity> get("productclass"),
														productclassEntity),
										criteriaBuilder.like(
												calibration_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%")));

						calibration_restrictions2 = criteriaBuilder.or(calibration_restrictions2, criteriaBuilder.equal(
								calibration_subRoot.<ProductclassEntity> get("productclass"), productclassEntity));
						calibration_restrictions2 = criteriaBuilder
								.or(calibration_restrictions2,
										criteriaBuilder.like(
												calibration_subRoot.<ProductclassEntity> get("productclass")
														.<String> get("parentid"),
												productclassEntity.getMenuid() + "%"));
						calibration_instrument_sq.where(calibration_restrictions2).select(calibration_subRoot);
						calibration_restrictions3 = criteriaBuilder.and(calibration_restrictions3,
								criteriaBuilder.equal(root, calibration_root.get("productEntity")));
						calibration_restrictions3 = criteriaBuilder.and(calibration_restrictions3,
								criteriaBuilder.exists(calibration_instrument_sq));
						calibration_sq.where(calibration_restrictions3).select(calibration_root);
						predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(calibration_sq));
					}
				}
			}
			restrictions = criteriaBuilder.and(restrictions, predicate);
		}
		// 品牌
		Long[] brands = (Long[]) map.get("brands");
		if (brands != null && brands.length != 0) {
			Predicate predicate = null;
			// List<Filter> fList = new ArrayList<Filter>();
			for (long brandid : brands) {
				ProductBrandEntity productBrandEntity = productBrandService.find(brands[0]);
				if (productBrandEntity != null) {
					// fList.add(Filter.eq("productBrandEntity", brandid));
					if (predicate == null) {
						predicate = criteriaBuilder.equal(root.<ProductBrandEntity> get("productBrandEntity"),
								productBrandEntity);
					} else {
						predicate = criteriaBuilder.or(predicate, criteriaBuilder
								.equal(root.<ProductBrandEntity> get("productBrandEntity"), productBrandEntity));
					}
					// 自动化测试方案
					Subquery<AutoTestEntity> autoTest_sq = criteriaQuery.subquery(AutoTestEntity.class);
					Root<AutoTestEntity> autoTest_root = autoTest_sq.from(AutoTestEntity.class);
					Subquery<InstrumentEntity> autoTest_instrument_sq = criteriaQuery.subquery(InstrumentEntity.class);
					Root<InstrumentEntity> autoTest_subRoot = autoTest_instrument_sq.from(InstrumentEntity.class);
					Predicate autoTest_restrictions2 = criteriaBuilder.conjunction();
					Predicate autoTest_restrictions3 = criteriaBuilder.conjunction();
					autoTest_restrictions2 = criteriaBuilder.and(autoTest_restrictions2,
							criteriaBuilder.equal(autoTest_subRoot.<AutoTestEntity> get("autoTest"), autoTest_root));
					autoTest_restrictions2 = criteriaBuilder.and(autoTest_restrictions2, criteriaBuilder.equal(
							autoTest_subRoot.<ProductclassEntity> get("productBrandEntity"), productBrandEntity));
					autoTest_instrument_sq.where(autoTest_restrictions2).select(autoTest_subRoot);
					autoTest_restrictions3 = criteriaBuilder.equal(root, autoTest_root.get("productEntity"));
					autoTest_restrictions3 = criteriaBuilder.and(autoTest_restrictions3,
							criteriaBuilder.exists(autoTest_instrument_sq));
					autoTest_sq.where(autoTest_restrictions3).select(autoTest_root);
					predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(autoTest_sq));
					// 方案需求
					Subquery<ProjectNeedEntity> projectNeed_sq = criteriaQuery.subquery(ProjectNeedEntity.class);
					Root<ProjectNeedEntity> projectNeed_root = projectNeed_sq.from(ProjectNeedEntity.class);
					Subquery<InstrumentEntity> projectNeed_instrument_sq = criteriaQuery
							.subquery(InstrumentEntity.class);
					Root<InstrumentEntity> projectNeed_subRoot = projectNeed_instrument_sq.from(InstrumentEntity.class);
					Predicate projectNeed_restrictions2 = criteriaBuilder.conjunction();
					Predicate projectNeed_restrictions3 = criteriaBuilder.conjunction();
					projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2, criteriaBuilder
							.equal(projectNeed_subRoot.<ProjectNeedEntity> get("projectNeed"), projectNeed_root));
					projectNeed_restrictions2 = criteriaBuilder.and(projectNeed_restrictions2, criteriaBuilder.equal(
							projectNeed_subRoot.<ProductclassEntity> get("productBrandEntity"), productBrandEntity));
					projectNeed_instrument_sq.where(projectNeed_restrictions2).select(projectNeed_subRoot);
					projectNeed_restrictions3 = criteriaBuilder.equal(root, projectNeed_root.get("productEntity"));
					projectNeed_restrictions3 = criteriaBuilder.and(projectNeed_restrictions3,
							criteriaBuilder.exists(projectNeed_instrument_sq));
					projectNeed_sq.where(projectNeed_restrictions3).select(projectNeed_root);
					predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(projectNeed_sq));
					// 产品测试
					Subquery<ProductTestEntity> productTestEntity_sq = criteriaQuery.subquery(ProductTestEntity.class);
					Root<ProductTestEntity> productTestEntity_root = productTestEntity_sq.from(ProductTestEntity.class);
					Subquery<InstrumentEntity> productTestEntity_instrument_sq = criteriaQuery
							.subquery(InstrumentEntity.class);
					Root<InstrumentEntity> productTestEntity_subRoot = productTestEntity_instrument_sq
							.from(InstrumentEntity.class);
					Predicate productTestEntity_restrictions2 = criteriaBuilder.conjunction();
					Predicate productTestEntity_restrictions3 = criteriaBuilder.conjunction();
					productTestEntity_restrictions2 = criteriaBuilder.and(productTestEntity_restrictions2,
							criteriaBuilder.equal(productTestEntity_subRoot.<ProductTestEntity> get("productTest"),
									productTestEntity_root));
					productTestEntity_restrictions2 = criteriaBuilder.and(productTestEntity_restrictions2,
							criteriaBuilder.equal(
									productTestEntity_subRoot.<ProductclassEntity> get("productBrandEntity"),
									productBrandEntity));
					productTestEntity_instrument_sq.where(productTestEntity_restrictions2)
							.select(productTestEntity_subRoot);
					productTestEntity_restrictions3 = criteriaBuilder.equal(root,
							productTestEntity_root.get("productEntity"));
					productTestEntity_restrictions3 = criteriaBuilder.and(productTestEntity_restrictions3,
							criteriaBuilder.exists(productTestEntity_instrument_sq));
					productTestEntity_sq.where(productTestEntity_restrictions3).select(productTestEntity_root);
					predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(productTestEntity_sq));
					// 测试需求
					Subquery<RequireTestEntity> requireTestEntity_sq = criteriaQuery.subquery(RequireTestEntity.class);
					Root<RequireTestEntity> requireTestEntity_root = requireTestEntity_sq.from(RequireTestEntity.class);
					Subquery<InstrumentEntity> requireTestEntity_instrument_sq = criteriaQuery
							.subquery(InstrumentEntity.class);
					Root<InstrumentEntity> requireTestEntity_subRoot = requireTestEntity_instrument_sq
							.from(InstrumentEntity.class);
					Predicate requireTestEntity_restrictions2 = criteriaBuilder.conjunction();
					Predicate requireTestEntity_restrictions3 = criteriaBuilder.conjunction();
					requireTestEntity_restrictions2 = criteriaBuilder.and(requireTestEntity_restrictions2,
							criteriaBuilder.equal(requireTestEntity_subRoot.<RequireTestEntity> get("requireTest"),
									requireTestEntity_root));
					requireTestEntity_restrictions2 = criteriaBuilder.and(requireTestEntity_restrictions2,
							criteriaBuilder.equal(
									requireTestEntity_subRoot.<ProductclassEntity> get("productBrandEntity"),
									productBrandEntity));
					requireTestEntity_instrument_sq.where(requireTestEntity_restrictions2)
							.select(requireTestEntity_subRoot);
					requireTestEntity_restrictions3 = criteriaBuilder.equal(root,
							requireTestEntity_root.get("productEntity"));
					requireTestEntity_restrictions3 = criteriaBuilder.and(requireTestEntity_restrictions3,
							criteriaBuilder.exists(requireTestEntity_instrument_sq));
					requireTestEntity_sq.where(requireTestEntity_restrictions3).select(requireTestEntity_root);
					predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(requireTestEntity_sq));
					// 自动化测试方案
					Subquery<CalibrationEntity> calibrationEntity_sq = criteriaQuery.subquery(CalibrationEntity.class);
					Root<CalibrationEntity> calibrationEntity_root = calibrationEntity_sq.from(CalibrationEntity.class);
					Subquery<InstrumentEntity> calibrationEntity_instrument_sq = criteriaQuery
							.subquery(InstrumentEntity.class);
					Root<InstrumentEntity> calibrationEntity_subRoot = calibrationEntity_instrument_sq
							.from(InstrumentEntity.class);
					Predicate calibrationEntity_restrictions2 = criteriaBuilder.conjunction();
					Predicate calibrationEntity_restrictions3 = criteriaBuilder.conjunction();
					calibrationEntity_restrictions2 = criteriaBuilder.and(calibrationEntity_restrictions2,
							criteriaBuilder.equal(calibrationEntity_subRoot.<CalibrationEntity> get("calibration"),
									calibrationEntity_root));
					calibrationEntity_restrictions2 = criteriaBuilder.and(calibrationEntity_restrictions2,
							criteriaBuilder.equal(
									calibrationEntity_subRoot.<ProductclassEntity> get("productBrandEntity"),
									productBrandEntity));
					calibrationEntity_instrument_sq.where(calibrationEntity_restrictions2)
							.select(calibrationEntity_subRoot);
					calibrationEntity_restrictions3 = criteriaBuilder.equal(root,
							calibrationEntity_root.get("productEntity"));
					calibrationEntity_restrictions3 = criteriaBuilder.and(calibrationEntity_restrictions3,
							criteriaBuilder.exists(calibrationEntity_instrument_sq));
					calibrationEntity_sq.where(calibrationEntity_restrictions3).select(calibrationEntity_root);
					predicate = criteriaBuilder.or(predicate, criteriaBuilder.exists(calibrationEntity_sq));
				}
			}
			// pageable.getFilters().add(Filter.or(fList));
			if (predicate != null) {
				restrictions = criteriaBuilder.and(restrictions, predicate);
			}
		}
	
		// 是否产品搜索 20160510 fang
		Boolean isProductSearch = (Boolean) map.get("isProductSearch");
		if (isProductSearch != null && isProductSearch) {
			// 关键字
			String keyword = (String) map.get("keyword");
			if (keyword != null && !keyword.trim().isEmpty()) {
				List<Filter> flist = new ArrayList<Filter>();
				//flist.add(Filter.like("industryClass", "%" + keyword + "%"));
				//flist.add(Filter.like("proName", "%" + keyword + "%"));
				flist.add(Filter.like("industryClass", "%" + keyword + "%"));
				pageable.getFilters().add(Filter.or(flist));
			}
			pageable.getFilters().add(Filter.isNotNull("industryClass"));
			pageable.getFilters().add(Filter.ne("industryClass", ""));
			System.err.println("-----------------产品搜搜--------------------");
		}else{
			// 关键字
			String keyword = (String) map.get("keyword");
			if (keyword != null && !keyword.trim().isEmpty()) {
				List<Filter> flist = new ArrayList<Filter>();
				flist.add(Filter.like("proName", "%" + keyword + "%"));
				flist.add(Filter.like("proNo", "%" + keyword + "%"));
				pageable.getFilters().add(Filter.or(flist));
			}
		}
		// 是否自营
		String isSelf = (String) map.get("isSelf");
		if (isSelf != null && !isSelf.isEmpty()) {
			Filter filter = "1".equals(isSelf) ? Filter.eq("proClass", 0) : Filter.ne("proClass", 0);
			pageable.getFilters().add(filter);
		}

		// 二手还是新品
		String isNew = (String) map.get("isNew");
		if (isNew != null && !isNew.isEmpty()) {
			Filter filter = "1".equals(isNew) ? Filter.eq("qualityStatus", QualityStatusEnum.all)
					: Filter.ne("qualityStatus", QualityStatusEnum.all);
			pageable.getFilters().add(filter);
		}

		// 排序集合
		List<Sequencer> orderList = new ArrayList<Sequencer>();
		// 人气排序
		String popuOrder = (String) map.get("popuOrder");

		if (popuOrder != null && !popuOrder.isEmpty()) {
			orderList.add("asc".equals(popuOrder) ? Sequencer.asc("lookTime") : Sequencer.desc("lookTime"));
		}
		// 价格排序todo
		String priceOrder = (String) map.get("priceOrder");
		if (priceOrder != null && !priceOrder.isEmpty()) {
			orderList.add("asc".equals(popuOrder) ? Sequencer.asc("price") : Sequencer.desc("price"));
		}

		pageable.setSequencers(orderList);
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}

	@Override
	public Page<ProductEntity> findListProductPage(Pageable pageable, HashMap<String, Object> map) {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);

		// 设置查询ROOT
		Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);
		// criteriaQuery =
		// criteriaQuery.orderBy(criteriaBuilder.asc(root.get("proMarketPriceType")));

		String keyword = (String) map.get("keyword");
		long productTypeId = (Long) map.get("productTypeId");
		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("webzh"), 1));
		if (keyword != null && !keyword.trim().isEmpty()) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(
							// 商品名称
							criteriaBuilder.like(root.<String> get("proName"), "%" + keyword + "%")));
		}

		// 商品类型
		if (productTypeId != 0l) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.<Long> get("productType"), productTypeId));
		}

		// 商品审核
		restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.<Integer> get("apprStatus"), 1));

		// 查找借款分页
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);

	}
	
	@Override
	public Page<ProductEntity> findSubscriptInfo(Pageable pageable, Map<String, Object> htMap) {
		// 获取条件构造器
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// 创建条件查询
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class);

		// 设置查询ROOT
		Root<ProductEntity> root = criteriaQuery.from(ProductEntity.class);

		// 设置限制条件
		Predicate restrictions = criteriaBuilder.conjunction();
		criteriaQuery.distinct(false);
		if (htMap.get("appr") != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.<Integer> get("appr"), (Integer) htMap.get("appr")));
		} else if (htMap.get("isDel") != null) { // 未分组
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.<Integer> get("isDel"), (Integer) htMap.get("isDel")));
		} else if (htMap.get("productBrandEntity") != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.equal(root.<ProductBrandEntity> get("productBrandEntity"),
							(ProductBrandEntity) htMap.get("productBrandEntity")));
		} else if (htMap.get("proList") != null) {
			List<ProductSubscribeEntity> proList = (List<ProductSubscribeEntity>) htMap.get("proList");
			for (ProductSubscribeEntity entity : proList) {
				if (entity.getProductEntity() != null) {
					restrictions = criteriaBuilder.and(restrictions,
							criteriaBuilder.notEqual(root.<Long> get("id"), entity.getProductEntity().getId()));
				}
			}
		}
		// 查找借款分页
		return findPage(criteriaBuilder, criteriaQuery, restrictions, root, pageable);
	}
	@Override
	public List<ProductEntity> searchList(String keyword) {
		/*CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductEntity> cQuery = criteriaBuilder.createQuery(ProductEntity.class);
		Root<ProductEntity> root = cQuery.from(ProductEntity.class);
		Path<String> proNo = root.get("proNo");
		Path<String> proName = root.get("proName");
		Predicate or1 = criteriaBuilder.or(criteriaBuilder.like(proNo, "%"+keyword+"%"),criteriaBuilder.like(proName,  "%"+keyword+"%"));*/
		//return entityManager.createQuery(cQuery.where(or1)).getResultList();
		//String sql="select p.* from jhj_product p where p.pro_name like '%"+keyword+"%' or p.pro_no like '%"+keyword+"%'";
		String sql="select p.* from jhj_product p where CONCAT(p.pro_name,p.pro_no) REGEXP '"+keyword+"' and p.proownaudit=1";
		System.out.println(sql);
		Query query = entityManager.createNativeQuery(sql,ProductEntity.class);
		List<ProductEntity> resultList = query.getResultList();
		return resultList;
	}
	@Override
	public List<ProductEntity> findListByNameAndProNo(int i, boolean b, String[] mcProductClass) {
		List<ProductEntity> resultList=new ArrayList<>();
		
		if(b) {//国外
			String[] split= {};
			for(int j=0;j<mcProductClass.length;j++) {
				split =mcProductClass[j].replace(" ", ",").split(",");
				if(split.length%2==0) {
					String sql="select p.* from jhj_product p left join jhj_product_brand b on p.brand_id=b.id  where b.name_en like '%"+split[0]+"%' and p.pro_no like '%"+split[1]+"%' and p.proownaudit=1  limit "+i;
					Query query = entityManager.createNativeQuery(sql,ProductEntity.class);
					resultList.addAll(query.getResultList());
				}
			}
		}else {
			for (String  p : mcProductClass) {
				String sql="select  p.* from jhj_product p where p.proownaudit=1 and  p.name like '%"+p+"%' limit "+i ;
				Query query = entityManager.createNativeQuery(sql,ProductEntity.class);
				resultList.addAll(query.getResultList());
			}
		}
		return resultList;
	}

	@Override
	public List<ProductEntity> findByIndustryClass(int i, Long id, String proType, String c1, String c2) {
		String sql="select * from jhj_product p  where p.producttype_id="+proType;
		if(c1!=""&&c1.equals("1")){
			//值为零即为全新的 否则为二手
			sql+=" and quality_status=0 ";
		}
		if(c1!=""&&c1.equals("2")){
			sql+=" and quality_status!=0 ";
		}
		if(c2!=""&&c2.equals("3")){
			//值为零即进口 否则为国产
			sql+=" and is_import=0 ";
		}
		if(c2!=""&&c2.equals("4")){
			sql+=" and is_import!=0 ";
		}
		sql+=" and  p.industry_class like "+"\"%"+id+"%\""+" ORDER BY modify_date DESC LIMIT 0,"+i+"";
		return  entityManager.createNativeQuery(sql, ProductEntity.class).getResultList();
	}


}
