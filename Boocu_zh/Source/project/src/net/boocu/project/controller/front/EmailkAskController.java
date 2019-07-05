/**
 * 
 */
package net.boocu.project.controller.front;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.entity.MessageTextEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.project.entity.UserInquiryEntity;
import net.boocu.project.service.AddressService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageTextService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.service.UserInquiryService;
import net.boocu.project.util.ConfigUtil;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;

/**
 * @author Administrator
 *
 */

@Controller("emailAskController")
@RequestMapping("/emailAsk")
public class EmailkAskController {

	private static final String TEMPATH = "/template/front/userCenter";

	@Resource
	private UserInquiryService userInquiryService;

	@Resource
	private ProductService productService;
	@Resource
	private SysMessageService messageService;
	@Resource
	HelpService helpService;

	@Autowired
	private AddressService addressService;
	@Resource
	SysMessageService sysMessageService;
	@Resource
	MessageTextService messageTextService;
	@Resource
	AdminService adminService;
	@Resource
	MemberService memberService;

	@RequestMapping(value = { "/validateUser.jspx" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void validateUser(HttpServletRequest request, HttpServletResponse response, Model model) {
		HttpSession session = request.getSession();
		try {
			// get user from session and validate it
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			Map<String, Object> map = new HashMap<String, Object>();
			if (member != null) {
				map.put("code", 1);
				map.put("realName", member.getRealName());
				map.put("phone", member.getMobile());
				map.put("email", member.getEmail());
				if (member.getAddressEntity() != null) {
					AddressEntity addr = addressService.findById(member.getAddressEntity().getId());
					if (addr != null) {
						map.put("address", addr.getProvince() + " " + addr.getCity() + " " + addr.getArea() + " "
								+ addr.getDetail());
					} else {
						map.put("address", "");
					}
				} else {
					map.put("address", "");
				}
			} else {
				map.put("code", 0);
			}
			RespUtil.renderJson(response, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 催促报价
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "/inquiry.jspx" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void inquiry(HttpServletRequest request, HttpServletResponse response) {
		Long id = ReqUtil.getLong(request, "id", (long) 0);
		try {
			String msg = null;
			if (userInquiryService.updateInquiry(id) > 0) {
				msg = "ok";
			} else {
				msg = "err";
			}
			response.getWriter().write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/getAskInfo", "/getAskInfo.jhtml" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String getAskInfo(HttpServletRequest request, HttpServletResponse response, Model model) {
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);

		Pageable pageable = new Pageable(pagenumber, rows);

		HttpSession session = request.getSession();

		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		if (member == null) {
			return "redirect:/login.jhtml";
		} else {
			Map<String, Object> parmas = new HashMap<String, Object>();
			parmas.put("userId", member.getId());
			model.addAttribute("currentMember", member);
			model.addAttribute("helps", helpService.findAll());
			model.addAttribute("inquiry", userInquiryService.findInquiryByPage(pageable, parmas));
			return TEMPATH + "/inquiry/inquiry";
		}
	}

	private String requality(int quality) {
		if (10 - quality == 10) {
			return "全新";
		} else if (10 - quality > 5) {
			return 10 - quality + "成新";
		} else {
			return "5成新以下";
		}
	}

	@RequestMapping(value = { "/sendAskEmail.jspx" }, method = { RequestMethod.GET, RequestMethod.POST })
	public void sendAskEmail(HttpServletRequest request, HttpServletResponse response, Model model) {
		String msg = null;
		try {
			String realName = ReqUtil.getString(request, "realName", null);
			String url = ReqUtil.getString(request, "url", null);
			String mobile = ReqUtil.getString(request, "mobile", null);
			String address = ReqUtil.getString(request, "address", null);
			String email = ReqUtil.getString(request, "email", null);
			String remark = ReqUtil.getString(request, "ramark", null);
			String productId = ReqUtil.getString(request, "productId", null);
			/*
			 * String info = ReqUtil.getString(request, "info", null); String
			 * onePrice = ReqUtil.getString(request, "onePrice", null);
			 * if(onePrice!=null && onePrice.trim().equals("电议")){ onePrice =
			 * "0"; }
			 */

			// 发送站内信
			if (memberService.getCurrent() != null) {
				SysMessageEntity sysEntity = new SysMessageEntity();
				MessageTextEntity text = new MessageTextEntity();
				text.setMessage_context(remark);
				text.setMessage_title("邮件咨询");
				sysEntity.setSendEntity(memberService.getCurrent());
				sysEntity.setState(0);
				sysEntity.setRecEntity(adminService.find(Filter.eq("username", "admin")));
				sysEntity.setMessageEntity(messageTextService.save(text));
				sysMessageService.save(sysEntity);
			}
			// 发送邮件
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost(ConfigUtil.getConfig("mail.smtphost"));
			mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			mailInfo.setUserName(ConfigUtil.getConfig("mail.username"));
			mailInfo.setPassword(ConfigUtil.getConfig("mail.pwd"));// 您的邮箱密码
			mailInfo.setFromAddress(ConfigUtil.getConfig("mail.username"));
			mailInfo.setToAddress(ConfigUtil.getConfig("mail.receive"));
			mailInfo.setSubject("晧辰仪器设备物联网平台 用户咨询商品");
			mailInfo.setContent("姓名:" + realName + "\n" + "手机号码：" + mobile + "\n" + "地址：" + address + "\n" + "邮箱:"
					+ email + "\n" + "留言:" + remark + "\n" + "咨询了以下商品： " + url);
			// 这个类主要来发送邮件
			SimpleMailSender sms = new SimpleMailSender();
			boolean result = sms.sendTextMail(mailInfo);

			if (result) {
				msg = "ok";
			} else {
				msg = "err";
			}

			response.getWriter().write(msg);

			HttpSession session = request.getSession();
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			// 判断用户是否登录，若已登录这添加到询价单，否则不添加
			if (member != null) {
				// 保存询价单
				UserInquiryEntity uie = new UserInquiryEntity();

				// 查询商品
				ProductEntity pe = productService.find(Long.parseLong(productId));
				if (pe != null) {
					String info = pe.getProductBrandEntity() != null ? pe.getProductBrandEntity().getName()
							: "" + " " + pe.getProNo() + " " + pe.getProName() + " "
									+ requality(pe.getQualityStatus().ordinal());

					// 价格类型
					String priceType = member.getMemberGradeEntity().getPriceType();
					BigDecimal onePrice = null;
					if (priceType != null && !"".equals(priceType)) {
						if (priceType.equals("同行")) {
							onePrice = pe.getProPeer();
						} else if (priceType.equals("来宾")) {
							onePrice = pe.getProCustomPrice();
						} else if (priceType.equals("客户")) {
							onePrice = pe.getProMemberPrice();
						} else if (priceType.equals("VIP")) {
							// VIP Price
							onePrice = pe.getProVipPrice();
						} else {
							// 电议
							onePrice = new BigDecimal(-1);
						}
					}

					uie.setProName(info);
					uie.setMemberEntity(member);
					pe.setId((productId != null && !"".equals(productId)) ? Long.valueOf(productId) : 0);
					uie.setProductEntity(pe);
					uie.setOnePrice(onePrice != null ? onePrice.floatValue() : 0);
					uie.setUrl(url);
					uie.setStatus(0);
					userInquiryService.insertInquiry(uie);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
