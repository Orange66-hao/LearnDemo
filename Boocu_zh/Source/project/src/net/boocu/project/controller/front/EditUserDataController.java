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
import net.boocu.web.Message;
import net.boocu.web.bean.RedirectBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.bean.regist.RegistBean;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberEntity.MemberShipEnum;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.basic.BasicSetting;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.binary.*;
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
@Controller("editUserDataController")
public class EditUserDataController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/dataManage/editData";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("修改邮箱");

	/** 成功2 */
	private static final Message SUCCESS_MESSAGE2 = Message.success("未改邮箱");

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
	private HelpService helpService;

	@Resource
	private MessageService messageService;

	/**
	 * 修改用户信息
	 */
	@RequestMapping(value = { "/editUserData.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String editUserData(RegistBean registBean, RedirectBean redirectBean, String referrer,
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 判断会员是否登录
		if (memberService.authorized()) {
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
			// 获取站内信未读
			Map ms = new HashMap<>();
			ms.put("member", memberService.getCurrent());
			ms.put("state", 0);
			ms.put("type", 1);
			model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
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

			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);

			// 友情推荐 add by fang 20150906
			model.addAttribute("friends", friendsService.findAll());

			// 帮助信息
			model.addAttribute("helps", helpService.findAll());

			if (currentMember.getMemberShip() == MemberShipEnum.personal) {
				model.addAttribute("item", currentMember);
				return TEMPLATE_PATH + "/user_personal";
			} else {
				model.addAttribute("item", currentMember);
				return TEMPLATE_PATH + "/user_enterprise";
			}
		} else {
			return "redirect:/login.jhtml";
		}

	}

	/**
	 * 用户信息修改成功跳转页面
	 */
	@RequestMapping(value = "/editUserData/register_sub", method = { RequestMethod.GET, RequestMethod.POST })
	public String register_sub(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		return TEMPLATE_PATH + "/register_sub";
	}

	/**
	 * 检查邮箱
	 */
	@RequestMapping(value = { "/editUserData/check_email", "/editUserData/check_email" }, method = RequestMethod.POST)
	public @ResponseBody String checkEmail(String email, Long id) {
		MemberEntity memberEntity = memberService.find(id);
		if (StringUtils.isBlank(email)) {
			return "false";
		}
		if (email.equals(memberEntity.getEmail())) {
			return "true";
		}
		// 判断邮箱是否存在
		if (memberService.emailExists(email)) {
			return "false";
		} else {
			return "true";
		}
	}

	/**
	 * ajax 个人注册用户修改
	 */
	@RequestMapping(value = "/editUserData/editUserData", method = RequestMethod.POST)
	public @ResponseBody Message mailRegist(RegistBean registBean, HttpServletRequest request,
			HttpServletResponse response) {
		return validate(registBean, request, response);

	}

	protected Message validate(RegistBean mailRegistBean, HttpServletRequest request, HttpServletResponse response) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberEntity = memberService.find(id);
		String password = memberEntity.getPassword();
		String username = memberEntity.getUsername();
		MemberShipEnum memberShip = memberEntity.getMemberShip();
		UserTypeEnum userType = memberEntity.getUserType();
		MemberGradeEntity memberGrade = memberEntity.getMemberGradeEntity();
//		String realName = memberEntity.getRealName();//取这个值没有作用
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

		String email = ReqUtil.getString(request, "email", "");

		// 验证邮箱地址是否在
		if (!email.equals(memberEntity.getEmail())) {
			if (memberService.emailExists(mailRegistBean.getEmail())) {
				return MessageUtil.error("email", "邮箱地址已存在");
			}
			if (!email.isEmpty()) {
				// 往邮箱里发送确认邮件
				String urllink = "";
				Blowfish bfish = new Blowfish("jiahuijie");

				urllink = "信息修改的账号激活：" + request.getScheme() + "://" + request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath()
						+ "/EmailEnabled.jhtml?reqCode=activate&id="
						+ bfish.encryptString(Long.toString(memberEntity.getId()));
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setMailServerHost("smtp.ym.163.com");
				mailInfo.setMailServerPort("25");
				mailInfo.setValidate(true);
				mailInfo.setUserName("center@wl95.com");
				mailInfo.setPassword("scncwcy821215");// 您的邮箱密码
				mailInfo.setFromAddress("center@wl95.com");
				mailInfo.setToAddress(email);
				mailInfo.setSubject("晧辰仪器设备物联网平台 账号激活");
				mailInfo.setContent(urllink);
				// 这个类主要来发送邮件
				SimpleMailSender sms = new SimpleMailSender();
				boolean sresult = sms.sendTextMail(mailInfo);
			}
			// 修改个人用户会员
			BeanUtils.copyProperties(mailRegistBean, memberEntity);
			memberEntity.setEnabled(1);
			memberEntity.setUserType(userType);
			memberEntity.setMemberGradeEntity(memberGrade);
			memberEntity.setMemberShip(memberShip);
			memberEntity.setPassword(password);
			memberEntity.setUsername(username);
//			memberEntity.setRealName(realName);//此处不用再把旧的值赋值给真实姓名这个属性
			memberService.update(memberEntity);
			// 重构Session
			WebUtils.refactorSession(request);
			HttpSession session = request.getSession();

			// sendMail(request, mailRegistBean.getEmail(),
			// TokenMethodEnum.user_regist,"/regist/ckemail");
			return SUCCESS_MESSAGE;
		} else {
			// 修改个人用户会员
			BeanUtils.copyProperties(mailRegistBean, memberEntity);//mailRegistBean:改变的对象, memberEntity:原来的对象
			memberEntity.setEnabled(2);
			memberEntity.setUserType(userType);
			memberEntity.setMemberGradeEntity(memberGrade);
			memberEntity.setMemberShip(memberShip);
			memberEntity.setPassword(password);
			memberEntity.setUsername(username);
//			memberEntity.setRealName(realName);//此处不用再把旧的值赋值给真实姓名这个属性
			memberService.update(memberEntity);
			// 重构Session
			WebUtils.refactorSession(request);
			HttpSession session = request.getSession();

			// sendMail(request, mailRegistBean.getEmail(),
			// TokenMethodEnum.user_regist,"/regist/ckemail");
			return SUCCESS_MESSAGE2;
		}

	}

}