/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.io.IOException;
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
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.bean.LoginBean;
import net.boocu.web.bean.RedirectBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
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


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.*;
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
@Controller("frontLoginController")
public class LoginController extends BaseController{
 
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
     *  登录页面 
     */
    @RequestMapping(value = {"/login","/login.jhtml"},method ={ RequestMethod.GET,RequestMethod.POST})
    public String index(RedirectBean redirectBean,LoginBean loginBean,HttpServletRequest request, HttpServletResponse response, Model model) {
        	
    	MessageUtil errors = MessageUtil.getInstance();
        if(request.getMethod().equals(RequestMethod.POST.name())){
	        
        	Message msg = validate(loginBean, request, response);
	    	if(msg.getType().equals(MessageTypeEnum.success)){

	    		String returnUrl = redirectBean.getRedirectUrl() != null?redirectBean.getRedirectUrl():ReqUtil.getString(request, "returnUrl", "/user/user");
		        return "redirect:"+returnUrl;		    		
	    	}
	    	errors = MessageUtil.fromJson(msg.getCont());
	    	 
        }
        RSAPublicKey publicKey = rsaService.generateKey(request);
        
        model.addAttribute("captchaId", UUID.randomUUID().toString());
        
        //add by fang 20150924
        model.addAttribute("redirectUrl", redirectBean.getRedirectUrl());
        System.out.println("+重定向" +redirectBean.getRedirectUrl());

        model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
        
        //联系我们
        model.addAttribute("help", helpService.find(Filter.eq("title", "关于我们")));
        
        model.addAttribute("errors", errors);
		return "front/login/login";
    	
    }
    /**
     * 
     * @param request
     * @param response
     * @param addr （username）
     */
    private void autoLoginCookie(HttpServletRequest request, HttpServletResponse response, String addr){

    	TokenEntity tokenEntity = null;
    	
    	Calendar c = Calendar.getInstance();
    	
    	c.add(Calendar.SECOND, MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	Date expiry = c.getTime();
    	
    	try {
    		do{
    			tokenEntity = tokenService.buildToken(TokenMethodEnum.auto_login, addr,expiry);
    		}while(tokenEntity == null);
    		
		} catch (Exception e) {
			
		}

    	//保存token下次访问验证
		tokenService.save(tokenEntity);
    	
    	//vcode 效验码防止篡改id和addr
    	String tokenString = tokenService.getTokenString(memberService.getCurrentId(), tokenEntity);	
    	//添加自动登录的cookie
    	WebUtils.addCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME, tokenString ,MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	
    }
    
    
    /**
     * ajax 登录 
     */
    @RequestMapping(value = "/login/login",method = RequestMethod.POST)
    public @ResponseBody
    Message login(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	System.out.println(rsaService.decryptParameter("password", request));
    	return validate(loginBean, request, response);
    	
    }
    
    protected Message validate(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	loginBean.setPassword(rsaService.decryptParameter("password", request));
    	System.out.println("验证码:"+loginBean.getCaptcha());
    	// 判断是否开放登录
    	SecuritySetting setting = SettingUtils.get().getSecurity();
    	if (!setting.getLoginEnabled()) {
    		return MessageUtil.error("system", "登录功能已关闭");
    	}
    	// Bean Validation
    	if (!verify(loginBean)) {
    		return new Message( MessageTypeEnum.error,"用户名或者密码错误");
    	}
    	
    	// 验证验证码
    	if (!captchaService.verify(CaptchaTypeEnum.login, loginBean.getCaptchaId(), loginBean.getCaptcha())) {
    		System.out.println("here----3"+loginBean.getUsername()+"---"+loginBean.getPassword()); 
    		/*return MessageUtil.error("captcha", "验证码错误");*/
    		return new Message( MessageTypeEnum.error,"验证码错误");
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
//    		return MessageUtil.error(
//    				/*MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误")*/
//    				MessageUtil.getInstance("账户", "或密码错误")
//    				);
    		return new Message( MessageTypeEnum.error,"用户名或者密码错误");
    	}    
    	// 账号状态
//		if(pMember.getEnabled() ==2){
//			return MessageUtil.error("username","账号未激活，请先登录注册邮箱激活！");
//		}
    	if(pMember.getEnabled() ==0){
    		return MessageUtil.error("账号被禁用","请与系统管理员联系,联系电话:0755-86641139！");
    	}
    	
    	// 判断是否会员被锁定
    	if (pMember.getLocked()) {
    		// 判断是否属于锁定范围
    		if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
    			
    			// 判断是否永久锁定
    			if (setting.getAccountLockTime() == 0) {
    				/*return MessageUtil.error("username","账户已被锁定");*/
    				return new Message( MessageTypeEnum.error,"账户已被锁定");
    			}
    			
    			// 判断是否解锁会员
    			if (setting.getAccountUnlocked(pMember.getLockedDate())) {
    				pMember.setLoginFailureCount(0);
    				pMember.setLocked(false);
    				pMember.setLockedDate(null);
    			} else {
    				/*return MessageUtil.error("username","账户已被锁定");*/
    				return new Message( MessageTypeEnum.error,"账户已被锁定");
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
    			return new Message( MessageTypeEnum.error,"账户或密码错误,若连续" + setting.getAccountLockCount() + "次输错账户将被锁定");
    		} else {
//    			return MessageUtil.error(
//    					/*MessageUtil.getInstance("username","账户或密码错误").push("password","账户或密码错误")*/
//    					MessageUtil.getInstance("账户","或密码错误")
//    					);
    			return new Message( MessageTypeEnum.error,"账户或密码错误");
    		}
    	}
    	
    	// 登录成功
    	pMember.setLoginIp(ReqUtil.getIpAddr(request));
    	pMember.setLoginDate(new Date());
    	pMember.setLoginFailureCount(0);
    	memberService.update(pMember);
    	
    	// 删除RSA私钥
//        rsaService.removePrivateKey(request);
    	
    	// 重构Session
    	WebUtils.refactorSession(request);
    	HttpSession session = request.getSession();
    	
    	//把用户放到session中，供后续使用
    	session.setAttribute("loginUser", pMember);
    	
    	if (pMember != null) {
    		MemberGradeEntity grader = pMember.getMemberGradeEntity();
    		if (grader != null) {
    			session.setAttribute("priceType", grader.getPriceType());
    		}
    	}
    	
    	// 获取站内信未读
    	Map ms = new HashMap<>();
    	ms.put("member", pMember);
    	ms.put("state", 0);
    	ms.put("type", 1);
    	session.setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
    	
    	session.setAttribute("helps", helpService.findAll());
    	
    	// 判断会员是否已登录
    	if (memberService.authorized()) {
    		session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
    	}
    	session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new ShiroPrincipal(pMember.getId(), pMember.getUsername()));
    	//添加了有效时间，与自动登录cookie有效时间一致
    	WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, pMember.getUsername(),MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	//若选择 记住我 时，添加自动登录cookie
		if(loginBean.getRemember() == 1){
    		autoLoginCookie( request,  response, pMember.getUsername());
    	}
    	
    	//会员邀请账号，当第一次登陆时跳转到密码修改页面
    	if (pMember.getFirst() == 1) {
			return Message.success("0");
		}
    	
    	return SUCCESS_MESSAGE;
    	
    }
    /**
     * ajax qq登录 
     */
    @RequestMapping(value = "/login/loginqq",method = RequestMethod.POST)
    public @ResponseBody
    Message loginqq(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	return validateqq(loginBean, request, response);
    	
    }
    
