/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.project.service.AutoTestService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.ProjectNeedService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 客户自动化测试方案
 * 
 * @author deng 20151116
 * @version 1.0
 */
@Controller("userPubProjectNeedController")
public class UserPubProjectNeedController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/userPub/projectNeed";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

	/** 错误信息 */
	private static final Message ERROR_MESSAGE = Message.error("信息有效期不能为空!");

	@Resource
	ProjectNeedService projectNeedService;

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
	 * 自动化测试方案需求
	 */
	@RequestMapping(value = { "/user_pub_projectNeed", "/user_pub_projectNeed.jhtml" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String user_pub_projectNeed(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
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
			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);

			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			return TEMPLATE_PATH + "/user_pub_projectNeed";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/**
	 * ajax 自动化测试方案需求保存
	 */
	@ResponseBody
	@RequestMapping(value = "/user_pub_projectNeed/user_pub_projectNeed", method = { RequestMethod.POST,
			RequestMethod.GET })
	public Message user_pub_projectNeeds(HttpServletRequest request, HttpServletResponse response,
			ProjectNeedEntity projectNeedEntity, String[] brandName) {
		if (projectNeedEntity.getProductEntity().getInforValidity() == null) {
			return ERROR_MESSAGE;
		}

		MemberEntity memberEntity = memberService.getCurrent();

		// 判断是发布类型todo
		projectNeedService.saveWithNeed(projectNeedEntity, brandName);
		return SUCCESS_MESSAGE;

	}

}