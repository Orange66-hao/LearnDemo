package net.boocu.project.controller.front;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import net.boocu.framework.enums.CaptchaTypeEnum;
import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.framework.util.WebUtils;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.enums.productType;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MailSignatureService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SubscribeInfoService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.util.ConfigUtil;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.bean.LoginBean;
import net.boocu.web.bean.common.Blowfish;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.bean.common.SimpleMailSender;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.enums.AccountLockTypeEnum;
import net.boocu.web.enums.LoginTypeEnum;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.setting.security.SecuritySetting;
import net.boocu.web.shiro.ShiroPrincipal;

@Controller("subscribeInfoController")
@RequestMapping("/subscribeInfo")
public class SubscribeInfoController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/subscribe/subscription";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");
	/** 失败信息 */
	private static final Message ERROR_MESSAGE = Message.error("操作失败!");

	private static JavaMailSender sender = null;

	@Resource(name = "productSubscribeServiceImpl")
	private ProductSubscribeService subscribeService;
	@Resource(name = "subscribeInfoServiceImpl")
	private SubscribeInfoService subscribeInfoService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "friendsServiceImpl")
	private FriendsService friendsService;

	@Resource(name = "helpServiceImpl")
	private HelpService helpService;

	@Resource(name = "productBrandServiceImpl")
	private ProductBrandService proBrandService;

	@Resource(name = "productclassServiceImpl")
	private ProductclassService proClassService;

	@Resource(name = "producttypeServiceImpl")
	private ProducttypeService producttypeService;

	@Resource(name = "industryClassServiceImpl")
	private IndustryClassService indclassService;

	@Resource
	private MessageService messageService;
	
	@Resource
    private RSAService rsaService;
	@Resource
	private MailSignatureService mailSignatureService;
	/** 显示用户订阅信息发送记录 */
	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model,LoginBean loginBean) {
		if(loginBean!=null&&StringUtils.isNotBlank(loginBean.getUsername())) {
	    	// 判断是否开放登录
	    	SecuritySetting setting = SettingUtils.get().getSecurity();
	    	if (!setting.getLoginEnabled()) {
	    		RespUtil.render(response, "text/html", "非法操作");
        		return null;
	    	}
	    	
	    	// 获取会员
	    	MemberEntity pMember = null;
	    	if (setting.verifyLoginMethod(LoginTypeEnum.email) && StringUtils.contains(loginBean.getUsername(), "@")) {
	    		pMember = memberService.findByEmail(loginBean.getUsername());
	    	}
	    	if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.mobile)
	    			&& StringUtils.length(loginBean.getUsername()) == 11) {
	    		pMember = memberService.findByMobile(loginBean.getUsername());
	    	}
	    	if (pMember == null && setting.verifyLoginMethod(LoginTypeEnum.username)) {
	    		pMember = memberService.findByUsername(loginBean.getUsername());
	    	}
	    	// 验证会员
	    	if (pMember == null) {
	    		RespUtil.render(response, "text/html", "非法操作");
        		return null;
	    	}    
	    	// 账号状态
