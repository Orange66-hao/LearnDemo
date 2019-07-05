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
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.McIndustryClassService;
import net.boocu.project.service.McMajorService;
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

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;


/**
 * 


 * @author fang
 *
 * 2015年8月27日
 */
@Controller("mcIndustryClassController")
@RequestMapping("/admin/mcIndustyClass")
public class McIndustryClassController {
	
	private static final String TEM_PATH ="/template/admin/basedata/mcIndustryClass";
	
	@Resource
	private McIndustryClassService mcIndustryClassService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private McMajorService mcMajorService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@RequestMapping(value="tomcIndustryClassList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/mcIndustryClasslist";
	}


	//跳转到实体添加页面
    @RequestMapping(value = "/add_mcIndustryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_McIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/mcIndustryClass_add";
    	
    } 
    
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
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
		Page<McIndustryClassEntity> page = mcIndustryClassService.findPage(pageable);
		List<McIndustryClassEntity> resultList = page.getCont();
		
		List<Map> resultData = new ArrayList<Map>();
		for (McIndustryClassEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
    		data.put("name", item.getName());
    		data.put("remark", item.getRemark());
    		data.put("sort", item.getSort());
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
    	McIndustryClassEntity topNode =  mcIndustryClassService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(McIndustryClassEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	//flist.add(Filter.eq("parentid", topNode.getId()));
    	List<McIndustryClassEntity> items = mcIndustryClassService.findList(flist, Sequencer.asc("sort"));
		for(McIndustryClassEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			/*List<McIndustryClassEntity> children = mcIndustryClassService.findList(Filter.eq("parentid", item.getId()));
			if(children.size() != 0){
				map.put("children", getNodeData(item));
			}*/
			resultList.add(map);
		}
    	return resultList;
    }
    
    //获取异步下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotree.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model){
    	Long id = ReqUtil.getLong(request, "id", 1l);
    	McIndustryClassEntity mcIndustryClassEntity = mcIndustryClassService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(mcIndustryClassEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", mcIndustryClassEntity.getId()));
        	List<McIndustryClassEntity> items = mcIndustryClassService.findList(flist, Sequencer.asc("sort"));
    		for(McIndustryClassEntity item : items){
    			Map<String,Object> map = new HashMap<String, Object>();
    			map.put("id", item.getId());
    			map.put("text", item.getName()+"/"+item.getNameEn());
    			map.put("state", "0".equals(item.getLeaf())?"closed":"open");
    			resultList.add(map);
    		}
    	}
    	return resultList;
    }
    
    /**
     * 
     * 方法:将新添加的McIndustryClassEntity（本身实体 ）保存到数据库中
     * 传入参数:McIndustryClassEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台McIndustryClassEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_mcIndustryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveMcIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,McIndustryClassEntity mcIndustryClassEntity) {
    	MemberEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		mcIndustryClassEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	/*if(mcIndustryClassEntity.getParentid().isEmpty()){
    		mcIndustryClassEntity.setParentid("1");
    	}*/
    	mcIndustryClassService.save(mcIndustryClassEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中McIndustryClassEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_mcIndustryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteMcIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,Long id) {
     	String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id5 : ids){
    			mcIndustryClassService.delete(id5);
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    }}
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_mcIndustryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editMcIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	McIndustryClassEntity mcIndustryClassEntity = new McIndustryClassEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		mcIndustryClassEntity = this.mcIndustryClassService.find(lid);
    		model.addAttribute("item", mcIndustryClassEntity);
    	}
    	return TEM_PATH+"/mcIndustryClass_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的McIndustryClassEntity（本身实体 ）
     * 传入参数:McIndustryClassEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台McIndustryClassEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_mcIndustryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditMcIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,
    		@RequestParam long id,
    		@RequestParam String name,
    		@RequestParam String remark,
    		@RequestParam int sort) {
    	McIndustryClassEntity OldMcIndustryClassEntity = mcIndustryClassService.find(id);
    	OldMcIndustryClassEntity.setRemark(remark);
    	OldMcIndustryClassEntity.setSort(sort);
    	OldMcIndustryClassEntity.setName(name);
    	mcIndustryClassService.update(OldMcIndustryClassEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
  //查询用户名是否存在
    @ResponseBody
    @RequestMapping(value="/register.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Message register(String edit_name,String name,HttpServletRequest request,HttpSession session){
    	 if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
    		 return Message.success("true");
		 }else{
			 List<Map<String, Object>> row=JdbcTemplate.queryForList("select * from mc_industry_class where name='"+name+"'");
    		 if (row.size() > 0) {
    			 return Message.success("false");
    		 }
    		 return Message.success("true");
		}
    	}
    
  //根据常用联系人的id组，查询对应name（名称）
		public Map getNodeData2(String rootId) {
			List<Map<String, Object>> topNode =JdbcTemplate.queryForList(" select ic.id id,ic.name name from mc_industry_class ic where ic.id in ("+rootId+")");
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
    
    
}
