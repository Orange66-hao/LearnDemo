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
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AuthorityEntity;
import net.boocu.web.entity.RoleEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.AuthorityService;
import net.boocu.web.service.RoleService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 角色
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminRoleController")
@RequestMapping("/admin/role")
public class RoleController {

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource
    private RoleService roleService;
 
    @Resource
    private AuthorityService authorityService;
    
    
    @RequestMapping(value="/list.jspx",method = RequestMethod.GET)
    public String list(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
 
        return "/admin/core/role/list";
    }
    
    
    @RequestMapping(value="/list/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void data(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		
		String keyword = ReqUtil.getString(request, "keyword", "");		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		

		if(!"".equals(keyword)){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", keyword));
			flist.add(Filter.like("description", keyword));
			pageable.setFilters(flist);
		}
		Page<RoleEntity> page = roleService.findPage(pageable);
		List<RoleEntity> resultList = page.getCont();
		
		for(RoleEntity m : resultList){
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

    	return "/admin/core/role/add";
    }
    
    @RequestMapping(value = "/save.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void save(HttpServletRequest request, HttpServletResponse response, Model model,RoleEntity role) {

    	role = this.roleService.save(role);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",role.getId());
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    @RequestMapping(value = "/detail.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id",0l);
    	
    	RoleEntity role = roleService.find(id);
    	
    	model.addAttribute("entity", role);
    	
    	List<AuthorityEntity> authorities = authorityService.findAll();
    	model.addAttribute("authorities", authorities);
    	
    	return "/admin/core/role/detail";
    }
    
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value="/authority/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void authorityData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		Long role_id = ReqUtil.getLong(request, "role_id",0l);

		RoleEntity role = roleService.find(role_id);
		
		String keyword = ReqUtil.getString(request, "keyword", "");
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%"+keyword+"%"));
			flist.add(Filter.like("description",  "%"+keyword+"%"));
			
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<AuthorityEntity> page = authorityService.findPage(pageable);
		List<AuthorityEntity> resultList = page.getCont();
		
		for(AuthorityEntity m : resultList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", m.getId());
			map.put("name", m.getName());
			map.put("description", m.getDescription());
			map.put("create_date", m.getCreateDate());
		  
			if(role.getAuthorities().contains(m)){
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
    
    
    @RequestMapping(value="/authority/check.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void authorityCheck(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
 
		Long role_id = ReqUtil.getLong(request, "role_id",0l);
		Long auth_id = ReqUtil.getLong(request, "auth_id",0l);
		
		RoleEntity role = roleService.find(role_id);
		
		AuthorityEntity auth = authorityService.find(auth_id);
		if(role.getAuthorities().contains(auth)){
			role.getAuthorities().remove(auth);
		}else{
			role.getAuthorities().add(auth);
		}
		
		roleService.update(role);
		 
		Map<String,Object> result = new HashMap<String,Object>();
	 
		RespUtil.renderJson(response, result);
    }
    
    
    
    @RequestMapping(value = "/delete.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void delete(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {

    	if(id != null && id.length>0){
    		this.roleService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
 
    
    @RequestMapping(value = "/saveEdit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void saveEdit(HttpServletRequest request, HttpServletResponse response, Model model,RoleEntity role) {

    	RoleEntity roleEntity = this.roleService.find(role.getId());
    	roleEntity.setName(role.getName());
    	roleEntity.setDescription(role.getDescription());
    	
    	this.roleService.update(roleEntity);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",role.getId());
    	
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    
}