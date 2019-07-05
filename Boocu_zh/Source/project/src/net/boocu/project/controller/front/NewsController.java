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

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.NewsEntity;
import net.boocu.project.entity.NewsEntity.NewsTypeEnum;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller - 
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontNewsController")
@RequestMapping("/news")
public class NewsController {
	
	private static final String TEMPATH = "/template/front/news";

	
	@Resource
	private NewsService newsService;
	
	@Resource
	private HelpService helpService;
	
	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private ProducttypeService producttypeService;
	
	@Resource
	private IndustryClassService industryClassService;
	
	@Resource
	private ProductBrandService productBrandService;
	@RequestMapping(value="list.jspx", method = {
			RequestMethod.POST, RequestMethod.GET })
	public String index(@RequestParam(required=false,defaultValue="1") int pageNum,//页码
			@RequestParam(required=false , defaultValue="20") int pageSize,//页记录数
 			HttpServletRequest request,
			HttpServletResponse response, Model model) {		
		
		Pageable pageable = new Pageable(pageNum,pageSize);
		
		//新闻资料	
		String newstype = ReqUtil.getString(request, "newstype", "");
		//推荐
		pageable.getFilters().add(Filter.eq("isrecommend", 1));
		model.addAttribute("newstype", newstype);
		if(!newstype.isEmpty()){
			pageable.getFilters().add(Filter.eq("newstype", NewsTypeEnum.valueOf(newstype)));
		}
		
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		
		model.addAttribute("page", newsService.findPage(pageable));
		
		//帮组信息
		model.addAttribute("helps",helpService.findAll());
		return TEMPATH+"/news";
	}
	
	@RequestMapping(value="/{id}.jhtml",method={RequestMethod.POST,RequestMethod.GET})
	public String proSaleDetail(
			@PathVariable long id,
			HttpServletRequest request,
			HttpServletResponse response, Model model){
		
		//帮组信息
		model.addAttribute("helps",helpService.findAll());
		
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		
		NewsEntity newsEntity = newsService.find(id);
		if(newsEntity != null){
			model.addAttribute("newsEntity", newsEntity);
			newsEntity.setReadtime(newsEntity.getReadtime()+1);
			newsService.update(newsEntity);
		}
		return TEMPATH+"/news_details";
	}
}