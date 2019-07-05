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
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductSaleEntity;
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
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Controller - 客户产品销售
 * 
 * @author deng 20151116
 * @version 1.0
 */
@Controller("userPubSaleController")
public class UserPubSaleController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/userPub/sale";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

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
	 * 客户产品销售
	 */
	@RequestMapping(value = { "/user_pub_sale", "/user_pub_sale.jhtml" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String userPubSale(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		if (currentMember != null) {
			// 产品品牌
			model.addAttribute("brands", productBrandService.findAll());

			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);

			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			return TEMPLATE_PATH + "/user_pub_sale";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/**
	 * ajax 客户产品销售
	 */
	@RequestMapping(value = "/user_pub_sale/user_pub_sale", method = RequestMethod.POST)
	public @ResponseBody Message fastSale(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("客户产品销售--------------");
		String proName = ReqUtil.getString(request, "proName", "");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		String status= ReqUtil.getString(request, "status", "");
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
		String isImport = ReqUtil.getString(request, "isImport", "1");
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
			ProductEntity result = new ProductEntity();
			if (basedataEntities.size() > 0) {
				for (BasedataEntity item : basedataEntities) {
					result.setProName(item.getProName());
					proNameEn = item.getProNameEn();
					proContentEn = item.getProContentEn();
					proSynopsisEn = item.getProSynopsisEn();
				}
			}
		}
		if (proClassId == 0) {
			return ERROR_MESSAGE;
		}

		if (inforValidity == null && longTerm == 0) {
			return ERROR_MESSAGE;
		}

		ProductSaleEntity productSaleEntity = new ProductSaleEntity();
		ProductEntity productEntity = new ProductEntity();
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
        productEntity.setIsImport(isImport);
		productEntity.setLongTerm(longTerm);
		productEntity.setApprStatus(0);
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
		productEntity.setProSynopsis(proSynopsis);
		productEntity.setProSynopsisEn(proSynopsisEn);
		productEntity.setProContent(proContent);
		productEntity.setProContentEn(proContentEn);
		// 行业分类
		if(StringUtils.isNotBlank(indClassId)){
			productEntity.setIndustryClass(indClassId);
		}
		ProductclassEntity productclassEntity = productclassService.find(proClassId);
		if (productclassEntity != null) {
			productEntity.setProductclass(productclassEntity);
		}
		productEntity.setIsTax(isTax);
		productEntity.setTaxRate(RateEnum.valueOf(taxRate));
		productEntity.setProcostPrice(procostPrice);
		productEntity.setProStock(proStock);
		if (proStock == 1) {
			productEntity.setProdNumber(prodNumber);
		} else {
			productEntity.setProdNumber("");
		}
		productEntity.setProownaudit(proownaudit);
		productEntity.setIsUnit(isUnit);
		if (isUnit == 0) {
			proUnit = "";
		}
		productEntity.setProductType(productTypeService.find(1l));
		productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
		productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
		productEntity.setProUnit(proUnit);
		if (memberEntity.getMemberGradeEntity().getName().equals("内部员工")) {
			productEntity.setProClass(0);
		} else {
			productEntity.setProClass(1);
		}
		productEntity.setStatus(StatusEnum.valueOf(status));
		productEntity.setProNo(proNo);
		productEntity.setProNameEn(proNameEn);
		productEntity.setPoption(poption);
		productEntity.setCycle(cycle);
		productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
		productEntity.setProName(proName);
		productEntity.setWeben(weben);
		productEntity.setWebzh(webzh);
		productEntity.setReferencePrice(referencePrice);
		productEntity.setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
		productEntity.setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype));
		productSaleEntity.setPriceType(CurrencyEnum.valueOf(priceType));
		productSaleEntity.setPriceUnit(PriceUnitEnum.valueOf(priceUnit));
		productSaleEntity.setProShopPrice(proShopPrice);
		BasedataEntity basedata=null;
		
		// 添加到资料库
		if (brandId != 0l && !proNo.isEmpty()) {
			List<Filter> filters = new ArrayList<Filter>();
			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
			filters.add(Filter.eq("productBrandEntity", productBrandEntity));
			filters.add(Filter.eq("proNo", proNo,true));
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
			}else{
				basedata=basedataEntities.get(0);
			}
		}
		if (proOriginal1 == "" && basedata != null) {
			productEntity.setProOriginal1(basedata.getImage());
		} else {
			productEntity.setProOriginal1(proOriginal1);
			productEntity.setProOriginal2(proOriginal2);
			productEntity.setProOriginal3(proOriginal3);
			productEntity.setProOriginal4(proOriginal4);
		}
		productSaleService.save(productSaleEntity, productEntity);
		return SUCCESS_MESSAGE;

	}

}