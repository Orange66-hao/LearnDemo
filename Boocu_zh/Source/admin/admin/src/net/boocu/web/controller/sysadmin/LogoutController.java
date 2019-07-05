package net.boocu.web.controller.sysadmin;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.WebUtils;
import net.boocu.web.constant.Constants;
import net.boocu.web.service.AdminService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller - 注销
 * 
 * @author lyulin
 * @version 1.0
 */
@Controller("adminLogoutController")
@RequestMapping("/admin/logout.jspx")
public class LogoutController extends BaseController {
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;
    
    /**
     * 注销
     */
    @RequestMapping
    public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    
        session.removeAttribute(Constants.PRINCIPAL_ATTR_NAME);

        session.removeAttribute(Constants.KEY_ATTR);
        try{
//        	session.invalidate();
        }catch(Exception e){

        }

        WebUtils.removeCookie(request, response, Constants.USERNAME_COOKIE_NAME);
        
    	Subject subject = SecurityUtils.getSubject();
    	
    	subject.logout();
    	
    	Map data =new HashMap();
    	data.put("result", 1);
    	RespUtil.renderJson(response, data);
    }

}