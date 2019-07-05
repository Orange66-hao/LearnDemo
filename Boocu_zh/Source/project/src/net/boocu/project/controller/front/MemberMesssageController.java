package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.MessageTextEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.MessageTextService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.service.WorldAreaService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;

@Controller("messageController")
@RequestMapping("/memberMessage")
public class MemberMesssageController {
	private static final String TEM_PATH = "/template/front/message";
	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");
	/** 失败信息 */
	private static final Message ERROR_MESSAGE = Message.error("操作失败!");
	@Resource
	private ProductSaleService productSaleService;

	@Resource
	private HelpService helpService;

	@Resource
	private SalesService salesService;

	@Resource
	private ProductService productService;

	@Resource
	private NewsService newsService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private ProducttypeService producttypeService;

	@Resource
	private IndustryClassService industryClassService;

	@Resource
	private MemberService memberService;

	@Resource
	private WorldAreaService worldAreaService;

	@Resource
	private FriendsService friendsService;

	@Resource
	private SysMessageService sysmessageService;
	@Resource
	private MessageTextService messageTextService;
	@Resource
	private AdminService adminService;
	@Resource
	private MessageService messageService;

	@RequestMapping(value = { "toMessage", "/toMessageList.jspx" }, method = { RequestMethod.GET, RequestMethod.POST })
	public String toMessageList(@RequestParam(required = false, defaultValue = "8") int pageSize,
			@RequestParam(required = false, defaultValue = "1") int pageNum, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		if (currentMember != null) {
			int appr = ReqUtil.getInt(request, "appr", 1);
			Pageable pageable = new Pageable();
			pageable.setPageSize(pageSize);
			String keyword = ReqUtil.getString(request, "keyword", "");
			Map map = new HashMap<>();
			map.put("keyword", keyword);
			map.put("member", currentMember);
			Pageable pageable1 = new Pageable();
			pageable1.setPageSize(pageSize);
			if (appr == 1) {
				pageable.setPageNumber(pageNum);
			} else {
				pageable1.setPageNumber(pageNum);
			}
			model.addAttribute("page", messageService.findPageOrSendList(pageable, map));
			map = new HashMap<>();
			map.put("member", currentMember);
			model.addAttribute("page1", sysmessageService.findPageOrRecList(pageable1, map));
			model.addAttribute("appr", appr);
			// 友情推荐
			model.addAttribute("friends", friendsService.findAll());

			// 帮助信息
			model.addAttribute("helps", helpService.findAll());
			return TEM_PATH + "/messageInfo";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	@RequestMapping(value = "/sendMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String sendMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		AdminEntity admin = adminService.find(Filter.eq("username", "admin"));
		model.addAttribute("admin", admin);
		return TEM_PATH + "/messageSend";
	}

	@RequestMapping(value = "/doSendMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Message doSendMessage(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		String title = ReqUtil.getString(request, "title", "");
		String context = ReqUtil.getString(request, "context", "");
		String rec = ReqUtil.getString(request, "rec", "");
		MessageTextEntity text = new MessageTextEntity();
		SysMessageEntity entity = new SysMessageEntity();
		text.setMessage_context(context);
		text.setMessage_title(title);
		entity.setSendEntity(currentMember);
		entity.setState(0);
		entity.setRecEntity(adminService.find(Filter.eq("username", rec)));
		entity.setMessageEntity(messageTextService.save(text));
		sysmessageService.save(entity);
		return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = "/delMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Message delMessage(@RequestParam Long[] messageId, @RequestParam Long type,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		for (Long id : messageId) {
			if (type == 1) {
				messageService.delete(id);
			} else if (type == 2) {
				sysmessageService.delete(id);
			} else {
				return ERROR_MESSAGE;
			}
		}
		return SUCCESS_MESSAGE;
	}

	/** 查看 */
	@RequestMapping(value = "/findMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String findMessage(@RequestParam(required = false) Long messageId, @RequestParam Long type,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if (type == 1) {
			MessageEntity entity = messageService.find(messageId);
			entity.setState(1);
			messageService.update(entity);
			model.addAttribute("mes", entity);
		} else {
			SysMessageEntity entity = sysmessageService.find(messageId);
			entity.setState(1);
			sysmessageService.update(entity);
			model.addAttribute("mes", entity);
		}
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		request.getSession().setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		return TEM_PATH + "/messageShow";
	}

	/** 回复 */
	@RequestMapping(value = "/ReplyMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String ReplyMessage(@RequestParam(required = false) Long messageId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		model.addAttribute("item", messageService.find(messageId));
		return TEM_PATH + "/messageReply";
	}

	/** 回复 */
	@RequestMapping(value = "/doReplyMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void doReplyMessage(@RequestParam(required = false) Long messageId, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MessageEntity recEntity = messageService.find(id);
		StringBuffer str = new StringBuffer();
		str.append("------原件信息------\n");
		str.append("发件人：" + recEntity.getSendEntity().getUsername() + "\n");
		str.append("发送时间:" + recEntity.getCreateDate() + "\n");
		str.append("收件人:" + recEntity.getRecEntity().getUsername() + "\n");
		str.append("标题:" + recEntity.getMessageEntity().getMessage_title() + "\n");
		str.append("\t内容\n" + recEntity.getMessageEntity().getMessage_context() + "\n");
		str.append("-------------------------\n\n\n");
		String title = ReqUtil.getString(request, "title", "");
		String context = "发件人:" + recEntity.getRecEntity().getUsername() + "\n";
		context += "内容:" + ReqUtil.getString(request, "context", "") + "\n";
		str.append(
				context + "\n发送时间：" + DateTimeUtil.formatDateTimetoString(new Date(), DateTimeUtil.FMT_yyyyMMddHHmmss));
		MessageTextEntity text = new MessageTextEntity();
		SysMessageEntity entity = new SysMessageEntity();
		text.setMessage_context(str.toString());
		text.setMessage_title(title);
		entity.setSendEntity(recEntity.getRecEntity());
		entity.setState(0);
		entity.setRecEntity(recEntity.getSendEntity());
		entity.setMessageEntity(messageTextService.save(text));
		sysmessageService.save(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "回复成功");
		RespUtil.renderJson(response, result);
	}
}
