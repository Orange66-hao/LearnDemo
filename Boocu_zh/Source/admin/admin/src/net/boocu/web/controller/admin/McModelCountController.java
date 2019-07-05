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
import net.boocu.project.entity.McModelCountEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McModelCountService;
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
@Controller("mcModelCountController")
@RequestMapping("/admin/mcModelCount")
public class McModelCountController {
	
	private static final String TEM_PATH ="/template/admin/mcModelCount";
	
	@Resource
	private McModelCountService mcModelCountService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/mcModelCountlist";
	}


	//跳转到实体添加页面
    @RequestMapping(value = "/add_mcModelCount.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_McModelCount(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/mcModelCount_add";
    	
    } 
    
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    /*	String keyword = ReqUtil.getString(request, "keyword", "");
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
		pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));*/
    	int trid=JdbcTemplate.queryForInt("SELECT count(*) FROM mc_company  WHERE create_date>date_format(date_sub(now(), INTERVAL 2 day),'%Y-%m-%d 00:00:00')");//三天内数据
    	int tswk=JdbcTemplate.queryForInt("SELECT count(*) FROM mc_company WHERE YEARWEEK(date_format(create_date,'%Y-%m-%d')) = YEARWEEK(now())");//本周内数据
    	int month=JdbcTemplate.queryForInt("select count(*) from mc_company where date_format(create_date,'%Y-%m')=date_format(now(),'%Y-%m')");//本月内数据
		
		List<Map> resultData = new ArrayList<Map>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", "客户管理");
			data.put("trid", trid);
    		data.put("tswk", tswk);
    		data.put("month", month);
			resultData.add(data);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rows",resultData); 
		RespUtil.renderJson(response, result);
    }
    
    
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	McModelCountEntity topNode =  mcModelCountService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(McModelCountEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<McModelCountEntity> items = mcModelCountService.findList(flist, Sequencer.asc("sort"));
		for(McModelCountEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McModelCountEntity> children = mcModelCountService.findList(Filter.eq("parentid", item.getId()));
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
    	McModelCountEntity mcModelCountEntity = mcModelCountService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(mcModelCountEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", mcModelCountEntity.getId()));
        	List<McModelCountEntity> items = mcModelCountService.findList(flist, Sequencer.asc("sort"));
    		for(McModelCountEntity item : items){
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
     * 方法:将新添加的McModelCountEntity（本身实体 ）保存到数据库中
     * 传入参数:McModelCountEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台McModelCountEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveMcModelCount(HttpServletRequest request, HttpServletResponse response, Model model,McModelCountEntity mcModelCountEntity) {
    	/*ModelCountEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		mcModelCountEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	if(mcModelCountEntity.getParentid().isEmpty()){
    		mcModelCountEntity.setParentid("1");
    	}*/
    	mcModelCountService.save(mcModelCountEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中McModelCountEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_mcModelCount.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteMcModelCount(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id : ids){
    			mcModelCountService.delete(id);
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    	
    	
    }}
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editMcModelCount(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	McModelCountEntity mcModelCountEntity = new McModelCountEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		mcModelCountEntity = this.mcModelCountService.find(lid);
    		model.addAttribute("item", mcModelCountEntity);
    	}
    	return TEM_PATH+"/mcModelCount_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的McModelCountEntity（本身实体 ）
     * 传入参数:McModelCountEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台McModelCountEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/doEdit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditMcModelCount(HttpServletRequest request, HttpServletResponse response, Model model
    		) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String address = ReqUtil.getString(request, "address", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String sort = ReqUtil.getString(request, "sort", "");
		//String mc_company = ReqUtil.getString(request, "mc_company", "");
    	McModelCountEntity OldMcModelCountEntity = mcModelCountService.find(id);
    	//OldMcModelCountEntity.setParentid(parentid);
    	OldMcModelCountEntity.setAddress(address);
    	OldMcModelCountEntity.setPhone(phone);
    	//OldMcModelCountEntity.setMc_company(mc_company);
    	OldMcModelCountEntity.setName(name);
    	OldMcModelCountEntity.setRemark(remark);
    	OldMcModelCountEntity.setSort(Integer.parseInt(sort));
    	mcModelCountService.update(OldMcModelCountEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_mcmember_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMcModelCountGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McModelCountEntity memberGradeEntity) {
    	List<McModelCountEntity> memberGradeEntities = mcModelCountService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(McModelCountEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
    
    	//根据常用联系人的id组，查询对应name（名称）
  		public Map getNodeData2(String nameid) {
  			List<Map<String, Object>> topNode =JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_company_name ic where ic.id= ("+nameid+")");
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
  			List<Map<String, Object>> topNode =JdbcTemplate.queryForList(" select * from mc_company where ids= "+ id);
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
  				 List<Map<String, Object>> row=JdbcTemplate.queryForList("select * from mc_company_name where name='"+name+"'");
  	    		 if (row.size() > 0) {
  	    			 return Message.success("false");
  	    		 }
  	    		 return Message.success("true");
  			}
  	    	}
    
    
}
