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
import net.boocu.project.entity.Order;
import net.boocu.project.entity.OrderProduct;
import net.boocu.project.service.OrderProductService;
import net.boocu.project.service.OrderService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/adminOrder")
public class AdminOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderProductService opService;
	
	private static final String TEM_PATH = "/template/admin/order";

	@RequestMapping(value="/toOrderList.jspx")
	public String toOrderList(HttpServletRequest request,
			HttpServletResponse response){
		return TEM_PATH+"/orderLists";
	}
	
	/**
	 * 订单列表数据
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/orderDataJson.json")
	public void orderDataJson(HttpServletRequest request, HttpServletResponse response){
		try {
			String orderNumber = ReqUtil.getString(request, "orderNumber", "");
			String email = ReqUtil.getString(request, "email", "");
			String buyName = ReqUtil.getString(request, "buyName", "");
			String recvName = ReqUtil.getString(request, "recvName", "");
			String mobile = ReqUtil.getString(request, "mobile", "");
			String status_con = ReqUtil.getString(request, "status", "");
			String payType_con = ReqUtil.getString(request, "payType", "");
			String createTime = ReqUtil.getString(request, "createTime", "");
			int pagenumber = ReqUtil.getInt(request, "page", 0);
			int rows = ReqUtil.getInt(request, "rows", 10);
			
			Pageable pageable = new Pageable(pagenumber, rows);
	
			HashMap<String, Object> params = new HashMap<String, Object>();
			
			String startTime = null;
			String endTime = null;
			//处理时间
			if(!"".equals(createTime)){
				startTime = createTime.substring(0, 10);
				endTime = createTime.substring(14, 24);
				params.put("startTime", startTime + " 00:00:00");
				params.put("endTime", endTime + " 23:59:59");
			}
			
			params.put("orderNumber", orderNumber);
			params.put("email", email);
			params.put("buyName", buyName);
			params.put("recvName", recvName);
			params.put("mobile", mobile);
			params.put("status", status_con);
			params.put("payType", payType_con);
			
			Page<Order> orders = orderService.findOrderJson(pageable, params);
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map> resultList = new ArrayList<Map>();
			for (Order item : orders.getCont()) {
				if (item.getBuyMember() != null) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", item.getId());
					String [] opIds = item.getOrderProductIds().split(",");
					StringBuilder name = new StringBuilder();
					for(String i : opIds){
						OrderProduct op = opService.findById(Long.parseLong(i));
						name.append(op.getProduct().getProName()).append(" ,");
					}
					//去掉末尾的“,”
					name.deleteCharAt(name.lastIndexOf(","));
					map.put("name", name.toString());
					map.put("orderNum", item.getOrderNumber());
					String status = "";
					switch (item.getStatus()) {
					case -1:
						status = "买家取消订单";
						break;
					case 0:
						status = "待支付";
						break;
					case 1:
						status = "确认订单";
						break;
					case 2:
						status = "仓库开始拣货";
						break;
					case 3:
						status = "打包中";
						break;
					case 4:
						status = "通知商家揽件";
						break;
					case 5:
						status = "未发货时申请退款";
						break;
					case 6:
						status = "待收货";
						break;
					case 7:
						status = "卖家取消订单";
						break;
					case 8:
						status = "已收货";
						break;
					case 9:
						status = "交易完成";
						break;
					case 10:
						status = "收货后申请退货";
						break;
					case 11:
						status = "退款完成";
						break;
					case 12:
						status = "退货完成";
						break;
					case 13:
						status = "交易关闭";
						break;
					default:
						break;
					}
					map.put("statusNum", item.getStatus());
					map.put("status", status);
					map.put("address", item.getDeliveryAddress());
					map.put("mobile",item.getAddressEntity() != null?item.getAddressEntity().getMobilePhone():"");
					map.put("proPrice", item.getSumPrice());
					map.put("disPrice", item.getDiscountPrice());
					map.put("userName", item.getBuyMember().getRealName());
					map.put("payCompleteTime", item.getPayCompleteTime());
					String payType = "";
					switch (item.getPayType()) {
					case 0:
						payType = "未支付";
						break;
					case 1:
						payType = "微信";
						break;
					case 2:
						payType = "支付宝";
						break;
					case 3:
						payType = "汇款";
						break;
					default:
						break;
					}
					map.put("payType", payType);
					map.put("payAmount", item.getPayAmount());
					map.put("tradeCompleteTime", item.getTradeCompleteTime());
					resultList.add(map);
				}
			}
			result.put("total", orders.getTotal());
			result.put("rows", resultList);
			RespUtil.renderJson(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/delOrder.jspx")
	public void delOrder(HttpServletRequest request, HttpServletResponse response){
		try {
			String orderId = ReqUtil.getString(request, "orderId", "0");
			
			String msg = "err";
			if(orderService.batchDel(orderId) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/findDetail.jspx")
	public String findDetail(HttpServletRequest request, HttpServletResponse response,Model model){
		
		Long id = ReqUtil.getLong(request, "id", (long)0);
		
		Order order = orderService.findById(id);
		model.addAttribute("order", order);		
		return TEM_PATH + "/orderDetails";
	}
	
	@RequestMapping("/updateDisCount.jspx")
	public void updateDisCount(HttpServletRequest request, HttpServletResponse response){

		try {
			Long orderId = ReqUtil.getLong(request, "orderId", (long)0);
			Float price = ReqUtil.getFloat(request, "disPrice", 0f);
			
			String msg = "err";
			
			if(orderId > 0 && price != 0){
				if(orderService.updateDisCount(orderId, price) > 0){
					msg = "ok";
				}
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/updateStat.jspx")
	public void updateStat(HttpServletRequest request, HttpServletResponse response){
		Long orderId = ReqUtil.getLong(request, "id", (long)0);
		int status  = ReqUtil.getInt(request, "newStat", -1);
		
		orderService.updateStatus(orderId, status);
	}
}
