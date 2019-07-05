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
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.AutoTestService;
import net.boocu.project.service.IndustryClassService;
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
 * 自动化测试方案
 * 
 * @author deng
 *
 * 2015年10月28日
 */
@Controller("autoTestSelfController")
@RequestMapping("/admin/prodoctMng/selfProMng/autoTest")
public class AutoTestSelfController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/selfProMng/autoTest";
	
	@Resource
	private AutoTestService autoTestService;
	
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
	
	@RequestMapping(value="toAutoTestList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/autoTestlist";
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
			sortValue = sortValue.replace("item", "autoTestEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("brandId", brandId);
		params.put("proClass", 0);
		Page<AutoTestEntity> page = autoTestService.findAutoTestPage(pageable, params);
		List<AutoTestEntity> autoTestEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(AutoTestEntity item : autoTestEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			if(item.getProductEntity().getMemberEntity() != null){
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String name, Object arg2) {
						return arg2==null || "autoTestEntity".equals(name) ;
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
    @RequestMapping(value = "/add_selfPubAutoTest.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_AutoTest(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/autoTest_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductRepairEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductRepairEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductRepairEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_selfPubAutoTest.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveAutoTest(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String projectName = ReqUtil.getString(request, "projectName", "");
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	long indClassId =  ReqUtil.getLong(request, "indClassId", 0l);
    	String proClassId = ReqUtil.getString(request, "proClassId", "");
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String status = ReqUtil.getString(request, "status", "");
    	BigDecimal budgetPrice = new BigDecimal(ReqUtil.getLong(request, "budgetPrice", 0l));
    	String budgetPriceUnit = ReqUtil.getString(request, "budgetPriceUnit", "yuan");
    	String budgetPriceType = ReqUtil.getString(request, "budgetPriceType", "rmb");
    	BigDecimal developPeriod = new BigDecimal(ReqUtil.getLong(request, "developPeriod", 0l));
    	String developPeriodUnit = ReqUtil.getString(request, "developPeriodUnit", "");
    	String projectIntroduction = ReqUtil.getString(request, "projectIntroduction", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String projectIntroductionEn = ReqUtil.getString(request, "projectIntroductionEn", "");
    	String projectNameEn = ReqUtil.getString(request, "projectNameEn", "");
    	String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
    	String proName = ReqUtil.getString(request, "proName", "");
    	String proNameEn = ReqUtil.getString(request, "proNameEn", "");
    	
    	AutoTestEntity autoTestEntity = new AutoTestEntity();
    	
    	ProductEntity productEntity = new ProductEntity();
		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}	
    	productEntity.setProName(proName);
    	productEntity.setProNameEn(proNameEn);
    	productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
    	productEntity.setStatus(StatusEnum.valueOf(status));
    	productEntity.setPrometaDescription(prometaDescription);
    	productEntity.setProClass(0);
    	autoTestEntity.setProjectName(projectName);
    	productEntity.setProNo(proNo);
    	productEntity.setPoption(poption);
    	productEntity.setProownaudit(proownaudit);
    	productEntity.setWebzh(webzh);
    	productEntity.setWeben(weben);
    	productEntity.setIsUnit(isUnit);
    	if(isUnit==0){
    		proUnit ="";
    	}
    	productEntity.setProUnit(proUnit);
    	productEntity.setIsTax(isTax);
    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
    	productEntity.setMemberEntity(memberService.find(1l));
    	autoTestEntity.setBudgetPrice(budgetPrice);
    	autoTestEntity.setBudgetPriceUnit(PriceUnitEnum.valueOf(budgetPriceUnit));
    	autoTestEntity.setBudgetPriceType(CurrencyEnum.valueOf(budgetPriceType));
    	autoTestEntity.setDevelopPeriod(developPeriod);
    	autoTestEntity.setDevelopPeriodUnit(DateTypeEnum.valueOf(developPeriodUnit));
    	productEntity.setProOriginal1(proOriginal1);
    	productEntity.setProOriginal2(proOriginal2);
    	productEntity.setProOriginal3(proOriginal3);
    	productEntity.setProOriginal4(proOriginal4);
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
    	autoTestEntity.setProjectIntroduction(projectIntroduction);
    	autoTestEntity.setProjectIntroductionEn(projectIntroductionEn);
    	autoTestEntity.setProjectNameEn(projectNameEn);

    	productEntity.setProductType(producttypeService.find(7l));
    	autoTestService.save(autoTestEntity,productEntity);
    	
    	
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
    @RequestMapping(value = "/delete_selfPubAutoTest.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteAutoTest(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	String[] strids =  request.getParameterValues("item.id");
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	if(ids != null && ids.length>0){
    		this.autoTestService.deleteList(ids);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_selfPubAutoTest.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editAutoTest(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	AutoTestEntity autoTestEntity = autoTestService.find(id);
    	if(autoTestEntity != null){
    		model.addAttribute("item", autoTestEntity);
    	}
    	
    	return TEM_PATH+"/autoTest_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductRepairEntity（村本身实体 ）
     * 传入参数:ProductRepairtEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductRepairEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_selfPubAutoTest.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditAutoTest(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	//获取到页面的基本信息
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String projectName = ReqUtil.getString(request, "projectName", "");
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	//String industryclass =  ReqUtil.getString(request, "industryclass", "");
    	//add by fang 20150908 
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String applyEn =  ReqUtil.getString(request, "applyEn", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String status = ReqUtil.getString(request, "status", "");
    	BigDecimal budgetPrice = new BigDecimal(ReqUtil.getLong(request, "budgetPrice", 0l));
    	String budgetPriceUnit = ReqUtil.getString(request, "budgetPriceUnit", "yuan");
    	String budgetPriceType = ReqUtil.getString(request, "budgetPriceType", "rmb");
    	BigDecimal developPeriod = new BigDecimal(ReqUtil.getLong(request, "developPeriod", 0l));
    	String developPeriodUnit = ReqUtil.getString(request, "developPeriodUnit", "");
    	String projectIntroduction = ReqUtil.getString(request, "projectIntroduction", "");
    	String prometaDescription =  ReqUtil.getString(request, "prometaDescription", "");
    	String projectIntroductionEn = ReqUtil.getString(request, "projectIntroductionEn", "");
    	String projectNameEn = ReqUtil.getString(request, "projectNameEn", "");
    	String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
    	String proName = ReqUtil.getString(request, "proName", "");
    	String proNameEn = ReqUtil.getString(request, "proNameEn", "");
    	
    	AutoTestEntity autoTestEntity = autoTestService.find(id);
    	if(autoTestEntity != null){
    		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
        	if(productBrandEntity != null){
        		autoTestEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
        	}
        	autoTestEntity.getProductEntity().setProName(proName);
        	autoTestEntity.getProductEntity().setProNameEn(proNameEn);
        	autoTestEntity.setProjectName(projectName);
        	autoTestEntity.getProductEntity().setPrometaDescription(prometaDescription);
        	autoTestEntity.getProductEntity().setProNo(proNo);
        	autoTestEntity.getProductEntity().setPoption(poption);
	    	autoTestEntity.getProductEntity().setProductType(producttypeService.find(7l));
	    	autoTestEntity.getProductEntity().setProownaudit(proownaudit);
	    	autoTestEntity.getProductEntity().setWebzh(webzh);
	    	autoTestEntity.getProductEntity().setWeben(weben);
	    	autoTestEntity.getProductEntity().setIsUnit(isUnit);
        	if(isUnit==0){
        		proUnit ="";
        	}
        	autoTestEntity.getProductEntity().setProUnit(proUnit);
        	autoTestEntity.getProductEntity().setIsTax(isTax);
        	autoTestEntity.getProductEntity().setTaxRate(RateEnum.valueOf(taxRate));
        	autoTestEntity.setDevelopPeriod(developPeriod);
        	autoTestEntity.setDevelopPeriodUnit(DateTypeEnum.valueOf(developPeriodUnit));
        	autoTestEntity.getProductEntity().setProOriginal1(proOriginal1);
        	autoTestEntity.getProductEntity().setProOriginal2(proOriginal2);
        	autoTestEntity.getProductEntity().setProOriginal3(proOriginal3);
        	autoTestEntity.getProductEntity().setProOriginal4(proOriginal4);
        	autoTestEntity.getProductEntity().setPrometaTitle(prometaTitle);
        	autoTestEntity.getProductEntity().setProMetaKeywords(proMetaKeywords);
        	autoTestEntity.setBudgetPrice(budgetPrice);
        	autoTestEntity.setBudgetPriceUnit(PriceUnitEnum.valueOf(budgetPriceUnit));
        	autoTestEntity.setBudgetPriceType(CurrencyEnum.valueOf(budgetPriceType));
        	autoTestEntity.getProductEntity().setApply(apply);
        	autoTestEntity.getProductEntity().setApplyEn(applyEn);
        	autoTestEntity.getProductEntity().setStatus(StatusEnum.valueOf(status));
        	autoTestEntity.setProjectIntroduction(projectIntroduction);
        	autoTestEntity.setProjectIntroductionEn(projectIntroductionEn);
        	autoTestEntity.setProjectNameEn(projectNameEn);
        	autoTestEntity.getProductEntity().setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
        	
        	autoTestService.save(autoTestEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
}
