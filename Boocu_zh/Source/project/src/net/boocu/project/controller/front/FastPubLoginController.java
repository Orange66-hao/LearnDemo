/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
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
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.bean.LoginBean;
import net.boocu.web.bean.RedirectBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.TokenEntity;
import net.boocu.web.enums.AccountLockTypeEnum;
import net.boocu.web.enums.LoginTypeEnum;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.enums.TokenMethodEnum;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.security.SecuritySetting;
import net.boocu.web.shiro.ShiroPrincipal;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 用户
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontFastPubLoginController")
public class FastPubLoginController extends BaseController {

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;

	@Resource
	private MemberService memberService;

	@Resource(name = "tokenServiceImpl")
	private TokenService tokenService;

	@Resource
	private RSAService rsaService;

	@Resource
	private HelpService helpService;

	@Resource
	private MessageService messageService;

	/**
	 * 登录页面
	 */
	@RequestMapping(value = { "/FastPubLogin", "/FastPubLogin.jhtml" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String index(RedirectBean redirectBean, LoginBean loginBean, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		if (currentMember != null) {
			return "redirect:/fastSale.jhtml";
		} else {
			MessageUtil errors = MessageUtil.getInstance();
			if (request.getMethod().equals(RequestMethod.POST.name())) {

				Message msg = validate(loginBean, request, response);
				if (msg.getType().equals(MessageTypeEnum.success)) {

					String returnUrl = redirectBean.getRedirectUrl() != null ? redirectBean.getRedirectUrl()
							: ReqUtil.getString(request, "returnUrl", "/user/user");
					return "redirect:" + returnUrl;
				}
				errors = MessageUtil.fromJson(msg.getCont());

			}
			RSAPublicKey publicKey = rsaService.generateKey(request);

			model.addAttribute("captchaId", UUID.randomUUID().toString());

			// add by fang 20150924
			model.addAttribute("redirectUrl", redirectBean.getRedirectUrl());
			System.out.println("+重定向" + redirectBean.getRedirectUrl());
			model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
			model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));

			// 获取站内信未读
			Map ms = new HashMap<>();
			ms.put("member", memberService.getCurrent());
			ms.put("state", 0);
			model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
			// 联系我们
			model.addAttribute("help", helpService.find(Filter.eq("title", "关于我们")));

			// 帮助信息
			model.addAttribute("helps", helpService.findAll());
			model.addAttribute("errors", errors);
			return "front/userCenter/fastPub/user_fast_login";
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param addr
	 *            （username）
	 */
	private void autoLoginCookie(HttpServletRequest request, HttpServletResponse response, String addr) {

		TokenEntity tokenEntity = null;

		Calendar c = Calendar.getInstance();

		c.add(Calendar.SECOND, MemberEntity.AUTOLOGIN_COOKIE_TIME);
		Date expiry = c.getTime();

		try {
			do {
				tokenEntity = tokenService.buildToken(TokenMethodEnum.auto_login, addr, expiry);
			} while (tokenEntity == null);

		} catch (Exception e) {

		}

		// 保存token下次访问验证
		tokenService.save(tokenEntity);

		// vcode 效验码防止篡改id和addr
		String tokenString = tokenService.getTokenString(memberService.getCurrentId(), tokenEntity);
		// 添加自动登录的cookie
		WebUtils.addCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME, tokenString,
				MemberEntity.AUTOLOGIN_COOKIE_TIME);

	}

	/**
	 * ajax 登录
	 */
	@RequestMapping(value = "/FastPubLogin/FastPubLogin", method = RequestMethod.POST)
	public @ResponseBody Message login(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
		System.out.println(rsaService.decryptParameter("password", request));
		;
		return validate(loginBean, request, response);

	}

	protected Message validate(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
		loginBean.setPassword(rsaService.decryptParameter("password", request));
		System.out.println("验证码:" + loginBean.getCaptcha());
		// 判断是否开放登录
		SecuritySetting setting = SettingUtils.get().getSecurity();
		if (!setting.getLoginEnabled()) {
			return MessageUtil.error("system", "登录功能已关闭");
		}
		// Bean Validation
		if (!verify(loginBean)) {

			return MessageUtil.error(MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误"));

		}

		// 验证验证码
		if (!captchaService.verify(CaptchaTypeEnum.login, loginBean.getCaptchaId(), loginBean.getCaptcha())) {
			System.out.println("here----3" + loginBean.getUsername() + "---" + loginBean.getPassword());
			return MessageUtil.error("captcha", "验证码错误");
		}

		// 获取会员
		MemberEntity pMember = null;
		if (setting.verifyLoginMethod(LoginTypeEnum.email) && StringUtils.contains(loginBean.getUsername(), "@")) {
			pMember = memberService.findByEmail(loginBean.getUsername());
		}
		if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.mobile)
				&& StringUtils.length(loginBean.getUsername()) == 11) {
			pMember = memberService.findByMobile(loginBean.getUsername());
		}
		if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.username)) {
			pMember = memberService.findByUsername(loginBean.getUsername());
		}

		// 验证会员
		if (pMember == null) {
			return MessageUtil.error(MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误"));
		}
		// 账号状态
		// if(pMember.getEnabled() ==2){
		// return MessageUtil.error("username","账号未激活，请先登录注册邮箱激活！");
		// }
		if (pMember.getEnabled() == 0) {
			return MessageUtil.error("username", "账号被禁用,请与系统管理员联系,联系电话:0755-86641139！");
		}

		// 判断是否会员被锁定
		if (pMember.getLocked()) {
			// 判断是否属于锁定范围
			if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {

				// 判断是否永久锁定
				if (setting.getAccountLockTime() == 0) {
					return MessageUtil.error("username", "账户已被锁定");
				}

				// 判断是否解锁会员
				if (setting.getAccountUnlocked(pMember.getLockedDate())) {
					pMember.setLoginFailureCount(0);
					pMember.setLocked(false);
					pMember.setLockedDate(null);
				} else {
					return MessageUtil.error("username", "账户已被锁定");
				}
			} else {
				pMember.setLoginFailureCount(0);
				pMember.setLocked(false);
				pMember.setLockedDate(null);
			}
		}

		// 判断是否密码错误
		if (!pMember.verifyLoginPassword(loginBean.getPassword())) {

			// 判断是否锁定会员
			pMember.setLoginFailureCount(pMember.getLoginFailureCount() + 1);
			if (setting.verifyAccountLock(AccountLockTypeEnum.member, pMember.getLoginFailureCount())) {
				pMember.setLocked(true);
				pMember.setLockedDate(new Date());
			}
			memberService.update(pMember);

			// 判断是否属于锁定范围
			if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
				return MessageUtil.error(
						MessageUtil.getInstance("username", "账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定")
								.push("password", "账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定"));
			} else {
				return MessageUtil.error(MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误"));
			}
		}

		// 登录成功
		pMember.setLoginIp(ReqUtil.getIpAddr(request));
		pMember.setLoginDate(new Date());
		pMember.setLoginFailureCount(0);
		memberService.update(pMember);

		// 删除RSA私钥
		// rsaService.removePrivateKey(request);

		// 重构Session
		WebUtils.refactorSession(request);
		HttpSession session = request.getSession();

		// 判断会员是否已登录
		if (memberService.authorized()) {
			session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
			WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
			WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
		}
		session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME,
				new ShiroPrincipal(pMember.getId(), pMember.getUsername()));
		// 添加了有效时间，与自动登录cookie有效时间一致
		WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, pMember.getUsername(),
				MemberEntity.AUTOLOGIN_COOKIE_TIME);
		// 若选择 记住我 时，添加自动登录cookie
		if (loginBean.getRemember() == 1) {
			autoLoginCookie(request, response, pMember.getUsername());
		}

		return SUCCESS_MESSAGE;

	}
}