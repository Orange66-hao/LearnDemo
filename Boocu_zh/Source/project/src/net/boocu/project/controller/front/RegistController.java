/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.FlushModeType;
import javax.servlet.http.HttpServlet;
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
import net.boocu.project.util.ConfigUtil;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.bean.LoginBean;
import net.boocu.web.bean.RedirectBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.bean.regist.RegistBean;
import net.boocu.web.controller.admin.MemberGradeController;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.ResourceEntity;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.enums.AccountLockTypeEnum;
import net.boocu.web.enums.LoginTypeEnum;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.basic.BasicSetting;
import net.boocu.web.setting.security.SecuritySetting;
import net.boocu.web.shiro.ShiroPrincipal;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sword.wechat4j.token.Token;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.PageFans;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.PageFansBean;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.javabeans.weibo.Company;
import com.qq.connect.oauth.Oauth;

 
 

/**
 * Controller - 用户
 * 
 * @author fang 20151012
 * @version 1.0
 */
@Controller("frontRegistController")
public class RegistController extends BaseController {
	/** 模板路径 */
    private static final String TEMPLATE_PATH = "front/register";
    
    /**成功信息*/
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
    private HelpService helpService;
    /**
     * 注册
     */
    @RequestMapping(value = {"/regist","/register","/regist.jhtml","/register.jhtml"},method = {RequestMethod.GET,RequestMethod.POST})
    public String regist(RegistBean registBean,RedirectBean redirectBean, 
    		String referrer, HttpServletRequest request, ModelMap model	, 
    		HttpServletResponse response) {

        // 判断会员是否已登录
        if (memberService.authorized()) {
            return "redirect:/";
        }

        // 判断是否重定向URL非站内链接
        BasicSetting setting = SettingUtils.get().getBasic();
        if (!verify(redirectBean) || !setting.verifyInboundLink(redirectBean.getRedirectUrl(), request)) {
            redirectBean.setRedirectUrl(null);
        }
               
      //post 提交    
        MessageUtil errors = MessageUtil.getInstance();
        if(request.getMethod().equals(RequestMethod.POST.name())){
	        Message msg = validate(registBean, request, response);
	    	if(msg.getType().equals(MessageTypeEnum.success)){ 		
	    		String returnUrl = redirectBean.getRedirectUrl() != null?redirectBean.getRedirectUrl():ReqUtil.getString(request, "returnUrl", "/index");
		        return "redirect:"+returnUrl;		    		
	    	}
	    	System.out.println(msg.getCont());
	    	errors = MessageUtil.fromJson(msg.getCont());

        }
        //get 或表单验证失败
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
        
		//友情推荐 add by fang 20150906  
		model.addAttribute("friends", friendsService.findAll());
		
		//帮助信息
		model.addAttribute("helps", helpService.findAll());
		
		//根据12月12号以前的日期create_date ,取用户,
		//循环取用户对象.
//		List<MemberEntity> memberEntities = memberService.findList(Filter.lt("createDate", "2015-12-12"));
//		for(MemberEntity item : memberEntities){
//			String pass = item.getPassword();
//			String newPwdString = CodeUtil.decryptBase64(pass, "ebos0223");
//			System.out.println(newPwdString);
//			MemberEntity memberEntity = memberService.find(item.getId());
//			memberEntity.setPassword(DigestUtils.md5Hex(newPwdString));
//			memberService.update(memberEntity );
//		}

        return TEMPLATE_PATH + "/register";
    }
    
