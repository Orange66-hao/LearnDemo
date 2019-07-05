package net.boocu.web.controller.admin.productmng;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 商品快捷发布求购
 * @author fang
 *
 * 2015年8月17日
 */
@Controller("productWantRentFastController")
@RequestMapping("/admin/prodoctMng/fastPublish/wantRent")
public class ProductWantRentFastController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/fastPublish/wantRent";
	
	@Resource
	private ProductWantRentService productWantRentService;
	
	@Resource
	private ProductBrandService productBrandService;
	
	@Resource
	private ProducttypeService producttypeService;
	
	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private IndustryClassService industryClassService;
	
	@RequestMapping(value="toProductWantRentList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/wantRentlist";
	}
	
	@RequestMapping(value="data.json",method={RequestMethod.POST,RequestMethod.GET})
	public void dataJson(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		/*if(!sortValue.isEmpty()){
			if(sortValue.lastIndexOf(".")!=-1){
				sortValue = sortValue.substring(sortValue.lastIndexOf(".")+1, sortValue.length());
			}
			System.out.println("排序字段:------"+sortValue);
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}*/
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 3);
		Page<ProductWantRentEntity> page = productWantRentService.findProductWantRentPage(pageable, params);
		List<ProductWantRentEntity> productWantRentEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductWantRentEntity item : productWantRentEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object arg2) {
					return arg2==null || "productWantRentEntity".equals(name) ;
				}
			});
			map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));
			//map.put("item",JsonUtil.getJsonObj(item) );
			resultList.add(map);
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到 实体添加页面
    @RequestMapping(value = "/add_fastPubWantRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/wantRent_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductWantRentEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductWantRentEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductWantRentEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_fastPubWantRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal magdebrugBudget = new BigDecimal(ReqUtil.getLong(request, "magdebrugBudget", 0l));
    	String magdebrugBudgetType = ReqUtil.getString(request, "magdebrugBudgetType", "rmb");
    	String magdebrugBudgetLimit = ReqUtil.getString(request, "magdebrugBudgetLimit", "yuan");
    	Date startRent = ReqUtil.getDate(request, "startRent", null);
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "day");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductWantRentEntity productWantRentEntity = new ProductWantRentEntity();
    	ProductEntity productEntity = new ProductEntity();
    	
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}
    	
    	productEntity.setProName(proName);
    	productEntity.setProductType(producttypeService.find(4l));
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	//快捷发布标识
    	productEntity.setProClass(3);
    	productEntity.setProNo(proNo);
    	productWantRentEntity.setRentperiod(rentperiod);
    	productWantRentEntity.setMagdebrugBudget(magdebrugBudget);
    	productWantRentEntity.setMagdebrugBudgetType(CurrencyEnum.valueOf(magdebrugBudgetType));
    	productWantRentEntity.setMagdebrugBudgetLimit(PriceUnitEnum.valueOf(magdebrugBudgetLimit));
    	productWantRentEntity.setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
    	productWantRentEntity.setStartRent(startRent);
    	productEntity.setProdNumber(prodNumber);
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productWantRentService.save(productWantRentEntity,productEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductWantRentEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_fastPubWantRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productWantRentService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_fastPubWantRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProductWantRentEntity productWantRentEntity = productWantRentService.find(id);
    	if(productWantRentEntity != null){
    		model.addAttribute("item", productWantRentEntity);
    	}
    	
    	return TEM_PATH+"/wantRent_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductWantRentEntity（ 本身实体 ）
     * 传入参数:ProductWantRentEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductWantRentEntity（ 本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_fastPubWantRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal magdebrugBudget = new BigDecimal(ReqUtil.getLong(request, "magdebrugBudget", 0l));
    	String magdebrugBudgetType = ReqUtil.getString(request, "magdebrugBudgetType", "rmb");
    	String magdebrugBudgetLimit = ReqUtil.getString(request, "magdebrugBudgetLimit", "yuan");
    	Date startRent = ReqUtil.getDate(request, "startRent", null);
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "day");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
    	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	String cycle = ReqUtil.getString(request, "cycle", "");
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
    	String remain = ReqUtil.getString(request, "remain", "");
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductWantRentEntity productWantRentEntity = productWantRentService.find(id);
    	if(productWantRentEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productWantRentEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
        	if(productclassEntity != null){
        		productWantRentEntity.getProductEntity().setProductclass(productclassEntity);
        	}
        	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
        	productWantRentEntity.getProductEntity().setProName(proName);
        	productWantRentEntity.getProductEntity().setProNo(proNo);
        	productWantRentEntity.setRentperiod(rentperiod);
        	productWantRentEntity.setMagdebrugBudget(magdebrugBudget);
        	productWantRentEntity.setMagdebrugBudgetType(CurrencyEnum.valueOf(magdebrugBudgetType));
        	productWantRentEntity.setMagdebrugBudgetLimit(PriceUnitEnum.valueOf(magdebrugBudgetLimit));
        	productWantRentEntity.setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
        	productWantRentEntity.setStartRent(startRent);
        	productWantRentEntity.getProductEntity().setProdNumber(prodNumber);
        	productWantRentEntity.getProductEntity().setStatus(StatusEnum.valueOf(status));
        	productWantRentEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	productWantRentEntity.getProductEntity().setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
        	productWantRentEntity.getProductEntity().setPoption(poption);
        	productWantRentEntity.getProductEntity().setIsUnit(isUnit);
        	productWantRentEntity.getProductEntity().setCycle(cycle);
        	productWantRentEntity.getProductEntity().setProUnit(proUnit);
        	productWantRentEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productWantRentEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productWantRentEntity.getProductEntity().setRemain(remain);
        	productWantRentService.update(productWantRentEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
