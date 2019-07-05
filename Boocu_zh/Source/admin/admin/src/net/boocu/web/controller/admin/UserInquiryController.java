/**
 * 
 */
package net.boocu.web.controller.admin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.project.service.UserInquiryService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/admin/userInquiry")
public class UserInquiryController {

	private static final String TEM_PATH = "/template/admin/inquiry";
	
	@Resource
	private UserInquiryService inquiryService;
	
	@RequestMapping(value="toInquriy.jspx")
	public String toInquriy(HttpServletRequest request,
			HttpServletResponse response){
		return TEM_PATH+"/inquiry";
	}
	
	@RequestMapping(value="inquiryDataJson.json")
	public void inquiryDataJson(HttpServletRequest request,
			HttpServletResponse response){
			Integer status = ReqUtil.getInt(request, "status", 0);
			int pageNumber = ReqUtil.getInt(request, "page", 0);
			int rows = ReqUtil.getInt(request, "rows", 10);
			
			Pageable pageable = new Pageable(pageNumber,rows);
		
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("status", status);
			Page<UserInquiryEntity> pages = inquiryService.inquiryData(pageable, params);
			
			Map<String,Object> result = new HashMap<String,Object>();
	
			List<Map> resultList = new ArrayList<Map>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(UserInquiryEntity uie : pages.getCont()){
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("proName", uie.getProName());
				map.put("id", uie.getId());
				map.put("url", uie.getUrl());
				map.put("askUserName", uie.getMemberEntity()!=null?uie.getMemberEntity().getRealName():"无");
				map.put("askUserId", uie.getMemberEntity()!=null?uie.getMemberEntity().getId():"0");
				map.put("onePrice", uie.getOnePrice());
				map.put("status", uie.getStatus());
				map.put("privilegePrice", uie.getPrivilegePrice());
				map.put("createDate",  sdf.format(uie.getCreateDate()));
				resultList.add(map);
			}
			result.put("total",pages.getTotal());
			result.put("rows",resultList); 
			RespUtil.renderJson(response, result);
	}
	
	@RequestMapping(value="give.jspx")
	public void give(HttpServletRequest request,
			HttpServletResponse response){
		try {
			Integer id = ReqUtil.getInt(request, "id", 0);
			Float price = ReqUtil.getFloat(request, "price", 0f);
			String msg = null;
			if(inquiryService.give(id, price) > 0){
				msg = "ok";
			}else{
				msg = "err";
			}
			response.getWriter().write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
