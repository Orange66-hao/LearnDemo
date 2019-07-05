package net.boocu.project.controller.front;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.support.json.JSONUtils;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.XMLParser;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.pay.alipay.config.AlipayConfig;
import net.boocu.pay.alipay.util.AlipayNotify;
import net.boocu.pay.alipay.util.AlipaySubmit;
import net.boocu.pay.tenpay.config.TenConfig;
import net.boocu.pay.tenpay.utils.TenUtils;
import net.boocu.project.entity.Order;
import net.boocu.project.entity.OrderProduct;
import net.boocu.project.service.OrderProductService;
import net.boocu.project.service.OrderService;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/pay")
public class PayController {
	
	private static final Logger logger = Logger.getLogger(PayController.class); 
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderProductService opService;
	
	@RequestMapping("/payOrder.jspx")
	public void payOrder(HttpServletRequest request,HttpServletResponse
		 	response,Model model){
		try {
			String orderId = ReqUtil.getString(request, "orderId", "");
			int payType = ReqUtil.getInt(request, "payType", 0);
			String sumprice = ReqUtil.getString(request, "sumPrice", "");
			
			//请求返回结果集
			Map<String,Object> result = new HashMap<String,Object>();
			
			//查询拿到订单信息
			Order order = orderService.findById(Long.parseLong(orderId));
			//判断订单状态，只有待支付（0） 才能进行支付操作
			if(order.getStatus() == 0){
				
				//查询到订单所包含的商品名称，用于拼接subject、body参数
				//body具体详情，品牌、名称、型号、成色
				StringBuilder body = new StringBuilder();
				String productId = "";
				String [] ids = order.getOrderProductIds().split(",");
				for(int i = 0;i<ids.length;i++){
					OrderProduct op = opService.findById(Long.parseLong(ids[i]));
					body.append(op.getProduct().getProductBrandEntity().getName() + op.getProduct().getProNo() + op.getProduct().getProName());
					productId += op.getProduct().getId();
					if(ids.length >= 1){
						body.append("···");
						break;
					}
				}
				
				//判断请求的价格和订单的价格是否一致，入不一致则请求价格被篡改，抛出异常。程序终止执行
				float total_price = order.getSumPrice();
				//由于float四则运算会丢精度 转成BigDecimal运算
				float xy = new BigDecimal(total_price).add(new BigDecimal(order.getDiscountPrice())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				logger.info("xy:"+xy+" sumprice:"+sumprice);
				if(xy != Float.parseFloat(sumprice)){
					throw new RuntimeException("订单价格被篡改");
				}else{
					if(sumprice.indexOf(".") < 0){
						sumprice = sumprice+".00";
					}
					
					String html = null;
					if(payType == 1){
						String tempStr = (xy * 100f)+"";
						String sump = null;
						if(tempStr.indexOf(".") >= 0){
							//去掉"."
							sump = tempStr.substring(0, tempStr.indexOf("."));
						}else{
							sump = tempStr;
						}
						
						//微信支付
						String random = RandomStringGenerator.getRandomStringByLength(32);
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("appid", TenConfig.getAppid());
						params.put("mch_id", TenConfig.getMchid());
						params.put("nonce_str", random);
						params.put("body", body.toString().trim());
						params.put("out_trade_no", order.getOrderNumber());
						params.put("total_fee", sump);
						params.put("spbill_create_ip", TenConfig.SPBILL_CREATE_IP);
						params.put("notify_url", TenConfig.getNotify_url());
						params.put("trade_type", "NATIVE");
						params.put("product_id", productId);
						String sign = TenUtils.getSign(params);
						//拼接xml
						StringBuilder xml = new StringBuilder("<xml>");
						xml.append("<appid>").append("<![CDATA["+TenConfig.getAppid()+"]]>").append("</appid>");
						xml.append("<mch_id>").append("<![CDATA["+TenConfig.getMchid()+"]]>").append("</mch_id>");
						xml.append("<nonce_str>").append("<![CDATA["+random+"]]>").append("</nonce_str>");
						xml.append("<body>").append("<![CDATA["+body.toString().trim()+"]]>").append("</body>");
						xml.append("<out_trade_no>").append("<![CDATA["+order.getOrderNumber()+"]]>").append("</out_trade_no>");
						xml.append("<total_fee>").append("<![CDATA["+sump+"]]>").append("</total_fee>");
						xml.append("<spbill_create_ip>").append("<![CDATA["+TenConfig.SPBILL_CREATE_IP+"]]>").append("</spbill_create_ip>");
						xml.append("<notify_url>").append("<![CDATA["+TenConfig.getNotify_url()+"]]>").append("</notify_url>");
						xml.append("<trade_type>").append("<![CDATA[NATIVE]]>").append("</trade_type>");
						xml.append("<product_id>").append("<![CDATA["+productId+"]]>").append("</product_id>");
						xml.append("<sign>").append("<![CDATA["+sign+"]]>").append("</sign>");
						xml.append("</xml>");
						
						logger.info("XML:\n"+xml.toString());
						String resStr = TenUtils.postData(TenConfig.UNIFIED_ORDER, xml.toString(), "text/html;charset=utf-8");
						
						logger.info(resStr);
						
						Map resMap = XMLParser.getMapFromXML(resStr);
						
						if(resMap == null){
							logger.info("统一下单接口调用失败");
						}else{
							String returnCode = (String) resMap.get("return_code");
							if(returnCode.equals("SUCCESS")){
								logger.info("统一下单接口调用成功");
								String codeUrl = (String) resMap.get("code_url");
								//把二维码连接返回给页面
								result.put("msg", codeUrl);
								
								//更新订单的支付方式，金额等
								order.setPayAmount(Float.parseFloat(sumprice));
								order.setPayType(payType);
								order.setWxPayNonceStr(random);
								order.setBody(body.toString());
								if(orderService.updateWhenPay(order) > 0){
									result.put("code", "ok");
								}else{
									result.put("code", "err");
								}
							}
						}
					}
					else if(payType == 2)
					{
						//支付宝支付
						//把请求参数打包成数组
						Map<String, String> params = new HashMap<String, String>();
						params.put("service", AlipayConfig.service);
				        params.put("partner", AlipayConfig.partner);
				        params.put("seller_id", AlipayConfig.seller_id);
				        params.put("_input_charset", AlipayConfig.input_charset);
						//params.put("qr_pay_mode", AlipayConfig.qr_pay_mode);
						//异步通知支付结果连接
						//params.put("notify_url", AlipayConfig.notify_url);
						//支付宝处理完请求后，自动跳转到指定页面
						params.put("return_url", AlipayConfig.return_url);
						//傻逼支付宝“anti_phishing_key”同步回调不返回这个参数，请求的时候不传递
						//params.put("anti_phishing_key", AlipaySubmit.query_timestamp());
						//支付用户的ip
						params.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
						params.put("out_trade_no", order.getOrderNumber());
						params.put("subject", body.toString());
						params.put("payment_type", AlipayConfig.payment_type);
						params.put("it_b_pay", AlipayConfig.it_b_pay);
						params.put("total_fee", sumprice);
						params.put("body", body.toString());
						
						//请求支付接口
						html = AlipaySubmit.buildRequest(params, "POST","确认");
						logger.info("支付宝请求HTML：\n"+html);
						//更新订单的支付方式，金额等
						order.setPayAmount(Float.parseFloat(sumprice));
						order.setPayType(payType);
						if(orderService.updateWhenPay(order) > 0){
							result.put("code", "ok");
						}else{
							result.put("code", "err");
						}
						result.put("msg", html);
						//model.addAttribute("htmlMessage", html);
						//return TEMPATH+"/ali_pay";//"redirect:/pay/to_ali_pay.jspx?html="+sb;
					}
				}
				RespUtil.renderJson(response,JSONUtils.toJSONString(result));
			}
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		}
	}
	
	@RequestMapping("/remit.jspx")
	public void remit(HttpServletRequest request,HttpServletResponse
		response){
		try {
			String msg = "err";
			String orderId = ReqUtil.getString(request, "orderId", "");
			int payType = ReqUtil.getInt(request, "payType", 0);
			String sumprice = ReqUtil.getString(request, "sumPrice", "0");
			if(payType == 3){
				Order order = orderService.findById(Long.parseLong(orderId));
				if(order != null){
					if(order.getStatus() == 0){
						order.setPayAmount(Float.parseFloat(sumprice));
						order.setPayType(payType);
						//修改订单状态
						if(orderService.updateWhenPay(order) > 0){
							//修改订单状态
							orderService.updateStatus(Long.parseLong(orderId), 1);
							msg = "ok";
						}
					}else{
						msg = "statErr";
					}
				}else{
					msg = "notFund";
				}
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		}
	}
	
	@RequestMapping("/mergePay.jspx")
	public String mergePay(HttpServletRequest request,HttpServletResponse
		 	response){
		//System.out.println(request.getRemoteAddr());
		System.out.println(request.getRemoteAddr());
		return "/template/texting/mobile/error";
	}
	
	@RequestMapping(value = "/ten_notify_url.jspx",method = { RequestMethod.POST, RequestMethod.GET })
	public void getTenNotifyUrl(HttpServletRequest request,HttpServletResponse
		 	response){
		try {
			//获取微信回调的流
			BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
	        String line = null;
	        StringBuilder sb = new StringBuilder();
	        while((line = br.readLine())!=null){
	            sb.append(line);
	        }
			//把微信返回的xml转换成map
	        Map result = XMLParser.getMapFromXML(sb.toString());
	        
			//返回给微信的xml
	        StringBuilder reXML = new StringBuilder("<xml>");
	        
	        //return_code 字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
	        if(result.get("return_code").toString().equals("SUCCESS")){
	        	//回调成功
	        	logger.info("微信支付成功，返回的XML：\n"+sb.toString());
	        	
		        //获取微信回调的参数
		        String orderNum = (String) result.get("out_trade_no");
		        //业务结果
		        String resultCode = (String) result.get("result_code");
		        
		        String transactionId = result.get("transaction_id").toString();
		        
		        String sign = result.get("sign").toString();
		        if(result.get("sign")!=null){
		        	//去除Sign
		        	result.remove("sign");
		        }
		        if(sign.equals(TenUtils.getSign(result))){
	        		//签名通过，判断交易状态
	        		if(resultCode.equals("SUCCESS")){
	        			if(orderNum !=null && !"".equals(orderNum)){
	    		        	Order order = orderService.findByOrderNum(orderNum);
	    		        	if(order != null){
	    	        			//支付成功
	    	        			if(order.getStatus() == 0){
	    							//订单如果是待支付状态，更改订单状态为确认中状态（1），更新支付完成时间
	    							orderService.updateStatus(order.getId(), 1);
	    							orderService.updatePayCompleteTime(order.getId());
	    							
	    							reXML.append("<return_code><![CDATA[SUCCESS]]></return_code>");
	    							reXML.append("<return_msg><![CDATA[OK]]></return_msg>");
	    							
	    							//插入支付日志
	    							Map<String,String> map = new HashMap<String,String>();
	    							map.put("pro_type", "销售");
	    							map.put("pay_type", "微信");
	    							map.put("pro_info", order.getBody());
	    							map.put("user_name", order.getBuyMember().getUsername());
	    							map.put("trade_no", orderNum);
	    							map.put("pay_amount", order.getPayAmount()+"");
	    							map.put("status", "支付完成");
	    							map.put("buyer_email", transactionId);
	    							orderService.insertPayLog(map);
	    	        			}else{
	    							reXML.append("<return_code><![CDATA[FAIL]]></return_code>");
	    							reXML.append("<return_msg><![CDATA[订单状态异常]]></return_msg>");
	    	        			}
			        		}else{
								reXML.append("<return_code><![CDATA[FAIL]]></return_code>");
								reXML.append("<return_msg><![CDATA[参数[out_trade_no]错误]]></return_msg>");
			        		}
				        }else{
							reXML.append("<return_code><![CDATA[FAIL]]></return_code>");
							reXML.append("<return_msg><![CDATA[参数[out_trade_no]为空]]></return_msg>");
				        }
		        	}else{
		        		logger.info("交易状态异常："+resultCode);
		        	}
		        }else{
					reXML.append("<return_code><![CDATA[FAIL]]></return_code>");
					reXML.append("<return_msg><![CDATA[签名失败]]></return_msg>");
		        }
	        }else{
	        	logger.info("微信支付回调失败，失败原因："+result.get("return_msg").toString());
	        }
	        reXML.append("</xml>");
	        logger.info("返回给微信的XML:\n"+reXML);
	        response.getWriter().write(new String(reXML.toString().getBytes("UTF-8"),"ISO-8859-1"));
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
	
	/**
	 * 支付宝同步回调页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/sync_notify.jspx")
	public String syncNotify(HttpServletRequest request, HttpServletResponse response){
		
		try {
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
				params.put(name, valueStr);
			}
			
			//商户订单号
			String out_trade_no = request.getParameter("out_trade_no");

			//支付宝交易号
			String trade_no = request.getParameter("trade_no");

			//交易状态
			String trade_status = request.getParameter("trade_status");
			
			//支付价格
			String total_fee = request.getParameter("total_fee");
			
			String seller_id = request.getParameter("seller_id");
			
			//买家支付宝账号
			String buyer_email = request.getParameter("buyer_email");
			
			//产品描述
			String body = request.getParameter("body");

			Float total_pay = Float.parseFloat(total_fee);
			
			boolean verify_result = AlipayNotify.verify(params);
			
			if(verify_result){//验证成功
				Order o = orderService.findByOrderNum(out_trade_no);
				if(o != null){
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					if(o.getPayAmount() == total_pay && AlipayConfig.seller_id.equals(seller_id)){
						//请求参数和回调参数一致
						if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
							//注意：付款完成后，支付宝系统发送该交易状态通知
							//判断该笔订单是否是待付款状态
							if(o.getStatus() == 0){
								//如果没有做过处理，更改订单状态为确认中状态（1），更新支付完成时间
								orderService.updateStatus(o.getId(), 1);
								orderService.updatePayCompleteTime(o.getId());
								
								//插入支付日志
								Map<String,String> map = new HashMap<String,String>();
								map.put("pro_type", "销售");
								map.put("pay_type", "支付宝");
								map.put("pro_info", body);
								map.put("user_name", o.getBuyMember().getUsername());
								map.put("trade_no", trade_no);
								map.put("pay_amount", o.getPayAmount()+"");
								map.put("status", "支付完成");
								map.put("buyer_email", buyer_email);
								orderService.insertPayLog(map);
								//跳转到支付成功页面
								//return "/order/toSuccess.jspx?orderId="+o.getId();
								return "redirect:/order/toSuccess.jspx?orderId="+o.getId();
							}
						}
						/*
						if(trade_status.equals("TRADE_FINISHED")){
							//注意：退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
							
							//判断该笔订单是否在商户网站中已经做过处理
							if(o.getStatus() == 9){
								//交易完成状态，不用管
							}else{
								//超过退款日期后发送改状态，预示着不可退款，进入交易完成状态（本程序中订单状态码为：9）
								//如果没有做过处理，把订单状态更新为交易完成状态
								orderService.updateStatus(o.getId(), 9);
								orderService.updateTradeCompleteTime(o.getId());
							}
						} 
						else if (trade_status.equals("TRADE_SUCCESS"))
						{

						}
						//请不要修改或删除
						//response.getWriter().write("success");
						 */
					}
				}
			}else{//验证失败
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("Sign Failure ， 验签失败！");
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}

		/*
		String is_success = ReqUtil.getString(request, "is_success", "");
		String sign_type = ReqUtil.getString(request, "sign_type", "");
		String sign = ReqUtil.getString(request, "sign", "");
		String out_trade_no = ReqUtil.getString(request, "out_trade_no", "");
		String subject = ReqUtil.getString(request, "subject", "");
		String payment_type = ReqUtil.getString(request, "payment_type", "");
		String exterface = ReqUtil.getString(request, "exterface", "");
		//支付宝交易单号
		String trade_no = ReqUtil.getString(request, "trade_no", "");
		//支付宝交易状态，衡量是否支付成功的标志
		String trade_status = ReqUtil.getString(request, "trade_status", "");
		//买家支付宝账号
		String buyer_email = ReqUtil.getString(request, "buyer_email", "");
		String total_fee = ReqUtil.getString(request, "total_fee", "");
		String body = ReqUtil.getString(request, "body", "");
		//通知校验ID
		String notify_id = ReqUtil.getString(request, "notify_id", "");
		
		Map<String,String> signMap = new HashMap<String,String>();
		signMap.put("service", exterface);
		signMap.put("partner", AlipayConfig.partner);
		signMap.put("seller_id", AlipayConfig.seller_id);
		signMap.put("_input_charset", AlipayConfig.input_charset);
		signMap.put("return_url", AlipayConfig.return_url);
		signMap.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
		signMap.put("out_trade_no", out_trade_no);
		signMap.put("subject", body.toString());
		signMap.put("payment_type", payment_type);
		signMap.put("it_b_pay", AlipayConfig.it_b_pay);
		signMap.put("total_fee", total_fee);
		signMap.put("body", body.toString());
		
		//把返回的参数进行MD5签名验证
		
		String mysign = AlipaySubmit.buildRequestMysign(signMap);
		//日志数据
		String responseTxt =  AlipayNotify.verifyResponse(notify_id);
        String sWord = "responseTxt=" + responseTxt + "\n 返回的签名：" + sign + "\n 参数的签名：" + mysign + "\n 返回回来的参数：" + AlipayCore.createLinkString(signMap);
	    AlipayCore.logResult(sWord);
		if(mysign.equals(sign)){
			//签名通过
			//判断交易状态
			Order order = orderService.findByOrderNum(out_trade_no);
			if(order != null && order.getStatus()==0){
				//订单存在并且是待支付状态
				//判断交易是否成功
				if(trade_status.equals("TRADE_SUCCESS")){
					orderService.updateStatus(order.getId(), 1);
					orderService.updatePayCompleteTime(order.getId());
					
					//插入支付日志
					Map<String,String> map = new HashMap<String,String>();
					map.put("pro_type", "销售");
					map.put("pay_type", "支付宝");
					map.put("pro_info", body);
					map.put("user_name", order.getBuyMember().getUsername());
					//支付宝交易单号
					map.put("trade_no", trade_no);
					map.put("pay_amount", total_fee);
					map.put("status", "支付完成");
					map.put("buyer_email", buyer_email);
					orderService.insertPayLog(map);
					
					//跳转到支付成功页面
					return "/order/toSuccess.jspx?orderId="+order.getId();
				}
			}
		}
		*/
		
		return null;
	}
	