    protected Message validateqq(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	// 判断是否开放登录
    	SecuritySetting setting = SettingUtils.get().getSecurity();
    	if (!setting.getLoginEnabled()) {
    		return MessageUtil.error("system", "登录功能已关闭");
    	}
    	
    	// 获取会员
    	MemberEntity pMember = null;
    	if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.username)) {
    		pMember = memberService.findByUsername(loginBean.getUsername());
    	}
    	
    	// 登录成功
    	pMember.setLoginIp(ReqUtil.getIpAddr(request));
    	pMember.setLoginDate(new Date());
    	pMember.setLoginFailureCount(0);
    	memberService.update(pMember);
    	
    	// 删除RSA私钥
        rsaService.removePrivateKey(request);
    	
    	// 重构Session
    	WebUtils.refactorSession(request);
    	HttpSession session = request.getSession();
    	
    	//把用户放到session中，供后续使用
    	session.setAttribute("loginUser", pMember);
    	
    	if (pMember != null) {
    		MemberGradeEntity grader = pMember.getMemberGradeEntity();
    		if (grader != null) {
    			session.setAttribute("priceType", grader.getPriceType());
    		}
    	}
    	
    	// 获取站内信未读
    	Map ms = new HashMap<>();
    	ms.put("member", pMember);
    	ms.put("state", 0);
    	ms.put("type", 1);
    	session.setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
    	
    	session.setAttribute("helps", helpService.findAll());
    	
    	// 判断会员是否已登录
    	if (memberService.authorized()) {
    		session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
    	}
    	session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new ShiroPrincipal(pMember.getId(), pMember.getUsername()));
    	//添加了有效时间，与自动登录cookie有效时间一致
    	WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, pMember.getUsername(),MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	//若选择 记住我 时，添加自动登录cookie
    	if(loginBean.getRemember() == 1){
    		autoLoginCookie( request,  response, pMember.getUsername());
    	}
    	
    	
    	
    	return SUCCESS_MESSAGE;
    	
    }
    /**
     * 手机快捷登录 
     */
    @RequestMapping(value = "/login/loginphone",method = RequestMethod.POST)
    public @ResponseBody
    Message loginphone(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	return validatephone(loginBean, request, response);
    	
    }
    
    protected Message validatephone(LoginBean loginBean, HttpServletRequest request, HttpServletResponse response) {
    	// 判断是否开放登录
    	HttpSession session = request.getSession();
    	SecuritySetting setting = SettingUtils.get().getSecurity();
    	if (!setting.getLoginEnabled()) {
    		return MessageUtil.error("system", "登录功能已关闭");
    	}
    	
    	// 获取会员
    	MemberEntity pMember = null;
    	if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.mobile)
    			&& StringUtils.length(loginBean.getUsername()) == 11) {
    		pMember = memberService.findByMobile(loginBean.getUsername());
    	}
    	
    	// 验证会员
    	if (pMember == null) {
    		return MessageUtil.error(
    				MessageUtil.getInstance("username", "账户不存在")
    				);
    	} 
    	String text=request.getParameter("text");
    	// 验证验证码
    	if (!text.equals(session.getAttribute(loginBean.getUsername()))) {
    		return MessageUtil.error("captcha", "验证码错误");
    	}
    	
    	
    	// 登录成功
    	pMember.setLoginIp(ReqUtil.getIpAddr(request));
    	pMember.setLoginDate(new Date());
    	pMember.setLoginFailureCount(0);
    	memberService.update(pMember);
    	
    	// 删除RSA私钥
    	rsaService.removePrivateKey(request);
    	
    	// 重构Session
    	//WebUtils.refactorSession(request);
    	
    	//把用户放到session中，供后续使用
    	session.setAttribute("loginUser", pMember);
    	
    	if (pMember != null) {
    		MemberGradeEntity grader = pMember.getMemberGradeEntity();
    		if (grader != null) {
    			session.setAttribute("priceType", grader.getPriceType());
    		}
    	}
    	
    	// 获取站内信未读
    	Map ms = new HashMap<>();
    	ms.put("member", pMember);
    	ms.put("state", 0);
    	ms.put("type", 1);
    	session.setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
    	
    	session.setAttribute("helps", helpService.findAll());
    	
    	// 判断会员是否已登录
    	if (memberService.authorized()) {
    		session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
    		WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
    	}
    	session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new ShiroPrincipal(pMember.getId(), pMember.getUsername()));
    	//添加了有效时间，与自动登录cookie有效时间一致
    	WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, pMember.getUsername(),MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	//若选择 记住我 时，添加自动登录cookie
    	if(loginBean.getRemember() == 1){
    		autoLoginCookie( request,  response, pMember.getUsername());
    	}
    	
    	
    	
    	return SUCCESS_MESSAGE;
    	
    }
    
    
    
    
    
    
    
    /**
     * 忘记密码
     */
	@RequestMapping(value="/userForgetPass.jspx", method= {RequestMethod.POST, RequestMethod.GET})
	public String userForgetPass(HttpServletRequest request, HttpServletResponse response, Model model){
		
		return  "/front/login/user_forgetpass";
	}
	

	/**
	 * 忘记密码 向注册邮箱发送邮件
	 */
	@ResponseBody
	@RequestMapping(value="/login/findPass.jspx", method= {RequestMethod.POST, RequestMethod.GET})
	public Map<String, Object> findPass(HttpServletRequest request, HttpServletResponse response, Model model){
		String email = ReqUtil.getString(request, "email", "");
		
		MemberEntity memberEntity = memberService.find(Filter.eq("email", email));
		Map<String, Object> result1 = new HashMap<String, Object>();
		if(memberEntity!=null && !memberEntity.isEmpty()){
			//往邮箱里发送确认邮件
			String  urllink="";
			Blowfish bfish=new Blowfish("jiahuijie");

			urllink = "点击登陆账号重置:"+request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath()+"/login/updatePassword2.jhtml?reqCode=activate&id="+bfish.encryptString(Long.toString(memberEntity.getId()));	    	 
			MailSenderInfo mailInfo = new MailSenderInfo(); 
			//mailInfo.setMailServerHost("smtp.ym.163.com"); 
			mailInfo.setMailServerHost("smtp.263.net"); 
			mailInfo.setMailServerPort("25"); 
			mailInfo.setValidate(true); 
			mailInfo.setUserName("center@wl95.com"); 
			//mailInfo.setPassword("scncwcy821215");//您的邮箱密码 
			mailInfo.setPassword("R8JDyfS$");//您的邮箱密码 
			mailInfo.setFromAddress("center@wl95.com"); 
			mailInfo.setToAddress(email); 
			mailInfo.setSubject("晧辰仪器设备物联网平台 登陆账号重置"); 
			mailInfo.setContent(urllink); 
			//这个类主要来发送邮件
			//SimpleMailSender sms = new SimpleMailSender();
			//boolean sresult= sms.sendTextMail(mailInfo);
			result1.put("result1", "success");
			return result1;
		}else{
			result1.put("result1", "failure");
			return result1;
		}
	}
	
	/**
	 * 修改密码
	 */
	@RequestMapping(value="/login/updatePassword2.jhtml", method={RequestMethod.GET, RequestMethod.POST})
	public String secondForgetPassword(HttpServletRequest request, HttpServletResponse response, Model model){
    	//已登录的会员名
    	MemberEntity currentMember = memberService.getCurrent();
    	model.addAttribute("currentMember",currentMember);
    	
    	String ids = ReqUtil.getString(request, "id", "");
    	Blowfish bfish=new Blowfish("jiahuijie");
    	Long id = Long.parseLong(bfish.decryptString(ids));
    	
    	MemberEntity memberEntity = memberService.find(id);
    	if(memberEntity !=null){
    		model.addAttribute("item", memberEntity);
    		return "/front/login/user_updatepass2";
    	}else{
    		return "/";
    	}
	}
	/**
	 * 手机号修改密码
	 */
	@RequestMapping(value="/login/phoneupdatePassword", method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Message phonesecondForgetPassword(HttpServletRequest request, HttpServletResponse response, Model model){
		//已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember",currentMember);
		
		String username = ReqUtil.getString(request, "username", "");
		String text = ReqUtil.getString(request, "text", "");
		Blowfish bfish=new Blowfish("jiahuijie");
		//Long id = Long.parseLong(bfish.decryptString(ids));
		// 验证验证码
    	if (!text.equals(request.getSession().getAttribute(username))) {
    		return MessageUtil.error("captcha", "验证码错误");
    	}
		MemberEntity memberEntity = memberService.findByMobile(username);
		if(memberEntity !=null){
			model.addAttribute("item", memberEntity);
			return SUCCESS_MESSAGE;
			//return "/front/login/user_updatepass2";
		}else{
			return MessageUtil.error("username", "账号不存在");
			//return "/";
		}
	}
	/**
	 * 手机号修改密码
	 */
	@RequestMapping(value="/login/phoneupdatePassword2", method={RequestMethod.GET, RequestMethod.POST})
	public String phonesecondForgetPassword2(HttpServletRequest request, HttpServletResponse response, Model model){
		
		String username = ReqUtil.getString(request, "username", "");
		
		MemberEntity memberEntity = memberService.findByMobile(username);
		if(memberEntity !=null){
			model.addAttribute("item", memberEntity);
			return "/front/login/user_updatepass2";
		}else{
			return "/";
		}
	}
	
	/**
	 * 提示去注册邮箱查看
	 */
	@RequestMapping(value="/login/updatePassword1.jhtml", method={RequestMethod.GET, RequestMethod.POST})
	public String firstForgetPassword(HttpServletRequest request, HttpServletResponse response, Model model){
		
    	String email = ReqUtil.getString(request, "email", "");
    	//真实路径
    	String proLink = request.getSession().getServletContext().getRealPath("/");
    	model.addAttribute("proLink", proLink);
    	
    	MemberEntity memberEntity = memberService.find(Filter.eq("email", email));
    	if(memberEntity!=null && !memberEntity.isEmpty()){
    		//往邮箱里发送确认邮件
    		String  urllink="";
    		Blowfish bfish=new Blowfish("jiahuijie");

    		urllink = "点击登陆账号重置:"+request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath()+"/login/updatePassword2.jhtml?reqCode=activate&id="+bfish.encryptString(Long.toString(memberEntity.getId()));	    	 
    		MailSenderInfo mailInfo = new MailSenderInfo(); 
    		//mailInfo.setMailServerHost("smtp.ym.163.com"); 
    		mailInfo.setMailServerHost("smtp.263.net"); 
    		mailInfo.setMailServerPort("25"); 
    		mailInfo.setValidate(true); 
    		mailInfo.setUserName("center@wl95.com"); 
    		//mailInfo.setPassword("scncwcy821215");//您的邮箱密码 
    		mailInfo.setPassword("R8JDyfS$");//您的邮箱密码 
    		mailInfo.setFromAddress("center@wl95.com"); 
    		mailInfo.setToAddress(memberEntity.getEmail()); 
    		mailInfo.setSubject("晧辰仪器设备物联网平台 登陆账号重置"); 
    		mailInfo.setContent(urllink); 
    		//这个类主要来发送邮件
    		SimpleMailSender sms = new SimpleMailSender();
    		boolean sresult= sms.sendTextMail(mailInfo);
    	}

    		model.addAttribute("email", memberEntity.getEmail());
    		
    		return "/front/login/user_updatepass";

	}
	
	/**
	 * 去注册邮箱查看
	 */
	@RequestMapping(value="/login/loginUpdatePass.jhtml", method={RequestMethod.GET, RequestMethod.POST})
	public String loginUpdatePass(HttpServletRequest request, HttpServletResponse response, Model model){
		
    	//已登录的会员名
    	MemberEntity currentMember = memberService.getCurrent();
    	model.addAttribute("currentMember",currentMember);
		
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		
    	if(currentMember!=null && !currentMember.isEmpty()){
    		//往邮箱里发送确认邮件
    		String  urllink="";
    		Blowfish bfish=new Blowfish("jiahuijie");

    		urllink = "点击登陆账号重置:"+request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath()+"/login/updatePassword2.jhtml?reqCode=activate&id="+bfish.encryptString(Long.toString(currentMember.getId()));	    	 
    		MailSenderInfo mailInfo = new MailSenderInfo(); 
    		//mailInfo.setMailServerHost("smtp.ym.163.com"); 
    		mailInfo.setMailServerHost("smtp.263.net"); 
    		mailInfo.setMailServerPort("25"); 
    		mailInfo.setValidate(true); 
    		mailInfo.setUserName("center@wl95.com"); 
    		//mailInfo.setPassword("scncwcy821215");//您的邮箱密码 
    		mailInfo.setPassword("R8JDyfS$");//您的邮箱密码 
    		mailInfo.setFromAddress("center@wl95.com"); 
    		mailInfo.setToAddress(currentMember.getEmail()); 
    		mailInfo.setSubject("晧辰仪器设备物联网平台 登陆账号重置"); 
    		mailInfo.setContent(urllink); 
    		//这个类主要来发送邮件
    		SimpleMailSender sms = new SimpleMailSender();
    		boolean sresult= sms.sendTextMail(mailInfo);
    		
    		model.addAttribute("email", currentMember.getEmail());
    		
    		return "/front/login/user_updatepass";
    	}else{
    		return "redirect:/login.jhtml";
    	}
	}
	
	/**
	 * 成功修改密码
	 */
	@ResponseBody
	@RequestMapping(value="/login/updatePassword3.jhtml", method={RequestMethod.GET, RequestMethod.POST})
	public Map<String, Object> updatePassword3(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		String password = ReqUtil.getString(request, "password", "");
		
		MemberEntity memberEntity = memberService.find(id);
		if(memberEntity !=null){
			/*if (memberEntity.getInvite() == 1){
				memberEntity.setFirst(0);
			}*/
			memberEntity.setFirst(0);
			memberEntity.setPassword(DigestUtils.md5Hex(password));
			memberService.update(memberEntity);
		}
		
		Map<String, Object> result1 = new HashMap<String, Object>();
		result1.put("result1", 1);
		result1.put("message", "修改密码成功");
		return result1;
	}
	
	@RequestMapping(value="/login/skipLogin.jspx", method={RequestMethod.GET, RequestMethod.POST})
	public void skipLogin(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberEntity = memberService.find(id);
		if(memberEntity !=null){
			if (memberEntity.getInvite() == 1) {
				memberEntity.setFirst(1);
			}
			memberService.update(memberEntity);
		}
		
		try {
			response.sendRedirect("/subscribeInfo/toList.jspx");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 跳转到重置密码成功页面
	 */
	@RequestMapping(value="/login/updatePassSu.jspx", method={RequestMethod.GET, RequestMethod.POST})
	public String updatePassSu(HttpServletRequest request, HttpServletResponse response, Model model){
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		
    	//已登录的会员名
    	MemberEntity currentMember = memberService.getCurrent();
    	model.addAttribute("currentMember",currentMember);
		
		return "/front/login/user_updatepass3";
	}
	
	/**
	 * 当会员邀请的用户第一次登录，不修改密码时
	 */
	@RequestMapping(value="/login/updatelogin.jspx", method={RequestMethod.GET, RequestMethod.POST})
	public String updateLogin(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberEntity = memberService.find(id);
		if(memberEntity !=null){
			if (memberEntity.getInvite() == 1) {
				memberEntity.setFirst(1);
			}
			memberService.update(memberEntity);
		}
		
		return "front/login/login";
	}
}