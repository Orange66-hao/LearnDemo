/**
 * 
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.entity.Order;
import net.boocu.project.entity.OrderProduct;
import net.boocu.project.service.AddressService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.OrderProductService;
import net.boocu.project.service.OrderService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	
	private static final String TEMPATH = "/template/front/userCenter/order";
	
	@Autowired
	private OrderService orderService;
	
    @Resource
    private HelpService helpService;
    
	@Resource
	private MessageService messageService;
	
	@Autowired
	private AddressService addrService;
    
    @Autowired
    private OrderProductService opService;
    
	@Resource
	private MemberService memberService;
    
	@RequestMapping("/toOrderPage.jspx")
	public String toOrderPage(HttpServletRequest request, HttpServletResponse response,Model model){
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		String txtKey = ReqUtil.getString(request, "txtKey", "");
		//订单状态
		String status = ReqUtil.getString(request, "selOType", "-2");
		//卖家名称
		String txtName = ReqUtil.getString(request, "txtName", "");
		
		int isFirst = ReqUtil.getInt(request, "isFirst", 0);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		
		Map<String,Object> parmas = new HashMap<String,Object>();
		if(isFirst != 1){
			parmas.put("orderNumber", txtKey);
			parmas.put("status", status);
			parmas.put("txtName", txtName);
		}else{
			parmas.put("orderNumber", "");
			parmas.put("status", "");
			parmas.put("txtName", "");
		}
		parmas.put("userId", member==null?"0":member.getId());
		model.addAttribute("currentMember", member);
		//model.addAttribute("helps",helpService.findAll());
		int waitPay = 0;
		int waitSend = 0;
		int waitRecv = 0;
		int complate = 0;

		Page<Order> orderp = orderService.findOrderByPage(pageable,parmas);
		List<Order> orderEntities = new ArrayList<Order>();
		List<Order> orders = orderp.getCont();
		for(Order o : orders){
			String [] ids = o.getOrderProductIds().split(",");
			List<OrderProduct> ops = new ArrayList<OrderProduct>();
			//查询orderProduct
			for(String i:ids){
				ops.add(opService.findById(Long.parseLong(i)));
			}
			o.setOrderProduct(ops);
			orderEntities.add(o);
			if(o.getStatus() == 0){
				waitPay++;
			}else if(o.getStatus() == 2 || o.getStatus() == 3){
				waitSend++;
			}else if(o.getStatus() == 2 || o.getStatus() == 6){
				waitRecv++;
			}else if(o.getStatus() == 8 || o.getStatus() == 9){
				complate++;
			}
		}
		
		model.addAttribute("waitPay", waitPay);
		model.addAttribute("waitSend", waitSend);
		model.addAttribute("waitRecv", waitRecv);
		model.addAttribute("complate", complate);
		
		model.addAttribute("txtName", txtName);
		model.addAttribute("status", status);
		model.addAttribute("txtKey", txtKey);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("orders", orderp);
		model.addAttribute("orderEntities", orderEntities);
		return TEMPATH+"/user_myorder";
	}
	
	@RequestMapping("/toOrderDetail.jspx")
	public String toOrderDetail(HttpServletRequest request, HttpServletResponse response,Model model){
		String orderId = ReqUtil.getString(request, "orderId", "");
		String aa= request.getParameter("orderId");
		Order order = orderService.findById(Long.parseLong(orderId));
		
		order.setDiscountPrice(new BigDecimal(order.getDiscountPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		
		if(member == null){
			return "redirect:/login.jhtml";
		}else{
			if((member.getId()+"").equals(order.getBuyMember().getId()+"")){
				//根据订单id查询OrderProduct
				String [] ids = order.getOrderProductIds().split(",");
				List<OrderProduct> list = new ArrayList<OrderProduct>();
				for(String i : ids){
					list.add(opService.findById(Long.parseLong(i)));
				}
				if(StringUtils.isBlank(order.getDeliveryAddress()) && member.getAddressEntity() != null){
					String addr = member.getAddressEntity().getProvince() + " " + member.getAddressEntity().getCity() 
					+ " " + member.getAddressEntity().getArea() + " " +member.getAddressEntity().getDetail();
					order.setDeliveryAddress(addr);
					orderService.updateOrderAddr(order.getId(), addr, member.getAddressEntity().getId());
				}
				model.addAttribute("orderProducts", list);
				model.addAttribute("currentMember", member);
				model.addAttribute("order", order);
				//model.addAttribute("helps",helpService.findAll());
				return TEMPATH+"/order_detail";
			}else{
				return "redirect:/login.jhtml";
			}
		}
	}
	
	@RequestMapping("/queryOrderStatus.jspx")
	public void queryOrder(HttpServletRequest request, HttpServletResponse response){
		String orderId = ReqUtil.getString(request, "orderId", "");
		Map<String,Object> resMap = new HashMap<String,Object>();
		if(orderId != null && !"".equals(orderId)){
			Order order = orderService.findById(Long.parseLong(orderId));
			resMap.put("orderId", orderId);
			if(order != null){
				resMap.put("code", "ok");
				if(order.getStatus() == 1){
					resMap.put("status", 1);
				}else{
					//支付失败
					resMap.put("status", 0);
				}
			}else{
				//订单号丢失，直接支付失败
				resMap.put("code", "fail");				
			}
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resMap));
	}
	
	@RequestMapping("/toSuccess.jspx")
	public String toSuccessPage(HttpServletRequest request, HttpServletResponse response,Model model){
		String orderId = ReqUtil.getString(request, "orderId", "");
		Order order = orderService.findById(Long.parseLong(orderId));
		
		order.setDiscountPrice(new BigDecimal(order.getDiscountPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		
		MemberEntity member = memberService.getCurrent();
		
		//根据订单id查询OrderProduct
		String [] ids = order.getOrderProductIds().split(",");
		List<OrderProduct> list = new ArrayList<OrderProduct>();
		for(String i : ids){
			list.add(opService.findById(Long.parseLong(i)));
		}
		model.addAttribute("orderProducts", list);
		model.addAttribute("currentMember", member);
		model.addAttribute("order", order);
		return TEMPATH+"/pay_success";
	}
	
	@RequestMapping("/batchDel.jspx")
	public void batchDel(HttpServletRequest request, HttpServletResponse response){
		String ids = ReqUtil.getString(request, "ids", "");
		String msg = "err";
		try {
			if(orderService.batchDel(ids) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/updateAddr.jspx")
	public void addOrder(HttpServletRequest request, HttpServletResponse response){
		Long addrId =  ReqUtil.getLong(request, "addr", (long) 0);
		Long orderId =  ReqUtil.getLong(request, "orderId", (long) 0);
		try {
			String msg = "err";
			AddressEntity ae = addrService.findById(addrId);
			
			String addr = ae.getCountry() +" "+ ae.getProvince() + " " + ae.getCity() + " " +
			ae.getArea() + " " +ae.getDetail();
			
			if(orderService.updateOrderAddr(orderId, addr,addrId) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
