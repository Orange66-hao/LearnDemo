/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseController;
import net.boocu.project.entity.CalibrationEntity;
import net.boocu.project.service.CalibrationService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Message;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 客户计量校准
 * 
 * @author deng 20150118
 * @version 1.0
 */
@Controller("userPubCalibrationController")
public class UserPubCalibrationController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/userPub/calibration";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

	/** 错误信息 */
	private static final Message ERROR_MESSAGE = Message.error("信息有效期不能为空!");

	@Resource
	CalibrationService calibrationService;

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
	 * 客户计量校准
	 */
	@RequestMapping(value = { "/user_pub_calibration", "/user_pub_calibration.jhtml" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String user_pub_calibration(HttpServletRequest request, ModelMap model, HttpServletResponse response) {
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

			return TEMPLATE_PATH + "/user_pub_calibration";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/**
	 * ajax 客户计量校准保存
	 */
	@ResponseBody
	@RequestMapping(value = "/user_pub_calibration/user_pub_calibration", method = { RequestMethod.POST,
			RequestMethod.GET })
	public Message user_pub_calibrations(HttpServletRequest request, HttpServletResponse response, String brand,
			CalibrationEntity calibrationEntity, String[] brandName) {

		if (calibrationEntity.getProductEntity().getProductclass().getId() == null) {
			return ERROR_MESSAGE;
		}
		if (calibrationEntity.getProductEntity().getInforValidity() == null
				&& calibrationEntity.getProductEntity().getLongTerm() == 0) {
			return ERROR_MESSAGE;
		}

		MemberEntity memberEntity = memberService.getCurrent();

		// 判断是发布类型todo
		calibrationService.saveWithCalibration(calibrationEntity, brandName, brand);
		return SUCCESS_MESSAGE;

	}

}