package net.boocu.web.controller.admin.productmng;


import java.math.BigDecimal;
import java.util.ArrayList;
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
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductRentService;
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
 * 2015年8月13日
 */
@Controller("productRentFastController")
@RequestMapping("/admin/prodoctMng/fastPublish/rent")
public class ProductRentFastController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/fastPublish/rent";
	
	@Resource
	private ProductRentService productRentService;
	
	@Resource
	private ProductBrandService productBrandService;
	
	@Resource
	private ProducttypeService producttypeService;
	
	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private IndustryClassService industryClassService;
	
	@RequestMapping(value="toProductRentList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/rentlist";
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
		Page<ProductRentEntity> page = productRentService.findProductRentPage(pageable, params);
		List<ProductRentEntity> productRentEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductRentEntity item : productRentEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object arg2) {
					return arg2==null || "productRentEntity".equals(name) ;
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

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_fastPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/rent_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductRentEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductRentEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductRentEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_fastPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	//String proType = ReqUtil.getString(request, "proType", "");
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "week");
    	BigDecimal rentPrice = new BigDecimal(ReqUtil.getLong(request, "rentPrice", 0l));
    	String rentPriceUnit = ReqUtil.getString(request, "rentPriceUnit", "yuan");
    	String rentPriceType = ReqUtil.getString(request, "rentPriceType", "rmb");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductRentEntity productRentEntity = new ProductRentEntity();
    	ProductEntity productEntity = new ProductEntity();
    	
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}
    	
    	productEntity.setProName(proName);
    	productEntity.setProductType(producttypeService.find(3l));
    	//productEntity.setIndustryclass(industryclass); 
    	//add by fang 20150908
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	//快捷发布标识
    	productEntity.setProClass(3);
    	productEntity.setProNo(proNo);
    	productRentEntity.setRentperiod(rentperiod);
    	productRentEntity.setRentPriceType(CurrencyEnum.valueOf(rentPriceType) );
    	productRentEntity.setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
    	productRentEntity.setRentPrice(rentPrice);
    	productRentEntity.setRentPriceUnit(PriceUnitEnum.valueOf(rentPriceUnit));
    	productEntity.setProdNumber(prodNumber);
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productRentService.save(productRentEntity,productEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductRentEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_fastPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductRent(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productRentService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_fastPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProductRentEntity productRentEntity = productRentService.find(id);
    	if(productRentEntity != null){
    		model.addAttribute("item", productRentEntity);
    	}
    	
    	return TEM_PATH+"/rent_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductRentEntity（村本身实体 ）
     * 传入参数:ProductRentEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductRentEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_fastPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "周");
    	BigDecimal rentPrice = new BigDecimal(ReqUtil.getLong(request, "rentPrice", 0l));
    	String rentPriceUnit = ReqUtil.getString(request, "rentPriceUnit", "");
    	String rentPriceType = ReqUtil.getString(request, "rentPriceType", "");
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
    	
    	ProductRentEntity productRentEntity = productRentService.find(id);
    	if(productRentEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productRentEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productRentEntity.getProductEntity().setProName(proName);
        	productRentEntity.getProductEntity().setProductType(producttypeService.find(3l));
        	productRentEntity.getProductEntity().setProNo(proNo);
        	productRentEntity.setRentperiod(rentperiod);
        	productRentEntity.setRentPriceType(CurrencyEnum.valueOf(rentPriceType) );
        	productRentEntity.setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
        	productRentEntity.setRentPrice(rentPrice);
        	productRentEntity.setRentPriceUnit(PriceUnitEnum.valueOf(rentPriceUnit));
        	productRentEntity.getProductEntity().setProdNumber(prodNumber);
        	productRentEntity.getProductEntity().setStatus(StatusEnum.valueOf(status));
        	productRentEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	productRentEntity.getProductEntity().setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
        	productRentEntity.getProductEntity().setPoption(poption);
        	productRentEntity.getProductEntity().setIsUnit(isUnit);
        	productRentEntity.getProductEntity().setCycle(cycle);
        	productRentEntity.getProductEntity().setProUnit(proUnit);
        	productRentEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productRentEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productRentEntity.getProductEntity().setRemain(remain);
        	productRentService.update(productRentEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
