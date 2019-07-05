package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.McContactsService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 
 * 
 * 
 * @author fang
 *
 *         2015年8月27日
 */
@Controller("mcContactsController")
@RequestMapping("/admin/mcContacts")
public class McContactsController {

	private static final String TEM_PATH = "/template/admin/mcContacts";

	@Resource(name = "adminServiceImpl")
	AdminService adminService;

	@Resource
	private McContactsService mcContactsService;

	@Resource
	private McCompanyNameController mcCompanyNameController;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@RequestMapping(value = "toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		AdminEntity admin = adminService.getCurrent();
		if ((admin.getId() == 1 && admin.getName().equals("admin"))||(admin.getId() == 75 && admin.getName().equals("林婉珠"))) {
			//model.addAttribute("showhandle", "false");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		}else {
			model.addAttribute("showhandle", "true");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		}
		return TEM_PATH + "/mcContactslist";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_mcContacts.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McContacts(HttpServletRequest request, HttpServletResponse response, Model model) {
		String model_yincang = request.getParameter("model_yincang");
		String companyId = request.getParameter("companyId");
		request.setAttribute("model_yincang", model_yincang);
		request.setAttribute("companyId", companyId);
		return TEM_PATH + "/mcContacts_add";

	}

	// 获取树形行业网络图
	@ResponseBody
	@RequestMapping(value = "getData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "create_date", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");

		Pageable pageable = new Pageable(pagenumber, rows);
		if (!sortValue.isEmpty()) {
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) : Sequencer.asc(sortValue);
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}

		if (!keyword.isEmpty()) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%" + keyword + "%"));
			flist.add(Filter.like("mail", "%" + keyword + "%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		/*
		 * pageable.getFilters().add(Filter.eq("apprStatus", 1));
		 * pageable.getFilters().add(Filter.eq("isDel", 0));
		 */
		Page<McContactsEntity> page = mcContactsService.findPage(pageable);
		List<McContactsEntity> resultList = page.getCont();

		List<Map> resultData = new ArrayList<Map>();
		for (McContactsEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
			data.put("ids", item.getId());
			data.put("name", item.getName());
			data.put("sort", item.getSort());
			data.put("mc_company", queryall(item.getMc_company()));
			data.put("job", item.getJob());
			data.put("phone", item.getPhone());
			data.put("create_date", item.getCreateDate());
			data.put("mail", item.getMail());
			data.put("remark", item.getRemark());
			resultData.add(data);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultData);
		RespUtil.renderJson(response, result);
	}

	// 获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		McContactsEntity topNode = mcContactsService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McContactsEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McContactsEntity> items = mcContactsService.findList(flist, Sequencer.asc("sort"));
		for (McContactsEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McContactsEntity> children = mcContactsService.findList(Filter.eq("parentid", item.getId()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}

	// 获取异步下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotree.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 1l);
		McContactsEntity mcContactsEntity = mcContactsService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcContactsEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcContactsEntity.getId()));
			List<McContactsEntity> items = mcContactsService.findList(flist, Sequencer.asc("sort"));
			for (McContactsEntity item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
				// map.put("state", "0".equals(item.getLeaf())?"closed":"open");
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 
	 * 方法:将新添加的McContactsEntity（本身实体 ）保存到数据库中 传入参数:McContactsEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McContactsEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcContacts(HttpServletRequest request, HttpServletResponse response, Model model,
			McContactsEntity mcContactsEntity, ModelCollectionEntity mcModelEntity) {
		/*
		 * ContactsEntity memberEntity = memberService.getCurrent();
		 * if(memberEntity != null){
		 * mcContactsEntity.setCreateuser(memberEntity.getId().toString()); }
		 * if(mcContactsEntity.getParentid().isEmpty()){
		 * mcContactsEntity.setParentid("1"); }
		 */
		mcContactsService.save(mcContactsEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McContactsEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcContacts.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcContacts(HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] idStrings = request.getParameterValues("id");
		Long[] ids = new Long[idStrings.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(idStrings[i]);
		}

		if (ids != null && ids.length > 0) {
			for (Long id : ids) {
				mcContactsService.delete(id);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);

		}
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcContacts(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String contact_type = request.getParameter("contact_type");

		McContactsEntity mcContactsEntity = new McContactsEntity();
		Long lid = Long.parseLong(id);
		mcContactsEntity = this.mcContactsService.find(lid);
		model.addAttribute("item", mcContactsEntity);
		if (contact_type != null && contact_type.equals("contactshow")) {
			return TEM_PATH + "/mcContacts_enterprise";
		} else {
			return TEM_PATH + "/mcContacts_edit";
		}
	}

	/**
	 * 
	 * 方法:保存更新之后的McContactsEntity（本身实体 ） 传入参数:McContactsEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台McContactsEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcContacts(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String job = ReqUtil.getString(request, "job", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String mc_company = ReqUtil.getString(request, "mc_company", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String mail = ReqUtil.getString(request, "mail", "");
		// String mc_company = ReqUtil.getString(request, "mc_company", "");
		McContactsEntity OldMcContactsEntity = mcContactsService.find(id);
		// OldMcContactsEntity.setParentid(parentid);
		OldMcContactsEntity.setJob(job);
		OldMcContactsEntity.setPhone(phone);
		// OldMcContactsEntity.setMc_company(mc_company);
		OldMcContactsEntity.setName(name);
		OldMcContactsEntity.setRemark(remark);
		OldMcContactsEntity.setSort(Integer.parseInt(sort));
		OldMcContactsEntity.setMail(mail);
		OldMcContactsEntity.setMc_company(mc_company);
		mcContactsService.update(OldMcContactsEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_mcmember_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcContactsGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McContactsEntity memberGradeEntity) {
		String mc_company_id = ReqUtil.getString(reqeust, "mc_company_id", "");

		List<Filter> flist = new ArrayList<Filter>();

		if (!mc_company_id.isEmpty()) {
			flist.add(Filter.like("mc_company", mc_company_id));
		}
		List<McContactsEntity> memberGradeEntities = mcContactsService.findList(flist);
		List<Map> resultList = new ArrayList<Map>();
		for (McContactsEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map getNodeData2(String rootId) {
		List<Map<String, Object>> topNode = quarycontacts(rootId);
		if (topNode == null) {
			return null;
		}
		String id[] = new String[topNode.size()];
		String name[] = new String[topNode.size()];
		String teacher[] = new String[topNode.size()];//老师姓名
		String major[] = new String[topNode.size()];//专业
		String resume[] = new String[topNode.size()];//简历
		int i = 0;
		for (Map<String, Object> map : topNode) {
			id[i] = (map.get("id")).toString();
			teacher[i] = (String) map.get("teacher");
			name[i] = (String) map.get("name");
			major[i] = (String) map.get("major");
			resume[i] = (String) map.get("resume");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("teacher", teacher);//老师姓名
		map.put("name", name);
		map.put("major",major);//专业
		map.put("resume",resume);//简历
		return map;
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map queryall(String id) {
		List<Map<String, Object>> topNode = mcCompanyNameController.quarymccompanyname(id);
		if (topNode == null) {
			return null;
		}
		String ids[] = new String[topNode.size()];
		String name[] = new String[topNode.size()];
		int i = 0;
		for (Map<String, Object> map : topNode) {
			ids[i] = (map.get("id")).toString();
			name[i] = (String) map.get("name");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", ids);
		map.put("name", name);
		return map;
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public List<Map<String, Object>> quarycontacts(String id) {
		return mcContactsService.quarycontacts(id);
	}

}
