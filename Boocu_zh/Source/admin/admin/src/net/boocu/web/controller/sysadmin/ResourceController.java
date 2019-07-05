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
import net.boocu.web.entity.ResourceEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.ResourceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 权限资源
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminResourceController")
@RequestMapping("/admin/resource")
public class ResourceController {

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource
    private ResourceService resourceService;
 
    @RequestMapping(value="/list.jspx",method = RequestMethod.GET)
    public String list(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
 
        return "/admin/core/resource/list";
    }
    
    
    @RequestMapping(value="/list/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void data(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		
		String keyword = ReqUtil.getString(request, "keyword", "");
//		model.addAttribute("keyword", keyword);
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		
		if(!"".equals(keyword)){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", keyword));
			flist.add(Filter.like("source", keyword));
			pageable.setFilters(flist);
		}
		Page<ResourceEntity> page = resourceService.findPage(pageable);
		List<ResourceEntity> resultList = page.getCont();
		
		for(ResourceEntity m : resultList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", m.getId());
			map.put("name", m.getName());
			map.put("source", m.getSource());
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

    	return "/admin/core/resource/add";
    }
    
    @RequestMapping(value = "/save.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void save(HttpServletRequest request, HttpServletResponse response, Model model,ResourceEntity resource) {

    	resource = this.resourceService.save(resource);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",resource.getId());
    	
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    @RequestMapping(value = "/detail.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model) {
 
    	Long id = ReqUtil.getLong(request, "id",0l);
    	
    	ResourceEntity resource = resourceService.find(id);
    	
    	model.addAttribute("entity", resource);
    	
    	return "/admin/core/resource/detail";
    }
    
    
    @RequestMapping(value = "/delete.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void delete(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {

    	if(id != null && id.length>0){
    		this.resourceService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
 
    
    @RequestMapping(value = "/saveEdit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void saveEdit(HttpServletRequest request, HttpServletResponse response, Model model,ResourceEntity resource) {

//    	ResourceEntity resource = this.resourceService.find(r.getId());
    	 
    	this.resourceService.update(resource);
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	result.put("id",resource.getId());
    	
    	
    	RespUtil.renderJson(response, result);
    	
    }
    
    
}