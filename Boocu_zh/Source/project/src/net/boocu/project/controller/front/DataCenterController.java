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

import net.boocu.project.service.DataCenterService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;

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
@Controller("frontDataCenterController")
@RequestMapping("/dataCenter")
public class DataCenterController {

	private static final String TEMPATH = "/template/front/dataCenter";

	@Resource
	private DataCenterService dataCenterService;

	@Resource
	private HelpService helpService;

	@Resource
	private ProductBrandService productBrandService;

	@RequestMapping(value = "list.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String dataCenter(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "4") int pageSize, // 页记录数
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		// 启用
		pageable.getFilters().add(Filter.eq("status", 1));
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		// 产品品牌
		model.addAttribute("brands", productBrandService.findAll());

		model.addAttribute("page", dataCenterService.findFrontDataCenterPage(pageable, con));

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		return TEMPATH + "/dataCenter";

	}

	@RequestMapping(value = "/dataJson", method = { RequestMethod.POST, RequestMethod.GET })
	public String dataJson(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "4") int pageSize, // 页记录数
			@RequestParam(required = false) String brands, // 人气排序
			@RequestParam(required = false) String keyword, // 搜索关键字
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		List<Filter> flist = new ArrayList<Filter>();
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		con.put("brands", brands);
		con.put("keyword", keyword);
		String strListUrl = "";
		// model.addAttribute("page",
		// productSaleService.findFrontProductSalePage(pageable,con));
		model.addAttribute("page", dataCenterService.findFrontDataCenterPage(pageable, con));
		return "/template/front/include/dataLists/" + strListUrl + "dataCenterList";
	}

}