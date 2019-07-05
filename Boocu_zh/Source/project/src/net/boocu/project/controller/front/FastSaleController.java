/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

/**
 * Controller - 快捷产品销售
 * 
 * @author deng 20151111
 * @version 1.0
 */
@Controller("frontFastSaleController")
public class FastSaleController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/fastPub/sale";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

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
	private MessageService messageService;

	/**
	 * 快捷产品销售
	 */
	@RequestMapping(value = { "/fastSale", "/fastSale.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String fast(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
	
		if (currentMember != null) {
			// 产品品牌
			model.addAttribute("brands", productBrandService.findAll());

			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);

			return TEMPLATE_PATH + "/fastSale";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/**
	 * ajax 快捷产品销售
	 */
	@RequestMapping(value = "/fastSale/fastSale", method = RequestMethod.POST)
	public @ResponseBody Message fastSale(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("快捷产品销售--------------");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		long memberId = ReqUtil.getLong(request, "memberId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		String poption = ReqUtil.getString(request, "poption", "");
		String proName = ReqUtil.getString(request, "proName", "");
		String proNameEn = ReqUtil.getString(request, "proNameEn", "");
		String status = ReqUtil.getString(request, "status", "");
		String priceType = ReqUtil.getString(request, "priceType", "");
		BigDecimal proShopPrice = new BigDecimal(ReqUtil.getLong(request, "proShopPrice", 0l));
		String priceUnit = ReqUtil.getString(request, "priceUnit", "");
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
		String brandName = ReqUtil.getString(request, "brandName", "");
		String areaProvince = ReqUtil.getString(request, "areaProvince", "");
		String areaCity = ReqUtil.getString(request, "areaCity", "");
		String areaCountry = ReqUtil.getString(request, "areaCountry", "");

		ProductSaleEntity productSaleEntity = new ProductSaleEntity();
		ProductEntity productEntity = new ProductEntity();
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
		productEntity.setProStock(proStock);
		if (proStock == 1) {
			productEntity.setProdNumber(prodNumber);
		} else {
			productEntity.setProdNumber("");
		}
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
		productEntity.setProductType(productTypeService.find(1l));
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
		productSaleEntity.setProShopPrice(proShopPrice);
		productSaleEntity.setPriceType(CurrencyEnum.valueOf(priceType));
		productSaleEntity.setPriceUnit(PriceUnitEnum.valueOf(priceUnit));
		productEntity.setProName(proName);

		productSaleService.save(productSaleEntity, productEntity);
		return SUCCESS_MESSAGE;

	}

}