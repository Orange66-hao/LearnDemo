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
import net.boocu.project.entity.SuCompanyNameEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McCompanyNameService;
import net.boocu.project.service.SuCompanyNameService;
import net.boocu.project.service.ProductService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
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


 * @author fang
 *
 * 2015年8月27日
 */
@Controller("suCompanyNameController")
@RequestMapping("/admin/suCompanyName")
public class SuCompanyNameController {
	
	private static final String TEM_PATH ="/template/admin/suCompanyName";
	
	@Resource
	private SuCompanyNameService suCompanyNameService;

	@Resource
	private McCompanyNameService mcCompanyNameService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/suCompanyNamelist";
	}


	//跳转到实体添加页面
    @RequestMapping(value = "/add_suCompanyName.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_SuCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/suCompanyName_add";
    	
    } 
    
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "create_date", "");
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
		Page<SuCompanyNameEntity> page = suCompanyNameService.findPage(pageable);
		List<SuCompanyNameEntity> resultList = page.getCont();
		
		List<Map> resultData = new ArrayList<Map>();
		for (SuCompanyNameEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
    		data.put("ids", item.getId());
    		data.put("name", item.getName());
    		data.put("su_company", queryall(item.getSu_company()));
    		data.put("address", item.getAddress());
    		data.put("create_date", item.getCreateDate());
    		data.put("sort", item.getSort());
    		data.put("phone", item.getPhone());
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
    	SuCompanyNameEntity topNode =  suCompanyNameService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(SuCompanyNameEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<SuCompanyNameEntity> items = suCompanyNameService.findList(flist, Sequencer.asc("sort"));
		for(SuCompanyNameEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<SuCompanyNameEntity> children = suCompanyNameService.findList(Filter.eq("parentid", item.getId()));
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
    	SuCompanyNameEntity suCompanyNameEntity = suCompanyNameService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(suCompanyNameEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", suCompanyNameEntity.getId()));
        	List<SuCompanyNameEntity> items = suCompanyNameService.findList(flist, Sequencer.asc("sort"));
    		for(SuCompanyNameEntity item : items){
    			Map<String,Object> map = new HashMap<String, Object>();
    			map.put("id", item.getId());
    			map.put("text", item.getName());
    			//map.put("state", "0".equals(item.getLeaf())?"closed":"open");
    			resultList.add(map);
    		}
    	}
    	return resultList;
    }
    
    /**
     * 
     * 方法:将新添加的SuCompanyNameEntity（本身实体 ）保存到数据库中
     * 传入参数:SuCompanyNameEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台SuCompanyNameEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveSuCompanyName(HttpServletRequest request, HttpServletResponse response, Model model,SuCompanyNameEntity suCompanyNameEntity) {
    	/*CompanyNameEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		suCompanyNameEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	if(suCompanyNameEntity.getParentid().isEmpty()){
    		suCompanyNameEntity.setParentid("1");
    	}*/
    	suCompanyNameService.save(suCompanyNameEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中SuCompanyNameEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_suCompanyName.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteSuCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id : ids){
    			suCompanyNameService.delete(id);
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    	
    	
    }}
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editSuCompanyName(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
		String company_type = request.getParameter("company_type");
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
			SuCompanyNameEntity suCompanyNameEntity = this.suCompanyNameService.find(lid);
    		model.addAttribute("item", suCompanyNameEntity);
    	}
		if (company_type != null && company_type.equals("companyshow")) {
			return TEM_PATH + "/suCompanyName_enterprise";
		} else {
			return TEM_PATH + "/suCompanyName_edit";
		}
    } 
     
    /**
     * 
     * 方法:保存更新之后的SuCompanyNameEntity（本身实体 ）
     * 传入参数:SuCompanyNameEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台SuCompanyNameEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/doEdit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditSuCompanyName(HttpServletRequest request, HttpServletResponse response, Model model
    		) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String address = ReqUtil.getString(request, "address", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String sort = ReqUtil.getString(request, "sort", "");
		//String su_company = ReqUtil.getString(request, "su_company", "");
    	SuCompanyNameEntity OldSuCompanyNameEntity = suCompanyNameService.find(id);
    	//OldSuCompanyNameEntity.setParentid(parentid);
    	OldSuCompanyNameEntity.setAddress(address);
    	OldSuCompanyNameEntity.setPhone(phone);
    	//OldSuCompanyNameEntity.setSu_company(su_company);
    	OldSuCompanyNameEntity.setName(name);
    	OldSuCompanyNameEntity.setRemark(remark);
    	OldSuCompanyNameEntity.setSort(Integer.parseInt(sort));
    	suCompanyNameService.update(OldSuCompanyNameEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_sumember_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getSuCompanyNameGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, SuCompanyNameEntity memberGradeEntity) {
    	List<SuCompanyNameEntity> memberGradeEntities = suCompanyNameService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(SuCompanyNameEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
    
    	//根据常用联系人的id组，查询对应name（名称）
  		public Map getNodeData2(String nameid) {
  			List<Map<String, Object>> topNode =suCompanyNameService.getNodeData2(nameid);
	  			if (topNode == null) {
	  				return null;
	  			}
  			String  id[] = new String[topNode.size()];
  			String  name[] = new String[topNode.size()];
  			int i=0;
  			for (Map<String, Object> map : topNode) {
  					id[i]=(map.get("id")).toString();
  					name[i]=(String) map.get("name");
  					i++;
  			}
  			Map<String, Object> map = new HashMap<String, Object>();
  				map.put("id", id);
  				map.put("name", name);
  			return map;
  		}
  		
  		//根据常用联系人的id组，查询对应name（名称）
  		public Map queryall(String id) {
  			List<Map<String, Object>> topNode =suCompanyNameService.queryall(id);
  			if (topNode == null) {
  				return null;
  			}
  			String  ids[] = new String[topNode.size()];
  			String  name[] = new String[topNode.size()];
  			int i=0;
  			for (Map<String, Object> map : topNode) {
  				ids[i]=(map.get("ids")).toString();
  				name[i]=(String) map.get("name");
  				i++;
  			}
  			Map<String, Object> map = new HashMap<String, Object>();
  			map.put("ids", ids);
  			map.put("name", name);
  			return map;
  		}
    
  	//查询公司名是否存在
  	    @ResponseBody
  	    @RequestMapping(value="/register.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
  	    public Message register(String edit_name,String name,HttpServletRequest request,HttpSession session){
  	    	 if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
  	    		 return Message.success("true");
  			 }else{
  				 List<Map<String, Object>> row=suCompanyNameService.register(name);
				 List<Map<String, Object>> registermccompanyname = mcCompanyNameService.registermccompanyname(name);
				 if (row.size() > 0 || registermccompanyname.size() > 0) {
  	    			 return Message.success("false");
  	    		 }
  	    		 return Message.success("true");
  			}
  	    	}
    
    
}
