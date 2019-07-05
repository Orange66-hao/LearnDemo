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
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.service.MemberService;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 自营商品维修
 * 
 * @author deng
 *
 * 2015年8月31日
 */
@Controller("productSelfRepairController")
@RequestMapping("/admin/prodoctMng/selfProMng/repair")
public class ProductRepairSelfController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/selfProMng/repair";
	
	@Resource
	private ProductRepairService productRepairService;
	
	@Resource
	private ProductBrandService productBrandService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProducttypeService producttypeService;
	
	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private IndustryClassService industryClassService;
	
	@RequestMapping(value="toProductRepairList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/repairlist";
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
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "productRepairEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 0);
		Page<ProductRepairEntity> page = productRepairService.findProductRepairPage(pageable, params);
		List<ProductRepairEntity> productRepairEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductRepairEntity item : productRepairEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object arg2) {
					return arg2==null || "productRepairEntity".equals(name) ;
				}
			});
			map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));
			resultList.add(map);
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_selfPubRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/repair_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductRepairEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductRepairEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductRepairEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_selfPubRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String proName = ReqUtil.getString(request, "proName","");
    	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	BigDecimal maintainPeriod = new BigDecimal(ReqUtil.getLong(request, "maintainPeriod", 0l));
    	String maintainPeriodunit = ReqUtil.getString(request, "maintainPeriodunit", "");
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
    	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
    	String fault =  ReqUtil.getString(request, "fault", "");
    	String faultEn = ReqUtil.getString(request, "faultEn", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String status = ReqUtil.getString(request, "status", "");
    	String maintenanceEngineerAddress = ReqUtil.getString(request, "maintenanceEngineerAddress","");
    	BigDecimal repairPrice = new BigDecimal(ReqUtil.getLong(request, "repairPrice", 0l));
    	String repairPriceUnit = ReqUtil.getString(request, "repairPriceUnit", "yuan");
    	String repairPriceType = ReqUtil.getString(request, "repairPriceType", "rmb");
    	String maintaindemo = ReqUtil.getString(request, "maintaindemo", "");
    	String maintaindemoEn = ReqUtil.getString(request, "maintaindemoEn", "");
    	
    	ProductRepairEntity productRepairEntity = new ProductRepairEntity();
    	
    	ProductEntity productEntity = new ProductEntity();
		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}	
    	productEntity.setProName(proName);
    	productEntity.setProNameEn(proNameEn);
    	productEntity.setProNo(proNo);
    	//productEntity.setIndustryclass(industryclass); 
    	//add by fang 20150908
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	productEntity.setPoption(poption);
    	productEntity.setProownaudit(proownaudit);
    	productEntity.setWebzh(webzh);
    	productEntity.setWeben(weben);
    	productEntity.setIsTax(isTax);
    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
    	productEntity.setIsUnit(isUnit);
    	if(isUnit==0){
    		proUnit ="";
    	}
    	productEntity.setProUnit(proUnit);
    	productRepairEntity.setMaintainPeriod(maintainPeriod);
    	productRepairEntity.setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
    	productRepairEntity.setMaintaindemo(maintaindemo);
    	productRepairEntity.setMaintaindemoEn(maintaindemoEn);
    	productEntity.setRepairPeriod(repairPeriod);
    	productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
    	productEntity.setProOriginal1(proOriginal1);
    	productEntity.setProOriginal2(proOriginal2);
    	productEntity.setProOriginal3(proOriginal3);
    	productEntity.setProOriginal4(proOriginal4);
    	productEntity.setProSynopsis(proSynopsis);
    	productEntity.setProSynopsisEn(proSynopsisEn);
    	productRepairEntity.setFault(fault);
    	productRepairEntity.setFaultEn(faultEn);
    	productEntity.setApply(apply);
    	productEntity.setApplyEn(applyEn);
    	productEntity.setPrometaTitle(prometaTitle);
    	productEntity.setProMetaKeywords(proMetaKeywords);
    	productEntity.setPrometaDescription(prometaDescription);
    	productRepairEntity.setMaintenanceEngineerAddress(maintenanceEngineerAddress);
    	productRepairEntity.setRepairPrice(repairPrice);
    	productRepairEntity.setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
    	productRepairEntity.setRepairPriceType(CurrencyEnum.valueOf(repairPriceType) );
    	productEntity.setProductType(producttypeService.find(5l));
    	productRepairService.save(productRepairEntity,productEntity);
    	
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductRepairEntity（广告本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_selfPubRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductRepair(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productRepairService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_selfPubRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProductRepairEntity productRepairEntity = productRepairService.find(id);
    	if(productRepairEntity != null){
    		model.addAttribute("item", productRepairEntity);
    	}
    	
    	return TEM_PATH+"/repair_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductRepairEntity（村本身实体 ）
     * 传入参数:ProductRepairtEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductRepairEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_selfPubRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductRepair(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	//获取到页面的基本信息
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	String cid = ReqUtil.getString(request, "cid", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String proName = ReqUtil.getString(request, "proName","");
    	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	BigDecimal maintainPeriod = new BigDecimal(ReqUtil.getLong(request, "maintainPeriod", 0l));
    	String maintainPeriodunit = ReqUtil.getString(request, "maintainPeriodunit", "");
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
    	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
    	String fault =  ReqUtil.getString(request, "fault", "");
    	String faultEn = ReqUtil.getString(request, "faultEn", "");
    	String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String maintenanceEngineerAddress = ReqUtil.getString(request, "maintenanceEngineerAddress", "");
    	BigDecimal repairPrice = new BigDecimal(ReqUtil.getLong(request, "repairPrice", 0l));
    	String repairPriceUnit = ReqUtil.getString(request, "repairPriceUnit", "yuan");
    	String repairPriceType = ReqUtil.getString(request, "repairPriceType", "rmb");
    	String maintaindemo = ReqUtil.getString(request, "maintaindemo", "");
    	String maintaindemoEn = ReqUtil.getString(request, "maintaindemoEn", "");
    	
    	
    	ProductRepairEntity productRepairEntity = productRepairService.find(id);
    	if(productRepairEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productRepairEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productRepairEntity.getProductEntity().setProName(proName);
        	productRepairEntity.getProductEntity().setProNameEn(proNameEn);
        	productRepairEntity.getProductEntity().setProNo(proNo);
        	productRepairEntity.getProductEntity().setPoption(poption);
	    	productRepairEntity.getProductEntity().setProductType(producttypeService.find(5l));
	    	
        	productRepairEntity.getProductEntity().setProownaudit(proownaudit);
        	productRepairEntity.getProductEntity().setWebzh(webzh);
        	productRepairEntity.getProductEntity().setWeben(weben);
        	productRepairEntity.getProductEntity().setIsTax(isTax);
        	productRepairEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	productRepairEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	productRepairEntity.getProductEntity().setProUnit(proUnit);
        	productRepairEntity.setMaintainPeriod(maintainPeriod);
        	productRepairEntity.setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
        	productRepairEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productRepairEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productRepairEntity.getProductEntity().setProOriginal1(proOriginal1);
        	productRepairEntity.getProductEntity().setProOriginal2(proOriginal2);
        	productRepairEntity.getProductEntity().setProOriginal3(proOriginal3);
        	productRepairEntity.getProductEntity().setProOriginal4(proOriginal4);
        	productRepairEntity.getProductEntity().setProSynopsis(proSynopsis);
        	productRepairEntity.getProductEntity().setProSynopsisEn(proSynopsisEn);
        	productRepairEntity.setFault(fault);
        	productRepairEntity.setFaultEn(faultEn);
        	productRepairEntity.getProductEntity().setApply(apply);
        	productRepairEntity.getProductEntity().setApplyEn(applyEn);
        	productRepairEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	productRepairEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	productRepairEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	productRepairEntity.setMaintenanceEngineerAddress(maintenanceEngineerAddress);
        	productRepairEntity.setRepairPrice(repairPrice);
        	productRepairEntity.setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
        	productRepairEntity.setRepairPriceType(CurrencyEnum.valueOf(repairPriceType) );
        	productRepairEntity.setMaintaindemo(maintaindemo);
        	productRepairEntity.setMaintaindemoEn(maintaindemoEn);
        	productRepairService.save(productRepairEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
