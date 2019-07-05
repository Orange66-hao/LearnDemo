package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;

@Controller("userSubController")
@RequestMapping("/admin/userSubscribe")
public class SubscribeController {

	private static final String TEM_PATH = "/template/admin/subscriptionMng/userSubscribeMng";

	@Resource
	MemberService memberService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	ProductSubscribeService subscribeService;

	@Resource
	ProductService productService;

	@Resource
	ProducttypeService typeService;
	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/memberSubscribeList";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		int appr = ReqUtil.getInt(request, "appr", 0);
		Pageable pageable = new Pageable(pagenumber, rows);
		Map<String, Object> htMap = new HashMap<String, Object>();
		htMap.put("appr", 1);
		Page<ProductSubscribeEntity> page = subscribeService.findAllSubByMember(pageable, htMap);
		List<ProductSubscribeEntity> memberEntities = page.getCont();
		List<MemberEntity> newList = new ArrayList<>(); // 去重复
		List<Map> resultList = new ArrayList<>();
		for (ProductSubscribeEntity entity : memberEntities) {
			if (newList.contains(entity.getMemberEntity())) {
				continue;
			}
			newList.add(entity.getMemberEntity());
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", entity.getId());
			item.put("username", entity.getMemberEntity().getUsername());
			item.put("userId", entity.getMemberEntity().getId());
			item.put("gradename", entity.getMemberEntity().getMemberGradeEntity().getName());
			item.put("email", entity.getMemberEntity().getEmail());
			item.put("createDate", DateTimeUtil.formatDatetoString(entity.getCreateDate()));
			resultList.add(item);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	/** 未分组 */
	@RequestMapping(value = "/getNoGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getNoGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		Pageable pageable = new Pageable(pagenumber, rows);
		Map<String, Object> htMap = new HashMap<String, Object>();
		htMap.put("appr", 2);
		Page<ProductSubscribeEntity> page = subscribeService.findAllSubByMember(pageable, htMap);
		List<MemberEntity> newList = new ArrayList<>();
		List<ProductSubscribeEntity> memberEntities = page.getCont();
		List<Map> resultList = new ArrayList<>();
		for (ProductSubscribeEntity entity : memberEntities) {
			if (newList.contains(entity.getMemberEntity())) {
				continue;
			}
			newList.add(entity.getMemberEntity());
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", entity.getId());
			item.put("userId", entity.getMemberEntity().getId());
			item.put("username", entity.getMemberEntity().getUsername());
			item.put("email", entity.getMemberEntity().getEmail());
			item.put("loginDate", DateTimeUtil.formatDatetoString(entity.getMemberEntity().getLoginDate()));
			resultList.add(item);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	/** 跳转修改 */
	@RequestMapping(value = "/edit_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberService.find(id));
		return TEM_PATH + "/memberSub_edit";
	}

	/** 查看 */
	@RequestMapping(value = "/look_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberService.find(id));
		return TEM_PATH + "/memberSub_info";
	}

	/** 修改 */
	@RequestMapping(value = "/save_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void save_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		Long gradeid = ReqUtil.getLong(request, "gradeid", 0l);
		MemberEntity memberentity = memberService.find(id);
		memberentity.setMemberGradeEntity(memberGradeService.find(gradeid));
		memberService.save(memberentity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "修改成功");
		RespUtil.renderJson(response, result);
	}

	/** 查看订阅信息 */
	@RequestMapping(value = "/look_subscribe.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_subscribe(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		List<ProductSubscribeEntity> subEntity = subscribeService
				.findList(Filter.eq("memberEntity", memberService.find(id)));
		
		for (ProductSubscribeEntity entity : subEntity) {
			String[] type =entity.getProType().split("、");
			String newType="";
			for (int i = 0; i < type.length; i++) {
				String s = type[i];
				if(i!=type.length-1){
					newType+=typeService.find(Long.valueOf(s)).getTypeName()+"、";
				}else{
					newType+=typeService.find(Long.valueOf(s)).getTypeName();
				}
			}
			entity.setProType(newType);
		}
		model.addAttribute("items", subEntity);
		return TEM_PATH + "/subscripbeView/user_subscribe_edit";
	}

	/** 删除 */
	@RequestMapping(value = "/del_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberentity = memberService.find(id);
		List<ProductSubscribeEntity> subList= subscribeService.findList(Filter.eq("memberEntity", memberentity));
		subscribeService.deleteList(subList);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}
}