//			if(pMember.getEnabled() ==2){
//				return MessageUtil.error("username","账号未激活，请先登录注册邮箱激活！");
//			}
	    	if(pMember.getEnabled() ==0){
	    		RespUtil.render(response, "text/html", "非法操作");
        		return null;
	    	}
	    	
	    	// 判断是否会员被锁定
	    	if (pMember.getLocked()) {
	    		// 判断是否属于锁定范围
	    		if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
	    			
	    			// 判断是否永久锁定
	    			if (setting.getAccountLockTime() == 0) {
	    				/*return MessageUtil.error("username","账户已被锁定");*/
	    				RespUtil.render(response, "text/html", "非法操作");
	            		return null;
	    			}
	    			
	    			// 判断是否解锁会员
	    			if (setting.getAccountUnlocked(pMember.getLockedDate())) {
	    				pMember.setLoginFailureCount(0);
	    				pMember.setLocked(false);
	    				pMember.setLockedDate(null);
	    			} else {
	    				/*return MessageUtil.error("username","账户已被锁定");*/
	    				RespUtil.render(response, "text/html", "非法操作");
	            		return null;
	    			}
	    		} else {
	    			pMember.setLoginFailureCount(0);
	    			pMember.setLocked(false);
	    			pMember.setLockedDate(null);
	    		}
	    	}
	    	
	    	// 判断是否密码错误
	    	if (!pMember.getPassword().equals(loginBean.getPassword())) {
	    		
	    		// 判断是否锁定会员
	    		pMember.setLoginFailureCount(pMember.getLoginFailureCount() + 1);
	    		if (setting.verifyAccountLock(AccountLockTypeEnum.member, pMember.getLoginFailureCount())) {
	    			pMember.setLocked(true);
	    			pMember.setLockedDate(new Date());
	    		}
	    		memberService.update(pMember);
	    		
	    		// 判断是否属于锁定范围
	    		if (setting.verifyAccountLockScope(AccountLockTypeEnum.member)) {
	    			RespUtil.render(response, "text/html", "非法操作");
	        		return null;
	    		} else {
	    			RespUtil.render(response, "text/html", "非法操作");
	        		return null;
	    		}
	    	}
	    	
	    	// 登录成功
	    	pMember.setLoginIp(ReqUtil.getIpAddr(request));
	    	pMember.setLoginDate(new Date());
	    	pMember.setLoginFailureCount(0);
	    	memberService.update(pMember);
	    	
	    	// 删除RSA私钥
