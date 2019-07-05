package net.boocu.web.controller.admin;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import nl.siegmann.epublib.epub.Main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 促销信息管理
 * @author dengwei
 *
 * 2015年8月12日
 */
@Controller("salesController")
@RequestMapping("/admin/index/sales")
public class SalesController {
	
	private static final String TEM_PATH ="/template/admin/index/sales";
	
	@Resource
	private ProducttypeService producttypeService;
	@Resource
	private ProductService productService;
	
	@Resource
	private SalesService salesService;
	
	@RequestMapping(value="toSalesList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/saleslist";
	}
	
	//前台促销信息显示
	@RequestMapping(value="display.json",method={RequestMethod.POST,RequestMethod.GET})
	public void display(HttpServletRequest request,HttpServletResponse response,Model model){
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
		pageable.getFilters().add(Filter.eq("display", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("isPromSale", 1));
		HashMap<String, Object> parms = new HashMap<String, Object>();
		parms.put("keyword", keyword);
		Page<ProductEntity> page = salesService.findSalesPage(pageable, parms);
		List<ProductEntity> productEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String proName, Object arg2) {
						return arg2==null || "productEntity".equals(proName) ;
					}
				});
				
				map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));	
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String Date = formatter.format(item.getCreateDate());
			    map.put("createDate", Date);
			    map.put("apprStatus", item.getApprStatus());
			resultList.add(map);
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	@RequestMapping(value="Ndisplay.json",method={RequestMethod.POST,RequestMethod.GET})
	public void Ndisplay(HttpServletRequest request,HttpServletResponse response,Model model){
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
		pageable.getFilters().add(Filter.eq("display", 0));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("isPromSale", 1));
		HashMap<String, Object> parms = new HashMap<String, Object>();
		parms.put("keyword", keyword);
		Page<ProductEntity> page = salesService.findSalesPage(pageable, parms);
		List<ProductEntity> productEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String proName, Object arg2) {
						return arg2==null || "productEntity".equals(proName) || "addressEntity".equals(proName) ;
					}
				});
				
				map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));	
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String Date = formatter.format(item.getCreateDate());
			    map.put("createDate", Date);
			    map.put("apprStatus", item.getApprStatus());
			resultList.add(map);
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}
	
	//同意
	@RequestMapping(value={"/agree"}, method={RequestMethod.POST, RequestMethod.GET})
	public void salesagree(HttpServletRequest request, HttpServletResponse response){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = productService.find(ids[i]);
			if(productEntity !=null){
				productEntity.setDisplay(1);
				productService.update(productEntity);	
			}
		}
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "同意成功");
		RespUtil.renderJson(response, result);
	}
	
	//取消
	@RequestMapping(value={"/cancel"}, method={RequestMethod.POST, RequestMethod.GET})
	public void salescancel(HttpServletRequest request, HttpServletResponse response){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = productService.find(ids[i]);
			if(productEntity !=null){
				productEntity.setDisplay(0);
				productEntity.setIsPromSale(0);
				productService.update(productEntity);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "取消成功");
		RespUtil.renderJson(response, result);
	}
/*
	//跳转到村实体添加页面
    @RequestMapping(value = "/add_sales.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Sales(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/sales_add";
    	
    } 
    
    *//**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     *//*
    @ResponseBody
    @RequestMapping(value = "/save_sales.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String image= ReqUtil.getString(request, "image", "");
    	int sort = ReqUtil.getInt(request, "sort", 1);
    	String link = ReqUtil.getString(request, "link", "");
    	String creatuser = ReqUtil.getString(request, "creatuser", "");
    	String updateuser = ReqUtil.getString(request, "updateuser", "");
    	long productTypeId = ReqUtil.getLong(request, "productTypeId",0l);
    	int isShow = ReqUtil.getInt(request, "isShow", 0);
    	long productId = ReqUtil.getLong(request, "productId", 0l);
    	BigDecimal salesPrice = new BigDecimal(ReqUtil.getLong(request, "salesPrice", 0l));
    	String salesPriceUnit = ReqUtil.getString(request, "salesPriceUnit", "yuan");
    	String salesPriceType = ReqUtil.getString(request, "salesPriceType", "rmb");
    	BigDecimal realPrice = new BigDecimal(ReqUtil.getLong(request, "realPrice", 0l));
    	BigDecimal b =new BigDecimal("10000");
    	SalesEntity salesEntity = new SalesEntity();
    	
    	
    	if(salesPriceUnit.equals("millionyuan")){
    		salesEntity.setRealPrice(salesPrice.multiply(b));
    	}
    	else{
    		salesEntity.setRealPrice(salesPrice);
    	}
    	salesEntity.setImage(image);
    	salesEntity.setSort(sort);
    	salesEntity.setLink(link);
    	salesEntity.setCreatuser(creatuser);
    	salesEntity.setUpdateuser(updateuser);
    	salesEntity.setIsShow(isShow);
    	salesEntity.setSalesPrice(salesPrice);
    	salesEntity.setSalesPriceUnit(PriceUnitEnum.valueOf(salesPriceUnit));
    	salesEntity.setSalesPriceType(CurrencyEnum.valueOf(salesPriceType));
    	ProductEntity productEntity = productService.find(productId);
    	ProducttypeEntity producttypeEntity = producttypeService.find(productTypeId);
    	if(producttypeEntity !=null){
    		productEntity.setProductType(producttypeEntity);
    	}
    	if(productEntity !=null){
    		salesEntity.setProductEntity(productEntity);
    	}
    	salesService.save(salesEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    *//**
     * 
     * 方法:删除选中ProductSaleEntity（广告本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     *//*
    @RequestMapping(value = "/delete_sales.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.salesService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }*/
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_sales.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editSales(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	ProductEntity productEntity = new ProductEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		productEntity = this.productService.find(lid);
    		model.addAttribute("item", productEntity);
    	}
    	return TEM_PATH+"/sales_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_sales.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditSales(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	int sort = ReqUtil.getInt(request, "sort", 1);
    	BigDecimal salesPrice = new BigDecimal(ReqUtil.getLong(request, "salesPrice", 0l));
    	String salesPriceUnit = ReqUtil.getString(request, "salesPriceUnit", "yuan");
    	String salesPriceType = ReqUtil.getString(request, "salesPriceType", "rmb");
    	String proOriginal1 = ReqUtil.getString(request, "proOriginal1", "");
    	BigDecimal b =new BigDecimal("10000");
    	BigDecimal a = new BigDecimal("6.5");
    	
    	ProductEntity productEntityOle = productService.find(id);
    	if(productEntityOle != null){
        	if(salesPriceUnit.equals("millionyuan")){
        		productEntityOle.setSalesRealPrice(salesPrice.multiply(b));
        		if(salesPriceType.equals("dollar")){
        			productEntityOle.setSalesRealPrice(salesPrice.multiply(b).multiply(a));
        		}
        	}else if(salesPriceType.equals("dollar")){
        		productEntityOle.setSalesRealPrice(salesPrice.multiply(a));
        	}else{
        		productEntityOle.setSalesRealPrice(salesPrice);
        	}
    		productEntityOle.setSort(sort);
    		productEntityOle.setSalesPrice(salesPrice);
    		productEntityOle.setSalesPriceUnit(PriceUnitEnum.valueOf(salesPriceUnit));
    		productEntityOle.setSalesPriceType(CurrencyEnum.valueOf(salesPriceType));
	    	productEntityOle.setProOriginal1(proOriginal1);
	    	salesService.update(productEntityOle);
    	}
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
}
