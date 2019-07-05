/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.*;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.*;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberService;

import org.apache.shiro.mgt.SecurityManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Controller - 商品基表
 * 
 * @author fang
 * @version 1.0
 */
@Controller("frontProductController")
@RequestMapping("/product")
public class ProductController {

	@Resource
	private ProductService productService;

	@Resource
	private ProducttypeService producttypeService;
	@Resource
	private MemberService memberService;
	@Resource
	private IndustryClassService industryClassService;
    @Resource
    private FlowService flowService;
	@Resource
	private HelpService helpService;

	private final static int SEARCHPAGESIZE = 30;

	@RequestMapping(value = "/list.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String List(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = SEARCHPAGESIZE + "") int pageSize, // 页记录数
			@RequestParam(required = false, defaultValue = "") String keyword, // 搜索关键字
			@RequestParam(required = false, defaultValue = "0") Long[] proClass, // 搜索关键字
			Model model, HttpServletRequest request) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		if (!keyword.isEmpty()) {
			con.put("keyword", keyword);
		}
		// 产品搜索 20160425
		Boolean isProductSearch = ReqUtil.getBoolean(request, "isProductSearch", Boolean.FALSE);
		// model.addAttribute("isBegin", "1");
		model.addAttribute("isProductSearch", isProductSearch);
		con.put("isProductSearch", isProductSearch);
		Page<ProductEntity> pages = productService.findFrontProductPage(pageable, con);
		model.addAttribute("page", pages);
		// 产品搜索 20160425
		if (isProductSearch) {
			model.addAttribute("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
			List<String> parentIndustryIds = new ArrayList<String>();
			for (ProductEntity productEnty : pages.getCont()) {
				if (productEnty.getIndustryClass() != null && !"".equals(productEnty.getIndustryClass())) {
					List<Map<String, Object>> parentIdList = industryClassService
							.getParentIds(productEnty.getIndustryClass().split(","));
					String parentIdsStr = "";
					for (Map<String, Object> map : parentIdList) {
						parentIdsStr += map.get("parentId") + ",";
					}
					parentIndustryIds.add(parentIdsStr);
				}
			}
			model.addAttribute("parentIndustryIds", parentIndustryIds);
		}
		// 帮助信息
		model.addAttribute("helps", helpService.findAll());
		return "/template/front/searchPro/list";
	}

	@RequestMapping(value = "/listAll.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String ListAll(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = SEARCHPAGESIZE + "") int pageSize, // 页记录数
			@RequestParam(required = false, defaultValue = "") String keyword, // 搜索关键字
			@RequestParam(required = false) Long[] proClass, // 搜索关键字
			Model model, HttpServletRequest request) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		if (!keyword.isEmpty()) {
			con.put("keyword", keyword);
		}
		if (proClass != null) {
			con.put("proClass", proClass);
		}
		Page<ProductEntity> pages = productService.findFrontProductPage(pageable, con);
		model.addAttribute("page", pages);
		Boolean isProductSearch = ReqUtil.getBoolean(request, "isProductSearch", Boolean.FALSE);
		model.addAttribute("isProductSearch", isProductSearch);
		if (isProductSearch) {
			model.addAttribute("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
			List<String> parentIndustryIds = new ArrayList<String>();
			for (ProductEntity productEnty : pages.getCont()) {
				if (productEnty.getIndustryClass() != null && !"".equals(productEnty.getIndustryClass())) {
					List<Map<String, Object>> parentIdList = industryClassService
							.getParentIds(productEnty.getIndustryClass().split(","));
					String parentIdsStr = "";
					for (Map<String, Object> map : parentIdList) {
						parentIdsStr += map.get("parentId") + ",";
					}
					parentIndustryIds.add(parentIdsStr);
				}
			}
			model.addAttribute("parentIndustryIds", parentIndustryIds);
		}
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		return "/template/front/searchPro/listAll";
	}

