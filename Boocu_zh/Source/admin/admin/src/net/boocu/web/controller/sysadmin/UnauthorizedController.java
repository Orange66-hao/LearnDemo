package net.boocu.web.controller.sysadmin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.RespUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("adminUnauthorizedController")
@RequestMapping("/admin")
public class UnauthorizedController {
	private static Logger log = Logger.getLogger(UnauthorizedController.class);
	
	@RequestMapping(value = "/unauthorized.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {

		  String requestType = request.getHeader("X-Requested-With");
          if (StringUtils.equalsIgnoreCase(requestType, "XMLHttpRequest")) {
              // 限制访问
        	  log.info("unauthorized ajax");
        	  
          	Map<String,Object> result = new HashMap<String,Object>();
        	result.put("result", 1000);
        	result.put("message", "没有访问权限");
        	
        	RespUtil.renderJson(response, result);
        	if(true){
        		return null;
        	}
          }
          
          return "/admin/core/unauthorized";
	    	
	}
}
