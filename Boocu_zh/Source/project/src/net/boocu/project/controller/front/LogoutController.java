 
/*
 * Copyright 2014-2015 bee-lending.com. All rights reserved.
 * Support: http://www.bee-lending.com
 */
package net.boocu.project.controller.front;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.WebUtils;
import net.boocu.web.Filter;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.TokenEntity;
import net.boocu.web.filter.AuthenticationFilter;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.TokenService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller - 注销
 * 
 * @author 蜜蜂贷
 * @version 1.0
 */
@Controller("logoutController")
@RequestMapping({"/logout.jhtml","/logout"})
public class LogoutController extends BaseController {

	@Resource
	public TokenService tokenService;
	
	@Resource
	public MemberService memberService;
    /**
     * 注销
     */
    @RequestMapping
    public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    	
    	
    	MemberEntity memberEntity = memberService.getCurrent();
    	if(memberEntity == null){
    		return HOMEPAGE_REDIRECT_URL;
    	}
    	
    	//删掉数据库token
    	TokenEntity tokenEntity = tokenService.find(Filter.eq("addr", memberEntity.getUsername()));     
    	if(tokenEntity != null)
    		tokenService.delete(tokenEntity);
        
        //删除掉自动登录cookie
        WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
        WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
        
        session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
        session.removeAttribute(MemberEntity.KEY_ATTR);
               
        session.removeAttribute("loginUser");
        session.removeAttribute("priceType");
        
        
        return HOMEPAGE_REDIRECT_URL;
    }

}

