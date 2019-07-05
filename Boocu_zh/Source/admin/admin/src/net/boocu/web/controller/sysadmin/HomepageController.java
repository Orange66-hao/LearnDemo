/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.sysadmin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.controller.BaseAdminController;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MenuEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.MenuService;
import net.boocu.web.shiro.ShiroPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 主页
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminHomepageController")
@RequestMapping("/admin/home")
public class HomepageController extends BaseAdminController {
    /** 索引重定向URL */
    private static final String INDEX_REDIRECT_URL = "redirect:/admin/admin";

    /** 模板路径 */
    private static final String TEMPLATE_PATH = "/admin";
  
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource
    private MenuService menuService;
    
    
    /**
     * 主页
     */
    @RequestMapping(value = "/index.jspx",method = {RequestMethod.POST,RequestMethod.GET})
    public String index(HttpServletRequest request,
			HttpServletResponse response, Model model) throws CloneNotSupportedException {
//      if (!adminService.authorized()) {
//      return ERROR_VIEW;
//  }
    	List<Filter> filters = new ArrayList<Filter>();
    	filters.add(Filter.isNull("parent"));
    	filters.add(Filter.eq("menuType", 0));
    	
//    	filters.add(Filter.eq("children", true));
    	List<Sequencer> sList = new ArrayList<Sequencer>();
    	sList.add(Sequencer.asc("order"));
    	List<MenuEntity> menuList = this.menuService.findList(filters, sList);
    	List<MenuEntity> hasChildrenList = new ArrayList<MenuEntity>();
    	/*for(MenuEntity menu : menuList){
    		if(menu.isHasChildren()){
    			hasChildrenList.add(menu);
    		}
    	}
    	*/
    	AdminEntity admin=adminService.getCurrent();
    	
    	if (admin != null) {
			
    		if ((admin.getId() == 1 && admin.getName().equals("admin"))||(admin.getId() == 75)) {
    			for(MenuEntity menu : menuList){
    				if(menu.isHasChildren()){
    					hasChildrenList.add(menu);
    				}
    			}
    		}
    		else{
    			if (admin.getUsertype().equals("0")) {
    				for(MenuEntity menu : menuList){
    					if(menu.getId() == 273){
    						hasChildrenList.add(menu);
    					}
						if(menu.getId() == 486){
    					   hasChildrenList.add(menu);
						}
    				}
				}
    			if (admin.getUsertype().equals("1")) {
					for(MenuEntity menu : menuList){
    					if(menu.getId() == 373 ||menu.getId() == 142 || menu.getId() == 99){
    						hasChildrenList.add(menu);
    					}
    				}
				}
    		}
		}
    	model.addAttribute("admin_id", admin.getId());
    	model.addAttribute("admin_name", admin.getName());
    	model.addAttribute("menuList", hasChildrenList);

        return TEMPLATE_PATH+"/home/Index";
    }

    
    @RequestMapping(value = "/summary.jspx",method = {RequestMethod.POST,RequestMethod.GET})
    public String summary(HttpServletRequest request,
			HttpServletResponse response, Model model) {
  
        return TEMPLATE_PATH+"/home/summary";
    }
 
    /**
     * 站点统计信息
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/indexItemBase.jspx",method = {RequestMethod.POST,RequestMethod.GET})
    public String indexItemBase(HttpServletRequest request,
			HttpServletResponse response, Model model) {
  
        return TEMPLATE_PATH+"/home/indexItemBase";
    }
    
    /**
     * 订单统计信息
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/indexItemOrder.jspx",method = {RequestMethod.POST,RequestMethod.GET})
    public String indexItemOrder(HttpServletRequest request,
			HttpServletResponse response, Model model) {
  
        return TEMPLATE_PATH+"/home/indexItemOrder";
    }
    
    /**
     * 商品统计信息
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/indexItemGoods.jspx",method = {RequestMethod.POST,RequestMethod.GET})
    public String indexItemGoods(HttpServletRequest request,
			HttpServletResponse response, Model model) {
  
        return TEMPLATE_PATH+"/home/indexItemGoods";
    }
    
}