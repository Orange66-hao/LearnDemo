/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.service.WorldAreaService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.stat.TableStat.Mode;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Controller - 区域
 * 
 * @author deng 20151205
 * @version 1.0
 */
@Controller("frontWorldAreaController")
@RequestMapping("/worldArea")
public class WorldAreaController {
	

	@Resource
	WorldAreaService worldAreaService;
	
    /**取得省的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_province.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public List<Map> get_worldArea(HttpServletRequest request, HttpServletResponse response, Model model,WorldAreaEntity worldAreaEntity) {
    	String idsString = ReqUtil.getString(request, "id", "");
    	List<Filter> filters = new ArrayList<Filter>();
    	List<WorldAreaEntity> worldAreaEntities = worldAreaService.findList(Filter.eq("ifabroad", idsString));	
    	List<Map> resultList = new ArrayList<Map>();
    	for(WorldAreaEntity item : worldAreaEntities){
    		int s1 = (new Long(item.getId())).toString().length();
    		if(s1 ==2){
	    		Map<String, Object> map = new HashMap<String, Object>();
	    		map.put("id", item.getId());
	    		map.put("text", item.getAREANAME());
	    		resultList.add(map);
    		}
    	}
    	return resultList;
    
    }
    
    /**取得城市的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_city.jspx", method={RequestMethod.GET, RequestMethod.POST})
    public List<Map> get_city(HttpServletRequest request, HttpServletResponse response, Model model){
    	String id = ReqUtil.getString(request, "id", "");
    	
    	HashMap<String, Object> Map = new HashMap<String, Object>();
    	List<WorldAreaEntity> worldAreaEntities = worldAreaService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(WorldAreaEntity item : worldAreaEntities){
    		int s1 = (new Long(item.getId())).toString().length();
    		if(s1 ==4){
    			String s2 = (new Long(item.getId())).toString();
    			String s3 = s2.substring(0, 2);
    			if(s3.equals(id)){
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("id", item.getId());
		    		map.put("text", item.getAREANAME());
		    		resultList.add(map);
    			}
    		}
    	}
    	return resultList;
    }
    
    /**取得区域的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_area.jspx", method={RequestMethod.GET, RequestMethod.POST})
    public List<Map> get_area(HttpServletRequest request, HttpServletResponse response){
    	String id = ReqUtil.getString(request, "id", "");
    	
    	HashMap<String, Object> Map = new HashMap<String, Object>();
    	List<WorldAreaEntity> worldAreaEntities = worldAreaService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(WorldAreaEntity item : worldAreaEntities){
    		int s1 = (new Long(item.getId())).toString().length();
    		if(s1 ==6){
    			String s2 = (new Long(item.getId())).toString();
    			String s3 = s2.substring(0, 4);
    			if(s3.equals(id)){
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("id", item.getId());
		    		map.put("text", item.getAREANAME());
		    		resultList.add(map);
    			}
    		}
    	}
    	return resultList;
    }
}