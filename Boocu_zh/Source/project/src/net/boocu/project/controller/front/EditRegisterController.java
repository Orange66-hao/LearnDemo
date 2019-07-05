/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.enums.CaptchaTypeEnum;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.framework.util.WebUtils;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.bean.RedirectBean;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.bean.regist.RegistBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberEntity.MemberShipEnum;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.basic.BasicSetting;
import net.boocu.web.setting.security.SecuritySetting;
import net.boocu.web.shiro.ShiroPrincipal;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Controller - 用户
 * 
 * @author fang 20151012
 * @version 1.0
 */
@Controller("frontEditRegisterController")
public class EditRegisterController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "/front/userCenter/editRegister";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "rsaServiceImpl")
	private RSAService rsaService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource(name = "tokenServiceImpl")
	private TokenService tokenService;

	@Resource
	private FriendsService friendsService;
	@Resource
	private MessageService messageService;
	@Resource
	private HelpService helpService;

	/**
	 * 注册
	 */
	@RequestMapping(value = { "/editRegister.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String editRegister(RegistBean registBean, RedirectBean redirectBean, String referrer,
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {

		// 判断会员是否已登录
		if (!memberService.authorized()) {
			return "redirect:/login";
		}

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		Long id = ReqUtil.getLong(request, "id", 0l);
	
		// post 提交
		MessageUtil errors = MessageUtil.getInstance();
		if (request.getMethod().equals(RequestMethod.POST.name())) {
			Message msg = validate(registBean, request, response);
			if (msg.getType().equals(MessageTypeEnum.success)) {
				String returnUrl = redirectBean.getRedirectUrl() != null ? redirectBean.getRedirectUrl()
						: ReqUtil.getString(request, "returnUrl", "/index");
				return "redirect:" + returnUrl;
			}
			System.out.println(msg.getCont());
			errors = MessageUtil.fromJson(msg.getCont());

		}
		// get 或表单验证失败
		model.addAttribute("registBean", registBean);
		model.addAttribute("errors", errors);
		// 密钥
		RSAPublicKey publicKey = rsaService.generateKey(request);
		model.addAttribute("captchaId", UUID.randomUUID().toString());
		model.addAttribute("redirectUrl", redirectBean.getRedirectUrl());
		model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
		model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		if (StringUtils.isNotBlank(referrer) && memberService.exists("username", referrer, true)) {
			model.addAttribute("referrer", referrer);
		}

		// 友情推荐 add by fang 20150906
		model.addAttribute("friends", friendsService.findAll());

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		MemberEntity memberEntity = memberService.find(id);
		if (memberEntity.getMemberShip() == MemberShipEnum.personal) {
			model.addAttribute("item", memberEntity);
			return TEMPLATE_PATH + "/editPersonal";
		} else {
			model.addAttribute("item", memberEntity);
			return TEMPLATE_PATH + "/editEnterPrise";
		}

	}

	/**
	 * ajax 注册
	 */
	@RequestMapping(value = "/editRegister/editRegister", method = RequestMethod.POST)
	public @ResponseBody Message mailRegist(RegistBean registBean, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("修改个人信息--------------");
		return validate(registBean, request, response);

	}

	protected Message validate(RegistBean mailRegistBean, HttpServletRequest request, HttpServletResponse response) {
		String pwd = rsaService.decryptParameter("password", request);
		Long id = ReqUtil.getLong(request, "id", 0l);
		String username = ReqUtil.getString(request, "username", "");
		String password = ReqUtil.getString(request, "password", "");
		System.out.println("username///////////" + username);
		System.out.println("password................." + password);
		mailRegistBean.setPassword(pwd);
		// 判断是否开放注册
		SecuritySetting setting = SettingUtils.get().getSecurity();
		if (!setting.getRegistEnabled()) {
			return MessageUtil.error("system", "注册功能已关闭");
		}

		// 验证验证码
		if (!captchaService.verify(CaptchaTypeEnum.regist, request.getParameter("captchaId"),
				mailRegistBean.getCaptcha())) {
			return MessageUtil.error("captcha", "验证码错误");
		}

		// 删除RSA私钥
		rsaService.removePrivateKey(request);

		// 新建会员
		MemberEntity member = memberService.find(id);
		BeanUtils.copyProperties(mailRegistBean, member);
		member.setEnabled(2);
		member.setUserType(UserTypeEnum.buyer);
		member.setPassword(password);
		memberService.update(member);

		// 重构Session
		WebUtils.refactorSession(request);
		HttpSession session = request.getSession();

		System.out.println("success.............................." + password);
		// sendMail(request, mailRegistBean.getEmail(),
		// TokenMethodEnum.user_regist,"/regist/ckemail");
		return SUCCESS_MESSAGE;
	}

}