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
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 产品信息求购
 * 
 * @author deng 20151118
 * @version 1.0
 */
@Controller("fastPubBuyController")
public class FastPubBuyController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/fastPubManage/buy";

	@Resource
	ProductBuyService productBuyService;

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
	ProductclassService productClassService;

	@Resource
	IndustryClassService industryClassService;

	@Resource
	private MessageService messageService;

	/**
	 * 产品信息销售
	 */
	@RequestMapping(value = { "/fastPubBuy", "/fastPubBuy.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String userInforSale(@RequestParam(required = false, defaultValue = "1") int pageNum, // 审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize, // 审核页记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum1, // 待审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize1, // 待审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum2, // 未审核核页码
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

		if (currentMember != null) {
			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			// 已审核
			Pageable pageable = new Pageable(pageNum, pageSize);
			pageable.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable.getFilters().add(Filter.eq("apprStatus", 1));
			pageable.getFilters().add(Filter.eq("productType", productTypeService.find(2l)));
			pageable.getFilters().add(Filter.eq("isDel", 0));
			pageable.getFilters().add(Filter.eq("proClass", 3));
			pageable.getFilters().add(Filter.ge("inforValidity", date));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page", productService.findPage(pageable));

			// 待审核
			Pageable pageable1 = new Pageable(pageNum1, pageSize1);
			pageable1.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable1.getFilters().add(Filter.eq("apprStatus", 0));
			pageable1.getFilters().add(Filter.eq("productType", productTypeService.find(2l)));
			pageable1.getFilters().add(Filter.eq("isDel", 0));
			pageable1.getFilters().add(Filter.eq("proClass", 3));
			pageable.getFilters().add(Filter.ge("inforValidity", date));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page1", productService.findPage(pageable1));

			// 未通过
			Pageable pageable2 = new Pageable(pageNum2, pageSize2);
			pageable2.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable2.getFilters().add(Filter.eq("apprStatus", 2));
			pageable2.getFilters().add(Filter.eq("productType", productTypeService.find(2l)));
			pageable2.getFilters().add(Filter.eq("isDel", 0));
			pageable2.getFilters().add(Filter.eq("proClass", 3));
			pageable.getFilters().add(Filter.ge("inforValidity", date));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page2", productService.findPage(pageable2));

			// 首页推荐
			Pageable pageable3 = new Pageable(pageNum3, pageSize3);
			pageable3.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable3.getFilters().add(Filter.eq("apprStatus", 1));
			pageable3.getFilters().add(Filter.eq("productType", productTypeService.find(2l)));
			pageable3.getFilters().add(Filter.eq("isDel", 0));
			pageable3.getFilters().add(Filter.eq("isPromSale", 1));
			pageable3.getFilters().add(Filter.eq("proClass", 3));
			pageable.getFilters().add(Filter.ge("inforValidity", date));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page3", productService.findPage(pageable3));

			// 下架
			Pageable pageable4 = new Pageable(pageNum4, pageSize4);
			// 过滤有效期到期 或者 手动下架
			List<Filter> filList = new ArrayList<Filter>();
			filList.add(Filter.le("inforValidity", date));
			filList.add(Filter.eq("proownaudit", 0));
			pageable4.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable4.getFilters().add(Filter.eq("apprStatus", 1));
			pageable4.getFilters().add(Filter.eq("proClass", 3));
			pageable4.getFilters().add(Filter.eq("productType", productTypeService.find(2l)));
			pageable4.getFilters().add(Filter.eq("isDel", 0));
			pageable4.getFilters().add(Filter.or(filList));
			model.addAttribute("page4", productService.findPage(pageable4));

			return TEMPLATE_PATH + "/fastPubBuy";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	// 修改产品求购
	@RequestMapping(value = "/edit_fastPubBuy", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit_fastPubBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
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
			return TEMPLATE_PATH + "/edit_fastBuy";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	// 修改快捷发布销售页面
	@ResponseBody
	@RequestMapping(value = "/save_editFastBuy.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> save_editFastBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
		long id = ReqUtil.getLong(request, "id", 0l);
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		long memberId = ReqUtil.getLong(request, "memberId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		String poption = ReqUtil.getString(request, "poption", "");
		String proName = ReqUtil.getString(request, "proName", "");
		String proNameEn = ReqUtil.getString(request, "proNameEn", "");
		String status = ReqUtil.getString(request, "status", "");
		String proMarketPriceType = ReqUtil.getString(request, "proMarketPriceType", "");
		BigDecimal proMarketPrice = new BigDecimal(ReqUtil.getLong(request, "proMarketPrice", 0l));
		String proMarketPriceLimit = ReqUtil.getString(request, "proMarketPriceLimit", "");
		int isUnit = ReqUtil.getInt(request, "isUnit", 0);
		String proUnit = ReqUtil.getString(request, "proUnit", "");
		String cycle = ReqUtil.getString(request, "cycle", "");
		String cycleUnit = ReqUtil.getString(request, "cycleUnit", "");
		String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
		String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
		String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
		String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
		String remain = ReqUtil.getString(request, "remain", "");
		int proStock = ReqUtil.getInt(request, "proStock", 0);
		String prodNumber = ReqUtil.getString(request, "prodNumber", "");
		BigDecimal referencePrice = new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l));
		String referencePriceLimit = ReqUtil.getString(request, "referencePriceLimit", "");
		String referencepricetype = ReqUtil.getString(request, "referencepricetype", "");
		String brandName = ReqUtil.getString(request, "brandId", "");
		String areaProvince = ReqUtil.getString(request, "areaProvince", "");
		String areaCity = ReqUtil.getString(request, "areaCity", "");
		String areaCountry = ReqUtil.getString(request, "areaCountry", "");

		ProductEntity productEntity = productService.find(id);
		if (productEntity != null) {
			if (!brandName.isEmpty()) {
				ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
				ProductBrandEntity productBrand = productBrandService.find(Filter.eq("name", brandName));
				if (productBrandEntity != null) {
					productEntity.setProductBrandEntity(productBrandEntity);
				} else if (productBrand != null) {
					productEntity.setProductBrandEntity(productBrand);
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
			productEntity.setIsUnit(isUnit);
			if (isUnit == 0) {
				proUnit = "";
			}
			productEntity.setAreaCity(areaCity);
			productEntity.setAreaCountry(areaCountry);
			productEntity.setAreaProvince(areaProvince);
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
			productEntity.setApprStatus(0);
			productEntity.setProStock(proStock);
			productEntity.setProductType(productTypeService.find(2l));
			productEntity.setRemain(remain);
			productEntity.setRepairPeriod(repairPeriod);
			productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
			productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
			productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
			productEntity.setProUnit(proUnit);
			if (memberEntity.getMemberGradeEntity().getName().equals("内部员工")) {
				productEntity.setProClass(0);
			} else {
				productEntity.setProClass(3);
			}
			productEntity.setProNo(proNo);
			productEntity.setProNameEn(proNameEn);
			productEntity.setPoption(poption);
			productEntity.setStatus(StatusEnum.valueOf(status));
			productEntity.setCycle(cycle);
			productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
			productEntity.getProductBuyEntity().setProMarketPrice(proMarketPrice);
			productEntity.getProductBuyEntity().setProMarketPriceType(CurrencyEnum.valueOf(proMarketPriceType));
			productEntity.getProductBuyEntity().setProMarketPriceLimit(PriceUnitEnum.valueOf(proMarketPriceLimit));
			productEntity.setProName(proName);

			productService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "操作成功");
		return result;
	}
}