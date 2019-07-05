package net.boocu.web.controller.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.MessageTextEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.MessageTextService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

@Controller("SysMessageController")
@RequestMapping("/admin/member/memberMessage")
public class SysMessageController {
	private static final String TEM_PATH = "/template/admin/member/memberMessage";

	@Resource
	AdminService adminService;

	@Resource
	SysMessageService sysmessageService;

	@Resource
	MemberService memberService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	ProductService productService;

	@Resource
	ProductSaleService productSaleService;

	@Resource
	ProductBuyService productBuyService;

	@Resource
	ProductRentService productRentService;

	@Resource
	ProductWantRentService productWantRentService;

	@Resource
	ProductRepairService productRepairService;

	@Resource
	ProducWantRepairService productWantRepairService;

	@Resource
	MessageService messageServicel;

	@Resource
	MessageTextService messageTextService;

	@Resource
	MessageService messageService;
	@RequestMapping(value = "/toMemberMessageList.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String toMemberMessageList(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return TEM_PATH + "/memberMessageList";
	}

	@RequestMapping(value = "/getMessageData.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void getMessageData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String username = ReqUtil.getString(request, "username", "");
		int appr = ReqUtil.getInt(request, "appr", 1);
		Pageable pageable = new Pageable();
		pageable.setPageSize(rows);
		pageable.setPageNumber(pagenumber);
		Map map = new HashMap<>();
		map.put("keyword", keyword);
		map.put("username", username);
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map> resultList = new ArrayList<Map>();
		Page<SysMessageEntity> page = sysmessageService.findPageOrSendList(pageable, map);
		for (SysMessageEntity item : page.getCont()) {
			map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("sendName", item.getSendEntity().getUsername());
			map.put("recName", item.getRecEntity().getUsername());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String loginDate = sdf.format(item.getCreateDate());
			map.put("createDate", loginDate);
			map.put("title", item.getMessageEntity().getMessage_title());
			map.put("context", item.getMessageEntity().getMessage_context());
			if (item.getState() == 0) {
				map.put("state", "未读");
			} else {
				map.put("state", "已读");
			}
			resultList.add(map);
		}
		result.put("total", page.getTotal());
		result.put("rows", resultList);

		RespUtil.renderJson(response, result);
	}

