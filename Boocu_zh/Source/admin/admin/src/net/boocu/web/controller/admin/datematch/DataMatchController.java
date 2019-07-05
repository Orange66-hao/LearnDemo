/**
 * 
 */
package net.boocu.web.controller.admin.datematch;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 数据匹配
 * @author Administrator
 * 2016-6-22
 *
 */
@Controller
@RequestMapping("/admin/datamatch/dataMatch")
public class DataMatchController {
	
	private static final String TEM_PATH = "/template/admin/datamatch";
	
	@RequestMapping(value="buyList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toBuyList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/buyDataList";
	}
	
	@RequestMapping(value="rentList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toRentList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/rentDataList";
	}
	
	@RequestMapping(value="repairList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toRepairList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/repairDataList";
	}
}
