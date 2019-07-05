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
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 产品类型
 * 
 * @author fang 20150907
 * @version 1.0
 */
@Controller("frontProductClassController")
@RequestMapping("/productClass")
public class ProductClassController {

	@Resource
	private ProductclassService productclassService;

	@Resource
	private SysMessageService messageService;

	/**
	 * 
	 * 概述:取得产品节点下的节点信息 传入:节点id 传出:节点下的节点信息集合
	 */
	@ResponseBody
	@RequestMapping(value = "dataJson/{id}", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getSecondNodeByTopNodeId(@PathVariable long id, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 行业分类
		List<Map> resultList = new ArrayList<Map>();
		ProductclassEntity productclassEntity = productclassService.find(id);
		if (productclassEntity != null) {
			List<Filter> fList = new ArrayList<Filter>();
			fList.add(Filter.eq("parentid", productclassEntity.getMenuid()));
			List<ProductclassEntity> list = productclassService.findList(fList, Sequencer.asc("id"));
			for (ProductclassEntity item : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
				map.put("leaf", item.getLeaf());
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 
	 * 概述:搜索三级目录
	 */
	@ResponseBody
	@RequestMapping(value = "searchLeafs", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getLeafsByTopNodeName(String name, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 行业分类
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> fList = new ArrayList<Filter>();
		fList.add(Filter.like("name", "%" + name + "%"));
		fList.add(Filter.eq("leaf", 1));
		List<ProductclassEntity> list = productclassService.findList(20, fList, Sequencer.asc("id"));
		for (ProductclassEntity item : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			map.put("leaf", item.getLeaf());
			resultList.add(map);
		}
		return resultList;
	}

	// 异步取得属性信息
	@ResponseBody
	@RequestMapping(value = "/combotreeAsyn.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataAsyn(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 行业分类
		Long id = ReqUtil.getLong(request, "id", 1l);
		List<Map> resultList = new ArrayList<Map>();
		ProductclassEntity productclassEntity = productclassService.find(id);
		if (productclassEntity != null) {
			List<Filter> fList = new ArrayList<Filter>();
			fList.add(Filter.eq("parentid", productclassEntity.getMenuid()));
			List<ProductclassEntity> list = productclassService.findList(fList, Sequencer.asc("id"));
			for (ProductclassEntity item : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
				map.put("state", "0".equals(item.getLeaf()) ? "closed" : "open");
				resultList.add(map);
			}
		}
		return resultList;
	}

	// 获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "/combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		ProductclassEntity topNode = productclassService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(ProductclassEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getMenuid()));
		List<ProductclassEntity> items = productclassService.findList(flist, Sequencer.asc("sort"));
		for (ProductclassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());

			map.put("text", item.getName());
			List<ProductclassEntity> children = productclassService.findList(Filter.eq("parentid", item.getMenuid()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}
}