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

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.RecruitEntity;
import net.boocu.project.enums.productType;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Controller -
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontUserController")
@RequestMapping("/user")
public class UserController {

	private static final String TEMPATH = "/template/front/userCenter";

	@Resource
	MemberService memberService;

	@Resource
	ProductService productService;

	@Resource
	HelpService helpService;

	@Resource
	ProducttypeService proTypeService;

	@Resource
	private MessageService messageService;

	@RequestMapping(value = { "user.jspx", "user" }, method = { RequestMethod.POST, RequestMethod.GET })
	public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		if (currentMember != null) {
			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);

			Filter member = Filter.eq("memberEntity", currentMember);
			Filter del = Filter.eq("isDel", 0);

			// 所有发布的商品
			model.addAttribute("allProduct", productService.findList(member, del));
			// 销售
			Filter sale = Filter.eq("productType", proTypeService.find(1l));
			model.addAttribute("sale", productService.findList(member, del, sale));

			// 求购
			Filter buy = Filter.eq("productType", proTypeService.find(2l));
			model.addAttribute("buy", productService.findList(member, del, buy));

			// 租赁
			Filter rent = Filter.eq("productType", proTypeService.find(3l));
			model.addAttribute("rent", productService.findList(member, del, rent));

			// 求租
			Filter wantRent = Filter.eq("productType", proTypeService.find(4l));
			model.addAttribute("wantRent", productService.findList(member, del, wantRent));

			// 维修
			Filter repait = Filter.eq("productType", proTypeService.find(5l));
			model.addAttribute("repair", productService.findList(member, del, repait));

			// 求修
			Filter wantrepair = Filter.eq("productType", proTypeService.find(6l));
			model.addAttribute("wantRepair", productService.findList(member, del, wantrepair));
			// 自动化测试方案
			Filter autoTest = Filter.eq("productType", proTypeService.find(7l));
			model.addAttribute("autoTest", productService.findList(member, del, autoTest));
			// 方案需求
			Filter projectNeed = Filter.eq("productType", proTypeService.find(8l));
			model.addAttribute("projectNeed", productService.findList(member, del, projectNeed));
			// 拍卖
			Filter auction = Filter.eq("productType", proTypeService.find(9l));
			model.addAttribute("auction", productService.findList(member, del, auction));
			// 招标
			Filter invite = Filter.eq("productType", proTypeService.find(10l));
			model.addAttribute("invite", productService.findList(member, del, invite));

			// 产品测试
			Filter productTest = Filter.eq("productType", proTypeService.find(11l));
			model.addAttribute("productTest", productService.findList(member, del, productTest));
			// 计量校准
			Filter calibration = Filter.eq("productType", proTypeService.find(13l));
			model.addAttribute("calibration", productService.findList(member, del, calibration));

			// 测试需求
			Filter requireTest = Filter.eq("productType", proTypeService.find(12l));
			model.addAttribute("requireTest", productService.findList(member, del, requireTest));
			return TEMPATH + "/user";
		} else {
			return "redirect:/login.jhtml";
		}

	}
	@RequestMapping(value = { "userfirst" }, method = { RequestMethod.POST, RequestMethod.GET })
	public String indexfirst(HttpServletRequest request, HttpServletResponse response, Model model) {
		//获取登录时保存到session的数据
		model.addAttribute("item", (MemberEntity) request.getSession().getAttribute("loginUser"));
		return "/template/front/login/user_updatepass4";
	}
}