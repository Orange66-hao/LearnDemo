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
import net.boocu.project.entity.RecruitEntity;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.RecruitService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

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
@Controller("frontHrController")
@RequestMapping("/hr")
public class HrController {

	private static final String TEMPATH = "/template/front/hr";

	@Resource
	private RecruitService recruitService;

	@Resource
	private HelpService helpService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private IndustryClassService industryClassService;

	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private MemberService memberService;
	
	@Resource
	private MessageService messageService;

	@RequestMapping(value = "list.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String index(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "13") int pageSize, // 页记录数
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Pageable pageable = new Pageable(pageNum, pageSize);
		// 招聘
		model.addAttribute("page", recruitService.findPage(pageable));

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
	
		// 帮组信息
		model.addAttribute("helps", helpService.findAll());

		return TEMPATH + "/hr";
	}

	@RequestMapping(value = "/{id}.jhtml", method = { RequestMethod.POST, RequestMethod.GET })
	public String RecruitDetail(@PathVariable long id, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		RecruitEntity recruitEntity = recruitService.find(id);
		if (recruitEntity != null) {
			model.addAttribute("recruitEntity", recruitEntity);
			recruitEntity.setReadtime(recruitEntity.getReadtime() + 1);
			recruitService.update(recruitEntity);
		}
		return TEMPATH + "/hr_details";
	}
}