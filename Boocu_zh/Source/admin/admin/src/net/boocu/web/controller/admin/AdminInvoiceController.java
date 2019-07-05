/**
 * 
 */
package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.project.service.InvoiceService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/adminInvoice")
public class AdminInvoiceController {
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private MemberService memberService;
	
	private static final String TEM_PATH = "/template/admin/invoice";

	@RequestMapping(value="/toInvoiceList.jspx")
	public String toOrderList(HttpServletRequest request,
		HttpServletResponse response){
		return TEM_PATH+"/invoiceLists";
	}
	
	@RequestMapping("/invoiceData.json")
	public void invoiceDataJson(HttpServletRequest request,
		HttpServletResponse response){
		int status = ReqUtil.getInt(request, "status", -1);
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("status", status);
		
		Page<InvoiceEntity> pages = invoiceService.invoiceDataJson(pageable, params);
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(InvoiceEntity ie : pages.getCont()){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", ie.getId());
			map.put("orderNum", ie.getOrderNumber());
			map.put("brandName", ie.getBrandName());
			map.put("proNo", ie.getProNo());
			map.put("proName", ie.getProName());
			map.put("userName", memberService.findById(ie.getUserId()).getRealName());
			map.put("item", ie.getItem());
			map.put("amount", ie.getInvoiceAmount());
			map.put("status", ie.getStatus());
			map.put("applyTime", ie.getApplyTime());
			map.put("type", ie.getType());
			map.put("buyTime", ie.getPayCompleteTime());
			resultList.add(map);
		}
		result.put("total", pages.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}
	
	@RequestMapping("/invoiceDetail.jspx")
	public String findInvoiceDetail(HttpServletRequest request,
			HttpServletResponse response,Model model){
		
		Long id = ReqUtil.getLong(request, "id", (long)0);
		
		int type = ReqUtil.getInt(request, "d_type", 0);
		
		InvoiceEntity ie = invoiceService.findById(id);
		MemberEntity me = memberService.findById(ie.getUserId());
		model.addAttribute("ie", ie);
		model.addAttribute("me", me);
		if(type == 0){
			return TEM_PATH+"/invoiceDetails";
		}else{
			return TEM_PATH+"/invoiceInfoDetails";
		}
	}
	
	@RequestMapping("/updateStatus.jspx")
	public void updateStatus(HttpServletRequest request,
		HttpServletResponse response){
		Long id = ReqUtil.getLong(request, "id", (long)0);
		int status = ReqUtil.getInt(request, "newStat", -1);
		
		invoiceService.updateInvoiceStat(id, status);
	}
}
