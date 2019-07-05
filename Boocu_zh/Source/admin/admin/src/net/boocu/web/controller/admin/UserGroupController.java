package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.support.json.JSONUtils;

import freemarker.template.Template;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.NewsareaEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.service.ProductService;
import net.boocu.project.util.ConfigUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.bean.common.MailSenderInfo;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.enums.TemplateTypeEnum;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.TemplateService;

@Controller("userGroupController")
@RequestMapping(value = "/admin/userGroupMng")
public class UserGroupController {
	private static final String TEM_PATH = "/template/admin/subscriptionMng/userGroupMng";

	@Resource
	MemberService memberService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	TemplateService templateService;

	@Resource
	ProductService productService;

	@Resource
	EMailService emailService;

	@RequestMapping(value = "/toUserGroupList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/userGroupList";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);

		Pageable pageable = new Pageable(pagenumber, rows);
		Page<MemberGradeEntity> page = memberGradeService.findPage(pageable);
		List<MemberGradeEntity> memberEntities = page.getCont();
		List<Map> resultList = new ArrayList<>();
		for (MemberGradeEntity entity : memberEntities) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", entity.getId());
			item.put("gradename", entity.getName());
			item.put("templateId", entity.getTemplate());
			if (entity.getTemplate() != null && !entity.getTemplate().isEmpty()) {
				item.put("templatename", templateService.get(entity.getTemplate()).getName());
			}else{
				item.put("templatename", "");
			}
			resultList.add(item);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	/** 跳转修改 */
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/userGroup_edit";
	}

	/** 跳转修改 */
	@RequestMapping(value = "/getTemplate.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getTemplate(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<net.boocu.web.Template> items = templateService.getList(TemplateTypeEnum.subMail);
		List<Map> resultList = new ArrayList<Map>();
		for (net.boocu.web.Template item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	/** 获取模版信息 */
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doEdit_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String template = ReqUtil.getString(request, "template", "");
		MemberGradeEntity entity = memberGradeService.find(id);
		entity.setTemplate(template);
		memberGradeService.update(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "修改成功");
		RespUtil.renderJson(response, result);
	}

	/** 增加跳转 */
	@RequestMapping(value = "/add_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/userGroup_add";
	}

	/** 执行增加用户组 */
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doAdd_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		String template = ReqUtil.getString(request, "template", "");
		String gradename = ReqUtil.getString(request, "gradename", "");
		String priceType = ReqUtil.getString(request, "priceType", "");
		MemberGradeEntity entity = new MemberGradeEntity();
		entity.setName(gradename);
		entity.setPriceType(priceType);
		entity.setTemplate(template);
		memberGradeService.save(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "增加成功");
		RespUtil.renderJson(response, result);
	}

	/** 执行删除用户组 */
	@RequestMapping(value = "/del_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_memberGrade(@RequestParam Long[] gradeId, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<MemberEntity> allList = new ArrayList<>();
		for (Long id : gradeId) {
			MemberGradeEntity gradeEntity = memberGradeService.find(id);
			List<MemberEntity> list = memberService.findList(Filter.eq("memberGradeEntity", gradeEntity));
			allList.addAll(list);
		}
		if (allList.size() > 0) {
			result.put("result", 0);
			result.put("message", "用户组已有用户成员,无法删除!");
		} else {
			for (Long id : gradeId) {
				memberGradeService.delete(id);
			}
			result.put("result", 1);
			result.put("message", "删除成功");
		}
		RespUtil.renderJson(response, result);
	}

	/** 获取模版信息 */
	@RequestMapping(value = "/getTemplateContext.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String getTemplateContext(HttpServletRequest request, HttpServletResponse response, Model model) {
		String mail = ReqUtil.getString(request, "template", "");
		net.boocu.web.Template item = templateService.get(mail);
		String html = templateService.read(item);
		model.addAttribute("temName", item.getName());
		model.addAttribute("context", html);
		return TEM_PATH + "/templateInfo";
	}
}
