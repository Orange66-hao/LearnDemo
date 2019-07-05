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
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductBuyService;
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
 * 客户商品销售
 * @author fang
 *
 * 2015年8月13日
 */
@Controller("productCusBuyController")
@RequestMapping("/admin/prodoctMng/cusPublish/buy")
public class ProductBuyCusController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/cusProMng/buy";
	
	@Resource
	private ProductBuyService productBuyService;
	
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
	
	@RequestMapping(value="toProductBuyList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/buylist";
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
			sortValue = sortValue.replace("item", "productBuyEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 1);
		Page<ProductBuyEntity> page = productBuyService.findProductBuyPage(pageable, params);
		List<ProductBuyEntity> productBuyEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		System.out.println("testLout----");
		for(ProductBuyEntity item : productBuyEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
				@Override
				public boolean apply(Object source, String name, Object arg2) {
					return arg2==null || "productBuyEntity".equals(name) ;
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
    @RequestMapping(value = "/add_cusPubBuy.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/buy_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductBuyEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductBuyEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductBuyEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_cusPubBuy.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal proMarketPrice = new BigDecimal(ReqUtil.getLong(request, "proMarketPrice", 0l)) ;
    	String proMarketPriceType = ReqUtil.getString(request, "proMarketPriceType", "");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductBuyEntity productBuyEntity = new ProductBuyEntity();
    	ProductEntity productEntity = new ProductEntity();
    	productEntity.setMemberEntity(memberService.find(1l));
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}
    	productEntity.setProName(proName);
    	productEntity.setProductType(producttypeService.find(2l));
    	//productEntity.setIndustryclass(industryclass); 
    	//add by fang 20150908
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	productEntity.setProNo(proNo);
    	productBuyEntity.setProMarketPrice(proMarketPrice);
    	productBuyEntity.setProMarketPriceType( CurrencyEnum.valueOf(proMarketPriceType));
    	productEntity.setProdNumber(prodNumber);
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productEntity.setProClass(1);
    	productBuyService.save(productBuyEntity,productEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductBuyEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_cusPubBuy.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductBuy(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productBuyService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_cusPubBuy.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProductBuyEntity productBuyEntity = productBuyService.find(id);
    	if(productBuyEntity != null){
    		model.addAttribute("item", productBuyEntity);
    	}
    	
    	return TEM_PATH+"/buy_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductBuyEntity（村本身实体 ）
     * 传入参数:ProductBuyEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductBuyEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_cusPubBuy.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductBuy(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal proMarketPrice = new BigDecimal(ReqUtil.getLong(request, "proMarketPrice", 0l)) ;
    	String proMarketPriceType = ReqUtil.getString(request, "proMarketPriceType", "");
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
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String referencepricetype =  ReqUtil.getString(request, "referencepricetype", "");
    	String referencePriceLimit =  ReqUtil.getString(request, "referencePriceLimit", "");
    	String proMarketPriceLimit =  ReqUtil.getString(request, "proMarketPriceLimit", "");
    	
    	ProductBuyEntity productBuyEntity = productBuyService.find(id);
    	if(productBuyEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productBuyEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productBuyEntity.getProductEntity().setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype) );
        	productBuyEntity.getProductEntity().setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
        	productBuyEntity.setProMarketPriceLimit(PriceUnitEnum.valueOf(proMarketPriceLimit));
        	productBuyEntity.getProductEntity().setIsTax(isTax);
        	productBuyEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	productBuyEntity.getProductEntity().setProOriginal1(proOriginal1);
        	productBuyEntity.getProductEntity().setProOriginal2(proOriginal2);
        	productBuyEntity.getProductEntity().setProOriginal3(proOriginal3);
        	productBuyEntity.getProductEntity().setProOriginal4(proOriginal4);
        	productBuyEntity.getProductEntity().setDownData(downData);
        	productBuyEntity.getProductEntity().setProSynopsis(proSynopsis);
        	productBuyEntity.getProductEntity().setProSynopsisEn(proSynopsisEn);
        	productBuyEntity.getProductEntity().setProContent(proContent);
        	productBuyEntity.getProductEntity().setProContentEn(proContentEn);
        	productBuyEntity.getProductEntity().setApply(apply);
        	productBuyEntity.getProductEntity().setApplyEn(applyEn);
        	productBuyEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	productBuyEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	productBuyEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	productBuyEntity.getProductEntity().setWebzh(webzh);
        	productBuyEntity.getProductEntity().setProStock(proStock);
        	productBuyEntity.getProductEntity().setProcostPrice(procostPrice);
        	productBuyEntity.getProductEntity().setReferencePrice(referencePrice);
        	productBuyEntity.getProductEntity().setProNameEn(proNameEn);
        	productBuyEntity.getProductEntity().setProownaudit(proownaudit);
        	productBuyEntity.getProductEntity().setWeben(weben);
        	productBuyEntity.getProductEntity().setProName(proName);
	    	productBuyEntity.getProductEntity().setProductType(producttypeService.find(2l));
	    	//productEntity.setIndustryclass(industryclass); 
	    	//add by fang 20150908
	    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
	    	if(productclassEntity != null){
	    		productBuyEntity.getProductEntity().setProductclass(productclassEntity);
	    	}
	    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
        	productBuyEntity.getProductEntity().setProNo(proNo);
        	productBuyEntity.setProMarketPrice(proMarketPrice);
        	productBuyEntity.setProMarketPriceType(CurrencyEnum.valueOf(proMarketPriceType) );
        	productBuyEntity.getProductEntity().setProdNumber(prodNumber);
        	productBuyEntity.getProductEntity().setStatus(StatusEnum.valueOf(status));
        	productBuyEntity.getProductEntity().setProClass(1);
        	productBuyEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	productBuyEntity.getProductEntity().setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
        	productBuyEntity.getProductEntity().setPoption(poption);
        	productBuyEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	productBuyEntity.getProductEntity().setCycle(cycle);
        	productBuyEntity.getProductEntity().setProUnit(proUnit);
        	productBuyEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productBuyEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productBuyEntity.getProductEntity().setRemain(remain);
        	productBuyService.save(productBuyEntity);
    	} 
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