	@RequestMapping(value = "/dataJson", method = { RequestMethod.POST, RequestMethod.GET })
	public String dataJson(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "7") int pageSize, // 页记录数
			@RequestParam(required = false) String popuOrder, // 人气排序
			@RequestParam(required = false) String priceOrder, // 价格排序
			@RequestParam(required = false) String keyword, // 搜索关键字
			@RequestParam(required = false) Long[] serTypeIds, // 服务类型
			@RequestParam(required = false) Long[] indClass, // 行业分类
			@RequestParam(required = false) Long[] proClass, // 产品分类
			@RequestParam(required = false) Long[] brands, // 品牌
			@RequestParam(required = false) String isSelf, // 是否自营
			@RequestParam(required = false) String isNew, // 是否全新
			@RequestParam(required = false) String brandste, // 品牌名称
			@RequestParam(required = false) String proClasste, // 产品分类名称
			@RequestParam(required = false) String indClasste, // 行业分类名称
			@RequestParam(required = false) String serTypeIdste, // 服务类型名称
			Model model, HttpServletRequest request, HttpServletResponse response) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		// 产品搜索 20160425
		Boolean isProductSearch = ReqUtil.getBoolean(request, "isProductSearch", Boolean.FALSE);
		List<Filter> flist = new ArrayList<Filter>();
		// 条件放置集合
		Map con = new HashMap<String, Object>();

		con.put("popuOrder", popuOrder);
		con.put("priceOrder", priceOrder);
		con.put("keyword", keyword);
		con.put("serTypeIds", serTypeIds);
		con.put("indClass", indClass);
		con.put("proClass", proClass);
		con.put("brands", brands);
		con.put("isSelf", isSelf);
		con.put("isNew", isNew);
		con.put("isProductSearch", isProductSearch);

		// 如果服务类型为空或者为一个以上(不包含一个)则搜索的行改为25行
		if (serTypeIds != null && serTypeIds.length != 1) {
			pageable = new Pageable(pageNum, SEARCHPAGESIZE);
		}
		String strListUrl = "";

		// model.addAttribute("page",
		// productSaleService.findFrontProductSalePage(pageable,con));
		Page<ProductEntity> pages = productService.findFrontProductPage(pageable, con);
		model.addAttribute("page", pages);
		
		model.addAttribute("brands5", brands);
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("isNew", isNew);
		model.addAttribute("popuOrder", popuOrder);
		model.addAttribute("priceOrder", priceOrder);
		model.addAttribute("isSelf", isSelf);
		model.addAttribute("serTypeIds", serTypeIds);

		if (brands != null && brands.length > 0) {
			model.addAttribute("brands3", brands[0]+"");
			model.addAttribute("brandste3", brandste);
		}
		if (proClass != null && proClass.length > 0) {
			model.addAttribute("proClass3", proClass[0]+"");
			model.addAttribute("proClasste3", proClasste);
		}
		if (indClass != null && indClass.length > 0) {
			model.addAttribute("indClass3", indClass[0]+"");
			model.addAttribute("indClasste3", indClasste);
		}
		if (serTypeIds != null && serTypeIds.length > 0) {
			model.addAttribute("serTypeIds3", serTypeIds[0]+"");
			model.addAttribute("serTypeIdste3", serTypeIdste);
		}
			
		/*request.setAttribute("popuOrder", popuOrder);
		request.setAttribute("priceOrder", priceOrder);
		request.setAttribute("isNew", isNew);
		request.setAttribute("isSelf", isSelf);*/
		model.addAttribute("isBegin", "1");
		// 存放服务类型id
		if (serTypeIds != null && serTypeIds.length == 1) {
			model.addAttribute("typeId", serTypeIds[0]);
			ProducttypeEntity producttypeEntity = producttypeService.find(serTypeIds[0]);
			strListUrl = producttypeEntity.getListUrl();
		}
		System.out.println("strListUrl" + strListUrl);
		if (serTypeIds != null && serTypeIds.length > 1) {
			strListUrl = "";
		}
		// 价格类型
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			MemberGradeEntity grader = memberService.getCurrent().getMemberGradeEntity();
			if (grader != null) {
				model.addAttribute("priceType", grader.getPriceType());
			}
		}

		model.addAttribute("isProductSearch", isProductSearch);
		if (isProductSearch) {
			model.addAttribute("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
			List<String> parentIndustryIds = new ArrayList<String>();
			for (ProductEntity productEnty : pages.getCont()) {
				if (productEnty.getIndustryClass() != null && !"".equals(productEnty.getIndustryClass())) {
					List<Map<String, Object>> parentIdList = industryClassService
							.getParentIds(productEnty.getIndustryClass().split(","));
					String parentIdsStr = "";
					for (Map<String, Object> map : parentIdList) {
						parentIdsStr += map.get("parentId") + ",";
					}
					parentIndustryIds.add(parentIdsStr);
				}
			}
			model.addAttribute("parentIndustryIds", parentIndustryIds);
		}
		return "/template/front/include/dataLists/" + strListUrl + "List";
	}

	@RequestMapping(value = "/{id}.jhtml", method = { RequestMethod.POST, RequestMethod.GET })
	public String productTrasit(@PathVariable long id,HttpServletRequest request) {

        ProductEntity productEntity = productService.find(id);
		switch (productEntity.getProductType().getListUrl()) {
		case "sale":
			return "redirect:/productSale/" + productEntity.getProductSaleEntity().getId() + ".jhtml";
		case "buy":
			return "redirect:/productBuy/" + productEntity.getProductBuyEntity().getId() + ".jhtml";
		case "rent":
			return "redirect:/productRent/" + productEntity.getProductRentEntity().getId() + ".jhtml";
		case "wantRent":
			return "redirect:/productWantRent/" + productEntity.getProductWantRentEntity().getId() + ".jhtml";
		case "repair":
			return "redirect:/productRepair/" + productEntity.getProductRepairEntity().getId() + ".jhtml";
		case "wantRepair":
			return "redirect:/productWantRepair/" + productEntity.getProducWanRepairEntity().getId() + ".jhtml";
		case "autoTest":
			return "redirect:/productAutoTest/" + productEntity.getAutoTestEntity().getId() + ".jhtml";
		case "projectNeed":
			return "redirect:/productProjectNeed/" + productEntity.getProjectNeedEntity().getId() + ".jhtml";
		case "auction":
			return "redirect://";
		case "invite":
			return "redirect://";
		case "productTest":
			return "redirect:/productTest/" + productEntity.getProductTestEntity().getId() + ".jhtml";
		case "requireTest":
			return "redirect:/productTequireTest/" + productEntity.getRequireTestEntity().getId() + ".jhtml";
		case "calibration":
			return "redirect:/productCalibration/" + productEntity.getCalibrationEntity().getId() + ".jhtml";

		}
		return "";

	}

}