/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.security.interfaces.RSAPublicKey;
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
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.bean.regist.RegistBean;
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

/**
 * Controller - 用户
 * 
 * @author fang 20151012
 * @version 1.0
 */
@Controller("frontfastRegistController")
public class FastRegistController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/fastPubManage/fastRegister";

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
	@RequestMapping(value = { "/fastRegister", "/fastRegister", "/fastRegister.jhtml",
			"/fastRegister.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String fastRegister(RegistBean registBean, RedirectBean redirectBean, String referrer,
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {

		// 判断会员是否已登录
		if (memberService.authorized()) {
			return "redirect:/";
		}

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 判断是否重定向URL非站内链接
		BasicSetting setting = SettingUtils.get().getBasic();
		if (!verify(redirectBean) || !setting.verifyInboundLink(redirectBean.getRedirectUrl(), request)) {
			redirectBean.setRedirectUrl(null);
		}
	
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

		return TEMPLATE_PATH + "/fastRegister";
	}

	/**
	 * 注册成功跳转页面
	 */
	@RequestMapping(value = "/fastRegister_sub.jhtml", method = { RequestMethod.GET, RequestMethod.POST })
	public String register_sub(HttpServletRequest request, HttpServletResponse response, Model model, String email) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		// 帮组信息
		model.addAttribute("helps", helpService.findAll());

		model.addAttribute("email", email);

		return TEMPLATE_PATH + "/fastRegister_sub";
	}

	/**
	 * ajax 注册
	 */
	@RequestMapping(value = "/fastRegister/fastRegister", method = RequestMethod.POST)
	public @ResponseBody Message mailRegist(RegistBean registBean, HttpServletRequest request,
			HttpServletResponse response) {
		System.out.println("进入注册--------------");
		return validate(registBean, request, response);

	}

	protected Message validate(RegistBean mailRegistBean, HttpServletRequest request, HttpServletResponse response) {
		String password = ((int) (100000 + Math.random() * 100000)) + "";
		String email = ReqUtil.getString(request, "email", "");
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
		// 验证邮箱地址是否在
		if (memberService.emailExists(mailRegistBean.getEmail())) {
			return MessageUtil.error("email", "邮箱地址已存在");
		}

		// 删除RSA私钥
		rsaService.removePrivateKey(request);

		// 新建会员
		MemberEntity member = new MemberEntity();
		BeanUtils.copyProperties(mailRegistBean, member);
		member.setEnabled(1);
		member.setUserType(UserTypeEnum.buyer);
		member.setPassword(DigestUtils.md5Hex(password));
		member.setUsername(email);
		member.setMemberShip(MemberShipEnum.personal);
		memberService.save(member);

		Long idLong = member.getId();
		if (!email.isEmpty()) {
			// 往邮箱里发送确认邮件
			String urllink = "";
			Blowfish bfish = new Blowfish("jiahuijie");
			urllink = "您已成功注册晧辰仪联网会员，你的登录名为" + email + "，系统为您随机生成了密码(" + password
					+ ")，点击激活账号，并及时前处用户中--->资料管理-----》密码修改中修改你的密码：" + request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort() + request.getContextPath()
					+ "/EmailEnabled.jhtml?reqCode=activate&id=" + bfish.encryptString(Long.toString(member.getId()));
			// "您在晧辰仪联网平台上发布的商品已成功，系统为您随机生成了密码("+password+")，点击激活账号并重置密码： "
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost("smtp.ym.163.com");
			mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			mailInfo.setUserName("center@wl95.com");
			mailInfo.setPassword("scncwcy821215");// 您的邮箱密码
			mailInfo.setFromAddress("center@wl95.com");
			mailInfo.setToAddress(email);
			mailInfo.setSubject("晧辰仪器设备物联网平台 注册账号激活");
			mailInfo.setContent(urllink);
			// 这个类主要来发送邮件
			SimpleMailSender sms = new SimpleMailSender();
			boolean sresult = sms.sendTextMail(mailInfo);
		}

		// 重构Session
		WebUtils.refactorSession(request);
		HttpSession session = request.getSession();

		// sendMail(request, mailRegistBean.getEmail(),
		// TokenMethodEnum.user_regist,"/regist/ckemail");
		return SUCCESS_MESSAGE;
	}

}