/**
 * 
 */
package net.boocu.project.controller.front.member;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.service.AddressService;
import net.boocu.project.service.HelpService;
import net.boocu.web.entity.MemberEntity;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/addressManage")
public class AddressController {
	
	private static final String TEMPATH = "/template/front/userCenter/dataManage";
	
	@Autowired
	private AddressService addrService;
	
	@Autowired
	private HelpService helpService;
	
	
	@RequestMapping("/toAddressPage.jhtml")
	public String toAddressPage(HttpServletRequest request,HttpServletResponse response,
			Model model){
			HttpSession session = request.getSession();
			
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			
			if(member == null){
				return "redirect:/login.jhtml";
			}else{
				//获取收货地址列表
				AddressEntity ae = new AddressEntity();
				ae.setMemberEntity(member);
				model.addAttribute("currentMember",member);
				model.addAttribute("helps",helpService.findAll());
				model.addAttribute("addrs", addrService.getAddrLists(ae));
			}
			
			return TEMPATH + "/addr/user_addr";
	}
	
	@RequestMapping("/addressJson.json")
	public void getAddressJson(HttpServletRequest request,HttpServletResponse response){
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		try {
			JSONArray json = null;
			if (member != null) {
				//获取收货地址列表
				AddressEntity ae = new AddressEntity();
				ae.setMemberEntity(member);
				List<AddressEntity> addrs = addrService.getAddrLists(ae);
				JsonConfig config = new JsonConfig();
				config.setExcludes(new String[]{"memberEntity"});
				json = JSONArray.fromObject(addrs,config);
				response.getWriter().write(json.toString());
			} else {
				response.getWriter().write("null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/updateAddress.jspx")
	public void updateAddress(HttpServletRequest request,HttpServletResponse response){
		String msg = "err";
		try {
			String addrId = ReqUtil.getString(request, "addrId", "");
			String continent = ReqUtil.getString(request, "continent", "");
			String country = ReqUtil.getString(request, "country", "");
			String province = ReqUtil.getString(request, "province", "");
			String city = ReqUtil.getString(request, "city", "");
			String area = ReqUtil.getString(request, "area", "");
			String detail = ReqUtil.getString(request, "detail", "");
			String postCode = ReqUtil.getString(request, "postCode", "");
			String recivName = ReqUtil.getString(request, "recivName", "");
			String codeM = ReqUtil.getString(request, "codeM", "");
			String mobilePhone = ReqUtil.getString(request, "mobilePhone", "");
			String codeTel = ReqUtil.getString(request, "codeTel", "");
			String telQH = ReqUtil.getString(request, "telQH", "");
			String telNumber = ReqUtil.getString(request, "telNumber", "");
			String telFj = ReqUtil.getString(request, "telFj", "");
			int isDefault = ReqUtil.getInt(request, "idDefault", 0);
			
			HttpSession session = request.getSession();
			
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			
			if(member == null){
				msg = "unlogin";
			}else{
				AddressEntity ae = addrService.findById(Long.parseLong(addrId));
				
				if(ae == null){
					msg = "err";
				}else{
					ae.setContinent(continent);
					ae.setCountry(country);
					ae.setProvince(province);
					ae.setCity(city);
					ae.setArea(area);
					ae.setDetail(detail);
					ae.setPostCode(postCode);
					ae.setRecvName(recivName);
					ae.setCodeM(codeM);
					ae.setMobilePhone(mobilePhone);
					ae.setCodeTel(codeTel);
					ae.setTelQH(telQH);
					ae.setTelNumber(telNumber);
					ae.setTelFj(telFj);
					ae.setIsDefault(isDefault);
					if(addrService.updateAddr(ae) > 0){
						member.setAddressEntity(ae);
						session.setAttribute("loginUser", member);
						msg = "ok";
					}
				}
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/addAddress.jspx")
	public void addAddress(HttpServletRequest request,HttpServletResponse response,
			Model model){
		
		String msg = "err";
		try {
			String continent = ReqUtil.getString(request, "continent", "");
			String country = ReqUtil.getString(request, "country", "");
			String province = ReqUtil.getString(request, "province", "");
			String city = ReqUtil.getString(request, "city", "");
			String area = ReqUtil.getString(request, "area", "");
			String detail = ReqUtil.getString(request, "detail", "");
			String postCode = ReqUtil.getString(request, "postCode", "");
			String recivName = ReqUtil.getString(request, "recivName", "");
			String codeM = ReqUtil.getString(request, "codeM", "");
			String mobilePhone = ReqUtil.getString(request, "mobilePhone", "");
			String codeTel = ReqUtil.getString(request, "codeTel", "");
			String telQH = ReqUtil.getString(request, "telQH", "");
			String telNumber = ReqUtil.getString(request, "telNumber", "");
			String telFj = ReqUtil.getString(request, "telFj", "");
			int isDefault = ReqUtil.getInt(request, "idDefault", 0);
			
			HttpSession session = request.getSession();
			
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			
			if(member == null){
				msg = "unlogin";
			}else{
				AddressEntity ae = new AddressEntity();
				ae.setContinent(continent); 
				ae.setCountry(country);
				ae.setProvince(province);
				ae.setCity(city);
				ae.setArea(area);
				ae.setDetail(detail);
				ae.setPostCode(postCode);
				ae.setRecvName(recivName);
				ae.setCodeM(codeM);
				ae.setMobilePhone(mobilePhone);
				ae.setCodeTel(codeTel);
				ae.setTelQH(telQH);
				ae.setTelNumber(telNumber);
				ae.setTelFj(telFj);
				ae.setIsDefault(isDefault);
				ae.setMemberEntity(member);
				addrService.addAddress(ae);
				member.setAddressEntity(ae);
				
				session.setAttribute("loginUser", member);
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/setDefAddr.jspx")
	public void setDefAddr(HttpServletRequest request,HttpServletResponse response){
		String msg = "err";
		try {
			
			String id = ReqUtil.getString(request, "id", "0");
			
			HttpSession session = request.getSession();
			
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			
			if(member == null){
				msg = "unlogin";
			}else{
				addrService.updateToDefault(member.getId(), Long.parseLong(id));
				AddressEntity addE = new AddressEntity();
				addE.setId(Long.parseLong(id));
				member.setAddressEntity(addE);
				session.setAttribute("loginUser", member);
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/delAddr.jspx")
	public void delAddr(HttpServletRequest request,HttpServletResponse response){
		String id = ReqUtil.getString(request, "id", "0");
		String msg = "err";
		try {
			addrService.delAddress(Long.parseLong(id));
			msg = "ok";
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