//	        rsaService.removePrivateKey(request);
	    	
	    	// 重构Session
	    	//WebUtils.refactorSession(request);
	    	HttpSession session = request.getSession();
	    	
	    	//把用户放到session中，供后续使用
	    	session.setAttribute("loginUser", pMember);
	    	
	    	if (pMember != null) {
	    		MemberGradeEntity grader = pMember.getMemberGradeEntity();
	    		if (grader != null) {
	    			session.setAttribute("priceType", grader.getPriceType());
	    		}
	    	}
	    	
	    	// 获取站内信未读
	    	Map ms = new HashMap<>();
	    	ms.put("member", pMember);
	    	ms.put("state", 0);
	    	ms.put("type", 1);
	    	session.setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
	    	
	    	session.setAttribute("helps", helpService.findAll());
	    	
	    	// 判断会员是否已登录
	    	if (memberService.authorized()) {
	    		session.removeAttribute(MemberEntity.PRINCIPAL_ATTR_NAME);
	    		WebUtils.removeCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME);
	    		WebUtils.removeCookie(request, response, MemberEntity.AUTOLOGIN_COOKIE_NAME);
	    	}
	    	session.setAttribute(MemberEntity.PRINCIPAL_ATTR_NAME, new ShiroPrincipal(pMember.getId(), pMember.getUsername()));
	    	//添加了有效时间，与自动登录cookie有效时间一致
	    	WebUtils.addCookie(request, response, MemberEntity.USERNAME_COOKIE_NAME, pMember.getUsername(),MemberEntity.AUTOLOGIN_COOKIE_TIME);
	   	 	if(pMember.getFirst()==1) {
		   	 	try {
					response.sendRedirect("/user/userfirst");
				} catch (IOException e) {
					e.printStackTrace();
				}
	   	 	}
		}
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		if (currentMember != null) {
			// 获取站内信未读
			Map ms = new HashMap<>();
			ms.put("member", memberService.getCurrent());
			ms.put("state", 0);
			ms.put("type", 1);
			model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
			// 友情推荐 add by fang 20150906
			model.addAttribute("friends", friendsService.findAll());

			// 接受信息的频率及记录
			List<Object> modelList = subscribeInfoService.getModel(currentMember.getId());
			model.addAttribute("model", modelList);

			Pageable pageable = new Pageable(1, 10);
			pageable.setShowtype("dingyue");//添加区分字段，在邮件订阅显示中，限制最大不超过5页。其他显示模块不限制
			Map map = new HashMap<>();

			map.put("createdate", modelList.isEmpty() ? "" : modelList.get(0).toString());
			map.put("member", currentMember);
			
			Page<SubscribeInfoEntity> qq=subscribeInfoService.findFrontSubscribePage(pageable, map);
			// 帮助信息
			model.addAttribute("page", subscribeInfoService.findFrontSubscribePage(pageable, map));

			// 帮助信息
			model.addAttribute("helps", helpService.findAll());
			// 类型集合
			model.addAttribute("proType", producttypeService.findAll());

			// 真实路径
			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);
			return "front/userCenter/dataManage/subscribe/user_subscribe";
		} else {
			return "redirect:/login.jhtml";
		}
	}
	

	/** 显示用户订阅信息发送记录 */
	@RequestMapping(value = "/dataList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toDataList(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "5") int pageSize,
			@RequestParam(required = false) String createdate, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		Pageable pageable = new Pageable(pageNum, 10);
		pageable.setShowtype("dingyue");//添加区分字段，在邮件订阅显示中，限制最大不超过5页。其他显示模块不限制
		Map map = new HashMap<>();
		map.put("createdate", createdate);
		map.put("member", memberService.getCurrent());
		model.addAttribute("page", subscribeInfoService.findFrontSubscribePage(pageable, map));
		return "front/include/dataLists/subscribeList";
	}

	/**
	 * 服务类型ID
	 * 查询是否有订阅当前的服务 回显前端
	 * @param type
	 */
	@ResponseBody
	@RequestMapping(value = "/getSubscribrStatus.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map getSubscribrStatus(@RequestParam(required = false, defaultValue = "0")Long type,Model model){
		MemberEntity current = memberService.getCurrent();

		List<ProductSubscribeEntity> list = subscribeService.findList(Filter.eq("memberEntity", current),Filter.eq("proType",type),Filter.eq("isDelete",0));
		Map map=new HashMap();
		if(list!=null&&list.size()>0){
			map.put("isSubscribe",true);
			//“1234” 全新二手  进口国产
			map.put("subscribeTerm",list.get(0).getSubscribeTerm());
			map.put("emailRate",list.get(0).getSubscribeEmail());
			map.put("mobileRate",list.get(0).getSubscribeMobile());
		}else{
			map.put("isSubscribe",false);
		}
		map.put("proType",type);
		return map;
	}
	/** 显示用户订阅信息发送记录 */
	/**
	 * 
	 * @param productType
	 * @param request
	 * @param response
	 * @param model
	 * @param type 要跳转的分类标记
	 * @return
	 */
	@RequestMapping(value = "/infoManage.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String infoManage(@RequestParam(required = false, defaultValue = "0") Long productType,
			HttpServletRequest request, HttpServletResponse response, Model model,@RequestParam(required = false, defaultValue = "1")Long type) {
		// 友情推荐 add by fang 20150906
		model.addAttribute("friends", friendsService.findAll());
		model.addAttribute("typePage", type);
		// 接受信息的频率及记录
		MemberEntity currentMember = memberService.getCurrent();
		if(currentMember==null) {
			try {
				response.sendRedirect("/login");
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addAttribute("currentMember", currentMember);
		List<Object> modelList = subscribeInfoService.getModel(currentMember.getId());
		model.addAttribute("model", modelList);

		Pageable pageable = new Pageable(1, 10);
		Map map = new HashMap<>();

		map.put("createdate", modelList.isEmpty() ? "" : modelList.get(0).toString());
		map.put("member", currentMember);
		// 帮助信息
		model.addAttribute("page", subscribeInfoService.findFrontSubscribePage(pageable, map));

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());
		// 类型集合
		model.addAttribute("proType", producttypeService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("memberEntity", currentMember));
		model.addAttribute("type", productType);
		if (productType != 0) {
			filters.add(Filter.like("proType", "%" + productType.toString() + "%"));
		}
		filters.add(Filter.eq("isDelete", 0));
		List<ProductSubscribeEntity> subEntity = subscribeService.findList(filters);
		model.addAttribute("items", subEntity);

		return "front/userCenter/dataManage/subscribe/user_subscribe_edit";
	}

}
