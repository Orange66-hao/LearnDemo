/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.sysadmin;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.enums.CaptchaTypeEnum;
import net.boocu.framework.util.SettingUtils;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.AuthenticationMessage;
import net.boocu.web.Message;
import net.boocu.web.bean.LoginBean;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.enums.AccountLockTypeEnum;
import net.boocu.web.filter.AuthenticationFilter;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.RSAService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 登录
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminLoginController")

@RequestMapping("/admin")
public class LoginController {

	private static Logger log = Logger.getLogger(LoginController.class);
	
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource(name = "rsaServiceImpl")
    private RSAService rsaService;

    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;
    
    @Resource
	private JdbcTemplate JdbcTemplate;

    /**
     * 登录
     */
    @RequestMapping(value="/login.jspx",method = RequestMethod.GET)
    public String login(HttpServletRequest request, ModelMap model) {
    	boolean qq=adminService.authorized();
        // 管理员已登录时，跳转值管理中心
        if (adminService.authorized()) {
            return "redirect:/admin/home/index.jspx";
        }
        // 密钥
        RSAPublicKey publicKey = rsaService.generateKey(request);

        model.addAttribute("captchaId", UUID.randomUUID().toString());
        model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));

        return "/admin/login/index";
    }

    /**
     * 登录
     */
    @RequestMapping(value="/login.jspx",method ={ RequestMethod.POST})
    public @ResponseBody
    Message loginDo(HttpServletRequest request,LoginBean loginBean) {
 
       /*// RSA解密并获取密码
       String password = rsaService.decryptParameter("password", request);
        System.out.println("password="+password);*/
    	String username=request.getParameter("username");
    	String password=request.getParameter("password");
    	
       /* List<Map<String, Object>> row=JdbcTemplate.queryForList("select * from sys_admin where username='"+username+"'"+" and password='"+DigestUtils.md5Hex(password)+"'");
        String successUrl=null;
		 if (row.size() > 0) {
			  successUrl = "/admin/home/index.jspx";
		 }*/
        
    	
    	// 验证验证码
		/*
		 * if (!captchaService.verify(CaptchaTypeEnum.login, loginBean.getCaptchaId(),
		 * loginBean.getCaptcha())) {
		 * System.out.println("here----3"+loginBean.getUsername()+"---"+loginBean.
		 * getPassword()); return MessageUtil.error("captcha", "验证码错误"); }
		 */
        // 登录成功
        String successUrl = (String) request.getAttribute(AuthenticationFilter.SUCCESS_URL);
        if (StringUtils.isNotBlank(successUrl)) {
            return AuthenticationMessage.success("登录成功", successUrl);
        }

        // 登陆失败
        String loginFailureKey = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        log.info("loginFailureKey="+loginFailureKey);
       
        if (StringUtils.isNotBlank(loginFailureKey)) {
            if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.pam.UnsupportedTokenException")) {
                return AuthenticationMessage.error("验证码错误", rsaService.generateKey(request));
            } else if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.UnknownAccountException")) {
                return AuthenticationMessage.error("账户或密码错误", rsaService.generateKey(request));
            } else if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.DisabledAccountException")) {
                return AuthenticationMessage.error("账户已被禁用", rsaService.generateKey(request));
            } else if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.LockedAccountException")) {
                return AuthenticationMessage.error("账户已被锁定", rsaService.generateKey(request));
            } else if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.IncorrectCredentialsException")) {
                SecuritySetting setting = SettingUtils.get().getSecurity();
                if (ArrayUtils.contains(setting.getAccountLockScopes(), AccountLockTypeEnum.admin)) {
                    return AuthenticationMessage.error("账户或密码错误，若连续" + setting.getAccountLockCount() + "次错误此账户将被锁定",
                            rsaService.generateKey(request));
                } else {
                    return AuthenticationMessage.error("账户或密码错误", rsaService.generateKey(request));
                }
            } else if (StringUtils.equals(loginFailureKey, "org.apache.shiro.authc.AuthenticationException")) {
                return AuthenticationMessage.error("账户或密码错误", rsaService.generateKey(request));
            }
        }
        return AuthenticationMessage.error("登录失败");
    }

    //http://localhost:9090/core/admin/adminUser!login.do
    
    @RequestMapping(value = "/adminUserlogin",method ={ RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody
    Message adminLogin(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return AuthenticationMessage.success("ok", "");
    	
    }
    
    /**判断session失效*/
    @ResponseBody
    @RequestMapping("/isActive.jspx")
    public Message isActive(){
    	AdminEntity admin = adminService.getCurrent();
    	if(admin == null){
    		return Message.error("");
    	}
    	return Message.success("");
    }
}