	@RequestMapping(value = "/getSendMessageData.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void getSendMessageData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int appr = ReqUtil.getInt(request, "appr", 2);
		Pageable pageable = new Pageable();
		pageable.setPageSize(rows);
		pageable.setPageNumber(pagenumber);
		Map map = new HashMap<>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map> resultList = new ArrayList<Map>();

		Page<MessageEntity> page = messageServicel.findPageOrRecList(pageable, map);
		for (MessageEntity item : page.getCont()) {
			map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("sendName", item.getSendEntity().getUsername());
			map.put("recName", item.getRecEntity().getUsername());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String loginDate = sdf.format(item.getCreateDate());
			map.put("createDate", loginDate);
			map.put("title", item.getMessageEntity().getMessage_title());
			map.put("context", item.getMessageEntity().getMessage_context());
			if (item.getState() == 0) {
				map.put("state", "未读");
			} else {
				map.put("state", "已读");
			}
			resultList.add(map);
		}
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	/** 得到用户组信息 */
	@RequestMapping(value = "/getMemberGradeData.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void getMemberGradeData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<MemberGradeEntity> gradeList = memberGradeService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (MemberGradeEntity item : gradeList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	/** 得到所有用户私信 */
	@RequestMapping(value = "/getMemberData.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void getMemberData(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<MemberEntity> memberList = memberService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (MemberEntity item : memberList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getUsername());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	/** 群发信息 */
	@RequestMapping(value = "/sendGrade.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void sendGrade(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String title = ReqUtil.getString(request, "title", "");
		String context = ReqUtil.getString(request, "context", "");
		Long grade = ReqUtil.getLong(request, "grade", -1l);
		List<MemberEntity> memberList = memberService
				.findList(Filter.ge("memberGradeEntity", memberGradeService.find(grade)));
		for (MemberEntity memberEntity : memberList) {
			MessageTextEntity text = new MessageTextEntity();
			MessageEntity entity = new MessageEntity();
			text.setMessage_context(context);
			text.setMessage_title(title);
			entity.setSendEntity(adminService.getCurrent());
			entity.setState(0);
			entity.setRecEntity(memberEntity);
			entity.setMessageEntity(messageTextService.save(text));
			messageServicel.save(entity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "发送成功");
		RespUtil.renderJson(response, result);
	}

	/** 私信 */
	@RequestMapping(value = "/sendMember.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void sendMember(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String title = ReqUtil.getString(request, "title", "");
		String context = ReqUtil.getString(request, "context", "");
		Long member = ReqUtil.getLong(request, "member", -1l);
		MessageTextEntity text = new MessageTextEntity();
		MessageEntity entity = new MessageEntity();
		text.setMessage_context(context);
		text.setMessage_title(title);
		entity.setSendEntity(adminService.getCurrent());
		entity.setState(0);
		entity.setRecEntity(memberService.find(member));
		entity.setMessageEntity(messageTextService.save(text));
		messageServicel.save(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "发送成功");
		RespUtil.renderJson(response, result);
	}

	/** 查看 */
	@RequestMapping(value = "/findView.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String findView(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		SysMessageEntity item = sysmessageService.find(id);
		item.setState(1);
		item.setModifyDate(new Date());
		sysmessageService.update(item);
		model.addAttribute("item", item);
		return TEM_PATH + "/memberMessageView";
	}

	/** 删除 */
	@RequestMapping(value = "/delMess.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void delMess(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		sysmessageService.delete(id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}
	/** 回复收件*/
	@RequestMapping(value = "/replyMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String replyMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		SysMessageEntity item = sysmessageService.find(id);
		model.addAttribute("item", item);
		return TEM_PATH + "/memberMessageReply";
	}
	/** 回复收件*/
	@RequestMapping(value = "/doReplyMessage.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void doReplyMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		SysMessageEntity recEntity=sysmessageService.find(id);
		StringBuffer str = new StringBuffer();
		str.append("------原件信息------\n");
		str.append("发件人："+recEntity.getSendEntity().getUsername()+"\n");
		str.append("发送时间:"+recEntity.getCreateDate()+"\n");
		str.append("收件人:"+recEntity.getRecEntity().getUsername()+"\n");
		str.append("标题:"+recEntity.getMessageEntity().getMessage_title()+"\n");
		str.append("\t内容\n"+recEntity.getMessageEntity().getMessage_context()+"\n");
		str.append("-------------------------\n\n\n");
		String title =ReqUtil.getString(request,"title", "");
		String context ="发件人:"+adminService.getCurrent().getUsername()+"\n";
		context+="内容："+ReqUtil.getString(request,"context", "")+"\n";
		str.append(context+"\n发送时间："+DateTimeUtil.formatDateTimetoString(new Date(), DateTimeUtil.FMT_yyyyMMddHHmmss));
		MessageTextEntity text = new MessageTextEntity();
		MessageEntity entity = new MessageEntity();
		text.setMessage_context(str.toString());
		text.setMessage_title(title);
		entity.setSendEntity(adminService.getCurrent());
		entity.setState(0);
		entity.setRecEntity(recEntity.getSendEntity());
		entity.setMessageEntity(messageTextService.save(text));
		messageServicel.save(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "回复成功");
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		request.getSession().setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		RespUtil.renderJson(response, result);
	}
	/** 删除发件 */
	@RequestMapping(value = "/delSendMess.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void delSendMess(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		messageServicel.delete(id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}
	/** 查看发件 */
	@RequestMapping(value = "/findSendView.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String findSendView(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MessageEntity item = messageServicel.find(id);
		item.setState(1);
		item.setModifyDate(new Date());
		messageServicel.update(item);
		model.addAttribute("item", item);
		return TEM_PATH + "/memberMessageView";
	}

}
