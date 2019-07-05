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
import net.boocu.project.entity.RecruitEntity;
import net.boocu.project.service.RecruitService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 新闻管理
 * @author deng
 *
 * 2015年8月11日
 */
@Controller("recruitController")
@RequestMapping("/admin/index/recruit")
public class RecruitController {
	
	private static final String TEM_PATH ="/template/admin/index/recruit";
	
	@Resource
	private RecruitService recruitService;
	
	@RequestMapping(value="toRecruitList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/recruitlist";
	}
	
	@RequestMapping(value="data.json",method={RequestMethod.POST,RequestMethod.GET})
	public void dataJson(HttpServletRequest request,HttpServletResponse response,Model model){
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
			flist.add(Filter.like("title", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<RecruitEntity> page = recruitService.findPage(pageable);
		List<RecruitEntity> recruitEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		for(RecruitEntity item : recruitEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("title", item.getTitle());
			map.put("sort", item.getSort());
			map.put("content", item.getContent());
			map.put("creatuser", item.getCreatuser());
			map.put("updateuser", item.getUpdateuser());
			map.put("status", item.getStatus()==1?"启用":"停用");
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_recruit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Recruit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/recruit_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_recruit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveRecruit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String title= ReqUtil.getString(request, "title", "");
    	String content= ReqUtil.getString(request, "content", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	int status = ReqUtil.getInt(request, "status", 0);
    	
    	RecruitEntity recruitEntity = new RecruitEntity();
    	 
    	
    	
    	recruitEntity.setTitle(title);
    	recruitEntity.setContent(content);
    	recruitEntity.setSort(sort);
    	recruitEntity.setCreatuser(creatuser);
    	recruitEntity.setStatus(status);
    	recruitService.save(recruitEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductSaleEntity（广告本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_recruit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteRecruit(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.recruitService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_recruit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editRecruit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	RecruitEntity recruitEntity = new RecruitEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		recruitEntity = this.recruitService.find(lid);
    		model.addAttribute("item", recruitEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/recruit_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_recruit.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditRecruit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String title= ReqUtil.getString(request, "title", "");
    	String content= ReqUtil.getString(request, "content", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	int status = ReqUtil.getInt(request, "status", 0);
    	
    	RecruitEntity recruitEntityOle = recruitService.find(id);
    	recruitEntityOle.setTitle(title);
    	recruitEntityOle.setContent(content);
    	recruitEntityOle.setSort(sort);
    	recruitEntityOle.setUpdateuser(updateuser);
    	recruitEntityOle.setStatus(status);
    	recruitService.update(recruitEntityOle);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
