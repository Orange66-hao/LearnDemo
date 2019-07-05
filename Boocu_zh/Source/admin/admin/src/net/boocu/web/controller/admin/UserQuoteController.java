/**
 * 
 */
package net.boocu.web.controller.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.project.service.UserQuoteService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Controller("quoteController")
@RequestMapping("/admin/quote")
public class UserQuoteController {
	
	private static final String TEM_PATH = "/template/admin/quote";
	
	@Autowired
	private UserQuoteService userQuoteService;

	@RequestMapping(value="/toQuote.jspx")
	public String toQuote(HttpServletRequest request,
			HttpServletResponse response){
		return TEM_PATH+"/quote";
	}
	
	@RequestMapping("/quoteDataJson.json")
	public void quoteDataJson(HttpServletRequest request,
			HttpServletResponse response){
		Integer status = ReqUtil.getInt(request, "status", 0);
		int pageNumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pageNumber,rows);
	
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("status", status);
		Page<UserQuoteEntity> pages = userQuoteService.quoteData(pageable, params);
		
		Map<String,Object> result = new HashMap<String,Object>();

		List<Map> resultList = new ArrayList<Map>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(UserQuoteEntity uqe : pages.getCont()){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("proName", uqe.getProName());
			map.put("id", uqe.getId());
			map.put("userName", uqe.getMemberEntity()!=null?uqe.getMemberEntity().getRealName():"无");
			map.put("userId", uqe.getMemberEntity()!=null?uqe.getMemberEntity().getId():"0");
			map.put("originalPrice", uqe.getOriginalPrice());
			map.put("status", uqe.getStatus());
			map.put("url", uqe.getType());
			map.put("privilegePrice", uqe.getPrivilegePrice());
			map.put("modifyDate",  sdf.format(uqe.getModifyDate()));
			resultList.add(map);
		}
		result.put("total",pages.getTotal());
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}
}
