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
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.McBrandService;
import net.boocu.project.service.McProductClassService;
import net.boocu.web.Filter;
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
import com.tencent.wxop.stat.f;

/**
 * 仪器分类管理
 * 
 * @author dengwei
 *
 *         2015年8月12日
 */
@Controller("mcproductclassController")
@RequestMapping("/admin/basedata/mcproductclass")
public class McProductclassController {

	private static final String TEM_PATH = "/template/admin/basedata/mcproductclass";

	@Resource
	private McProductClassService mcproductclassService;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;
	
	@Resource
	private McBrandService mcBrandService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;

	@RequestMapping(value = "toMcProductClassList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/mcproductclasslist";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_mcproductclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_IndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		String model_yincang=request.getParameter("model_yincang");
    	request.setAttribute("model_yincang", model_yincang);
		return TEM_PATH + "/mcproductclass_add";

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
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		/*pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));*/
		Page<McProductClassEntity> page = mcproductclassService.findPage(pageable);
		List<McProductClassEntity> resultList = page.getCont();
		
		List<Map> resultData = new ArrayList<Map>();
		for (McProductClassEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("rowid", item.getId());
			data.put("name", item.getName()/*+"/"+item.getNameEn()*/);
			data.put("mc_major", queryall_pro(item.getMc_major()));
			data.put("sort", item.getSort());
			data.put("remark", item.getRemark());
			resultData.add(data);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultData); 
		RespUtil.renderJson(response, result);
		
	}

	// 获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		McProductClassEntity topNode = mcproductclassService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McProductClassEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getMenuid()));
		List<McProductClassEntity> items = mcproductclassService.findList(flist, Sequencer.asc("sort"));
		for (McProductClassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());

			map.put("text", item.getName()+"/"+item.getNameEn());
			List<McProductClassEntity> children = mcproductclassService.findList(Filter.eq("parentid", item.getMenuid()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}

	// 异步获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotree.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 1l);
		McProductClassEntity mcproductclassEntity = mcproductclassService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcproductclassEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcproductclassEntity.getMenuid()));
			List<McProductClassEntity> items = mcproductclassService.findList(flist, Sequencer.asc("sort"));
			for (McProductClassEntity item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName()+"/"+item.getNameEn());
				map.put("state", "0".equals(item.getLeaf()) ? "closed" : "open");
				resultList.add(map);
			}
		}

		return resultList;
	}
	
	 /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_mcmember_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMcProductClass(HttpServletRequest reqeust, HttpServletResponse response, Model model, McProductClassEntity memberGradeEntity) {
				RespUtil.renderJson(response,JSONUtils.toJSONString(mcproductclassService.getMcProductClass(reqeust, response, model, memberGradeEntity)));
    }
	
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_productclass_names_two.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMcMajorGradeNamesTwo(HttpServletRequest reqeust, HttpServletResponse response, Model model, McProductClassEntity memberGradeEntity) {
    	List<McProductClassEntity> memberGradeEntities = mcproductclassService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(McProductClassEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
	
	
	

	/**
	 * 
	 * 方法:将新添加的McProductClassEntity（本身实体 ）保存到数据库中 传入参数:McProductClassEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McProductClassEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/save_mcproductclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,
			McProductClassEntity mcproductclassEntity) {
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			mcproductclassEntity.setCreateuser(memberEntity.getId().toString());
		}
		/*if (mcproductclassEntity.getParentid().isEmpty()) {
			mcproductclassEntity.setParentid("01");
		}*/
		mcproductclassEntity.setIds(random.createData(16));
		mcproductclassService.save(mcproductclassEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("mcproductclass_ids", mcproductclassEntity.getIds());
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McProductClassEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_mcproductclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model, Long id) {
    	
    	
    	/*String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id : ids){
    			mcproductclassService.delete(id);
    	}*/
    		mcproductclassService.delete(id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}
	/*
	 * if(id != null ){ McProductClassEntity mcproductclassEntity =
	 * mcproductclassService.find(id); List<ProductEntity> productEntity =
	 * productService.findList(Filter.eq("mcproductclass", mcproductclassEntity));
	 * if(productEntity==null && productEntity.size()==0){
	 * List<McProductClassEntity> mcproductclassEntity2 =
	 * mcproductclassService.findList(Filter.eq("parentid",
	 * mcproductclassEntity.getMenuid())); if(mcproductclassEntity2 !=null &&
	 * mcproductclassEntity2.size()!=0){ for(McProductClassEntity item :
	 * mcproductclassEntity2){ McProductClassEntity mcproductclassEntity3 =
	 * mcproductclassService.find(Filter.eq("parentid", item.getMenuid()));
	 * if(mcproductclassEntity3!=null){
	 * mcproductclassService.delete(mcproductclassEntity3); } }
	 * mcproductclassService.deleteList(mcproductclassEntity2); }
	 * mcproductclassService.delete(mcproductclassEntity.getId());
	 * result.put("result", 1); result.put("message", "操作成功"); }else{
	 * result.put("result", 0); result.put("message", "该分类有数据存在"); } }
	 * RespUtil.renderJson(response, result);
	 */

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_mcproductclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String productclass_type=request.getParameter("productclass_type");
		McProductClassEntity mcproductclassEntity = new McProductClassEntity();
		 
		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcproductclassEntity = this.mcproductclassService.find(lid);
			
				List<Filter> flist = new ArrayList<Filter>();
				flist.add(Filter.eq("mc_productclass", lid));
				List<McBrandEntity> itemBrand=mcBrandService.findList(flist);
			
			model.addAttribute("item", mcproductclassEntity);
			model.addAttribute("itembrand", itemBrand);
		}
		
		if (productclass_type != null && productclass_type.equals("productclassshow")) {
			return TEM_PATH+"/mcproductclass_enterprise";
		}else{
			return TEM_PATH+"/mcproductclass_edit";
		}

	}

	/**
	 * 
	 * 方法:保存更新之后的McProductClassEntity（本身实体 ） 传入参数:McProductClassEntity的字段
	 * id（更新实体的id） 传出参数:result（方法结果信息）
	 * 逻辑：查询该id的实体，存在则读取前台McProductClassEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/save_edit_mcproductclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditIndustryClass(HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam long id,
			// @RequestParam String menuid,
			//@RequestParam Long parentid,
			// @RequestParam String leaf,
			@RequestParam String name, /*@RequestParam String nameEn,*/ @RequestParam String remark,@RequestParam String mc_major, @RequestParam int sort) {
		// 上层
		//McProductClassEntity mcproductclassEntity = mcproductclassService.find(parentid);

		McProductClassEntity OldMcProductClassEntity = mcproductclassService.find(id);

		// OldMcProductClassEntity.setMenuid(menuid);
		// OldMcProductClassEntity.setLeaf(leaf);
		//OldMcProductClassEntity.setParentid(mcproductclassEntity.getMenuid());
		OldMcProductClassEntity.setName(name);
		//OldMcProductClassEntity.setNameEn(nameEn);
		OldMcProductClassEntity.setRemark(remark);
		OldMcProductClassEntity.setMc_major(mc_major);
		// OldMcProductClassEntity.setStatus(status);
		OldMcProductClassEntity.setSort(sort);
		mcproductclassService.update(OldMcProductClassEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}
	
		//根据产品id组，查询对应name（名称）
		public Map getNodeData2(String productclass_id) {
			return mcproductclassService.getNodeData2(productclass_id);
		}
	
		//根据常用联系人的id组，查询对应name（名称）
  		public Map queryall_pro(String id) {
  			return mcproductclassService.queryall_pro(id);
  		}
		
		
}
