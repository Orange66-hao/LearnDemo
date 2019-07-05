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
import net.boocu.project.entity.AdvertIndexEntity;
import net.boocu.project.service.AdvertIndexService;
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
 * 首页广告
 * @author dengwei
 *
 * 2015年8月10日
 */
@Controller("advertController")
@RequestMapping("/admin/index/advert")
public class AdvertController {
	
	private static final String TEM_PATH ="/template/admin/index/advert";
	
	@Resource
	private AdvertIndexService advertIndexService;
	
	@RequestMapping(value="toAdvertList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/advertlist";
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
		Page<AdvertIndexEntity> page = advertIndexService.findPage(pageable);
		List<AdvertIndexEntity> advertIndexEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(AdvertIndexEntity item : advertIndexEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("image", item.getImage());
			map.put("sort", item.getSort());
			map.put("link", item.getLink());
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
    @RequestMapping(value = "/add_advert.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_AdvertIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/advert_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_advert.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	String image= ReqUtil.getString(request, "image", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String link = ReqUtil.getString(request, "link", "");
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	
    	AdvertIndexEntity advertIndexEntity = new AdvertIndexEntity();
    	
    	advertIndexEntity.setName(name);
    	advertIndexEntity.setImage(image);
    	advertIndexEntity.setSort(sort);
    	advertIndexEntity.setLink(link);
    	advertIndexEntity.setCreatuser(creatuser);
    	advertIndexEntity.setUpdateuser(updateuser);
    	advertIndexService.save(advertIndexEntity);
    	
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
    @RequestMapping(value = "/delete_advert.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.advertIndexService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_advert.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	AdvertIndexEntity advertEntity = new AdvertIndexEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		advertEntity = this.advertIndexService.find(lid);
    		model.addAttribute("item", advertEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/advert_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_advert.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditAdvertIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String name= ReqUtil.getString(request, "name", "");
    	String image= ReqUtil.getString(request, "image", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String link = ReqUtil.getString(request, "link", "");
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	
    	AdvertIndexEntity advertIndexEntityOle = advertIndexService.find(id);
    	if(advertIndexEntityOle != null)
    	advertIndexEntityOle.setName(name);
    	advertIndexEntityOle.setImage(image);
    	advertIndexEntityOle.setSort(sort);
    	advertIndexEntityOle.setLink(link);
    	advertIndexEntityOle.setCreatuser(creatuser);
    	advertIndexEntityOle.setUpdateuser(updateuser);
    	advertIndexService.update(advertIndexEntityOle);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
