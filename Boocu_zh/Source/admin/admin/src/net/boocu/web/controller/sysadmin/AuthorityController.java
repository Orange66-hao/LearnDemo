/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.sysadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AuthorityEntity;
import net.boocu.web.entity.ResourceEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.AuthorityService;
import net.boocu.web.service.ResourceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 权限节点
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminAuthorityController")
@RequestMapping("/admin/authority")
public class AuthorityController {

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource
    private AuthorityService authorityService;
    
    @Resource
    private ResourceService resourceService;
    
    @RequestMapping(value="/list.jspx",method = RequestMethod.GET)
    public String list(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
 
        return "/admin/core/authority/list";
    }
    
    
    @RequestMapping(value="/list/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void data(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		
		String keyword = ReqUtil.getString(request, "keyword", "");
//		model.addAttribute("keyword", keyword);
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		Page<AuthorityEntity> page = authorityService.findPage(pageable);
		List<AuthorityEntity> resultList = page.getCont();
		
		for(AuthorityEntity m : resultList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", m.getId());
			map.put("name", m.getName());
			map.put("description", m.getDescription());
			map.put("create_date", m.getCreateDate());
		  
			resultDataList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultDataList); 
		
		RespUtil.renderJson(response, result);
    }

    
    @RequestMapping(value = "/add.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return "/admin/core/authority/add";
    }
    
    @RequestMapping(value = "/save.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void save(HttpServletRequest request, HttpServletResponse response, Model model,AuthorityEntity authority) {

    	authority = this.authorityService.save(authority);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",authority.getId());
    	
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    @RequestMapping(value = "/detail.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model) {
     
    	Long id = ReqUtil.getLong(request, "id", 0L);
    	AuthorityEntity authority = authorityService.find(id);
    	
    	model.addAttribute("entity", authority);
    	
    	return "/admin/core/authority/detail";
    }
    
    @RequestMapping(value="/resource/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void resourceData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		
		Long auth_id = ReqUtil.getLong(request, "auth_id", 0l);
		
		AuthorityEntity auth = this.authorityService.find(auth_id);
		
		
		String keyword = ReqUtil.getString(request, "keyword", "");
//		model.addAttribute("keyword", keyword);
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		Page<ResourceEntity> page = resourceService.findPage(pageable);
		List<ResourceEntity> resultList = page.getCont();
		
		for(ResourceEntity m : resultList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", m.getId());
			map.put("name", m.getName());
			map.put("source", m.getSource());
			map.put("create_date", m.getCreateDate());
		  
			if(auth.getResources().contains(m)){
				map.put("checked", "checked");
			}else{
				map.put("checked", "");
			}
			
			resultDataList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultDataList); 
		
		RespUtil.renderJson(response, result);
    }

    
    @RequestMapping(value="/resource/check.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void authorityCheck(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		Long resource_id = ReqUtil.getLong(request, "resource_id", 0L);
		Long auth_id = ReqUtil.getLong(request, "auth_id", 0L);
		
		AuthorityEntity auth = authorityService.find(auth_id);
		
		ResourceEntity resource = resourceService.find(resource_id);
		
		if(auth.getResources().contains(resource)){
			auth.getResources().remove(resource);
		}else{
			auth.getResources().add(resource);
		}
		
		authorityService.update(auth);
		 
		Map<String,Object> result = new HashMap<String,Object>();
	 
		RespUtil.renderJson(response, result);
    }
    
    
    
    @RequestMapping(value = "/delete.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void delete(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {

    	if(id != null && id.length>0){
    		this.authorityService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
 
    
    @RequestMapping(value = "/saveEdit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void saveEdit(HttpServletRequest request, HttpServletResponse response, Model model,AuthorityEntity authority) {

    	
    	AuthorityEntity auth = this.authorityService.find(authority.getId());
    	auth.setName(authority.getName());
    	auth.setDescription(authority.getDescription());
   
    	this.authorityService.update(auth);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",authority.getId());
    	
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    
}