package net.boocu.web.controller.admin.audit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.service.ProductBrandService;
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
import com.alibaba.fastjson.JSON;


/**
 * 商品销售
 * @author fang
 *
 * 2015年8月7日
 */
@Controller("auditbrandController")
@RequestMapping("/admin/audit/brand")
public class AuditBrandController {
	
	private static final String TEM_PATH ="/template/admin/audit/brand";
	
	@Resource
	private ProductBrandService productBrandService;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/brandlist";
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
		Page<ProductBrandEntity> page = productBrandService.findPage(pageable);
		List<ProductBrandEntity> productBrandEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(ProductBrandEntity item : productBrandEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("nameEn", item.getNameEn());
			map.put("country", item.getCountry());
			map.put("image", item.getImage());
			map.put("sort", item.getSort());
			map.put("area", item.getArea());
			map.put("isRecommend", item.getIsRecommend()==1?"显示":"不显示");
			map.put("status", item.getStatus()==1?"启用":"停用");
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String addAdvert(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/brand_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的BrandEntity（本身实体 ）保存到数据库中
     * 传入参数:BrandEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台BrandEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	String nameEn= ReqUtil.getString(request, "nameEn", "");
    	String image = ReqUtil.getString(request, "image", "");
    	String country = ReqUtil.getString(request, "country", "");
    	int sort = ReqUtil.getInt(request, "sort", 1);
    	String area = ReqUtil.getString(request, "area", "");
    	int isRecommend = ReqUtil.getInt(request, "isRecommend", 0);
    	int status = ReqUtil.getInt(request, "status", 0);
    	
    	ProductBrandEntity productBrandEntity = new ProductBrandEntity();
    	productBrandEntity.setName(name);
    	productBrandEntity.setNameEn(nameEn);
    	productBrandEntity.setImage(image);
    	productBrandEntity.setCountry(country);
    	productBrandEntity.setSort(sort);
    	productBrandEntity.setArea(area);
    	productBrandEntity.setIsRecommend(isRecommend);
    	productBrandEntity.setStatus(status);
    	
    	productBrandService.save(productBrandEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中BrandEntity（广告本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteBrand(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.productBrandService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	ProductBrandEntity item = new ProductBrandEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		item = this.productBrandService.find(lid);
    		model.addAttribute("item", item);
    		System.out.println(item.getCountry());
    	}
    	
    	return TEM_PATH+"/brand_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的BrandEntity（村本身实体 ）
     * 传入参数:BrandEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台BrandEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditBrand(HttpServletRequest request, HttpServletResponse response, Model model,ProductBrandEntity productBrandEntity) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	ProductBrandEntity productBrandEntityOle = productBrandService.find(id);
    	if(productBrandEntityOle != null){
    		productBrandEntityOle.setName(productBrandEntity.getName());
    		productBrandEntityOle.setNameEn(productBrandEntity.getNameEn());
    		productBrandEntityOle.setImage(productBrandEntity.getImage());
    		productBrandEntityOle.setCountry(productBrandEntity.getCountry());
    		productBrandEntityOle.setSort(productBrandEntity.getSort());
    		productBrandEntityOle.setArea(productBrandEntity.getArea());
    		productBrandEntityOle.setIsRecommend(productBrandEntity.getIsRecommend());
    		productBrandEntityOle.setStatus(productBrandEntity.getStatus());
    		productBrandService.update(productBrandEntityOle);
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
    
    /**取得品牌名的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_brand_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getBrandNames(HttpServletRequest request, HttpServletResponse response, Model model,ProductBrandEntity productBrandEntity) {
    	List<ProductBrandEntity> productBrandEntities = productBrandService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(ProductBrandEntity item : productBrandEntities){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList) );
    
    }
}
