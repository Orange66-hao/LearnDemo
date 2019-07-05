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
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McBrandService;
import net.boocu.project.service.McMajorService;
import net.boocu.project.service.McProductClassService;
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
@Controller("mcBrandController")
@RequestMapping("/admin/mcBrand")
public class McBrandController {

	private static final String TEM_PATH = "/template/admin/mcBrand";

	@Resource
	private McBrandService mcBrandService;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;

	@Resource
	private McProductClassService mcproductclassService;

	@Resource
	private McMajorService mcMajorService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@RequestMapping(value = "toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/mcBrandlist";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_mcBrand.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		String model_yincang = request.getParameter("model_yincang");
		request.setAttribute("model_yincang", model_yincang);
		return TEM_PATH + "/mcBrand_add";
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
		Page<McBrandEntity> page = mcBrandService.findPage(pageable);
		List<McBrandEntity> resultList = page.getCont();

		List<Map> resultData = new ArrayList<Map>();
		for (McBrandEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
			data.put("ids", item.getId());
			data.put("name", item.getName());
			data.put("mc_company", queryall(item.getMc_productclass()));
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
		McBrandEntity topNode = mcBrandService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McBrandEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McBrandEntity> items = mcBrandService.findList(flist, Sequencer.asc("sort"));
		for (McBrandEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McBrandEntity> children = mcBrandService.findList(Filter.eq("parentid", item.getId()));
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
		McBrandEntity mcBrandEntity = mcBrandService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcBrandEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcBrandEntity.getId()));
			List<McBrandEntity> items = mcBrandService.findList(flist, Sequencer.asc("sort"));
			for (McBrandEntity item : items) {
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
	 * 方法:将新添加的McBrandEntity（本身实体 ）保存到数据库中 传入参数:McBrandEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McBrandEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcBrand(HttpServletRequest request, HttpServletResponse response, Model model,
			McBrandEntity mcBrandEntity) {
		/*
		 * BrandEntity memberEntity = memberService.getCurrent();
		 * if(memberEntity != null){
		 * mcBrandEntity.setCreateuser(memberEntity.getId().toString()); }
		 * if(mcBrandEntity.getParentid().isEmpty()){
		 * mcBrandEntity.setParentid("1"); }
		 */
		mcBrandEntity.setIds(random.createData(16));
		mcBrandService.save(mcBrandEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("mcbrand_ids", mcBrandEntity.getIds());
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McBrandEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组） 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcBrand.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		String[] idStrings = request.getParameterValues("id");
		Long[] ids = new Long[idStrings.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(idStrings[i]);
		}

		if (ids != null && ids.length > 0) {
			for (Long id : ids) {
				mcBrandService.delete(id);
			}
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);

		}
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String brand_type = request.getParameter("brand_type");
		McBrandEntity mcBrandEntity = new McBrandEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcBrandEntity = this.mcBrandService.find(lid);
			McProductClassEntity proen = this.mcproductclassService
					.find(Long.parseLong(mcBrandEntity.getMc_productclass()));
			McMajorEntity itemMajor = null;
			if (proen != null && !proen.isEmpty()) {
				itemMajor = mcMajorService.find(Long.parseLong(proen.getMc_major()));
			}

			model.addAttribute("itemmajor", itemMajor);
			model.addAttribute("item", mcBrandEntity);
		}

		if (brand_type != null && brand_type.equals("brandshow")) {
			return TEM_PATH + "/mcBrand_enterprise";
		} else {
			return TEM_PATH + "/mcBrand_edit";
		}

	}

	/**
	 * 
	 * 方法:保存更新之后的McBrandEntity（本身实体 ） 传入参数:McBrandEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台McBrandEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		// String mc_company = ReqUtil.getString(request, "mc_company", "");
		McBrandEntity OldMcBrandEntity = mcBrandService.find(id);
		// OldMcBrandEntity.setParentid(parentid);
		OldMcBrandEntity.setSort(sort);
		// OldMcBrandEntity.setMc_company(mc_company);
		OldMcBrandEntity.setName(name);
		OldMcBrandEntity.setRemark(remark);
		OldMcBrandEntity.setMc_productclass(mc_productclass);
		mcBrandService.update(OldMcBrandEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_mcmember_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcBrandGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McBrandEntity memberGradeEntity) {
		RespUtil.renderJson(response, JSONUtils
				.toJSONString(mcBrandService.getMcBrandGradeNames(reqeust, response, model, memberGradeEntity)));
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_model_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			McBrandEntity memberGradeEntity) {
		List<McBrandEntity> memberGradeEntities = mcBrandService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (McBrandEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map getNodeData2(String rootId) {
		return mcBrandService.getNodeData2(rootId);
	}

	// 根据常用联系人的id组，查询对应name（名称）
	public Map queryall(String id) {
		return mcBrandService.queryall(id);
	}

}
