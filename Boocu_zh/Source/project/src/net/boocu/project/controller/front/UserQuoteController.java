/**
 * 
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.UserQuoteEntity;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.UserQuoteService;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/quote")
public class UserQuoteController {

	@Autowired
	private UserQuoteService userQuoteService;
	
	@Autowired
	private ProductService productService;
	
    @Resource
    HelpService helpService;
	
	@Autowired
	private MemberService memberService;
	
	private static final String TEMPATH = "/template/front/userCenter/quote";
	
	@RequestMapping("/getQuoteInfo.jspx")
	public String getQuoteInfo(HttpServletRequest request, HttpServletResponse response, Model model){
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int status = ReqUtil.getInt(request, "status", 0);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		if(member == null){
			return "redirect:/login.jhtml";
		}else{
			Map<String,Object> parmas = new HashMap<String,Object>();
			parmas.put("status", status);
			parmas.put("userId", member.getId());
			model.addAttribute("currentMember", member);
			model.addAttribute("status",status);
	    	//帮组信息
	    	model.addAttribute("helps",helpService.findAll());
			model.addAttribute("quote", userQuoteService.quoteData(pageable,parmas));
			return TEMPATH+"/quote";
		}
	}
	
	private String requality(int quality){
		if(10-quality == 10){
			return "全新";
		}else if(10-quality > 5){
			return 10-quality+"成新";
		}else{
			return "5成新以下";
		}
	} 
	
	@RequestMapping("/insertQuote.jspx")
	public void insertQuote(HttpServletRequest request, HttpServletResponse response){
		try {
			String msg = "err";
			String id = ReqUtil.getString(request, "proId", "0");
			String type = ReqUtil.getString(request, "type", "");
			if(!"0".equals(id)){
				if(userQuoteService.findById(Long.parseLong(id)) <= 0){
					ProductEntity pe = productService.find(Long.parseLong(id));
					UserQuoteEntity uqe = new UserQuoteEntity();
					String brandName = pe.getProductBrandEntity()!=null?pe.getProductBrandEntity().getName():"";
					String proNo = pe.getProNo();
					String proName = pe.getProName();
					String qual = requality(pe.getQualityStatus().ordinal());
					
					String info = brandName + " " + proNo + " " + proName + " " + qual;
					uqe.setProName(info);
					MemberEntity me = new MemberEntity();
					me.setId(pe.getMemberEntity()!=null?pe.getMemberEntity().getId():0);
					uqe.setMemberEntity(me);
					uqe.setProductEntity(pe);
					//计算价格
					if(pe.getMemberEntity() != null){
						MemberEntity member = memberService.find(pe.getMemberEntity().getId());
						BigDecimal originalPrice = null;
						//价格类型
						String priceType = member.getMemberGradeEntity().getPriceType();
						if(priceType != null && !"".equals(priceType)){
							if(priceType.equals("同行")){
								originalPrice = pe.getProPeer();
							}else if(priceType.equals("来宾")){
								originalPrice = pe.getProCustomPrice();
							}else if(priceType.equals("客户")){
								originalPrice = pe.getProMemberPrice();
							}else if(priceType.equals("VIP")){
								//VIP Price
								originalPrice = pe.getProVipPrice();
							}else{
								//电议
								originalPrice = new BigDecimal(-1);
							}
						}
						uqe.setOriginalPrice(originalPrice.floatValue());
					}else{
						//电议
						uqe.setOriginalPrice(-1);
					}
					String url = "http://" + request.getLocalAddr()+":"+request.getLocalPort() + "/" + type + "/" + id +".jhtml";
					uqe.setType(url);
					if(userQuoteService.insertQuote(uqe)>0){
						msg = "ok";
					}
				}else{
					msg = "exist";
				}
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/quote.jspx")
	public void quote(HttpServletRequest request, HttpServletResponse response){
		try {
			String msg = "err";
			Long id = ReqUtil.getLong(request, "id", (long) 0);
			Float price = ReqUtil.getFloat(request, "price", 0f);
			if(userQuoteService.updateGive(id, price) > 0 && userQuoteService.updateQuote(id) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
