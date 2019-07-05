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
import net.boocu.project.entity.NewsareaEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.service.NewsareaService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;


/**
 * 新闻区域管理
 * @author deng
 *
 * 2015年8月11日
 */
@Controller("newsareaController")
@RequestMapping("/admin/index/newsarea")
public class NewsareaController {
	
	private static final String TEM_PATH ="/template/admin/index/newsarea";
	
	@Resource
	private NewsareaService newsareaService;
	
	@RequestMapping(value="toNewsareaList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/newsarealist";
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
			flist.add(Filter.like("name", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<NewsareaEntity> page = newsareaService.findPage(pageable);
		List<NewsareaEntity> newsareaEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(NewsareaEntity item : newsareaEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("sort", item.getSort());
			map.put("status", item.getStatus()==1?"启用":"停用");
			map.put("creatuser", item.getCreatuser());
			map.put("updateuser", item.getUpdateuser());
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_newsarea.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Newsarea(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/newsarea_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_newsarea.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	int status = ReqUtil.getInt(request, "status", 1);
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	
    	NewsareaEntity newsareaEntity = new NewsareaEntity();
    	
    	newsareaEntity.setName(name);
    	newsareaEntity.setSort(sort);
    	newsareaEntity.setStatus(status);
    	newsareaEntity.setCreatuser(creatuser);
    	newsareaEntity.setUpdateuser(updateuser);
    	newsareaService.save(newsareaEntity);
    	
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
    @RequestMapping(value = "/delete_newsarea.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.newsareaService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_newsarea.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	NewsareaEntity newsareaEntity = new NewsareaEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		newsareaEntity = this.newsareaService.find(lid);
    		model.addAttribute("item", newsareaEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/newsarea_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_newsarea.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditNewsarea(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String name= ReqUtil.getString(request, "name", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	int status = ReqUtil.getInt(request, "status", 1);
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	
    	NewsareaEntity newsareaEntityOle = newsareaService.find(id);
    	if(newsareaEntityOle != null)
    	newsareaEntityOle.setName(name);
    	newsareaEntityOle.setSort(sort);
    	newsareaEntityOle.setStatus(status);
    	newsareaEntityOle.setCreatuser(creatuser);
    	newsareaEntityOle.setUpdateuser(updateuser);
    	newsareaService.update(newsareaEntityOle);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    /**取得新闻区域的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_newsarea_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getBrandNames(HttpServletRequest request, HttpServletResponse response, Model model,NewsareaEntity newsareaEntity) {
    	List<NewsareaEntity> newsareaEntities = newsareaService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(NewsareaEntity item : newsareaEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList) );
    
    }
}
