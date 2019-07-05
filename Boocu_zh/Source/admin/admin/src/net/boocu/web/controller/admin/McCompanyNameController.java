package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.McCompanyNameEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McCompanyNameService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.SuCompanyNameService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;
import net.sf.json.JSONArray;

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
@Controller("mcCompanyNameController")
@RequestMapping("/admin/mcCompanyName")
public class McCompanyNameController {

	private static final String TEM_PATH = "/template/admin/mcCompanyName";

	@Resource(name = "adminServiceImpl")
	AdminService adminService;

	@Resource
	private McCompanyNameService mcCompanyNameService;

	@Resource
	private SuCompanyNameService suCompanyNameService;

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
		return TEM_PATH + "/mcCompanyNamelist";
	}

	// 跳转到实体添加页面 添加公司名称
	@RequestMapping(value = "/add_mcCompanyName.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/mcCompanyName_add";

	}

	// 跳转到实体添加页面 添加高校名称
	@RequestMapping(value = "/add_universitiesName.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McCompanyName2(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("type",2);
		return "/template/admin/mccompanyname" + "/universitiesName_add";

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
			pageable.getFilters().add(Filter.or(flist));
		}
		/*
		 * pageable.getFilters().add(Filter.eq("apprStatus", 1));
		 * pageable.getFilters().add(Filter.eq("isDel", 0));
		 */
		Page<McCompanyNameEntity> page = mcCompanyNameService.findPage(pageable);
		List<McCompanyNameEntity> resultList = page.getCont();

		List<Map> resultData = new ArrayList<Map>();
		for (McCompanyNameEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
			data.put("ids", item.getId());
			data.put("name", item.getName());
			data.put("mc_company", queryall(item.getMc_company()));
			data.put("address", item.getAddress());
			data.put("create_date", item.getCreateDate());
			data.put("sort", item.getSort());
			data.put("phone", item.getPhone());
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
		McCompanyNameEntity topNode = mcCompanyNameService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McCompanyNameEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McCompanyNameEntity> items = mcCompanyNameService.findList(flist, Sequencer.asc("sort"));
		for (McCompanyNameEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McCompanyNameEntity> children = mcCompanyNameService.findList(Filter.eq("parentid", item.getId()));
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
		McCompanyNameEntity mcCompanyNameEntity = mcCompanyNameService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcCompanyNameEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcCompanyNameEntity.getId()));
			List<McCompanyNameEntity> items = mcCompanyNameService.findList(flist, Sequencer.asc("sort"));
			for (McCompanyNameEntity item : items) {
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
	 * 方法:将新添加的McCompanyNameEntity（本身实体 ）保存到数据库中 传入参数:McCompanyNameEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McCompanyNameEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcCompanyName(HttpServletRequest request, HttpServletResponse response, Model model,
			McCompanyNameEntity mcCompanyNameEntity) {
		/*
		 * CompanyNameEntity memberEntity = memberService.getCurrent();
		 * if(memberEntity != null){
		 * mcCompanyNameEntity.setCreateuser(memberEntity.getId().toString()); }
		 * if(mcCompanyNameEntity.getParentid().isEmpty()){
		 * mcCompanyNameEntity.setParentid("1"); }
		 */
		mcCompanyNameService.save(mcCompanyNameEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McCompanyNameEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcCompanyName.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] idStrings = request.getParameterValues("id");
		Long[] ids = new Long[idStrings.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(idStrings[i]);
		}

		if (ids != null && ids.length > 0) {
			for (Long id : ids) {
				mcCompanyNameService.delete(id);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);

		}
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String company_type = request.getParameter("company_type");
		McCompanyNameEntity mcCompanyNameEntity = new McCompanyNameEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcCompanyNameEntity = this.mcCompanyNameService.find(lid);
			model.addAttribute("item", mcCompanyNameEntity);
		}
		if (company_type != null && company_type.equals("companyshow")) {
			return TEM_PATH + "/mcCompanyName_enterprise";
		} else {
			return TEM_PATH + "/mcCompanyName_edit";
		}
	}

	/**
	 * 
	 * 方法:保存更新之后的McCompanyNameEntity（本身实体 ） 传入参数:McCompanyNameEntity的字段
	 * id（更新实体的id） 传出参数:result（方法结果信息）
	 * 逻辑：查询该id的实体，存在则读取前台McCompanyNameEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcCompanyName(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String address = ReqUtil.getString(request, "address", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String sort = ReqUtil.getString(request, "sort", "");
		// String mc_company = ReqUtil.getString(request, "mc_company", "");
		McCompanyNameEntity OldMcCompanyNameEntity = mcCompanyNameService.find(id);
		// OldMcCompanyNameEntity.setParentid(parentid);
		OldMcCompanyNameEntity.setAddress(address);
		OldMcCompanyNameEntity.setPhone(phone);
		// OldMcCompanyNameEntity.setMc_company(mc_company);
		OldMcCompanyNameEntity.setName(name);
		OldMcCompanyNameEntity.setRemark(remark);
		OldMcCompanyNameEntity.setSort(Integer.parseInt(sort));
		mcCompanyNameService.update(OldMcCompanyNameEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_mcmember_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcCompanyNameGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McCompanyNameEntity memberGradeEntity) {
		List<McCompanyNameEntity> memberGradeEntities = mcCompanyNameService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (McCompanyNameEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		// String bb=JSONArray.fromObject(resultList).toString();
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map getNodeData2(String nameid) {
		List<Map<String, Object>> topNode = mcCompanyNameService.quarymccompanyname(nameid);
		if (topNode == null) {
			return null;
		}
		String id[] = new String[topNode.size()];
		String name[] = new String[topNode.size()];
		int i = 0;
		for (Map<String, Object> map : topNode) {
			id[i] = (map.get("id")).toString();
			name[i] = (String) map.get("name");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		return map;
	}

	// 根据公司名的id，查询对应name（名称）
	public Map queryall(String id) {
		List<Map<String, Object>> topNode = mcCompanyNameService.queryallids(id);
		if (topNode == null) {
			return null;
		}
		String ids[] = new String[topNode.size()];
		String name[] = new String[topNode.size()];
		int i = 0;
		for (Map<String, Object> map : topNode) {
			ids[i] = (map.get("ids")).toString();
			name[i] = (String) map.get("name");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		map.put("name", name);
		return map;
	}

	// 查询公司名是否存在
	@ResponseBody
	@RequestMapping(value = "/register.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Message register(String edit_name, String name, HttpServletRequest request, HttpSession session) {
		if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
			return Message.success("true");
		} else {
			List<Map<String, Object>> row = mcCompanyNameService.registermccompanyname(name);
			List<Map<String, Object>> register = suCompanyNameService.register(name);
			if (row.size() > 0 || register.size()>0) {
				return Message.success("false");
			}
			return Message.success("true");
		}
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public List<Map<String, Object>> quarymccompanyname(String id) {
		return mcCompanyNameService.quarymccompanyname(id);
	}

}
