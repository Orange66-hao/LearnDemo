package net.boocu.web.controller.admin;


import com.alibaba.druid.support.json.JSONUtils;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.McBrandAndModelEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.service.McBrandAndModelService;
import net.boocu.project.service.McModelService;
import net.boocu.project.service.ProductService;
import net.boocu.web.*;
import net.boocu.web.service.MemberService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 


 * @author fang
 *
 * 2015年8月27日
 */
@Controller("mcBrandAndModelController")
@RequestMapping("/admin/mcBrandAndModel")
public class McBrandAndModelController {
	
	private static final String TEM_PATH ="/template/admin/mcBrandAndModel";
	
	@Resource
	private McBrandAndModelService mcBrandAndModelService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/mcModellist";
	}


	//跳转到实体添加页面
    @RequestMapping(value = "/add_mcModel.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_McModel(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String model_yincang=request.getParameter("model_yincang");
    	request.setAttribute("model_yincang", model_yincang);
    	return TEM_PATH+"/mcModel_add";
    	
    } 
    
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String  sortOrder = ReqUtil.getString(request, "order", "desc");
		
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
		Page<McBrandAndModelEntity> page = mcBrandAndModelService.findPage(pageable);
		List<McBrandAndModelEntity> resultList = page.getCont();
		
		List<Map> resultData = new ArrayList<Map>();
		for (McBrandAndModelEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
    		data.put("ids", item.getId());
    		data.put("name", item.getMc_model());
    		//data.put("mc_brand", queryall(item.getMc_brand()));
    		data.put("remark", item.getRemark());
			resultData.add(data);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultData); 
		RespUtil.renderJson(response, result);
    }
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	McBrandAndModelEntity topNode =  mcBrandAndModelService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(McBrandAndModelEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<McBrandAndModelEntity> items = mcBrandAndModelService.findList(flist, Sequencer.asc("sort"));
		for(McBrandAndModelEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getMc_model());
			List<McBrandAndModelEntity> children = mcBrandAndModelService.findList(Filter.eq("parentid", item.getId()));
			if(children.size() != 0){
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
    	return resultList;
    }
    
    //获取异步下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotree.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model){
    	Long id = ReqUtil.getLong(request, "id", 1l);
    	McBrandAndModelEntity mcBrandAndModelEntity = mcBrandAndModelService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(mcBrandAndModelEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", mcBrandAndModelEntity.getId()));
        	List<McBrandAndModelEntity> items = mcBrandAndModelService.findList(flist, Sequencer.asc("sort"));
    		for(McBrandAndModelEntity item : items){
    			Map<String,Object> map = new HashMap<String, Object>();
    			map.put("id", item.getId());
    			map.put("text", item.getMc_model());
    			//map.put("state", "0".equals(item.getLeaf())?"closed":"open");
    			resultList.add(map);
    		}
    	}
    	return resultList;
    }
    
    /**
     * 
     * 方法:将新添加的McModelEntity（本身实体 ）保存到数据库中
     * 传入参数:McModelEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台McModelEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveMcModel(HttpServletRequest request, HttpServletResponse response, Model model,McBrandAndModelEntity mcBrandAndModelEntity) {
    	/*ModelEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		mcModelEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	if(mcModelEntity.getParentid().isEmpty()){
    		mcModelEntity.setParentid("1");
    	}*/
    	mcBrandAndModelService.save(mcBrandAndModelEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中McModelEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_mcModel.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteMcModel(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id : ids){
    			mcBrandAndModelService.delete(id);
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    	
    	
    }}
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editMcModel(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String brandAndModel_type = request.getParameter("brandAndModel_type");
		Long lid = Long.parseLong(id);
		McBrandAndModelEntity mcBrandAndModelEntity = this.mcBrandAndModelService.find(lid);
        model.addAttribute("item", mcBrandAndModelEntity);
        model.addAttribute("company_url", mcBrandAndModelEntity.getCompany_type() == 1? "/admin/mcCompanyName/get_mcmember_names.jspx": "/admin/suCompanyName/get_sumember_names.jspx");
		if (brandAndModel_type != null && brandAndModel_type.equals("brandAndModelshow")) {
			return TEM_PATH + "/mcBrandAndModels_enterprise";
		} else {
			return TEM_PATH + "/mcBrandAndModel_edit";
		}
    }
     
    /**
     * 
     * 方法:保存更新之后的McBrandAndModelEntity（本身实体 ）
     * 传入参数:McBrandAndModelEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台McBrandAndModelEntity（存本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/doEdit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditMcModel(HttpServletRequest request, HttpServletResponse response, Model model
    		) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
		String mc_brand = ReqUtil.getString(request, "mc_brand", "");
		String mc_model = ReqUtil.getString(request, "mc_model", "");
		String mc_productclass_number = ReqUtil.getString(request, "mc_productclass_number", "");
		String mc_productclass_name = ReqUtil.getString(request, "mc_productclass_name", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String mc_company = ReqUtil.getString(request, "mc_company", "");

		McBrandAndModelEntity oldMcBrandAndModelEntity = mcBrandAndModelService.find(id);

		oldMcBrandAndModelEntity.setMc_brand(mc_brand);
		oldMcBrandAndModelEntity.setMc_model(mc_model);
		oldMcBrandAndModelEntity.setMc_productclass_number(mc_productclass_number);
		oldMcBrandAndModelEntity.setMc_productclass_name(mc_productclass_name);
		oldMcBrandAndModelEntity.setMc_company(mc_company);
		oldMcBrandAndModelEntity.setRemark(remark);

		mcBrandAndModelService.update(oldMcBrandAndModelEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
    	return result;
    }
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_mcmember_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McBrandAndModelEntity memberGradeEntity) {
    	String mc_model_id = ReqUtil.getString(reqeust, "mc_model_id", "");
    	
    	/*List<Filter> flist = new ArrayList<Filter>(); 
    	
    	if(!mc_product_id.isEmpty()){
    		flist.add(Filter.in("mc_productclass", "in("+mc_product_id+")" ));
    		
    	}
    	List<McModelEntity> memberGradeEntities = mcModelService.findList(flist);
    	List<Map> resultList = new ArrayList<Map>();
    	for(McModelEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));*/
    	if (mc_model_id == "" || mc_model_id.length() == 0) {
    		mc_model_id=null;
		}
    	List<Map<String, Object>> topNode;
    		topNode =JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_brand_and_model ic where ic.mc_brand in ("+mc_model_id+")");
		String  id[] = new String[topNode.size()];
		String  name[] = new String[topNode.size()];
		int i=0;
		List<Map> resultList = new ArrayList<Map>();
		for (Map<String, Object> map2 : topNode) {
			Map<String, Object> map = new HashMap<String, Object>();
				//id[i]=(map2.get("id")).toString();
				map.put("id", (map2.get("id")).toString());
				//name[i]=(String) map2.get("name");
				map.put("text", (String) map2.get("name"));
				resultList.add(map);
				i++;
		}
			/*map.put("id", id);
			map.put("text", name);*/
			RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    	//return resultList;
    }
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_names_two.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMcGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McBrandAndModelEntity memberGradeEntity) {
    	List<McBrandAndModelEntity> memberGradeEntities = mcBrandAndModelService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(McBrandAndModelEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getMc_model());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
  		//根据常用联系人的id组，查询对应name（名称）
  		public Map queryall(String id) {
  			List<Map<String, Object>> topNode =JdbcTemplate.queryForList(" select * from mc_brand where id= "+ id);
  			if (topNode == null) {
  				return null;
  			}
  			String  ids[] = new String[topNode.size()];
  			String  name[] = new String[topNode.size()];
  			int i=0;
  			for (Map<String, Object> map : topNode) {
  				ids[i]=(map.get("id")).toString();
  				name[i]=(String) map.get("name");
  				i++;
  			}
  			Map<String, Object> map = new HashMap<String, Object>();
  			map.put("id", ids);
  			map.put("name", name);
  			return map;
  		}

	// 查询公司该品牌和型号是否存在
	@ResponseBody
	@RequestMapping(value = "/addBrandAndModel.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Message addBrandAndModel(String brand,String model,String mc_productclass_name,String mc_company,HttpServletRequest request, HttpSession session) {
		Message message = mcBrandAndModelService.addBrandAndModel(brand, model, mc_productclass_name, mc_company);
		return message;
	}
	// 根据仪器品牌型号的id查询对应数据
	public Map getNodeData2(String rootId) {
		List<Map<String, Object>> topNode = quaryMcBrandAndModel(rootId);
		if (topNode == null) {
			return null;
		}
		String id[] = new String[topNode.size()];
		String mc_brand[] = new String[topNode.size()];
		String mc_productclass_number[] = new String[topNode.size()];
		String mc_productclass_name[] = new String[topNode.size()];
		String mc_company[] = new String[topNode.size()];
		String mc_model[] = new String[topNode.size()];
		int i = 0;
		for (Map<String, Object> map : topNode) {
			id[i] = (map.get("id")).toString();
			mc_productclass_number[i] = (String) map.get("mc_productclass_number");
			mc_brand[i] = (String) map.get("mc_brand");
			mc_productclass_name[i] = (String) map.get("mc_productclass_name");
			mc_company[i] = (String) map.get("mc_company");
			mc_model[i] = (String) map.get("mc_model");
			i++;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("mc_productclass_number", mc_productclass_number);
		map.put("mc_brand", mc_brand);
		map.put("mc_productclass_name",mc_productclass_name);
		map.put("mc_company",mc_company);
		map.put("mc_model",mc_model);
		return map;
	}
	// 根据常用联系人的id组，查询对应name（名称）
	public List<Map<String, Object>> quaryMcBrandAndModel(String id) {
		return mcBrandAndModelService.quaryMcBrandAndModel(id);
	}
}
