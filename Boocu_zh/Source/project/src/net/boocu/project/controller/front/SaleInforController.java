/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.BasedataEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BasedataService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 产品信息销售
 * 
 * @author deng 20151118
 * @version 1.0
 */
@Controller("saleInforController")
public class SaleInforController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "/front/userCenter/productInforManage/sale";

	/** 错误信息 */
	private static final Message ERROR_MESSAGE = Message.error("信息有效期不能为空!");

	@Resource
	ProductSaleService productSaleService;

	@Resource
	ProductService productService;

	@Resource
	MemberService memberService;

	@Resource
	HelpService helpService;

	@Resource
	ProductBrandService productBrandService;

	@Resource
	ProducttypeService productTypeService;

	@Resource
	ProductclassService productclassService;

	@Resource
	IndustryClassService industryClassService;

	@Resource
	BasedataService basedataService;

	@Resource
	private MessageService messageService;

	/**
	 * 产品信息销售
	 */
	@RequestMapping(value = { "/saleInfor", "/saleInfor.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String userInforSale(@RequestParam(required = false, defaultValue = "1") int pageNum, // 审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize, // 审核页记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum1, // 待审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize1, // 待审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum2, // 未审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize2, // 未审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum3, // 首页推荐页码
			@RequestParam(required = false, defaultValue = "12") int pageSize3, // 首页推荐记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum4, // 下架页码
			@RequestParam(required = false, defaultValue = "15") int pageSize4, // 下架记录数
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {

		// 审核状态
		int appr = ReqUtil.getInt(request, "appr", 1);

		// 获取当前日期
		Date date = new Date();

		model.addAttribute("appr", appr);
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		// 帮组信息
		model.addAttribute("helps", helpService.findAll());

		// 过滤自营发布 或者 客户发布
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("proClass", 0));
		filters.add(Filter.eq("proClass", 1));

		// 有效期
		List<Filter> filters2 = new ArrayList<Filter>();
		filters2.add(Filter.ge("inforValidity", date));
		filters2.add(Filter.eq("longTerm", 1));

		if (currentMember != null) {
			// 已审核
			Pageable pageable = new Pageable(pageNum, pageSize);
			pageable.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable.getFilters().add(Filter.eq("apprStatus", 1));
			pageable.getFilters().add(Filter.eq("productType", productTypeService.find(1l)));
			pageable.getFilters().add(Filter.eq("isDel", 0));
			pageable.getFilters().add(Filter.or(filters));
			pageable.getFilters().add(Filter.or(filters2));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page", productService.findPage(pageable));

			// 待审核
			Pageable pageable1 = new Pageable(pageNum1, pageSize1);
			pageable1.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable1.getFilters().add(Filter.eq("apprStatus", 0));
			pageable1.getFilters().add(Filter.eq("productType", productTypeService.find(1l)));
			pageable1.getFilters().add(Filter.eq("isDel", 0));
			pageable1.getFilters().add(Filter.or(filters));
			pageable1.getFilters().add(Filter.or(filters2));
			pageable1.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page1", productService.findPage(pageable1));

			// 未通过
			Pageable pageable2 = new Pageable(pageNum2, pageSize2);
			pageable2.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable2.getFilters().add(Filter.eq("apprStatus", 2));
			pageable2.getFilters().add(Filter.eq("productType", productTypeService.find(1l)));
			pageable2.getFilters().add(Filter.eq("isDel", 0));
			pageable2.getFilters().add(Filter.or(filters));
			pageable2.getFilters().add(Filter.or(filters2));
			pageable2.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page2", productService.findPage(pageable2));

			// 首页推荐
			Pageable pageable3 = new Pageable(pageNum3, pageSize3);
			pageable3.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable3.getFilters().add(Filter.eq("apprStatus", 1));
			pageable3.getFilters().add(Filter.eq("productType", productTypeService.find(1l)));
			pageable3.getFilters().add(Filter.eq("isDel", 0));
			pageable3.getFilters().add(Filter.eq("isPromSale", 1));
			pageable3.getFilters().add(Filter.or(filters));
			pageable3.getFilters().add(Filter.or(filters2));
			pageable3.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page3", productService.findPage(pageable3));

			// 下架
			Pageable pageable4 = new Pageable(pageNum4, pageSize4);
			// 过滤有效期到期 或者 手动下架
			List<Filter> filList = new ArrayList<Filter>();
			filList.add(Filter.eq("proownaudit", 0));
			filList.add(Filter.isNull("inforValidity"));
			filList.add(Filter.le("inforValidity", date));
			List<Filter> fiLists = new ArrayList<Filter>();
			fiLists.add(Filter.eq("apprStatus", 1));
			fiLists.add(Filter.eq("apprStatus", 0));
			pageable4.getFilters().add(Filter.eq("longTerm", 0));
			pageable4.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable4.getFilters().add(Filter.or(fiLists));
			pageable4.getFilters().add(Filter.eq("productType", productTypeService.find(1l)));
			pageable4.getFilters().add(Filter.eq("isDel", 0));
			pageable4.getFilters().add(Filter.or(filters));
			pageable4.getFilters().add(Filter.or(filList));
			model.addAttribute("page4", productService.findPage(pageable4));

			return TEMPLATE_PATH + "/saleInfor";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	// 推荐
	@RequestMapping(value = { "/isPromSale" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void isPromSale(HttpServletRequest request, HttpServletResponse response, Model model, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			productEntity.setIsPromSale(1);
			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "推荐成功");
		RespUtil.renderJson(response, result);

	}

	// 取消推荐
	@RequestMapping(value = { "/isPromSaleOut" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void isPromSaleOut(HttpServletRequest request, HttpServletResponse response, Model model, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			productEntity.setIsPromSale(0);
			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "取消推荐成功");
		RespUtil.renderJson(response, result);

	}

	// 下架
	@RequestMapping(value = "/proownaudit", method = { RequestMethod.GET, RequestMethod.POST })
	public void proownaudit(HttpServletRequest request, HttpServletResponse response, Model model, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			productEntity.setProownaudit(0);
			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "下架商品成功");
		RespUtil.renderJson(response, result);
	}

	// 删除
	@RequestMapping(value = "/del", method = { RequestMethod.GET, RequestMethod.POST })
	public void del(HttpServletRequest request, HttpServletResponse response, Model model, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			productEntity.setIsDel(1);
			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除商品成功");
		RespUtil.renderJson(response, result);
	}

	// 发布
	@RequestMapping(value = "/saleInforUpdate", method = { RequestMethod.GET, RequestMethod.POST })
	public void update(HttpServletRequest request, HttpServletResponse response, Model model, Long id) {
		ProductEntity productEntity = productService.find(id);
		if (productEntity != null) {
			// 增加有效期的时间
			Date inforValidity = productEntity.getInforValidity();
			if (inforValidity != null) {
				Date createDate = productEntity.getCreateDate();
				Date date2 = new DateTime(inforValidity).plusDays(productEntity.getInforNumber()).toDate();
				productEntity.setInforValidity(date2);
				productEntity.setProownaudit(1);
				productService.update(productEntity);
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", 1);
				result.put("message", "更新产品成功");
				RespUtil.renderJson(response, result);
			} else {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("message", "发布失败, 有效期为空");
				RespUtil.renderJson(response, result);
			}
		}

	}

	// 上架
	@RequestMapping(value = "/proown1/proown1", method = { RequestMethod.GET, RequestMethod.POST })
	public void proown1(HttpServletRequest request, HttpServletResponse response, Model model, Long id) {
		ProductEntity productEntity = productService.find(id);
		if (productEntity != null) {
			// 增加有效期的时间
			/*
			 * Date inforValidity = productEntity.getInforValidity(); Date
			 * createDate = productEntity.getCreateDate();
			 */
			Date date = new Date();
			Date date2 = new DateTime(date).plusDays(productEntity.getInforNumber()).toDate();
			productEntity.setInforValidity(date2);
			productEntity.setProownaudit(1);
			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "上架产品成功");
		RespUtil.renderJson(response, result);
	}

	// 批量上架
	@RequestMapping(value = "/proown/proown", method = { RequestMethod.GET, RequestMethod.POST })
	public void proown(HttpServletRequest request, HttpServletResponse response, Model model, Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			if (productEntity != null) {
				// 增加有效期的时间
				Date inforValidity = productEntity.getInforValidity();
				Date createDate = productEntity.getCreateDate();
				Date date = new Date();
				// 结束时间减去创建时间 取得有效期的天数
				Integer date1 = (int) ((inforValidity.getTime() - createDate.getTime()) / 1000 / 60 / 60 / 24);
				Date date2 = new DateTime(date).plusDays(date1).toDate();
				productEntity.setInforValidity(date2);
				productEntity.setProownaudit(1);
				productService.update(productEntity);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "上架产品成功");
		RespUtil.renderJson(response, result);
	}

	// 修改产品信息销售
	@RequestMapping(value = "/edit_saleInfor", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit_saleInfor(HttpServletRequest request, HttpServletResponse response, Model model) {
		long id = ReqUtil.getLong(request, "id", 0l);

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		if (currentMember != null) {
			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			// 品牌
			model.addAttribute("brands", productBrandService.findAll());
			ProductEntity productEntity = productService.find(id);
			model.addAttribute("item", productEntity);
			return TEMPLATE_PATH + "/edit_user_pub_sale";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	// 修改客户商品销售
	@ResponseBody
	@RequestMapping(value = "/save_updateEdit_user_pub_sale", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> save_updateEdit_user_pub_sale(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		long id = ReqUtil.getLong(request, "id", 0l);
		String proName = ReqUtil.getString(request, "proName", "");
		String status=ReqUtil.getString(request, "status", "");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		long memberId = ReqUtil.getLong(request, "memberId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		String poption = ReqUtil.getString(request, "poption", "");
		String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
		String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
		int isUnit = ReqUtil.getInt(request, "isUnit", 0);
		String cycle = ReqUtil.getString(request, "cycle", "");
		String cycleUnit = ReqUtil.getString(request, "cycleUnit", "");
		String proUnit = ReqUtil.getString(request, "proUnit", "");
		String proNameEn = ReqUtil.getString(request, "proNameEn", "");
		int proownaudit = ReqUtil.getInt(request, "proownaudit", 0);
		int webzh = ReqUtil.getInt(request, "webzh", 0);
		int weben = ReqUtil.getInt(request, "weben", 0);
		int proStock = ReqUtil.getInt(request, "proStock", 0);
		String procostPrice = ReqUtil.getString(request, "procostPrice", "");
		int isTax = ReqUtil.getInt(request, "isTax", 0);
		String taxRate = ReqUtil.getString(request, "taxRate", "");
		String proOriginal1 = ReqUtil.getString(request, "proOriginal1", "");
		String proOriginal2 = ReqUtil.getString(request, "proOriginal2", "");
		String proOriginal3 = ReqUtil.getString(request, "proOriginal3", "");
		String proOriginal4 = ReqUtil.getString(request, "proOriginal4", "");
		String proSynopsis = ReqUtil.getString(request, "proSynopsis", "");
		String proSynopsisEn = ReqUtil.getString(request, "proSynopsisEn", "");
		String proContent = ReqUtil.getString(request, "proContent", "");
		String proContentEn = ReqUtil.getString(request, "proContentEn", "");
		long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
		String indClassId = ReqUtil.getString(request, "indClassId", "");
		String apply = ReqUtil.getString(request, "apply", "");
		String prometaTitle = ReqUtil.getString(request, "prometaTitle", "");
		String proMetaKeywords = ReqUtil.getString(request, "proMetaKeywords", "");
		String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
		// BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request,
		// "inforValidity", 0l));
		// String inforValidityUnit = ReqUtil.getString(request,
		// "inforValidityUnit", "");
		Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
		String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
		String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
		String prodNumber = ReqUtil.getString(request, "prodNumber", "");
		BigDecimal referencePrice = new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l));
		String referencePriceLimit = ReqUtil.getString(request, "referencePriceLimit", "");
		String referencepricetype = ReqUtil.getString(request, "referencepricetype", "");
		BigDecimal proShopPrice = new BigDecimal(ReqUtil.getLong(request, "proShopPrice", 0l));
		String priceType = ReqUtil.getString(request, "priceType", "");
		String priceUnit = ReqUtil.getString(request, "priceUnit", "");
		String downData = ReqUtil.getString(request, "downData", "");
		String brandName = ReqUtil.getString(request, "brandName", "");
		BigDecimal returnPeriod = new BigDecimal(ReqUtil.getLong(request, "returnPeriod", 0l));
		String returnPeriodUnit = ReqUtil.getString(request, "returnPeriodUnit", "");
		String areaProvince = ReqUtil.getString(request, "areaProvince", "");
		String areaCity = ReqUtil.getString(request, "areaCity", "");
		String areaCountry = ReqUtil.getString(request, "areaCountry", "");
		Integer longTerm = ReqUtil.getInt(request, "longTerm", 0);
		if (weben == 1 && brandId != 0) {
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("isDel", 0));
			filters.add(Filter.eq("productBrandEntity", productBrandService.find(brandId)));
			filters.add(Filter.eq("proNo", proNo));
			List<BasedataEntity> basedataEntities = basedataService.findList(filters);
			if (basedataEntities.size() > 0) {
				for (BasedataEntity item : basedataEntities) {
					proNameEn = item.getProNameEn();
					proContentEn = item.getProContentEn();
					proSynopsisEn = item.getProSynopsisEn();
				}
			}
		}
		if (proClassId == 0) {
			result.put("result", 2);
			return result;
		}

		if (inforValidity == null && longTerm == 0) {
			result.put("result", 0);
			return result;
		}

		ProductEntity productEntity = productService.find(id);
		if (productEntity != null) {
			productEntity.setLongTerm(longTerm);
			if (!brandName.isEmpty()) {
				ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
				List<ProductBrandEntity> productBrand = productBrandService.findList(Filter.eq("name", brandName));
				if (productBrandEntity != null) {
					productEntity.setProductBrandEntity(productBrandEntity);
				} else if (productBrand.size() > 0) {
					productEntity.setProductBrandEntity(productBrand.get(0));
				} else {
					ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
					productBrandEntity2.setName(brandName);
					productBrandEntity2.setApprStatus(0);
					productBrandService.save(productBrandEntity2);
					productEntity.setProductBrandEntity(productBrandEntity2);
				}
			}
			MemberEntity memberEntity = memberService.find(memberId);
			if (memberEntity != null) {
				productEntity.setMemberEntity(memberEntity);
			}
			productEntity.setAreaCity(areaCity);
			productEntity.setAreaCountry(areaCountry);
			productEntity.setAreaProvince(areaProvince);
			productEntity.setReturnPeriod(returnPeriod);
			productEntity.setReturnPeriodUnit(DateTypeEnum.valueOf(returnPeriodUnit));
			productEntity.setDownData(downData);
			productEntity.setRepairPeriod(repairPeriod);
			productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
			productEntity.setInforValidity(inforValidity);
			if (inforValidity != null) {
				Date date = new Date();
				Integer date1 = (int) ((inforValidity.getTime() - date.getTime()) / 1000 / 60 / 60 / 24);
				productEntity.setInforNumber(date1);
			}
			// productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
			productEntity.setApply(apply);
			productEntity.setPrometaTitle(prometaTitle);
			productEntity.setProMetaKeywords(proMetaKeywords);
			productEntity.setPrometaDescription(prometaDescription);
			productEntity.setProOriginal1(proOriginal1);
			productEntity.setProOriginal2(proOriginal2);
			productEntity.setProOriginal3(proOriginal3);
			productEntity.setProOriginal4(proOriginal4);
			productEntity.setProSynopsis(proSynopsis);
			productEntity.setProSynopsisEn(proSynopsisEn);
			productEntity.setProContent(proContent);
			productEntity.setProContentEn(proContentEn);
			if (memberEntity.getMemberGradeEntity().getName().equals("内部员工")) {
				productEntity.setProClass(0);
			} else {
				productEntity.setProClass(1);
			}
			// 行业分类
			productEntity.setIndustryClass(indClassId);
			ProductclassEntity productclassEntity = productclassService.find(proClassId);
			if (productclassEntity != null) {
				productEntity.setProductclass(productclassEntity);
			}
			productEntity.setIsTax(isTax);
			productEntity.setTaxRate(RateEnum.valueOf(taxRate));
			productEntity.setProcostPrice(procostPrice);
			productEntity.setProownaudit(proownaudit);
			productEntity.setIsUnit(isUnit);
			if (isUnit == 0) {
				proUnit = "";
			}
			productEntity.setStatus(StatusEnum.valueOf(status));
			productEntity.setApprStatus(0);
			productEntity.setProductType(productTypeService.find(1l));
			productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
			productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
			productEntity.setProUnit(proUnit);
			productEntity.setProNo(proNo);
			productEntity.setProNameEn(proNameEn);
			productEntity.setPoption(poption);
			productEntity.setCycle(cycle);
			productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
			productEntity.setProName(proName);
			productEntity.setWeben(weben);
			productEntity.setWebzh(webzh);
			productEntity.setProStock(proStock);
			if (proStock == 1) {
				productEntity.setProdNumber(prodNumber);
			} else {
				productEntity.setProdNumber("");
			}
			if (qualityStatus.equals("all")) {
				productEntity.setReferencePrice(new BigDecimal(0));
				productEntity.setReferencePriceLimit(CurrencyEnum.valueOf("rmb"));
				productEntity.setReferencepricetype(PriceUnitEnum.valueOf("yuan"));
			} else {
				productEntity.setReferencePrice(referencePrice);
				productEntity.setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
				productEntity.setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype));
			}
			productEntity.getProductSaleEntity().setPriceType(CurrencyEnum.valueOf(priceType));
			productEntity.getProductSaleEntity().setPriceUnit(PriceUnitEnum.valueOf(priceUnit));
			productEntity.getProductSaleEntity().setProShopPrice(proShopPrice);

			// 添加到资料库
			if (brandId != 0l && !proNo.isEmpty()) {
				List<Filter> filters = new ArrayList<Filter>();
				ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
				filters.add(Filter.eq("productBrandEntity", productBrandEntity));
				filters.add(Filter.eq("proNo", proNo));
				List<BasedataEntity> basedataEntities = basedataService.findList(filters);
				if (basedataEntities.size() <= 0) {
					BasedataEntity basedataEntity = new BasedataEntity();
					basedataEntity.setProductBrandEntity(productBrandEntity);
					basedataEntity.setApprStatus(0);
					basedataEntity.setProNo(proNo);
					basedataEntity.setProName(proName);
					basedataEntity.setProNameEn(proNameEn);
					basedataEntity.setIndustryClass(indClassId);
					if (productclassEntity != null) {
						basedataEntity.setProductclass(productclassEntity);
					}
					basedataEntity.setImage(proOriginal1);
					basedataEntity.setProSynopsis(proSynopsis);
					basedataEntity.setProSynopsisEn(proSynopsisEn);
					basedataEntity.setProContent(proContent);
					basedataEntity.setProContentEn(proContentEn);
					basedataEntity.setIsDel(0);
					basedataService.save(basedataEntity);
				}
			}
			productService.update(productEntity);
		}
		result.put("result", 1);
		result.put("message", "操作成功");
		return result;
	}
}