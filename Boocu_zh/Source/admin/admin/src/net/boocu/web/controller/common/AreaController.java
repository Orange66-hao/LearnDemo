/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.common;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.boocu.web.service.AreaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
 
/**
 * 
 * @author 鲁小翔
 *
 * 2015年7月14日
 */
@Controller("areaController")
@RequestMapping("/area")
public class AreaController {
	@Resource(name = "areaServiceImpl")
    private AreaService areaService;

    /**根据parentId找children*/
    @RequestMapping(value = "/findChildrenById.jspx",method = { RequestMethod.POST, RequestMethod.GET})
    public void findByParentId(Long parentId,HttpServletResponse response) throws Exception {
    	List<Map<String, Object>> areas=areaService.findChildrenBySql(parentId);
    	String resultJson=JSON.toJSONString(areas);
    	response.getWriter().write(resultJson);
    }
    /**根据parentName找children*/
    @RequestMapping(value = "/findChildrenByName.jspx",method = { RequestMethod.POST, RequestMethod.GET})
    public void findByParentName(String parentName,HttpServletResponse response) throws Exception {
    	List<Map<String, Object>> areas=areaService.findChildrenBySql(parentName);
    	String resultJson=JSON.toJSONString(areas);
    	response.getWriter().write(resultJson);
    }
}