    //qq注册，回调，得到openid，用户昵称等信息
    @RequestMapping(value = "/doPost", method = { RequestMethod.POST, RequestMethod.GET })
    public String doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        try {
            String accessToken=request.getParameter("accessToken");
            // 利用获取到的accessToken 去获取当前用的openid 
            OpenID openIDObj =  new OpenID(accessToken);
            String openID = openIDObj.getUserOpenID();
            
         // 密钥
            RSAPublicKey publicKey = rsaService.generateKey(request);
            //用查询openid是否已经存在,存在则直接登录
            MemberEntity showopenid= memberService.showopenid(openID);
            if (showopenid != null) {
            	request.setAttribute("username", showopenid.getUsername()); 
            	request.setAttribute("captchaId", UUID.randomUUID().toString());
            	request.setAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
            	request.setAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
            	return "front/login/login";
			}
                // -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ----------------------------
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                	request.setAttribute("nickname", userInfoBean.getNickname());   
                	request.setAttribute("openID", openID); 
                	request.setAttribute("captchaId", UUID.randomUUID().toString());
                	request.setAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
                	request.setAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
            } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
            }
               
        } catch (QQConnectException e) {
        }
        return TEMPLATE_PATH + "/qqregister";
    }
    //微信注册，回调，得到openid，用户昵称等信息
    @RequestMapping(value = "/doPostweixin", method = { RequestMethod.POST, RequestMethod.GET })
    public String doPostweixin(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	WxOAuth2Client WX=new WxOAuth2Client();
    	String code=request.getParameter("code");
		System.out.println("code--------------------"+code);
		WXOAuthToken token = null;
			token = WX.getAccessTokenByAuthorizationCode(code);
		System.out.println(token.toString()+"-------------");
		String json=WX.getUser(token);
		
		WXuser wxuser=new WXuser(json);
		
		// 密钥
        RSAPublicKey publicKey = rsaService.generateKey(request);
        //用查询openid是否已经存在,存在则直接登录
        MemberEntity showwxopenid= memberService.showwxopenid(wxuser.getOpenid());
        if (showwxopenid != null) {
        	request.setAttribute("username", showwxopenid.getUsername()); 
        	request.setAttribute("captchaId", UUID.randomUUID().toString());
        	request.setAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        	request.setAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
        	return "front/login/login";
		}
            	request.setAttribute("nickname", wxuser.getNickname());   
            	request.setAttribute("openID", wxuser.getOpenid()); 
            	request.setAttribute("captchaId", UUID.randomUUID().toString());
            	request.setAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
            	request.setAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		
    	return TEMPLATE_PATH + "/wxregister";
    }
    
    
    
    /**
     * 注册成功跳转页面
     */
    @RequestMapping(value="/register_sub.jhtml", method={RequestMethod.GET, RequestMethod.POST})
    public String register_sub(HttpServletRequest request, HttpServletResponse response, Model model){
    	//已登录的会员名
    	MemberEntity currentMember = memberService.getCurrent();
    	model.addAttribute("currentMember",currentMember);
    	
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
    	
    	return TEMPLATE_PATH + "/register_sub";
    }
    
     /**
     * ajax 注册
     */
     @RequestMapping(value = "/register/register", method = RequestMethod.POST)
     public @ResponseBody
     Message mailRegist(RegistBean registBean, HttpServletRequest
     request, HttpServletResponse response) {
    	 System.out.println("进入注册--------------");
    	 System.out.println(registBean.toString());
     	 return validate(registBean, request, response);
     }
     
     /**
      * ajax qq注册
      */
     @RequestMapping(value = "/register/registerqq", method = RequestMethod.POST)
     public @ResponseBody
     Message qqRegist(RegistBean registBean, HttpServletRequest
    		 request, HttpServletResponse response) {
    	 System.out.println("进入注册--------------");
 	    
 	     // 删除RSA私钥
 	     rsaService.removePrivateKey(request);
 	    
 	    
 	    // 新建会员
	     MemberEntity member = new MemberEntity();
	     member.setEnabled(1);
	     member.setUserType(UserTypeEnum.buyer);
	     member.setUsername(registBean.getUsername());
	     member.setMobile(registBean.getMobile());
	     member.setOpenid(registBean.getOpenid());
	     member.setEmail(registBean.getEmail());
	     memberService.save(member);
	     // 重构Session
	     //WebUtils.refactorSession(request);
	     LoginBean loginbean =new LoginBean();
	     loginbean.setUsername(registBean.getUsername());
	     
 	     //sendMail(request, mailRegistBean.getEmail(), TokenMethodEnum.user_regist,"/regist/ckemail");
 	    // return logincontroller.loginqq(loginbean, request, response);
	      return SUCCESS_MESSAGE;
    	 
     }
     /**
      * ajax 微信注册
      */
     @RequestMapping(value = "/register/registerwx", method = RequestMethod.POST)
     public @ResponseBody
     Message wxRegist(RegistBean registBean, HttpServletRequest
    		 request, HttpServletResponse response) {
    	 System.out.println("进入注册--------------");
    	 
    	 // 删除RSA私钥
    	 rsaService.removePrivateKey(request);
    	 
    	 
    	 // 新建会员
    	 MemberEntity member = new MemberEntity();
    	 member.setEnabled(1);
    	 member.setUserType(UserTypeEnum.buyer);
    	 member.setUsername(registBean.getUsername());
    	 member.setMobile(registBean.getMobile());
    	 member.setWxopenid(registBean.getOpenid());
    	 member.setEmail(registBean.getEmail());
    	 memberService.save(member);
    	 // 重构Session
    	 //WebUtils.refactorSession(request);
    	 LoginBean loginbean =new LoginBean();
    	 loginbean.setUsername(registBean.getUsername());
    	 
    	 //sendMail(request, mailRegistBean.getEmail(), TokenMethodEnum.user_regist,"/regist/ckemail");
    	 // return logincontroller.loginqq(loginbean, request, response);
    	 return SUCCESS_MESSAGE;
    	 
     }
     
     /**
      * ajax 已有仪联网账号，绑定qq
      */
     @RequestMapping(value = "/register/registerqqupdate", method = RequestMethod.POST)
     public @ResponseBody
     Message registerqqupdate(RegistBean mailRegistBean, HttpServletRequest
    		 request, HttpServletResponse response) {
    	 System.out.println("进入绑定--------------");
    	// 判断是否开放注册
	     SecuritySetting setting = SettingUtils.get().getSecurity();
	     if (!setting.getRegistEnabled()) {
	    	 return MessageUtil.error("system", "注册功能已关闭");
	     }
	     //将用户名，密码等值取出，存放到loginbean判断用户名和密码是否正确
	     LoginBean loginBean=new LoginBean();
	     loginBean.setUsername(mailRegistBean.getUsernumber());
	     loginBean.setCaptcha(mailRegistBean.getCaptcha());
	     loginBean.setCaptchaId(request.getParameter("captchaId"));
	     loginBean.setPassword(mailRegistBean.getPassword());
	     if (!verify(loginBean)) {
	    		return MessageUtil.error(
	    				MessageUtil.getInstance("usernumber","账户或密码错误").push("password","账户或密码错误")
	    				);
	    	}
	     
	    // 验证验证码
	    	if (!captchaService.verify(CaptchaTypeEnum.login, loginBean.getCaptchaId(), loginBean.getCaptcha())) {
	    		System.out.println("here----3"+loginBean.getUsername()+"---"+loginBean.getPassword()); 
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
	    		return MessageUtil.error(
	    				MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误")
	    				);
	    	}    
	    	// 账号状态
//			if(pMember.getEnabled() ==2){
//				return MessageUtil.error("username","账号未激活，请先登录注册邮箱激活！");
//			}
	    	if(pMember.getEnabled() ==0){
	    		return MessageUtil.error("username","账号被禁用,请与系统管理员联系,联系电话:0755-86641139！");
	    	}
	    	
	    	// 判断是否会员被锁定
	    	if (pMember.getLocked()) {
	    		// 判断是否属于锁定范围
	    		if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
	    			
	    			// 判断是否永久锁定
	    			if (setting.getAccountLockTime() == 0) {
	    				return MessageUtil.error("username","账户已被锁定");
	    			}
	    			
	    			// 判断是否解锁会员
	    			if (setting.getAccountUnlocked(pMember.getLockedDate())) {
	    				pMember.setLoginFailureCount(0);
	    				pMember.setLocked(false);
	    				pMember.setLockedDate(null);
	    			} else {
	    				return MessageUtil.error("username","账户已被锁定");
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
	    					MessageUtil.getInstance(
	    							"username","账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定"
	    							).push(
	    									"password","账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定"
	    									)
	    					);
	    		} else {
	    			return MessageUtil.error(
	    					MessageUtil.getInstance("username","账户或密码错误").push("password","账户或密码错误")
	    					);
	    		}
	    	}	
	    	
	    	//获取qq的openid，绑定到该用户下
	    	String openid=request.getParameter("openid");
	    	// 登录成功
	    	pMember.setLoginIp(ReqUtil.getIpAddr(request));
	    	pMember.setLoginDate(new Date());
	    	pMember.setLoginFailureCount(0);
	    	pMember.setOpenid(openid);
	    	memberService.update(pMember);
    	 // 删除RSA私钥
    	 //rsaService.removePrivateKey(request);
    	 
         
        /* //用查询openid是否已经存在,存在则直接登录
         MemberEntity showopenid= memberService.showopenid(openid);
         if (showopenid != null) {
         	request.setAttribute("username", showopenid.getUsername()); 
         	return "front/login/login";
			}*/
    	 
    	 // 重构Session
    	 //WebUtils.refactorSession(request);
    	 LoginController logincontroller  = new LoginController();
    	 LoginBean loginbean =new LoginBean();
    	 loginbean.setUsername(mailRegistBean.getUsername());
    	 
    	 //sendMail(request, mailRegistBean.getEmail(), TokenMethodEnum.user_regist,"/regist/ckemail");
    	 // return logincontroller.loginqq(loginbean, request, response);
    	 return SUCCESS_MESSAGE;
    	 
     }
     
     /**
      * ajax 已有仪联网账号，绑定微信
      */
     @RequestMapping(value = "/register/registerwxupdate", method = RequestMethod.POST)
     public @ResponseBody
     Message registerwxupdate(RegistBean mailRegistBean, HttpServletRequest
    		 request, HttpServletResponse response) {
    	 System.out.println("进入绑定--------------");
    	 // 判断是否开放注册
    	 SecuritySetting setting = SettingUtils.get().getSecurity();
    	 if (!setting.getRegistEnabled()) {
    		 return MessageUtil.error("system", "注册功能已关闭");
    	 }
    	 //将用户名，密码等值取出，存放到loginbean判断用户名和密码是否正确
    	 LoginBean loginBean=new LoginBean();
    	 loginBean.setUsername(mailRegistBean.getUsernumber());
    	 loginBean.setCaptcha(mailRegistBean.getCaptcha());
    	 loginBean.setCaptchaId(request.getParameter("captchaId"));
    	 loginBean.setPassword(mailRegistBean.getPassword());
    	 if (!verify(loginBean)) {
    		 return MessageUtil.error(
    				 MessageUtil.getInstance("usernumber","账户或密码错误").push("password","账户或密码错误")
    				 );
    	 }
    	 
    	 // 验证验证码
    	 if (!captchaService.verify(CaptchaTypeEnum.login, loginBean.getCaptchaId(), loginBean.getCaptcha())) {
    		 System.out.println("here----3"+loginBean.getUsername()+"---"+loginBean.getPassword()); 
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
    		 return MessageUtil.error(
    				 MessageUtil.getInstance("username", "账户或密码错误").push("password", "账户或密码错误")
    				 );
    	 }    
    	 // 账号状态
//			if(pMember.getEnabled() ==2){
//				return MessageUtil.error("username","账号未激活，请先登录注册邮箱激活！");
//			}
    	 if(pMember.getEnabled() ==0){
    		 return MessageUtil.error("username","账号被禁用,请与系统管理员联系,联系电话:0755-86641139！");
    	 }
    	 
    	 // 判断是否会员被锁定
    	 if (pMember.getLocked()) {
    		 // 判断是否属于锁定范围
    		 if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
    			 
    			 // 判断是否永久锁定
    			 if (setting.getAccountLockTime() == 0) {
    				 return MessageUtil.error("username","账户已被锁定");
    			 }
    			 
    			 // 判断是否解锁会员
    			 if (setting.getAccountUnlocked(pMember.getLockedDate())) {
    				 pMember.setLoginFailureCount(0);
    				 pMember.setLocked(false);
    				 pMember.setLockedDate(null);
    			 } else {
    				 return MessageUtil.error("username","账户已被锁定");
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
    					 MessageUtil.getInstance(
    							 "username","账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定"
    							 ).push(
    									 "password","账户或密码错误，若连续" + setting.getAccountLockCount() + "次输错账户将被锁定"
    									 )
    					 );
    		 } else {
    			 return MessageUtil.error(
    					 MessageUtil.getInstance("username","账户或密码错误").push("password","账户或密码错误")
    					 );
    		 }
    	 }	
    	 
    	 //获取wx的openid，绑定到该用户下
    	 String openid=request.getParameter("openid");
    	 // 登录成功
    	 pMember.setLoginIp(ReqUtil.getIpAddr(request));
    	 pMember.setLoginDate(new Date());
    	 pMember.setLoginFailureCount(0);
    	 pMember.setWxopenid(openid);
    	 memberService.update(pMember);
    	 // 删除RSA私钥
    	 //rsaService.removePrivateKey(request);
    	 
    	 
    	 /* //用查询openid是否已经存在,存在则直接登录
         MemberEntity showopenid= memberService.showopenid(openid);
         if (showopenid != null) {
         	request.setAttribute("username", showopenid.getUsername()); 
         	return "front/login/login";
			}*/
    	 
    	 // 重构Session
    	 //WebUtils.refactorSession(request);
    	 LoginController logincontroller  = new LoginController();
    	 LoginBean loginbean =new LoginBean();
    	 loginbean.setUsername(mailRegistBean.getUsername());
    	 
    	 //sendMail(request, mailRegistBean.getEmail(), TokenMethodEnum.user_regist,"/regist/ckemail");
    	 // return logincontroller.loginqq(loginbean, request, response);
    	 return SUCCESS_MESSAGE;
    	 
     }
     
    /**
     * 检查用户名
     */
    @RequestMapping(value = {"/regist/check_username","/register/check_username"}, method = RequestMethod.POST)
    public @ResponseBody
    String checkUsername(String username) {
    	
        if (StringUtils.isBlank(username)) {
        	return "false";

        }
        // 判断用户名是否存在
        if (memberService.usernameExists(username)) {        	
        	        	
        	return "false";
        	
        } else {
        	
        	return "true";
        	
        }
    }
    /**
     * 检查短信验证码
     */
    @RequestMapping(value = {"/regist/check_text","/register/check_phone"}, method = RequestMethod.POST)
    public @ResponseBody
    Boolean checkPhone(String username,String text, HttpSession session) {
    	
    	return text.equals(session.getAttribute(username).toString());
    }
   
    /**
     * 检查邮箱
     */
    @RequestMapping(value = {"/regist/check_email", "/register/check_email"}, method = RequestMethod.POST)
    public @ResponseBody String checkEmail(String email) {
    	
    	if (StringUtils.isBlank(email)){
    		return "false";
    	}
    	//判断邮箱是否存在
    	if(memberService.emailExists(email)) {
    		return "false";
    	}
    	else{
    		return "true";
    	}
    }   
    protected Message validate (RegistBean mailRegistBean, HttpServletRequest
    	     request, HttpServletResponse response) {
    	String pwd = rsaService.decryptParameter("password", request);
    	mailRegistBean.setPassword (pwd);
    	// 判断是否开放注册
	     SecuritySetting setting = SettingUtils.get().getSecurity();
	     if (!setting.getRegistEnabled()) {
	    	 return MessageUtil.error("system", "注册功能已关闭");
	     }
	    
	     // 验证用户名长度
	     if (!setting.verifyUsernameLength(mailRegistBean.getUsername())) {
	    	 return MessageUtil.error("username", "用户名由5-20位数字、字母、下划线组成");
	     }
	     
	     // 验证密码长度
	     if (!setting.verifyPasswordLength(mailRegistBean.getPassword())) {
	    	 return MessageUtil.error("password", "密码不能小于5位");
	     }
	     mailRegistBean.setPassword(DigestUtils.md5Hex(mailRegistBean.getPassword()));
	    
	     // 验证验证码
        if (!captchaService.verify(CaptchaTypeEnum.regist, request.getParameter("captchaId"), mailRegistBean.getCaptcha())) {
           return MessageUtil.error("captcha", "验证码错误");
        }
	     // 验证邮箱地址是否在
	     if (memberService.emailExists(mailRegistBean.getEmail())) {
	    	 return MessageUtil.error("email", "邮箱地址已存在");
	     } 
	     // 验证用户名是否在
	     if (memberService.usernameExists(mailRegistBean.getUsername())) {
	    	 return MessageUtil.error("username", "用户名已存在");
	     }
	    
	     // 删除RSA私钥
	     rsaService.removePrivateKey(request);
	    
	    
	     // 新建会员
	     MemberEntity member = new MemberEntity();
	     member.setEnabled(1);
	     member.setUserType(UserTypeEnum.buyer);
	     MemberGradeEntity memberGradeEntity=new MemberGradeEntity();
	     memberGradeEntity.setId(1000341632L);
	     member.setMemberGradeEntity(memberGradeEntity);
	     BeanUtils.copyProperties(mailRegistBean, member);
	     memberService.save(member);
	    
	     String email = ReqUtil.getString(request, "email", "");
	     MemberEntity memberEntity = memberService.find(Filter.eq("email", email));
	     if (!email.isEmpty())
	     {
	    	 //往邮箱里发送确认邮件
	    	 String  urllink="";
	    	 Blowfish bfish=new Blowfish("jiahuijie");

	    	 urllink = "点击激活账号："+request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort() + request.getContextPath()+"/EmailEnabled.jhtml?reqCode=activate&id="+bfish.encryptString(Long.toString(memberEntity.getId()));	    	 
	    	 MailSenderInfo mailInfo = new MailSenderInfo(); 
	    	 mailInfo.setMailServerHost(ConfigUtil.getConfig("mail.smtphost")); 
	    	 mailInfo.setMailServerPort("25"); 
	    	 mailInfo.setValidate(true); 
	    	 mailInfo.setUserName(ConfigUtil.getConfig("mail.username")); 
	    	 mailInfo.setPassword(ConfigUtil.getConfig("mail.pwd"));//您的邮箱密码 
	    	 mailInfo.setFromAddress(ConfigUtil.getConfig("mail.username")); 
	    	 mailInfo.setToAddress(email); 
	    	 mailInfo.setSubject(ConfigUtil.getConfig("mail.subjuct")); 
	    	 mailInfo.setContent(urllink); 
	    	 //这个类主要来发送邮件
	    	 SimpleMailSender sms = new SimpleMailSender();
	    	 boolean sresult= sms.sendTextMail(mailInfo);

	    	 if(sresult)
	    	 {
	    		 String result="{\"status\":true,\"error\":\"\"}"; 
	    	 }
	    	 else
	    	 {
	    		 String result="{\"status\":false,\"error\":\"\"}";
	    	 }
	     }
	     
	     // 重构Session
	     WebUtils.refactorSession(request);
	     HttpSession session = request.getSession();
	    
	    
	     //sendMail(request, mailRegistBean.getEmail(), TokenMethodEnum.user_regist,"/regist/ckemail");
	     return SUCCESS_MESSAGE;
	}
    /**
     * 邮箱激活
     */
    @RequestMapping(value="/EmailEnabled.jhtml", method={RequestMethod.GET, RequestMethod.POST})
    public String EmailEnabled(HttpServletRequest request, HttpServletResponse response, Model model){
    	String ids = ReqUtil.getString(request, "id", "");
    	Blowfish bfish=new Blowfish("jiahuijie");
    	Long id = Long.parseLong(bfish.decryptString(ids));
    	
    	MemberEntity memberEntity = memberService.find(id);
    	if(memberEntity != null) {
        	memberEntity.setEnabled(2);
        	memberService.update(memberEntity);  
    	}   	  	
	     // 重构Session
	     WebUtils.refactorSession(request);
	     HttpSession session = request.getSession();
	     
	     // 判断会员是否已登录
	     if (memberService.authorized()) {
		     session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
		     WebUtils.removeCookie(request, response,
		     MemberEntity.USERNAME_COOKIE_NAME);
	     }
	     session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new
	     ShiroPrincipal(memberEntity.getId(), memberEntity.getUsername()));
	     WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME,
	     memberEntity.getUsername());
        
    	//已登录的会员名
    	MemberEntity currentMember = memberService.getCurrent();
    	model.addAttribute("currentMember",currentMember);
    	
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
    	
    	return TEMPLATE_PATH + "/register_activate";
    }
    
    //手机号注册  20160316 fang
    @RequestMapping(value="/register/phoneRegister",method=RequestMethod.POST)
    @ResponseBody
    public Message register(String username,String password,String text,HttpServletRequest request,HttpServletResponse response,HttpSession session){
    	 SecuritySetting setting = SettingUtils.get().getSecurity();
    	  // 验证用户名长度
	     if (!setting.verifyUsernameLength(username) ){
	    	 return MessageUtil.error("username", "用户名由5-20位数字、字母、下划线组成");
	     }
	     
	     // 验证密码长度
	     if (!setting.verifyPasswordLength(password)) {
	    	 return MessageUtil.error("password", "密码不能小于5位");
	     }
	    
	     // 验证验证码
	     if (!text.equals(session.getAttribute(username))) {
	         return MessageUtil.error("captcha", "验证码错误");
	     }
	     // 验证用户名是否在
	     if (memberService.usernameExists(username)) {
	    	 return MessageUtil.error("username", "用户名已存在");
	     }
	     // 删除RSA私钥
	     rsaService.removePrivateKey(request);
	    
	    
	     // 新建会员
	     MemberEntity member = new MemberEntity();
	     member.setEnabled(1);
	     member.setUserType(UserTypeEnum.buyer);
	     member.setUsername(username);
	     member.setPassword(DigestUtils.md5Hex(password));
	     member.setMobile(username);
	     memberService.save(member);
	    
	     // 重构Session
    	//WebUtils.refactorSession(request);
    	
    	//把用户放到session中，供后续使用
    	request.getSession().setAttribute("loginUser", member);
    	
    	if (member != null) {
    		MemberGradeEntity grader = member.getMemberGradeEntity();
    		if (grader != null) {
    			session.setAttribute("priceType", grader.getPriceType());
    		}
    	}
    	session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new ShiroPrincipal(member.getId(), member.getUsername()));
    	//添加了有效时间，与自动登录cookie有效时间一致
    	WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, member.getUsername(),MemberEntity.AUTOLOGIN_COOKIE_TIME);
    	return Message.success("注册成功!");
    }
    
}