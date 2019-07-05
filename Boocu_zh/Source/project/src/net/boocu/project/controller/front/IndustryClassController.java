/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 
 * 
 * @author fang 20150907
 * @version 1.0
 */
@Controller("frontIndustryClassController")
@RequestMapping("/industryClass")
public class IndustryClassController {
	

	@Resource
	private IndustryClassService industryClassService;

	@Autowired
	private ProductclassService productclassService;

	/**
	 * 
	 * 概述:取得行业节点下所有的节点信息
	 * 传入:节点id
	 * 传出:节点下的节点信息集合
	 */
	@ResponseBody
	@RequestMapping(value="dataJson/{id}", method = {RequestMethod.POST, RequestMethod.GET })
	public List<Map> getSecondNodeByTopNodeId(@PathVariable long id,HttpServletRequest request,HttpServletResponse response, Model model) {
		//产品分类
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> fList = new ArrayList<Filter>();
		fList.add(Filter.eq("parentid", id));
		List<IndustryClassEntity> list = industryClassService.findList(fList,Sequencer.asc("id"));
		for(IndustryClassEntity item : list){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			map.put("leaf", item.getLeaf());
			resultList.add(map);
		}
		
		return resultList;
	}
	/**
	 * 
	 * 概述:搜索三级目录
	 */
	@ResponseBody
	@RequestMapping(value="searchLeafs", method = {RequestMethod.POST, RequestMethod.GET })
	public List<Map> getLeafsByTopNodeName(String name , HttpServletRequest request,HttpServletResponse response, Model model) {
		//行业分类
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> fList = new ArrayList<Filter>();
		fList.add(Filter.like("name","%"+name+"%"));
		fList.add(Filter.eq("leaf",1));
		List<IndustryClassEntity> list = industryClassService.findList(20,fList,Sequencer.asc("id"));
		for(IndustryClassEntity item : list){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			map.put("leaf", item.getLeaf());
			resultList.add(map);
		}
		return resultList;
	}
	
	//异步获取属性信息
	@ResponseBody
	@RequestMapping(value="/combotreeDataAsyn.json", method={RequestMethod.GET, RequestMethod.POST})
	public List<Map> getCombotreeDataAsyn(HttpServletResponse response, HttpServletRequest request, Model model){
		//行业分类
		Long id = ReqUtil.getLong(request, "id", 1l);
		List<Map> resultList = new ArrayList<Map>();
		IndustryClassEntity industryClassEntity = industryClassService.find(id);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("parentid", industryClassEntity.getId()));
		List<IndustryClassEntity> industryClassEntities = industryClassService.findList(filters, Sequencer.asc("sort"));
		for(IndustryClassEntity item : industryClassEntities){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			map.put("state", "0".equals(item.getLeaf())?"closed":"open");
			resultList.add(map);
		}
		
		return resultList;
	}
	
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	IndustryClassEntity topNode =  industryClassService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(IndustryClassEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<IndustryClassEntity> items = industryClassService.findList(flist, Sequencer.asc("sort"));
		for(IndustryClassEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<IndustryClassEntity> children = industryClassService.findList(Filter.eq("parentid", item.getId()));
			if(children.size() != 0){
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
    	return resultList;
    }
	//根据仪器Id获取对应的产品集合
	@ResponseBody
	@RequestMapping("expandByYiqiId.jspx")
	public List<Map> expandByYiqiId(HttpServletRequest request, HttpServletResponse response) {
		Long yiqiid = ReqUtil.getLong(request, "yiqiId", 0L);
		/**
		 * 此处需要过滤掉多对多的属性，否则报错
		 */
		List<Map> resultList=new ArrayList<>();

		ProductclassEntity productclassEntity = productclassService.find(yiqiid);
		if(productclassEntity!=null){
			Iterator<IndustryClassEntity> iterator = productclassEntity.getIndustryClassEntities().iterator();
			while (iterator.hasNext()){
				IndustryClassEntity next = iterator.next();
				Map map=new HashMap();
				map.put("id",next.getId());
				map.put("text",next.getName());
				resultList.add(map);
			}
		}

		return resultList;
	}
}