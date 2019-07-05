/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.ProductEntity;
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

/**
 * Controller - 快捷发布信息管理
 * 
 * @author deng 20151125
 * @version 1.0
 */
@Controller("fastPubController")
public class FastPubController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/fastPubManage";

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
	 * 产品信息管理
	 */
	@RequestMapping(value = { "/fastPub", "/fastPub.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String userInforSale(@RequestParam(required = false, defaultValue = "1") int pageNum, // 审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize, // 审核页记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum1, // 待审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize1, // 待审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum2, // 未审核核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize2, // 未审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum3, // 首页推荐页码
			@RequestParam(required = false, defaultValue = "15") int pageSize3, // 首页推荐记录数
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {

		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		// 审核状态
		int appr = ReqUtil.getInt(request, "appr", 1);

		model.addAttribute("appr", appr);
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 帮组信息
		model.addAttribute("helps", helpService.findAll());

		// 已审核
		Pageable pageable = new Pageable(pageNum, pageSize);
		pageable.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
		pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("proClass", 3));
		model.addAttribute("page", productService.findPage(pageable));

		// 待审核
		Pageable pageable1 = new Pageable(pageNum1, pageSize1);
		pageable1.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
		pageable1.getFilters().add(Filter.eq("apprStatus", 0));
		pageable1.getFilters().add(Filter.eq("isDel", 0));
		pageable1.getFilters().add(Filter.eq("proClass", 3));
		model.addAttribute("page1", productService.findPage(pageable1));

		// 未通过
		Pageable pageable2 = new Pageable(pageNum2, pageSize2);
		pageable2.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
		pageable2.getFilters().add(Filter.eq("apprStatus", 2));
		pageable2.getFilters().add(Filter.eq("isDel", 0));
		pageable2.getFilters().add(Filter.eq("proClass", 3));
		model.addAttribute("page2", productService.findPage(pageable2));

		// 首页推荐
		Pageable pageable3 = new Pageable(pageNum3, pageSize3);
		pageable3.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
		pageable3.getFilters().add(Filter.eq("apprStatus", 1));
		pageable3.getFilters().add(Filter.eq("isDel", 0));
		pageable3.getFilters().add(Filter.eq("isPromSale", 1));
		pageable3.getFilters().add(Filter.eq("proClass", 3));
		model.addAttribute("page3", productService.findPage(pageable3));

		return TEMPLATE_PATH + "/fastPub";
	}

	// 修改产品信息
	@RequestMapping(value = "/edit_fastPub", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit_fastPub(HttpServletRequest request, HttpServletResponse response, Model model) {
		long id = ReqUtil.getLong(request, "id", 0l);

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 帮组信息
		model.addAttribute("helps", helpService.findAll());

		// 品牌
		model.addAttribute("brands", productBrandService.findAll());

		ProductEntity productEntity = productService.find(id);
		if (productEntity.getProductType().getId() == 1) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/sale/edit_fastSale";
		} else if (productEntity.getProductType().getId() == 2) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/buy/edit_fastBuy";
		} else if (productEntity.getProductType().getId() == 3) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/rent/edit_fastRent";
		} else if (productEntity.getProductType().getId() == 4) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/wantRent/edit_fastWantRent";
		} else if (productEntity.getProductType().getId() == 5) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/repair/edit_fastRepair";
		} else if (productEntity.getProductType().getId() == 6) {
			model.addAttribute("item", productEntity);
			return "/front/userCenter/fastPubManage/wantRepair/edit_fastWantRepair";
		} else {
			return null;
		}
	}
}