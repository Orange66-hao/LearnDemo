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
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.AreaEntity;














import net.boocu.web.service.MemberService;
import net.sf.json.JSONObject;
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
 * 2015年8月7日
 */
@Controller("productCusSaleController")
@RequestMapping("/admin/prodoctMng/cusPublish/sale")
public class ProductSaleCusController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/cusProMng/sale";
	
	@Resource
	private ProductSaleService productSaleService;
	
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
	
	@RequestMapping(value="toProductSaleList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/salelist";
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
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 1);
		Page<ProductSaleEntity> page = productSaleService.findProductSalePage(pageable, params);
		List<ProductSaleEntity> productSaleEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		System.out.println("testLout----");
		for(ProductSaleEntity item : productSaleEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			if(item.getProductEntity().getMemberEntity() != null){
			//map.put("item",JsonUtil.getJsonObj(item)  );
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String name, Object arg2) {
						return arg2==null || "productSaleEntity".equals(name) ;
					}
				});
				map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));
				resultList.add(map);
			}
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_cusPubSale.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_ProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/sale_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_cusPubSale.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal proShopPrice = new BigDecimal(ReqUtil.getLong(request, "proShopPrice", 0l)) ;
    	String priceType = ReqUtil.getString(request, "priceType", "");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String status = ReqUtil.getString(request, "status", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	
    	ProductSaleEntity productSaleEntity = new ProductSaleEntity();
    	ProductEntity productEntity = new ProductEntity();
    	
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}
    	productEntity.setProClass(1);
    	productEntity.setProName(proName);
    	productEntity.setMemberEntity(memberService.find(1l));
    	productEntity.setProductType(producttypeService.find(1l));
    	//productEntity.setIndustryclass(industryclass); 
    	//add by fang 20150908
    	ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", proClassId));
    	if(productclassEntity != null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	IndustryClassEntity industryClassEntity = industryClassService.find(indClassId);
    	productEntity.setProNo(proNo);
    	productSaleEntity.setProShopPrice(proShopPrice);
    	productSaleEntity.setPriceType(CurrencyEnum.valueOf(priceType));
    	productEntity.setProdNumber(prodNumber);
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productEntity.setProClass(1);
    	productSaleService.save(productSaleEntity,productEntity);
    	
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
    @RequestMapping(value = "/delete_cusPubSale.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.productSaleService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_cusPubSale.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	ProductSaleEntity productSaleEntity = productSaleService.find(id);
    	if(productSaleEntity != null){
    		model.addAttribute("item", productSaleEntity);
    	}
    	
    	return TEM_PATH+"/sale_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_cusPubSale.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	long producttypeId = ReqUtil.getLong(request, "producttypeId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal proShopPrice = new BigDecimal(ReqUtil.getLong(request, "proShopPrice", 0l)) ;
    	String priceType = ReqUtil.getString(request, "priceType", "");
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
    	String priceUnit =  ReqUtil.getString(request, "priceUnit", "");
    	
    	ProductSaleEntity productSaleEntity = productSaleService.find(id);
    	if(productSaleEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		productSaleEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	
        	productSaleEntity.getProductEntity().setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype) );
        	productSaleEntity.getProductEntity().setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
        	productSaleEntity.setPriceUnit(PriceUnitEnum.valueOf(priceUnit));
        	productSaleEntity.getProductEntity().setIsTax(isTax);
        	productSaleEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	productSaleEntity.getProductEntity().setProOriginal1(proOriginal1);
        	productSaleEntity.getProductEntity().setProOriginal2(proOriginal2);
        	productSaleEntity.getProductEntity().setProOriginal3(proOriginal3);
        	productSaleEntity.getProductEntity().setProOriginal4(proOriginal4);
        	productSaleEntity.getProductEntity().setDownData(downData);
        	productSaleEntity.getProductEntity().setProSynopsis(proSynopsis);
        	productSaleEntity.getProductEntity().setProSynopsisEn(proSynopsisEn);
        	productSaleEntity.getProductEntity().setProContent(proContent);
        	productSaleEntity.getProductEntity().setProContentEn(proContentEn);
        	productSaleEntity.getProductEntity().setApply(apply);
        	productSaleEntity.getProductEntity().setApplyEn(applyEn);
        	productSaleEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	productSaleEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	productSaleEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	productSaleEntity.getProductEntity().setWebzh(webzh);
        	productSaleEntity.getProductEntity().setProStock(proStock);
        	productSaleEntity.getProductEntity().setProcostPrice(procostPrice);
        	productSaleEntity.getProductEntity().setReferencePrice(referencePrice);
        	productSaleEntity.getProductEntity().setProNameEn(proNameEn);
        	productSaleEntity.getProductEntity().setProownaudit(proownaudit);
        	productSaleEntity.getProductEntity().setWeben(weben);
        	productSaleEntity.getProductEntity().setProName(proName);
        	ProducttypeEntity producttypeEntity = producttypeService.find(producttypeId);
	    	if(producttypeEntity != null){
	    		productSaleEntity.getProductEntity().setProductType(producttypeEntity);
	    	}
        	productSaleEntity.getProductEntity().setProNo(proNo);
        	productSaleEntity.setProShopPrice(proShopPrice);
        	productSaleEntity.setPriceType(CurrencyEnum.valueOf(priceType));
        	productSaleEntity.getProductEntity().setProdNumber(prodNumber);
        	productSaleEntity.getProductEntity().setStatus(StatusEnum.valueOf(status));
        	productSaleEntity.getProductEntity().setProClass(1);
        	productSaleEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	productSaleEntity.getProductEntity().setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
        	productSaleEntity.getProductEntity().setPoption(poption);
        	productSaleEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	productSaleEntity.getProductEntity().setCycle(cycle);
        	productSaleEntity.getProductEntity().setProUnit(proUnit);
        	productSaleEntity.getProductEntity().setRepairPeriod(repairPeriod);
        	productSaleEntity.getProductEntity().setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
        	productSaleEntity.getProductEntity().setRemain(remain);
        	productSaleService.save(productSaleEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
