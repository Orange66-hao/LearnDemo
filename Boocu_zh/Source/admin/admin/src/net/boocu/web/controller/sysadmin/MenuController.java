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

import net.boocu.framework.util.JsonUtils;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MenuEntity;
import net.boocu.web.service.MenuService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 登录
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminMenuController")
@RequestMapping("/admin/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    
    @RequestMapping(value = "/tree.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String  tree(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return "/admin/core/menu/tree";
    	
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/tree/data.json",method ={ RequestMethod.POST, RequestMethod.GET})
    public void  treeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
 
    	Long menuid = ReqUtil.getLong(request, "menuid", 0L);
    	 
    	List<Map> mapList = new ArrayList<Map>();
    	
    	 List<Filter> filters = new ArrayList<Filter>();
    	Filter filter = Filter.isNull("parent");
    	
    	if(menuid != 0l){
    		MenuEntity menu = this.menuService.find(menuid);
    		
    		filter = Filter.eq("parent", menu);
    	}else{
    		filter = Filter.isNull("parent");
    	}
    	filters.add(filter);
    	
        List<Sequencer> sequencers = new ArrayList<Sequencer>();
        sequencers.add(Sequencer.asc("order"));
        System.out.println("开始排序--------");
    	List<MenuEntity> menuList = menuService.findList(filters,sequencers);
    	for(MenuEntity menu : menuList){
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("menuid", menu.getId());
    		map.put("name", menu.getTitle());
    		map.put("isParent",menu.getParent()==null || menu.isHasChildren());
    		
//    		List<Map> childrenList = new ArrayList<Map>();
//    		for(MenuEntity m1 : menu.getChildren()){
//    			Map<String,Object> map2 = new HashMap<String,Object>();
//    			map2.put("menuid", m1.getId());
//    			map2.put("name", m1.getTitle());
//    			map2.put("isParent",m1.getParent()==null);
//        		
//    			List<Map> children2List = new ArrayList<Map>();
//        		for(MenuEntity m2 : m1.getChildren()){
//        			Map<String,Object> map3 = new HashMap<String,Object>();
//        			map3.put("menuid", m2.getId());
//        			map3.put("name", m2.getTitle());
//        			map3.put("isParent",m2.getParent()==null);
//        			
//        			List<Map> children3List = new ArrayList<Map>();
//            		for(MenuEntity m3 : m2.getChildren()){
//            			Map<String,Object> map4 = new HashMap<String,Object>();
//            			map4.put("menuid", m3.getId());
//            			map4.put("name", m3.getTitle());
//            			map4.put("isParent",m3.getParent()==null);
//            			
//            			children3List.add(map4);
//            		}
//            		
//            		map3.put("children",children3List);
//        			children2List.add(map3);
//        			
//        		}
//        		map2.put("children",children2List);
//        		
//        		childrenList.add(map2);
//    		}
//    		map.put("children",childrenList);
    		
    		mapList.add(map);
    	}


    	String json = JsonUtils.toJson(mapList);
    	RespUtil.renderJson(response, json);
    }
 
    @RequestMapping(value = "/add.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String  add(HttpServletRequest request, HttpServletResponse response, Model model) {
 
    	Long parentid = ReqUtil.getLong(request, "parentid",0l);
    	
    	MenuEntity menu =this.menuService.find(parentid);
    	
    	model.addAttribute("menu", menu);

     	Filter filter = Filter.isNull("parent");
    	List<MenuEntity> menuList = menuService.findList(filter);
 
    	model.addAttribute("menuList", menuList);
     
    	return "/admin/core/menu/add";
    }
    
//    title:退出2
//    menutype:2
//    pid:0
//    url:
//    target:
//    sorder:0
//    canexp:1
//    icon:default.png
//    icon_hover:default.png
    
    @RequestMapping(value = "/addSave.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void addSave(HttpServletRequest request, HttpServletResponse response, Model model,MenuEntity menu) {

    	Long pid = ReqUtil.getLong(request, "pid",0l);
    	
    	if(pid != 0l){
    		MenuEntity parent =this.menuService.find(pid);
    		menu.setParent(parent);
    	}
    	this.menuService.save(menu);
    	
    	Map data =new HashMap();
    	data.put("result", 1);
    	RespUtil.renderJson(response, data);
    	
    }
    
    
    @RequestMapping(value = "/edit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String  edit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	
    	MenuEntity menu =this.menuService.find(id);
    	
    	model.addAttribute("menu", menu);

     	Filter filter = Filter.isNull("parent");
    	List<MenuEntity> menuList = menuService.findList(filter);
 
    	model.addAttribute("menuList", menuList);
    	
    	
    	return "/admin/core/menu/edit";
    	
    }
    
//  menu.id:5
//  menu.title:退出
//  menu.menutype:2
//  menu.pid:0
//  menu.url:/admin/logout.do
//  menu.target:_self
//  menu.sorder:
//  menu.canexp:1
//  menu.icon:/adminthemes/new/images/menu_01.gif
    @RequestMapping(value = "/editSave.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void editSave(HttpServletRequest request, HttpServletResponse response, Model model,MenuEntity menu) {
    	Long pid = ReqUtil.getLong(request, "pid",0l);
  
    	if(pid != 0l){
    		MenuEntity parent =this.menuService.find(pid);
    		menu.setParent(parent);
    	}
    	this.menuService.update(menu);
    	
    	Map data =new HashMap();
    	data.put("result", 1);
    	RespUtil.renderJson(response, data);
    	
    }
    
 
    @RequestMapping(value = "/delete.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void delete(HttpServletRequest request, HttpServletResponse response, Model model) {
   
    	Long id = ReqUtil.getLong(request, "id",0l);
    	MenuEntity menu =this.menuService.find(id);
    	
    	menuService.delete(menu);
    	
    	Map data =new HashMap();
    	data.put("result", 1);
    	RespUtil.renderJson(response, data);
    	
    }
 
//    id:92
//    targetid:91
//    movetype:inner
//    movetype:next
    
    @RequestMapping(value = "/move.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void move(HttpServletRequest request, HttpServletResponse response, Model model) {
 
    	Long id = ReqUtil.getLong(request, "id",0l);
    	Long targetid = ReqUtil.getLong(request, "targetid",0l);
    
    	String movetype = ReqUtil.getString(request, "movetype", "");
    	
    	MenuEntity menu =this.menuService.find(id);
    	MenuEntity target =this.menuService.find(targetid);
    	
    	if(movetype.equalsIgnoreCase("inner")){
    		menu.setParent(target);
    		this.menuService.update(menu);
    	}else if(movetype.equalsIgnoreCase("next")){
    		menu.setOrder(target.getOrder()+1);
    		this.menuService.update(menu);
    	} else if(movetype.equalsIgnoreCase("prev")){
    		menu.setOrder(target.getOrder()-1);
    		this.menuService.update(menu);
    	}
 
    	Map data =new HashMap();
    	data.put("result", 1);
    	RespUtil.renderJson(response, data);
    	
    }
 
    
    		

//    [{"menuid":1, "name":"店铺","isParent":true,"children":[{"menuid":2, "name":"店铺管理","isParent":true,"children":[{"menuid":3, "name":"店铺列表","isParent":false} ,{"menuid":4, "name":"审核店铺","isParent":false} ]} ]} ,{"menuid":5, "name":"商品","isParent":true,"children":[{"menuid":6, "name":"商品管理","isParent":true,"children":[{"menuid":7, "name":"商品列表","isParent":false} ,{"menuid":8, "name":"添加商品","isParent":false} ,{"menuid":9, "name":"进货","isParent":false} ,{"menuid":10, "name":"库存维护","isParent":false} ]} ,{"menuid":11, "name":"商品设置","isParent":true,"children":[{"menuid":12, "name":"分类列表","isParent":false} ,{"menuid":13, "name":"品牌列表","isParent":false} ,{"menuid":14, "name":"类型列表","isParent":false} ,{"menuid":15, "name":"规格列表","isParent":false} ]} ,{"menuid":16, "name":"标签管理","isParent":true,"children":[{"menuid":17, "name":"标签列表","isParent":false} ,{"menuid":18, "name":"标签商品设置","isParent":false} ]} ]} ,{"menuid":19, "name":"订单","isParent":true,"children":[{"menuid":20, "name":"订单管理","isParent":true,"children":[{"menuid":21, "name":"订单列表","isParent":false} ,{"menuid":22, "name":"待结算订单","isParent":false} ,{"menuid":23, "name":"待发货订单","isParent":false} ,{"menuid":24, "name":"待收货订单","isParent":false} ]} ,{"menuid":25, "name":"单据管理","isParent":true,"children":[{"menuid":26, "name":"收款单","isParent":false} ,{"menuid":27, "name":"退货单","isParent":false} ]} ,{"menuid":28, "name":"退货申请","isParent":true,"children":[{"menuid":29, "name":"退货申请","isParent":false} ]} ]} ,{"menuid":30, "name":"会员","isParent":true,"children":[{"menuid":31, "name":"会员管理","isParent":true,"children":[{"menuid":32, "name":"会员列表","isParent":false} ,{"menuid":33, "name":"会员等级","isParent":false} ]} ,{"menuid":34, "name":"商品评论","isParent":true,"children":[{"menuid":35, "name":"商品评论列表","isParent":false} ,{"menuid":36, "name":"购买咨询列表","isParent":false} ]} ,{"menuid":37, "name":"店铺信息维护","isParent":true,"children":[{"menuid":38, "name":"商城公告","isParent":false} ,{"menuid":39, "name":"联系平台","isParent":false} ]} ]} ,{"menuid":40, "name":"促销","isParent":true,"children":[{"menuid":41, "name":"优惠卷管理","isParent":true,"children":[{"menuid":42, "name":"优惠券列表","isParent":false} ]} ]} ,{"menuid":43, "name":"页面","isParent":true,"children":[{"menuid":44, "name":"广告管理","isParent":true,"children":[{"menuid":45, "name":"广告位","isParent":false} ,{"menuid":46, "name":"广告列表","isParent":false} ]} ,{"menuid":47, "name":"页面设置","isParent":true,"children":[{"menuid":48, "name":"文档管理","isParent":false} ,{"menuid":49, "name":"导航栏管理","isParent":false} ]} ]} ,{"menuid":50, "name":"设置","isParent":true,"children":[{"menuid":51, "name":"网店设置","isParent":true,"children":[{"menuid":52, "name":"系统设置","isParent":false} ,{"menuid":53, "name":"站点设置","isParent":false} ,{"menuid":54, "name":"smtp设置","isParent":false} ]} ,{"menuid":55, "name":"配送和支付","isParent":true,"children":[{"menuid":56, "name":"支付方式","isParent":false} ,{"menuid":57, "name":"配送方式","isParent":false} ,{"menuid":58, "name":"物流公司","isParent":false} ,{"menuid":59, "name":"地区管理","isParent":false} ,{"menuid":60, "name":"仓库管理","isParent":false} ]} ,{"menuid":61, "name":"快递单管理","isParent":true,"children":[{"menuid":62, "name":"快递单模板管理","isParent":false} ,{"menuid":63, "name":"发货信息管理","isParent":false} ]} ,{"menuid":64, "name":"权限管理","isParent":true,"children":[{"menuid":65, "name":"管理员管理","isParent":false} ,{"menuid":66, "name":"角色管理","isParent":false} ,{"menuid":67, "name":"权限点管理","isParent":false} ]} ,{"menuid":68, "name":"模板管理","isParent":true,"children":[{"menuid":69, "name":"前台模板","isParent":false} ,{"menuid":70, "name":"后台模板","isParent":false} ]} ]} ,{"menuid":71, "name":"test","isParent":true,"children":[{"menuid":72, "name":"工具","isParent":true,"children":[{"menuid":73, "name":"URL映射","isParent":false} ,{"menuid":74, "name":"菜单管理","isParent":false} ]} ,{"menuid":75, "name":"主题管理","isParent":true,"children":[{"menuid":76, "name":"站点前台主题","isParent":false} ,{"menuid":77, "name":"站点后台主题","isParent":false} ]} ,{"menuid":78, "name":"页面管理","isParent":true,"children":[{"menuid":79, "name":"文章模型","isParent":false} ,{"menuid":80, "name":"文章管理","isParent":false} ,{"menuid":81, "name":"导航栏管理","isParent":false} ]} ,{"menuid":82, "name":"网店设置","isParent":true,"children":[{"menuid":83, "name":"系统设置","isParent":false} ,{"menuid":84, "name":"站点设置","isParent":false} ,{"menuid":85, "name":"域名管理","isParent":false} ,{"menuid":86, "name":"smtp设置","isParent":false} ,{"menuid":87, "name":"组件管理","isParent":false} ]} ,{"menuid":88, "name":"权限管理","isParent":true,"children":[{"menuid":89, "name":"管理员管理","isParent":false} ,{"menuid":90, "name":"角色管理","isParent":false} ]} ]} ,{"menuid":91, "name":"浏览网站","isParent":false}
    
    
}