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
import net.boocu.project.entity.HelpEntity;
import net.boocu.project.service.HelpService;
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
 * 帮助信息管理
 * @author deng
 *
 * 2015年8月12日
 */
@Controller("helpController")
@RequestMapping("/admin/index/help")
public class HelpController {
	
	private static final String TEM_PATH ="/template/admin/index/help";
	
	@Resource
	private HelpService helpService;
	
	@RequestMapping(value="toHelpList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/helplist";
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
			flist = new ArrayList<Filter>();
			flist.add(Filter.like("titleEn", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<HelpEntity> page = helpService.findPage(pageable);
		List<HelpEntity> helpEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(HelpEntity item : helpEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("title", item.getTitle()+"/"+item.getTitleEn());
			map.put("content", item.getContent());
			map.put("contentEn", item.getTitleEn());
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_help.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Help(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/help_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_help.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String title= ReqUtil.getString(request, "title", "");
    	String titleEn= ReqUtil.getString(request, "titleEn", "");
    	String content= ReqUtil.getString(request, "content", "");
    	String contentEn= ReqUtil.getString(request, "contentEn", "");
    	HelpEntity helpEntity = new HelpEntity();
    	
    	helpEntity.setTitle(title);
    	helpEntity.setTitleEn(titleEn);
    	helpEntity.setContent(content);
    	helpEntity.setContentEn(contentEn);
    	helpService.save(helpEntity);
    	
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
    @RequestMapping(value = "/delete_help.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.helpService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_help.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	HelpEntity helpEntity = new HelpEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		helpEntity = this.helpService.find(lid);
    		model.addAttribute("item", helpEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/help_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_help.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditHelp(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String title= ReqUtil.getString(request, "title", "");
    	String titleEn= ReqUtil.getString(request, "titleEn", "");
    	String content= ReqUtil.getString(request, "content", "");
    	String contentEn= ReqUtil.getString(request, "contentEn", "");
    	HelpEntity helpEntityOle = helpService.find(id);
    	if(helpEntityOle != null)
    	helpEntityOle.setTitle(title);
    	helpEntityOle.setTitleEn(titleEn);
    	helpEntityOle.setContent(content);
    	helpEntityOle.setContentEn(contentEn);
    	helpService.update(helpEntityOle);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
