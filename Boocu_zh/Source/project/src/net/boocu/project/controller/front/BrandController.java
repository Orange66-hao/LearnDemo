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
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Sequencer;

import net.boocu.web.enums.MessageTypeEnum;
import nl.siegmann.epublib.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 品牌
 * 
 * @author fang 20150907
 * @version 1.0
 */
@Controller("frontBrandController")
@RequestMapping("/brand")
public class BrandController {

	@Resource
	private SysMessageService messageService;
	@Resource
	private ProductBrandService productBrandService;

	/**
	 * 
	 * 概述:取得字母区域对应的品牌 传入:字母 传出:品牌集合
	 */
	@ResponseBody
	@RequestMapping(value = "dataJson/areaBrands", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getSecondNodeByTopNodeId(@RequestParam String area, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 产品分类
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> fList = new ArrayList<Filter>();
		if (!"ALL".equals(area)) {
			fList.add(Filter.eq("area", area));
		}
		fList.add(Filter.eq("status", 1));
		fList.add(Filter.eq("apprStatus", 1));
		fList.add(Filter.eq("isDel", 0));
		List<ProductBrandEntity> list = productBrandService.findList(fList, Sequencer.asc("id"));
		for (ProductBrandEntity item : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getNameEn());
			resultList.add(map);
		}

		return resultList;
	}
    /**判断品牌存在否*/
    @ResponseBody
    @RequestMapping(value = "/checkBrandExist.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Message checkBrandExist(HttpServletRequest request, HttpServletResponse response, Model model, ProductBrandEntity productBrandEntity) {
        String brandId = ReqUtil.getString(request, "brandId", "");
		Message message= null;
		try {
			if(StringUtils.isNotBlank(brandId)){
                ProductBrandEntity pb = productBrandService.find(Long.parseLong(brandId));
                if(pb!=null){
                    message=new Message(MessageTypeEnum.success,"true");
                    return  message;
                }
            }
		} catch (NumberFormatException e) {
			e.printStackTrace();
			message=new Message(MessageTypeEnum.error,"false");
		}
		message=new Message(MessageTypeEnum.error,"false");
        return  message;
    }
	/** 取得品牌名的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_brand_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getBrandNames(HttpServletRequest request, HttpServletResponse response, Model model,
			ProductBrandEntity productBrandEntity) {
		List<Filter> fList = new ArrayList<Filter>();
		//审核过的为1   默认没删除为0
		fList.add(Filter.eq("apprStatus", 1));
		fList.add(Filter.eq("isDel", 0));
		List<ProductBrandEntity> productBrandEntities = productBrandService.findList(fList);
		List<Map> resultList = new ArrayList<Map>();
		for (ProductBrandEntity item : productBrandEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getNameEn());
			map.put("textZh", item.getName());
			resultList.add(map);
		}
		return resultList;

	}

	/**
	 * 
	 * 概述:搜索品牌目录
	 */
	@ResponseBody
	@RequestMapping(value = "searchLeafs", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getLeafsByTopNodeName(String name, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 行业分类
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> fList = new ArrayList<Filter>();
		fList.add(Filter.like("name", "%" + name + "%"));
		fList.add(Filter.eq("apprStatus", 1));
		fList.add(Filter.eq("isDel", 0));
		List<ProductBrandEntity> list = productBrandService.findList(20, fList, Sequencer.asc("id"));
		for (ProductBrandEntity item : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		return resultList;
	}
}