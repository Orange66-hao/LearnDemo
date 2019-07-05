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
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McMajorService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.random.random;
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
@Controller("mcMajorController")
@RequestMapping("/admin/mcMajor")
public class McMajorController {

	private static final String TEM_PATH = "/template/admin/basedata/mcmajor";
	@Resource
	private McMajorService mcMajorService;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@RequestMapping(value = "toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/mcMajorlist";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_mcMajor.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McMajor(HttpServletRequest request, HttpServletResponse response, Model model) {
		String model_yincang = request.getParameter("model_yincang");
		request.setAttribute("model_yincang", model_yincang);
		return TEM_PATH + "/mcMajor_add";

	}

	// 获取树形行业网络图
	@ResponseBody
	@RequestMapping(value = "getData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
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
		Page<McMajorEntity> page = mcMajorService.findPage(pageable);
		List<McMajorEntity> resultList = page.getCont();

		List<Map> resultData = new ArrayList<Map>();
		for (McMajorEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
			data.put("ids", item.getId());
			data.put("name", item.getName());
			// data.put("mc_industryclass",
			// queryall(item.getMc_industryclass()));
			data.put("sort", item.getSort());
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
		McMajorEntity topNode = mcMajorService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McMajorEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McMajorEntity> items = mcMajorService.findList(flist, Sequencer.asc("sort"));
		for (McMajorEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McMajorEntity> children = mcMajorService.findList(Filter.eq("parentid", item.getId()));
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
		McMajorEntity mcMajorEntity = mcMajorService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcMajorEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcMajorEntity.getId()));
			List<McMajorEntity> items = mcMajorService.findList(flist, Sequencer.asc("sort"));
			for (McMajorEntity item : items) {
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
	 * 方法:将新添加的McMajorEntity（本身实体 ）保存到数据库中 传入参数:McMajorEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McMajorEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcMajor(HttpServletRequest request, HttpServletResponse response, Model model,
			McMajorEntity mcMajorEntity) {
		/*
		 * MajorEntity memberEntity = memberService.getCurrent();
		 * if(memberEntity != null){
		 * mcMajorEntity.setCreateuser(memberEntity.getId().toString()); }
		 * if(mcMajorEntity.getParentid().isEmpty()){
		 * mcMajorEntity.setParentid("1"); }
		 */
		mcMajorEntity.setIds(random.createData(16));
		mcMajorService.save(mcMajorEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("mcmajor_ids", mcMajorEntity.getIds());
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McMajorEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组） 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcMajor.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcMajor(HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] idStrings = request.getParameterValues("id");
		Long[] ids = new Long[idStrings.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(idStrings[i]);
		}

		if (ids != null && ids.length > 0) {
			for (Long id : ids) {
				mcMajorService.delete(id);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);

		}
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcMajor(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String major_type = request.getParameter("major_type");
		McMajorEntity mcMajorEntity = new McMajorEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcMajorEntity = this.mcMajorService.find(lid);
			model.addAttribute("item", mcMajorEntity);
		}
		if (major_type != null && major_type.equals("majorshow")) {
			return TEM_PATH + "/mcMajor_enterprise";
		} else {
			return TEM_PATH + "/mcMajor_edit";
		}

	}

	/**
	 * 
	 * 方法:保存更新之后的McMajorEntity（本身实体 ） 传入参数:McMajorEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台McMajorEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcMajor(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String mc_industryclass = ReqUtil.getString(request, "mc_industryclass", "");
		// String mc_company = ReqUtil.getString(request, "mc_company", "");
		McMajorEntity OldMcMajorEntity = mcMajorService.find(id);
		// OldMcMajorEntity.setParentid(parentid);
		OldMcMajorEntity.setSort(sort);
		// OldMcMajorEntity.setMc_company(mc_company);
		OldMcMajorEntity.setName(name);
		OldMcMajorEntity.setRemark(remark);
		OldMcMajorEntity.setMc_industryclass(mc_industryclass);
		mcMajorService.update(OldMcMajorEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_major_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcMajorGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McMajorEntity memberGradeEntity) {
		RespUtil.renderJson(response, JSONUtils
				.toJSONString(mcMajorService.getMcMajorGradeNames(reqeust, response, model, memberGradeEntity)));
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_major_names_two.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcMajorGradeNamesTwo(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McMajorEntity memberGradeEntity) {
		List<McMajorEntity> memberGradeEntities = mcMajorService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (McMajorEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_model_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McMajorEntity memberGradeEntity) {
		List<McMajorEntity> memberGradeEntities = mcMajorService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (McMajorEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map getNodeData2(String rootId) {
		return mcMajorService.getNodeData2(rootId);
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map queryall(String id) {
		return mcMajorService.queryall(id);
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_mcmember_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McModelEntity memberGradeEntity) {
		RespUtil.renderJson(response, JSONUtils
				.toJSONString(mcMajorService.getMcModelGradeNames(reqeust, response, model, memberGradeEntity)));
	}

}
