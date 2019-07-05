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
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBrandService;
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
 * 客户商品求修
 * 
 * @author deng
 *
 * 2015年8月31日
 */
@Controller("productCusWantRepairController")
@RequestMapping("/admin/prodoctMng/cusProMng/wantRepair")
public class ProductWantRepairCusController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/cusProMng/wantRepair";
	
	@Resource
	private ProducWantRepairService productWantRepairService;
	
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
	
	@RequestMapping(value="toProductWantRepairList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/wantRepairlist";
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
			sortValue = sortValue.replace("item", "productWantRepairEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 1);
		Page<ProducWantRepairEntity> page = productWantRepairService.findProductWantRepairPage(pageable, params);
		List<ProducWantRepairEntity> productRepairEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProducWantRepairEntity item : productRepairEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			if(item.getProductEntity().getMemberEntity() != null){
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object arg2) {
					return arg2==null || "producWanRepairEntity".equals(name) ;
				}
			});
			map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));
			resultList.add(map);}
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_cusPubWantRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/wantRepair_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductRepairEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductRepairEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductRepairEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_cusPubWantRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductWantRepair(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
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
    	String bugloko =  ReqUtil.getString(request, "bugloko", "");
    	String buglokoEn =  ReqUtil.getString(request, "buglokoEn", "");
    	String faultCause =  ReqUtil.getString(request, "faultCause", "");
    	String faultCauseEn = ReqUtil.getString(request, "faultCauseEn", "");
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
    	
    	ProducWantRepairEntity productWantRepairEntity = new ProducWantRepairEntity();
    	
    	ProductEntity productEntity = new ProductEntity();
		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}	
    	productEntity.setProClass(1);
    	productEntity.setProName(proName);
    	productEntity.setProNameEn(proNameEn);
    	productEntity.setProNo(proNo);
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
    	productEntity.setMemberEntity(memberService.find(1l));
    	productEntity.setProUnit(proUnit);
    	productWantRepairEntity.setMaintainPeriod(maintainPeriod);
    	productWantRepairEntity.setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
    	productWantRepairEntity.setMaintenanceEngineerAddress(maintenanceEngineerAddress);
    	productWantRepairEntity.setRepairPrice(repairPrice);
    	productWantRepairEntity.setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
    	productWantRepairEntity.setRepairPriceType(CurrencyEnum.valueOf(repairPriceType) );
    	productEntity.setRepairPeriod(repairPeriod);
    	productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
    	productEntity.setProOriginal1(proOriginal1);
    	productEntity.setProOriginal2(proOriginal2);
    	productEntity.setProOriginal3(proOriginal3);
    	productEntity.setProOriginal4(proOriginal4);
    	productWantRepairEntity.setBugloko(bugloko);
    	productWantRepairEntity.setBuglokoEn(buglokoEn);
    	productWantRepairEntity.setFaultCause(faultCause);
    	productWantRepairEntity.setFaultCauseEn(faultCauseEn);
    	//productEntity.setIndustryclass(industryclass); 
    	//add by fang 20150908
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	productEntity.setApply(apply);
    	productEntity.setApplyEn(applyEn);
    	productEntity.setPrometaTitle(prometaTitle);
    	productEntity.setProMetaKeywords(proMetaKeywords);
    	productEntity.setPrometaDescription(prometaDescription);
  
    	productEntity.setProductType(producttypeService.find(6l));
    	productWantRepairService.save(productWantRepairEntity,productEntity);
    	
    	
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
    @RequestMapping(value = "/delete_cusPubWantRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductWantRepair(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productWantRepairService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_cusPubWantRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductWantRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProducWantRepairEntity productWantRepairEntity = productWantRepairService.find(id);
    	if(productWantRepairEntity != null){
    		model.addAttribute("item", productWantRepairEntity);
    	}
    	
    	return TEM_PATH+"/WantRepair_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductRepairEntity（村本身实体 ）
     * 传入参数:ProductRepairtEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductRepairEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_cusPubWantRepair.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
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
    	String bugloko =  ReqUtil.getString(request, "bugloko", "");
    	String buglokoEn =  ReqUtil.getString(request, "buglokoEn", "");
    	String faultCause =  ReqUtil.getString(request, "faultCause", "");
    	String faultCauseEn = ReqUtil.getString(request, "faultCauseEn", "");
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
    	
    	ProducWantRepairEntity productWantRepairEntity = productWantRepairService.find(id);
    	if(productWantRepairEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productWantRepairEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productWantRepairEntity.getProductEntity().setProName(proName);
        	productWantRepairEntity.getProductEntity().setProNameEn(proNameEn);
        	productWantRepairEntity.getProductEntity().setProNo(proNo);
        	productWantRepairEntity.getProductEntity().setPoption(poption);
        	productWantRepairEntity.getProductEntity().setProownaudit(proownaudit);
        	productWantRepairEntity.getProductEntity().setWebzh(webzh);
        	productWantRepairEntity.getProductEntity().setWeben(weben);
        	productWantRepairEntity.getProductEntity().setIsTax(isTax);
        	productWantRepairEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	productWantRepairEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	productWantRepairEntity.getProductEntity().setProUnit(proUnit);
	    	productWantRepairEntity.getProductEntity().setProductType(producttypeService.find(6l));
        	productWantRepairEntity.setMaintainPeriod(maintainPeriod);
        	productWantRepairEntity.setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
        	productWantRepairEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productWantRepairEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productWantRepairEntity.getProductEntity().setProOriginal1(proOriginal1);
        	productWantRepairEntity.getProductEntity().setProOriginal2(proOriginal2);
        	productWantRepairEntity.getProductEntity().setProOriginal3(proOriginal3);
        	productWantRepairEntity.getProductEntity().setProOriginal4(proOriginal4);
        	productWantRepairEntity.setBugloko(bugloko);
        	productWantRepairEntity.setBuglokoEn(buglokoEn);
        	productWantRepairEntity.setFaultCause(faultCause);
        	productWantRepairEntity.setFaultCauseEn(faultCauseEn);
        	productWantRepairEntity.setMaintenanceEngineerAddress(maintenanceEngineerAddress);
        	productWantRepairEntity.getProductEntity().setApply(apply);
        	productWantRepairEntity.getProductEntity().setApplyEn(applyEn);
        	productWantRepairEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	productWantRepairEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	productWantRepairEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	productWantRepairEntity.setRepairPrice(repairPrice);
        	productWantRepairEntity.setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
        	productWantRepairEntity.setRepairPriceType(CurrencyEnum.valueOf(repairPriceType) );
        	productWantRepairService.save(productWantRepairEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
