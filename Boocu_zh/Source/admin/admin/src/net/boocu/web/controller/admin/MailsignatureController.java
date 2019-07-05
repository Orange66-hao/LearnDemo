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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.service.MailSignatureService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;

@Controller("userMailController")
@RequestMapping("/admin/Mailsignature")
public class MailsignatureController {

	private static final String TEM_PATH = "/template/admin/mailsignature";

	@Resource
	MemberService memberService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	ProductSubscribeService subscribeService;
	
	@Resource
	MailSignatureService mailSignatureService;

	@Resource
	ProductService productService;

	@Resource
	ProducttypeService typeService;
	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/mailsignaturelist";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);

		Pageable pageable = new Pageable(pagenumber, rows);
		Page<MailSignatureEntity> page = mailSignatureService.findPage(pageable);
		List<MailSignatureEntity> memberEntities = page.getCont();
		List<Map> resultList = new ArrayList<>();
		for (MailSignatureEntity entity : memberEntities) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", entity.getId());
			item.put("name", entity.getName());
			item.put("content", entity.getContent());
			
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

	/** 增加跳转 */
	@RequestMapping(value = "/add_mailsignature.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/mailsignature_add";
	}
	
	/** 执行增加用户组 */
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doAdd_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = ReqUtil.getString(request, "name", "");
		String content = ReqUtil.getString(request, "contents", "");
		MailSignatureEntity entity = new MailSignatureEntity();
		entity.setName(name);
		entity.setContent(content);
		mailSignatureService.save(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "增加成功");
		RespUtil.renderJson(response, result);
	}
	
	/** 跳转修改 */
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", mailSignatureService.find(id));
		return TEM_PATH + "/mailsignature_edit";
	}

	/** 查看 */
	@RequestMapping(value = "/look_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberService.find(id));
		return TEM_PATH + "/memberSub_info";
	}

	/** 修改 */
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void save_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String content = ReqUtil.getString(request, "contents", "");
		MailSignatureEntity entity = mailSignatureService.find(id);
		entity.setContent(content);
		entity.setName(name);
		mailSignatureService.update(entity);
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

	
	/** 执行删除用户组 */
	@RequestMapping(value = "/del_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_memberGrade(@RequestParam Long[] gradeId, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		
			for (Long id : gradeId) {
				mailSignatureService.delete(id);
			}
			result.put("result", 1);
			result.put("message", "删除成功");
		
		RespUtil.renderJson(response, result);
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
	
	 /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_mailsignature_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, MemberGradeEntity memberGradeEntity) {
    	List<MailSignatureEntity> memberGradeEntities = mailSignatureService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(MailSignatureEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
	
	
	
}
