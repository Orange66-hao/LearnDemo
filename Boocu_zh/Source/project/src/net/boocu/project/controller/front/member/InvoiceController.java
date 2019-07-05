/**
 * 
 */
package net.boocu.project.controller.front.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.entity.InvoiceEntity;
import net.boocu.project.entity.InvoiceInfoEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.AddressService;
import net.boocu.project.service.InvoiceInfoService;
import net.boocu.project.service.InvoiceService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;

/**
 * 发票管理前台
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/invoice")
public class InvoiceController {

	private static final String TEM_PATH = "/template/front/userCenter/invoice";
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private InvoiceInfoService invoiceInfoService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AddressService addressService;
	
	@RequestMapping("/invoice.jspx")
	public String getInvoice(HttpServletRequest request, HttpServletResponse response,
			Model model){
			try {
				int pagenumber = ReqUtil.getInt(request, "page", 0);
				int rows = ReqUtil.getInt(request, "rows", 10);
				String payTime = ReqUtil.getString(request, "payTime", "");
				
				Pageable pageable = new Pageable(pagenumber,rows);
				
				HttpSession session = request.getSession();
				
				MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
				if(member == null){
					return "redirect:/login.jhtml";
				}else{
					model.addAttribute("currentMember", member);
					Map<String,Object> params = new HashMap<String,Object>();
					if(!"".equals(payTime)){
						params.put("startTime", payTime.substring(0, 10) + " 00:00:00");
						params.put("endTime", payTime.substring(14, 24) + " 23:59:59");
					}
					params.put("userId", member.getId());
					model.addAttribute("payTime", payTime);
					Page<InvoiceEntity> pages = invoiceService.getInvoiceInfo(pageable, params);
					List<InvoiceEntity> ieList = new ArrayList<InvoiceEntity>();
					for(InvoiceEntity ies : pages.getCont()){
						String ids  = ies.getProIds();
						String [] proIds = ids.split(",");
						List<ProductEntity> list = new ArrayList<ProductEntity>();
						for(int i=0;i<proIds.length;i++){
							list.add(productService.find(Long.parseLong(proIds[i])));
						}
						ies.setProductList(list);
						ieList.add(ies);
						
					}
					model.addAttribute("invList", ieList);
					model.addAttribute("invoice", pages);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return TEM_PATH + "/user_invoice";
	}
	
	@RequestMapping("/addInvoiceInfo.jspx")
	public void addInvoiceInfo(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		try {
			String msg = "err";
			Long info_id = ReqUtil.getLong(request, "info_id", (long)0);
			Long addr_id = ReqUtil.getLong(request, "addr_id", (long)0);
			Long voice_id = ReqUtil.getLong(request, "voice_id", (long)0);
			int type = ReqUtil.getInt(request, "type", -1);
			int code = ReqUtil.getInt(request, "code", -1);
			
			String unit_name = ReqUtil.getString(request, "unit_name", "");
			String record_code = ReqUtil.getString(request, "record_code", "");
			String reg_address = ReqUtil.getString(request, "reg_address", "");
			String reg_phone = ReqUtil.getString(request, "reg_phone", "");
			String bank = ReqUtil.getString(request, "bank", "");
			String bank_code = ReqUtil.getString(request, "bank_code", "");
			
			if(type == 0){
				//普通发票
				if(code == 1){
					//存在发票地址,把发票和地址关联起来
					if(invoiceService.updateInvoice(voice_id, info_id) > 0){
						msg = "ok";
					}
				}else{
					//不存在地址，则新增地址
					InvoiceInfoEntity iie = new InvoiceInfoEntity();
					AddressEntity ae = new AddressEntity();
					ae.setId(addr_id);
					iie.setAddressEntity(ae);
					iie.setUserId(member.getId());
					iie.setUnitName(unit_name);
					iie.setType(type);
					Long id = invoiceInfoService.addSimpleII(iie);
					if(id > 0){
						//更新发票
						if(invoiceService.updateInvoice(voice_id, id) > 0)
						msg = "ok";
					}
				}
			}else if(type ==1){
				//增值发票
				if(code == 1){
					//存在发票,把发票和地址关联
					if(invoiceService.updateInvoice(voice_id, info_id) > 0){
						msg = "ok";
					}
				}else{
					//不存在增值发票地址
					InvoiceInfoEntity iie = new InvoiceInfoEntity();
					AddressEntity ae = new AddressEntity();
					ae.setId(addr_id);
					iie.setAddressEntity(ae);
					iie.setUserId(member.getId());
					iie.setUnitName(unit_name);
					iie.setType(type);
					iie.setBank(bank);
					iie.setBankCode(bank_code);
					iie.setRecordCode(record_code);
					iie.setRegAddress(reg_address);
					iie.setRegPhone(reg_phone);
					Long id = invoiceInfoService.addSimpleII(iie);
					if(id > 0){
						//更新发票
						if(invoiceService.updateInvoice(voice_id, id) > 0)
						msg = "ok";
					}
				}
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/invoiceInfo.jspx")
	public void getInvoiceInfo(HttpServletRequest request, 
		HttpServletResponse response){
		try {
			int type = ReqUtil.getInt(request, "type", 0);
			HttpSession session = request.getSession();
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		
			Map<String,Object> map = new HashMap<String,Object>();
			if (member != null) {
				InvoiceInfoEntity info = invoiceInfoService.findById(member.getId(),type);
				if(info != null){
					map.put("info_id", info.getId());
					map.put("unitName", info.getUnitName());
					map.put("recordCode", info.getRecordCode());
					map.put("regAddress", info.getRegAddress());
					map.put("regPhone", info.getRegPhone());
					map.put("bank", info.getBank());
					map.put("bankCode", info.getBankCode());
					map.put("addr_id", info.getAddressEntity().getId());
					map.put("addr_pcd", info.getAddressEntity().getProvince() + " " + 
					info.getAddressEntity().getCity() + " " + info.getAddressEntity().getArea());
					map.put("addr_detail", info.getAddressEntity().getDetail());
					map.put("type", info.getType());
					map.put("code", 1);
				}else{
					//取用户的默认收货地址
					AddressEntity ae = addressService.findById(member.getAddressEntity().getId());
					map.put("addr_id", ae.getId());
					String proince = ae.getProvince() !=null?ae.getProvince():"";
					String city = ae.getCity() !=null?ae.getCity():"";
					String area = ae.getArea()!=null?ae.getArea():"";
					map.put("addr_pcd", proince+" "+city+" "+area);
					map.put("addr_detail", ae.getDetail());
					map.put("code", 0);
				}
			}
			RespUtil.renderJson(response, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
