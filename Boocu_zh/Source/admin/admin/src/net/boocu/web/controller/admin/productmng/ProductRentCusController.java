package net.boocu.web.controller.admin.productmng;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Convert;
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
import net.boocu.project.entity.ProductEntity.RateEnum;
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
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 客户商品销售
 * @author fang
 *
 * 2015年8月13日
 */
@Controller("productCusRentController")
@RequestMapping("/admin/prodoctMng/cusPublish/rent")
public class ProductRentCusController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/cusProMng/rent";
	
	@Resource
	private ProductRentService productRentService;
	
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
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "productRentEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 1);
		Page<ProductRentEntity> page = productRentService.findProductRentPage(pageable, params);
		List<ProductRentEntity> productRentEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		System.out.println("testLout----");
		for(ProductRentEntity item : productRentEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			if(item.getProductEntity().getMemberEntity() != null)
			System.out.println("------------用户名-----------------"+item.getProductEntity().getMemberEntity().getUsername());
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
    @RequestMapping(value = "/add_cusPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
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
    @RequestMapping(value = "/save_cusPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "week");
    	BigDecimal rentPrice = new BigDecimal(ReqUtil.getLong(request, "rentPrice", 0l));
    	String rentPriceUnit = ReqUtil.getString(request, "rentPriceUnit", "yuan");
    	String rentPriceType = ReqUtil.getString(request, "rentPriceType", "rmb");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductRentEntity productRentEntity = new ProductRentEntity();
    	ProductEntity productEntity = new ProductEntity();
    	productEntity.setMemberEntity(memberService.find(1l));
    	//System.out.println("用户名:-------------------"+memberService.getCurrent().getUsername());
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
    	productEntity.setProClass(1);
    	productEntity.setProNo(proNo);
    	productRentEntity.setRentperiod(rentperiod);
    	productRentEntity.setRentPriceType(CurrencyEnum.valueOf(rentPriceType) );
    	productRentEntity.setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
    	productRentEntity.setRentPrice(rentPrice);
    	productRentEntity.setRentPriceUnit(PriceUnitEnum.valueOf(rentPriceUnit));
    	productEntity.setProdNumber(prodNumber);
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productEntity.setProClass(1);
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
    @RequestMapping(value = "/delete_cusPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
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
    @RequestMapping(value = "/edit_cusPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
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
    @RequestMapping(value = "/save_edit_cusPubRent.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductRent(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l)) ;
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "week");
    	BigDecimal rentPrice = new BigDecimal(ReqUtil.getLong(request, "rentPrice", 0l));
    	String rentPriceUnit = ReqUtil.getString(request, "rentPriceUnit", "yuan");
    	String rentPriceType = ReqUtil.getString(request, "rentPriceType", "rmb");
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
    	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int proStock =  ReqUtil.getInt(request, "proStock", 0);
    	String procostPrice =  ReqUtil.getString(request, "procostPrice", "");
    	BigDecimal referencePrice =  new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l)) ;
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	String downData =  ReqUtil.getString(request, "downData", "");
    	String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
    	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
    	String proContent =  ReqUtil.getString(request, "proContent", "");
    	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
    	String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String cid =  ReqUtil.getString(request, "cid", "");
    	String referencepricetype =  ReqUtil.getString(request, "referencepricetype", "");
    	String referencePriceLimit =  ReqUtil.getString(request, "referencePriceLimit", "");
    	String proMarketPriceLimit =  ReqUtil.getString(request, "proMarketPriceLimit", "");
    	
    	ProductRentEntity productRentEntity = productRentService.find(id);
    	if(productRentEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productRentEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productRentEntity.getProductEntity().setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype) );
        	productRentEntity.getProductEntity().setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
        	productRentEntity.getProductEntity().setIsTax(isTax);
        	productRentEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	productRentEntity.getProductEntity().setProOriginal1(proOriginal1);
        	productRentEntity.getProductEntity().setProOriginal2(proOriginal2);
        	productRentEntity.getProductEntity().setProOriginal3(proOriginal3);
        	productRentEntity.getProductEntity().setProOriginal4(proOriginal4);
        	productRentEntity.getProductEntity().setDownData(downData);
        	productRentEntity.getProductEntity().setProSynopsis(proSynopsis);
        	productRentEntity.getProductEntity().setProSynopsisEn(proSynopsisEn);
        	productRentEntity.getProductEntity().setProContent(proContent);
        	productRentEntity.getProductEntity().setProContentEn(proContentEn);
        	productRentEntity.getProductEntity().setApply(apply);
        	productRentEntity.getProductEntity().setApplyEn(applyEn);
        	productRentEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	productRentEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	productRentEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	productRentEntity.getProductEntity().setWebzh(webzh);
        	productRentEntity.getProductEntity().setProStock(proStock);
        	productRentEntity.getProductEntity().setProcostPrice(procostPrice);
        	productRentEntity.getProductEntity().setReferencePrice(referencePrice);
        	productRentEntity.getProductEntity().setProNameEn(proNameEn);
        	productRentEntity.getProductEntity().setProownaudit(proownaudit);
        	productRentEntity.getProductEntity().setWeben(weben);
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
        	//productRentEntity.getProductEntity().setProClass(0);
        	productRentEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	productRentEntity.getProductEntity().setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
        	productRentEntity.getProductEntity().setPoption(poption);
        	productRentEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	productRentEntity.getProductEntity().setCycle(cycle);
        	productRentEntity.getProductEntity().setProUnit(proUnit);
        	productRentEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productRentEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productRentEntity.getProductEntity().setRemain(remain);
        	productRentService.save(productRentEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
