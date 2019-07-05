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

import net.boocu.framework.util.FileUtils.FalseFileFilter;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller -
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontSalesController")
@RequestMapping("/sales")
public class SalesController {

	private static final String TEMPATH = "/template/front/sales";

	@Resource
	private SalesService salesService;

	@Resource
	private ProductService productService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private ProducttypeService producttypeService;

	@Resource
	private HelpService helpService;

	@Resource
	private IndustryClassService industryClassService;

	@Resource
	private MemberService memberService;

	@Resource
	private MessageService messageService;

	@Resource
	private ProductSaleService productSaleService;
	@Resource
	private ProductBrandService productBrandService;

	@RequestMapping(value = "list.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String index(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "12") int pageSize, // 页记录数
			HttpServletRequest request, HttpServletResponse response, Model model) {
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		Pageable pageable = new Pageable(pageNum, pageSize);
		pageable.getFilters().add(Filter.eq("isPromSale", 1));
		pageable.getFilters().add(Filter.eq("display", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		// 促销图片
		model.addAttribute("page", salesService.findFrontSalesPage(pageable, con));

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		return TEMPATH + "/sale";
	}

	@RequestMapping(value = "/dataJson", method = { RequestMethod.POST, RequestMethod.GET })
	public String dataJson(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "12") int pageSize, // 页记录数
			@RequestParam(required = false) String popuOrder, // 人气排序
			@RequestParam(required = false) String priceOrder, // 价格排序
			@RequestParam(required = false) String timeOrder, // 更新时间排序
			@RequestParam(required = false) String keyword, // 搜索关键字
			@RequestParam(required = false) String isSelf, // 是否自营
			@RequestParam(required = false) String isNew, // 是否全新
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		List<Filter> flist = new ArrayList<Filter>();
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		con.put("popuOrder", popuOrder);
		con.put("priceOrder", priceOrder);
		con.put("timeOrder", timeOrder);
		con.put("keyword", keyword);
		con.put("isSelf", isSelf);
		con.put("isNew", isNew);
		String strListUrl = "";
		// model.addAttribute("page",productSaleService.findFrontProductSalePage(pageable,con));
		model.addAttribute("page", salesService.findFrontSalesPage(pageable, con));
		return "/template/front/include/dataLists/" + strListUrl + "salesList";
	}
}