	@RequestMapping("/ali_notify_url.jspx")
	public void getNotifyUrl(HttpServletRequest request,HttpServletResponse
		 	response){
		try {
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			//商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			
			//支付价格
			String total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"),"UTF-8");
			
			String seller_id = new String(request.getParameter("seller_id").getBytes("ISO-8859-1"),"UTF-8");
			
			//买家支付宝账号
			String buyer_email = new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"),"UTF-8");
			
			//产品描述
			String body = new String(request.getParameter("body").getBytes("ISO-8859-1"),"UTF-8");

			Float total_pay = Float.parseFloat(total_fee);
			
			if(AlipayNotify.verify(params)){//验证成功
				Order o = orderService.findByOrderNum(out_trade_no);
				if(o != null){
					//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					if(o.getPayAmount() == total_pay && AlipayConfig.seller_id.equals(seller_id)){
						//请求参数和回调参数一致
						if(trade_status.equals("TRADE_FINISHED")){
							//注意：退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
							
							//判断该笔订单是否在商户网站中已经做过处理
							if(o.getStatus() == 9){
								//交易完成状态，不用管
							}else{
								//超过退款日期后发送改状态，预示着不可退款，进入交易完成状态（本程序中订单状态码为：9）
								//如果没有做过处理，把订单状态更新为交易完成状态
								orderService.updateStatus(o.getId(), 9);
								orderService.updateTradeCompleteTime(o.getId());
							}
						} 
						else if (trade_status.equals("TRADE_SUCCESS"))
						{
							//注意：付款完成后，支付宝系统发送该交易状态通知
							//判断该笔订单是否是待付款状态
							if(o.getStatus() == 0){
								//如果没有做过处理，更改订单状态为确认中状态（1），更新支付完成时间
								orderService.updateStatus(o.getId(), 1);
								orderService.updatePayCompleteTime(o.getId());
								
								//插入支付日志
								Map<String,String> map = new HashMap<String,String>();
								map.put("pro_type", "销售");
								map.put("pay_type", "支付宝");
								map.put("pro_info", body);
								map.put("user_name", o.getBuyMember().getUsername());
								map.put("trade_no", trade_no);
								map.put("pay_amount", o.getPayAmount()+"");
								map.put("status", "支付完成");
								map.put("buyer_email", buyer_email);
								orderService.insertPayLog(map);
							}
						}
						//请不要修改或删除
						response.getWriter().write("success");	
					}
				}
			}else{//验证失败
				response.getWriter().write("fail");
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}
}