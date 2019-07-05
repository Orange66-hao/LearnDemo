package net.boocu.web.controller.admin.productmng;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.CalibrationEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductTestEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.project.entity.RequireTestEntity;
import net.boocu.project.service.AutoTestService;
import net.boocu.project.service.CalibrationService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductTestService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.ProjectNeedService;
import net.boocu.project.service.RequireTestService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.MessageAuditService;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品审核
 * 
 * @author deng
 *
 * 2015年11月2日
 */
@Controller("messageAuditController")
@RequestMapping("/admin/prodoctMng/messageAudit")
public class MessageAuditController {
	
	private static final String TEM_PATH ="/template/admin/prodoctMng/messageAudit";
	
	@Resource
	MessageAuditService messageAuditService;
	
	@Resource
	ProductSaleService productSaleService;
	
	@Resource
	ProductBuyService  productBuyService;
	
	@Resource
	ProductRentService productRentService;
	
	@Resource
	ProductWantRentService productWantRentService;
	
	@Resource
	ProducWantRepairService productWantRepairService;
	
	@Resource
	ProductRepairService productRepairService;
	
	@Resource
	AutoTestService autoTestService;
	
	@Resource
	ProjectNeedService projectNeedService;
	
	@Resource
	ProductTestService productTestService;
	
	@Resource
	RequireTestService requireTestService;
	
	@Resource
	CalibrationService calibrationService;
	
    @Resource
    ProductBrandService productBrandService;
    
    @Resource
    ProducttypeService productTypeService;
    
    @Resource
    ProductclassService productclassService;
    
    @Resource
    IndustryClassService industryClassService;
	
    @Resource
    MemberService memberService;
    
    @Resource
    InstrumentService instrumentService;
    
	@RequestMapping(value="toMessageAuditList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/messageAuditlist";
	}
	
	//未审核的商品列表
	@RequestMapping(value="nAuditData.json",method={RequestMethod.POST,RequestMethod.GET})
	public void nAuditData(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		String productTypeId = ReqUtil.getString(request, "productTypeId", "0");
		String time = ReqUtil.getString(request, "time", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "productEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		java.util.Date date = new java.util.Date(System.currentTimeMillis());	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(!time.isEmpty()){
		calendar.add(Calendar.DAY_OF_MONTH,-Integer.parseInt(time));
		}
		date = calendar.getTime();
		HashMap<String, Object> params = new HashMap<String, Object>();
		pageable.getFilters().add(Filter.eq("apprStatus", 0));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		params.put("keyword", keyword);
		params.put("productTypeId", productTypeId);
		params.put("date", date);
		params.put("time", time);
		Page<ProductEntity> page = messageAuditService.findMessageAuditPage(pageable, params);
		List<ProductEntity> productEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String proName, Object arg2) {
						return arg2==null || "productEntity".equals(proName) || "autoTest".equals(proName) || "projectNeed".equals(proName)
								|| "productTest".equals(proName) || "calibration".equals(proName) || "requireTest".equals(proName);
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
	
	//已审核的商品列表
	@RequestMapping(value="auditData.json",method={RequestMethod.POST,RequestMethod.GET})
	public void auditdata(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		String productTypeId = ReqUtil.getString(request, "productTypeId", "0");
		String time = ReqUtil.getString(request, "time", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "productEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		java.util.Date date = new java.util.Date(System.currentTimeMillis());	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(!time.isEmpty()){
		calendar.add(Calendar.DAY_OF_MONTH,-Integer.parseInt(time));
		}
		date = calendar.getTime();
		HashMap<String, Object> params = new HashMap<String, Object>();
		pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		params.put("keyword", keyword);
		params.put("productTypeId", productTypeId);
		params.put("date", date);
		params.put("time", time);
		Page<ProductEntity> page = messageAuditService.findMessageAuditPage(pageable, params);
		List<ProductEntity> productEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String proName, Object arg2) {
						return arg2==null || "productEntity".equals(proName) || "autoTest".equals(proName) || "projectNeed".equals(proName)
								|| "productTest".equals(proName) || "calibration".equals(proName) || "requireTest".equals(proName);
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
	
	//被驳回的商品列表
	@RequestMapping(value="rejectData.json",method={RequestMethod.POST,RequestMethod.GET})
	public void rejectdata(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		String productTypeId = ReqUtil.getString(request, "productTypeId", "0");
		String time = ReqUtil.getString(request, "time", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "productEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		java.util.Date date = new java.util.Date(System.currentTimeMillis());	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(!time.isEmpty()){
		calendar.add(Calendar.DAY_OF_MONTH,-Integer.parseInt(time));
		}
		date = calendar.getTime();
		HashMap<String, Object> params = new HashMap<String, Object>();
		pageable.getFilters().add(Filter.eq("apprStatus", 2));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		params.put("keyword", keyword);
		params.put("productTypeId", productTypeId);
		params.put("date", date);
		params.put("time", time);
		Page<ProductEntity> page = messageAuditService.findMessageAuditPage(pageable, params);
		List<ProductEntity> productEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String proName, Object arg2) {
						return arg2==null || "productEntity".equals(proName) || "autoTest".equals(proName) || "projectNeed".equals(proName)
								|| "productTest".equals(proName) || "calibration".equals(proName) || "requireTest".equals(proName);
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
	//审核
	@RequestMapping(value="/auditProductAudit.jspx", method = {RequestMethod.POST, RequestMethod.GET})
	public void audit(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = messageAuditService.find(ids[i]);
			productEntity.setApprStatus(1);
			messageAuditService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "审核成功");
		RespUtil.renderJson(response, result);
	}
	
	//反审核
	@RequestMapping(value="/unAuditProductAudit.jspx", method = {RequestMethod.POST, RequestMethod.GET})
	public void unAuditProductAudit(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = messageAuditService.find(ids[i]);
			productEntity.setApprStatus(0);
			messageAuditService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "反审核成功");
		RespUtil.renderJson(response, result);
	}
	
	//驳回
	@RequestMapping(value="/rejectProductAudit.jspx", method = {RequestMethod.POST, RequestMethod.GET})
	public void reject(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = messageAuditService.find(ids[i]);
			productEntity.setApprStatus(2);
			messageAuditService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "驳回成功");
		RespUtil.renderJson(response, result);
	}
	
	//促销显示
	@RequestMapping(value="/agreeProductAudit.jspx", method = {RequestMethod.POST, RequestMethod.GET})
	public void agreeProductAudit(HttpServletRequest request, HttpServletResponse response, Model model){
		String[] strid = request.getParameterValues("item.id");
		Long [] ids = new Long[strid.length];
		
		for(int i=0;i<strid.length;i++){
			ids[i] = Long.parseLong(strid[i]);
		}
		for(int i=0;i<ids.length;i++){
			ProductEntity productEntity = messageAuditService.find(ids[i]);
			productEntity.setDisplay(1);
			productEntity.setIsPromSale(1);
			messageAuditService.update(productEntity);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "同意成功");
		RespUtil.renderJson(response, result);
	}
	
	//删除
	@RequestMapping(value="/deleteProductAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
	public void delete(HttpServletRequest request, HttpServletResponse response, Model model){
    	String[] idStrings = request.getParameterValues("item.id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
		//遍历Id
    	if(ids !=null && ids.length>0){
		for(Long id : ids){
			ProductEntity productEntity = messageAuditService.find(id);
			Long productId = productEntity.getId();
			if(productEntity !=null){
				//删除销售数据
				ProductSaleEntity productSaleEntity = productSaleService.find(productId);
				if(productSaleEntity !=null){
					productSaleEntity.setIsDel(1);
					productSaleService.update(productSaleEntity);
				}
				//删除求购数据
				ProductBuyEntity productBuyEntity = productBuyService.find(productId);
				if(productBuyEntity !=null){
					productBuyEntity.setIsDel(1);
					productBuyService.update(productBuyEntity);
				}
				//删除租赁数据
				ProductRentEntity productRentEntity = productRentService.find(productId);
				if(productRentEntity !=null){
					productRentEntity.setIsDel(1);
					productRentService.update(productRentEntity);
				}
				//删除求租数据
				ProductWantRentEntity productWantRentEntity = productWantRentService.find(productId);
				if(productWantRentEntity !=null){
					productWantRentEntity.setIsDel(1);
					productWantRentService.update(productWantRentEntity);
				}
				//删除维修数据
				ProductRepairEntity productRepairEntity = productRepairService.find(productId);
				if(productRepairEntity !=null){
					productRepairEntity.setIsDel(1);
					productRepairService.update(productRepairEntity);
				}
				//删除求修数据
				ProducWantRepairEntity producWantRepairEntity = productWantRepairService.find(productId);
				if(producWantRepairEntity !=null){
					producWantRepairEntity.setIsDel(1);
					productWantRepairService.update(producWantRepairEntity);
				}
				productEntity.setIsDel(1);
				messageAuditService.update(productEntity);
			}
		}
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}
	
	//跳转到实体更新页面
	@RequestMapping(value="/edit_productAudit.jspx", method={ RequestMethod.GET, RequestMethod.POST})
	public String edit_productAudit(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		ProductEntity productEntity = messageAuditService.find(id);
		if(productEntity !=null){
			//根据商品类型进行选择
			if(productEntity.getProductType().getId()==1l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_sale";
			}else if(productEntity.getProductType().getId()==2l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_buy";
			}else if(productEntity.getProductType().getId()==3l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_rent";
			}else if(productEntity.getProductType().getId()==4l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_wantRent";
			}else if(productEntity.getProductType().getId()==5l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_repair";
			}else if(productEntity.getProductType().getId()==6l){
				model.addAttribute("item", productEntity);
				return TEM_PATH + "/editProductAudit/productAudit_wantRepair";
			}else if(productEntity.getProductType().getId()==7l){
				AutoTestEntity autoTestEntity = autoTestService.find(Filter.eq("productEntity", productEntity));
			    	model.addAttribute("item", autoTestEntity);	
			    	List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("autoTest", autoTestEntity));
			    	//获取instrumentEntities 第一个元素
			    	if(instrumentEntities.size()>0 && instrumentEntities!=null){
				    	InstrumentEntity instrumentEntity = instrumentEntities.get(0);
				    	model.addAttribute("instrumentEntity", instrumentEntity);		    		
				    	instrumentEntities.remove(0);
				    	//去除instrumentEntities 第一个元素
				    	model.addAttribute("instrument", instrumentEntities);
			    	}
				return TEM_PATH + "/editProductAudit/productAudit_autoTest";
			}else if(productEntity.getProductType().getId()==8l){
				ProjectNeedEntity projectNeedEntity = projectNeedService.find(Filter.eq("productEntity", productEntity));
		    	model.addAttribute("item", projectNeedEntity);	
		    	List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("projectNeed", projectNeedEntity));
		    	//获取instrumentEntities 第一个元素
		    	if(instrumentEntities.size()>0 && instrumentEntities!=null){
			    	InstrumentEntity instrumentEntity = instrumentEntities.get(0);
			    	model.addAttribute("instrumentEntity", instrumentEntity);		    		
			    	instrumentEntities.remove(0);
			    	//去除instrumentEntities 第一个元素
			    	model.addAttribute("instrument", instrumentEntities);
		    	}
			return TEM_PATH + "/editProductAudit/productAudit_projectNeed";
		}else if(productEntity.getProductType().getId()==11l){
			ProductTestEntity productTestEntity = productTestService.find(Filter.eq("productEntity", productEntity));
	    	model.addAttribute("item", productTestEntity);	
	    	List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("productTest", productTestEntity));
	    	//获取instrumentEntities 第一个元素
	    	if(instrumentEntities.size()>0 && instrumentEntities!=null){
		    	InstrumentEntity instrumentEntity = instrumentEntities.get(0);
		    	model.addAttribute("instrumentEntity", instrumentEntity);		    		
		    	instrumentEntities.remove(0);
		    	//去除instrumentEntities 第一个元素
		    	model.addAttribute("instrument", instrumentEntities);
	    	}
	    	return TEM_PATH + "/editProductAudit/productAudit_productTest";
		}else if(productEntity.getProductType().getId()==12l){
			RequireTestEntity requireTestEntity = requireTestService.find(Filter.eq("productEntity", productEntity));
	    	model.addAttribute("item", requireTestEntity);	
	    	List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("requireTest", requireTestEntity));
	    	//获取instrumentEntities 第一个元素
	    	if(instrumentEntities.size()>0 && instrumentEntities!=null){
		    	InstrumentEntity instrumentEntity = instrumentEntities.get(0);
		    	model.addAttribute("instrumentEntity", instrumentEntity);		    		
		    	instrumentEntities.remove(0);
		    	//去除instrumentEntities 第一个元素
		    	model.addAttribute("instrument", instrumentEntities);
	    	}
	    	return TEM_PATH + "/editProductAudit/productAudit_requireTest";
		}else if(productEntity.getProductType().getId()==13l){
			CalibrationEntity calibrationEntity = calibrationService.find(Filter.eq("productEntity", productEntity));
	    	model.addAttribute("item", calibrationEntity);	
	    	List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("calibration", calibrationEntity));
	    	//获取instrumentEntities 第一个元素
	    	if(instrumentEntities.size()>0 && instrumentEntities!=null){
		    	InstrumentEntity instrumentEntity = instrumentEntities.get(0);
		    	model.addAttribute("instrumentEntity", instrumentEntity);		    		
		    	instrumentEntities.remove(0);
		    	//去除instrumentEntities 第一个元素
		    	model.addAttribute("instrument", instrumentEntities);
	    	}
	    	return TEM_PATH + "/editProductAudit/productAudit_calibration";
		}
	}
		return null;
	}
	
    //修改商品销售
    @ResponseBody
    @RequestMapping(value="/update_editSale_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editSale_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 0l);
     	String proName= ReqUtil.getString(request, "proName", "");
     	long brandId= ReqUtil.getLong(request, "brandId",0l);
     	String proNo = ReqUtil.getString(request, "proNo", "");
     	String poption = ReqUtil.getString(request, "poption", "");
     	String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
     	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
     	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
     	String cycle = ReqUtil.getString(request, "cycle", "");
     	String cycleUnit = ReqUtil.getString(request, "cycleUnit", "");
     	String proUnit = ReqUtil.getString(request, "proUnit", "");
     	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
     	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
     	int webzh =  ReqUtil.getInt(request, "webzh", 0);
     	int weben =  ReqUtil.getInt(request, "weben", 0);
     	int proStock =  ReqUtil.getInt(request, "proStock", 0);
     	String procostPrice =  ReqUtil.getString(request, "procostPrice", "");
     	int isTax =  ReqUtil.getInt(request, "isTax", 0);
     	String taxRate =  ReqUtil.getString(request, "taxRate", "");
     	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
     	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
     	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
     	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
     	String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
     	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
     	String proContent =  ReqUtil.getString(request, "proContent", "");
     	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
     	long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
     	String apply =  ReqUtil.getString(request, "apply", "");
     	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
     	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
     	String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
//     	BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
//     	String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");
     	Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
     	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
     	BigDecimal referencePrice = new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l));
     	String referencePriceLimit = ReqUtil.getString(request, "referencePriceLimit", "");
     	String referencepricetype = ReqUtil.getString(request, "referencepricetype", "");
    	BigDecimal proShopPrice = new BigDecimal(ReqUtil.getLong(request, "proShopPrice", 0l));
    	String priceType = ReqUtil.getString(request, "priceType", "");
    	String priceUnit = ReqUtil.getString(request, "priceUnit", "");
    	String downData = ReqUtil.getString(request, "downdata", "");
    	String brandName =ReqUtil.getString(request, "brandName", "");
    	BigDecimal returnPeriod = new BigDecimal(ReqUtil.getLong(request, "returnPeriod", 0l));
    	String returnPeriodUnit = ReqUtil.getString(request, "returnPeriodUnit", "");
    	String areaProvince = ReqUtil.getString(request, "areaProvince", "");
    	String areaCity = ReqUtil.getString(request, "areaCity", "");
    	String areaCountry = ReqUtil.getString(request, "areaCountry", "");
    	String dataName = ReqUtil.getString(request, "dataName", "");
     	
    	ProductEntity productEntity = messageAuditService.find(id);
    	if(productEntity !=null){
		if(!brandName.isEmpty()){
			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
			if(productBrandEntity != null){
				productEntity.setProductBrandEntity(productBrandEntity);
			}else{
				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
				productBrandEntity2.setName(brandName);
				productBrandService.save(productBrandEntity2);
				productEntity.setProductBrandEntity(productBrandEntity2);
			}	
		}
    	productEntity.setAreaCity(areaCity);
    	productEntity.setAreaCountry(areaCountry);
    	productEntity.setAreaProvince(areaProvince);
    	productEntity.setReturnPeriod(returnPeriod);
    	productEntity.setReturnPeriodUnit(DateTypeEnum.valueOf(returnPeriodUnit));
    	productEntity.setRepairPeriod(repairPeriod);
    	productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
    	productEntity.setInforValidity(inforValidity);
    	if(inforValidity !=null){
    		Date date = new Date();
    		Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
    		productEntity.setInforNumber(date1);
    	}
//    	productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
    	productEntity.setDownData(downData);
    	productEntity.setDataName(dataName);
    	productEntity.setApply(apply);
    	productEntity.setPrometaTitle(prometaTitle);
    	productEntity.setProMetaKeywords(proMetaKeywords);
    	productEntity.setPrometaDescription(prometaDescription);
    	productEntity.setProOriginal1(proOriginal1);
	    productEntity.setProOriginal2(proOriginal2);
     	productEntity.setProOriginal3(proOriginal3);
	    productEntity.setProOriginal4(proOriginal4);
	   	productEntity.setProSynopsis(proSynopsis);
	    productEntity.setProSynopsisEn(proSynopsisEn);
	    productEntity.setProContent(proContent);
	    productEntity.setProContentEn(proContentEn);
	    //行业分类
	    String tt1 ="";
	    if(indClassId==null){
	    	productEntity.setIndustryClass(null);
	    }else{
		    for (int i = 0; i < indClassId.length; i++) {
				String string = indClassId[i];
				if(!string.isEmpty()){
					if(CommonUtil.isNumeric(string)==true){
						IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
						if(industryClassEntity !=null){
							tt1 +=industryClassEntity.getName()+",";
						}	
					}else{
						tt1 += indClassId[i]+",";
					}	
				}
			}
			if(tt1!=""){
		     	productEntity.setIndustryClass(tt1);
			}	
	    }
    	ProductclassEntity productclassEntity = productclassService.find(proClassId);
    	if(productclassEntity !=null){
    		productEntity.setProductclass(productclassEntity);
    	}
    	productEntity.setIsTax(isTax);
    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
    	productEntity.setProcostPrice(procostPrice);
    	productEntity.setProownaudit(proownaudit);
    	productEntity.setIsUnit(isUnit);
     	if(isUnit==0){
     		proUnit ="";
     	}    	 
     	productEntity.setApprStatus(1);
     	productEntity.setProductType(productTypeService.find(1l));
     	productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
     	productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
     	productEntity.setProUnit(proUnit);
    	productEntity.setProNo(proNo);
    	productEntity.setProNameEn(proNameEn);
    	productEntity.setPoption(poption);
    	productEntity.setCycle(cycle);
    	productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
    	productEntity.setProName(proName);
    	productEntity.setWeben(weben);
    	productEntity.setWebzh(webzh);
    	productEntity.setProStock(proStock);
    	if(proStock == 1){
    		productEntity.setProdNumber(prodNumber);
    	}else{
    		productEntity.setProdNumber("");
    	}
    	if(qualityStatus.equals("all")){
    		productEntity.setReferencePrice(new BigDecimal(0));
    		productEntity.setReferencePriceLimit(CurrencyEnum.valueOf("rmb"));
    		productEntity.setReferencepricetype(PriceUnitEnum.valueOf("yuan")); 
    	}else{
    		productEntity.setReferencePrice(referencePrice);
    		productEntity.setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
    		productEntity.setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype)); 
    	}
    	productEntity.getProductSaleEntity().setPriceType(CurrencyEnum.valueOf(priceType));
    	productEntity.getProductSaleEntity().setPriceUnit(PriceUnitEnum.valueOf(priceUnit));
    	productEntity.getProductSaleEntity().setProShopPrice(proShopPrice);
   	 		
  	 	messageAuditService.update(productEntity);
   	 	}
   	 	Map<String, Object> result = new HashMap<String, Object>();
   	 	result.put("result", 1);
   	 	result.put("message", "修改成功");
   	 	return result;		
	}
    
    //修改商品销售
    @ResponseBody
    @RequestMapping(value="/update_editBuy_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editBuy_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
  	  long id = ReqUtil.getLong(request, "id", 0l);
  	  String proName= ReqUtil.getString(request, "proName", "");
  	  long brandId= ReqUtil.getLong(request, "brandId",0l);
  	  String proNo = ReqUtil.getString(request, "proNo", "");
  	  BigDecimal proMarketPrice = new BigDecimal(ReqUtil.getLong(request, "proMarketPrice", 0l));
  	  String proMarketPriceLimit = ReqUtil.getString(request, "proMarketPriceLimit", "yuan");
  	  String proMarketPriceType = ReqUtil.getString(request, "proMarketPriceType", "rmb");
  	  String poption = ReqUtil.getString(request, "poption", "");
  	  String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
  	  String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
  	  int isUnit = ReqUtil.getInt(request, "isUnit", 0);
  	  String cycle = ReqUtil.getString(request, "cycle", "");
  	  String cycleUnit = ReqUtil.getString(request, "cycleUnit", "");
  	  String proUnit = ReqUtil.getString(request, "proUnit", "");
  	  String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
  	  int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
  	  int webzh =  ReqUtil.getInt(request, "webzh", 0);
  	  int weben =  ReqUtil.getInt(request, "weben", 0);
  	  int proStock =  ReqUtil.getInt(request, "proStock", 0);
  	  String procostPrice =  ReqUtil.getString(request, "procostPrice", "");
  	  int isTax =  ReqUtil.getInt(request, "isTax", 0);
  	  String taxRate =  ReqUtil.getString(request, "taxRate", "");
  	  String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
  	  String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
  	  String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
  	  String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
  	  String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
  	  String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
  	  String proContent =  ReqUtil.getString(request, "proContent", "");
  	  String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
  	  long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
  	  String apply =  ReqUtil.getString(request, "apply", "");
  	  String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
  	  String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
  	  String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
  /*	  BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
  	  String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");*/
  	  Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
  	  String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
  	  String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
  	  String prodNumber = ReqUtil.getString(request, "prodNumber", "");
  	  BigDecimal referencePrice = new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l));
  	  String referencePriceLimit = ReqUtil.getString(request, "referencePriceLimit", "");
  	  String referencepricetype = ReqUtil.getString(request, "referencepricetype", "");
  	  String brandName = ReqUtil.getString(request, "brandName", "");
  	  String areaProvince = ReqUtil.getString(request, "areaProvince", "");
  	  String areaCity = ReqUtil.getString(request, "areaCity", "");
  	  String areaCountry = ReqUtil.getString(request, "areaCountry", "");
  	  
  	  ProductEntity productEntity = messageAuditService.find(id);
  	  if(productEntity != null){
  		if(!brandName.isEmpty()){
			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
			if(productBrandEntity != null){
				productEntity.setProductBrandEntity(productBrandEntity);
			}else{
				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
				productBrandEntity2.setName(brandName);
				productBrandService.save(productBrandEntity2);
				productEntity.setProductBrandEntity(productBrandEntity2);
			}	
		}
  		  //行业分类
  		  String tt1 ="";
  		  if(indClassId==null){
  			  productEntity.setIndustryClass(null);
  		  }else{
  			  for (int i = 0; i < indClassId.length; i++) {
  				  String string = indClassId[i];
  				  if(!string.isEmpty()){
  					  if(CommonUtil.isNumeric(string)==true){
  						  IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
  						  if(industryClassEntity !=null){
  							  tt1 +=industryClassEntity.getName()+",";
  						  }	
  					  }else{
  						  tt1 += indClassId[i]+",";
  					  }	
  				  }
  			  }
  			  if(tt1!=""){
  				  productEntity.setIndustryClass(tt1);
  			  }	
  		  }
  		  if(qualityStatus.equals("all")){
  			  productEntity.setReferencePrice(new BigDecimal(0));
  			  productEntity.setReferencePriceLimit(CurrencyEnum.valueOf("rmb"));
  			  productEntity.setReferencepricetype(PriceUnitEnum.valueOf("yuan")); 
  		  }else{
  			  productEntity.setReferencePrice(referencePrice);
  			  productEntity.setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
  			  productEntity.setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype)); 
  		  }
  		  productEntity.setAreaCity(areaCity);
  		  productEntity.setAreaCountry(areaCountry);
  		  productEntity.setAreaProvince(areaProvince);
  		  productEntity.setApprStatus(1);
  		  productEntity.setRepairPeriod(repairPeriod);
  		  productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
  		  productEntity.setInforValidity(inforValidity);
  		  if(inforValidity !=null){
  			  Date date = new Date();
  			  Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
  			  productEntity.setInforNumber(date1);
  		  }
  /*		  productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));*/
  		  productEntity.setApply(apply);
  		  productEntity.setPrometaTitle(prometaTitle);
  		  productEntity.setProMetaKeywords(proMetaKeywords);
  		  productEntity.setPrometaDescription(prometaDescription);
  		  productEntity.setProOriginal1(proOriginal1);
  		  productEntity.setProOriginal2(proOriginal2);
  		  productEntity.setProOriginal3(proOriginal3);
  		  productEntity.setProOriginal4(proOriginal4);
  		  productEntity.setProSynopsis(proSynopsis);
  		  productEntity.setProSynopsisEn(proSynopsisEn);
  		  productEntity.setProContent(proContent);
  		  productEntity.setProContentEn(proContentEn);
  	
  		  ProductclassEntity productclassEntity = productclassService.find(proClassId);
  		  if(productclassEntity !=null){
  			  productEntity.setProductclass(productclassEntity);
  		  }
  		  productEntity.setIsTax(isTax);
  		  productEntity.setTaxRate(RateEnum.valueOf(taxRate));
  		  productEntity.setProcostPrice(procostPrice);
  		  productEntity.setProStock(proStock);
  		  if(proStock == 1){
  			  productEntity.setProdNumber(prodNumber);
  		  }else{
  			  productEntity.setProdNumber("");
  		  }
  		  productEntity.setProownaudit(proownaudit);
  		  productEntity.setIsUnit(isUnit);
  		  if(isUnit==0){
  			  proUnit ="";
  		  }    	 
  		  productEntity.setProductType(productTypeService.find(2l));
  		  productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
  		  productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
  		  productEntity.setProUnit(proUnit);
  		  productEntity.setProNo(proNo);
  		  productEntity.setProNameEn(proNameEn);
  		  productEntity.setPoption(poption);
  		  productEntity.setCycle(cycle);
  		  productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
  		  productEntity.setProName(proName);
  		  productEntity.setWeben(weben);
  		  productEntity.setWebzh(webzh);
  		  productEntity.getProductBuyEntity().setProMarketPrice(proMarketPrice);
  		  productEntity.getProductBuyEntity().setProMarketPriceLimit(PriceUnitEnum.valueOf(proMarketPriceLimit));
  		  productEntity.getProductBuyEntity().setProMarketPriceType(CurrencyEnum.valueOf(proMarketPriceType));
  	    	 
  	      messageAuditService.update(productEntity);
    	  }
    	Map<String, Object> result = new HashMap<String, Object>();
	  	result.put("result", 1);
	  	result.put("message", "操作成功");
	  	return result;	 
    }
    
    //修改商品租赁
    @ResponseBody
    @RequestMapping(value="/update_editRent_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editRent_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
     	long brandId= ReqUtil.getLong(request, "brandId",0l);
     	String proNo = ReqUtil.getString(request, "proNo", "");
     	BigDecimal rentPrice = new BigDecimal(ReqUtil.getLong(request, "rentPrice", 0l));
     	String rentPriceUnit = ReqUtil.getString(request, "rentPriceUnit", "yuan");
     	String rentPriceType = ReqUtil.getString(request, "rentPriceType", "rmb");
     	String status = ReqUtil.getString(request, "status", "");
     	String poption = ReqUtil.getString(request, "poption", "");
     	String qualityStatus = ReqUtil.getString(request, "qualityStatus", "");
     	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
     	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
     	String cycle = ReqUtil.getString(request, "cycle", "");
     	String cycleUnit = ReqUtil.getString(request, "cycleUnit", "");
     	String proUnit = ReqUtil.getString(request, "proUnit", "");
     	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
     	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
     	int webzh =  ReqUtil.getInt(request, "webzh", 0);
     	int weben =  ReqUtil.getInt(request, "weben", 0);
     	int proStock =  ReqUtil.getInt(request, "proStock", 0);
     	String procostPrice =  ReqUtil.getString(request, "procostPrice", "");
     	int isTax =  ReqUtil.getInt(request, "isTax", 0);
     	String taxRate =  ReqUtil.getString(request, "taxRate", "");
     	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
     	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
     	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
     	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
     	String proSynopsis =  ReqUtil.getString(request, "proSynopsis", "");
     	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
     	String proContent =  ReqUtil.getString(request, "proContent", "");
     	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
     	long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
     	String apply =  ReqUtil.getString(request, "apply", "");
     	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
     	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
     	String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
//     	BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
//     	String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");
     	Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
     	BigDecimal referencePrice = new BigDecimal(ReqUtil.getLong(request, "referencePrice", 0l));
     	String referencePriceLimit = ReqUtil.getString(request, "referencePriceLimit", "");
     	String referencepricetype = ReqUtil.getString(request, "referencepricetype", "");
     	String brandName = ReqUtil.getString(request, "brandName", "");
    	String areaProvince = ReqUtil.getString(request, "areaProvince", "");
    	String areaCity = ReqUtil.getString(request, "areaCity", "");
    	String areaCountry = ReqUtil.getString(request, "areaCountry", "");

    	ProductEntity productEntity = messageAuditService.find(id);
    	if(productEntity != null){   		
    		if(!brandName.isEmpty()){
    			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    			if(productBrandEntity != null){
    				productEntity.setProductBrandEntity(productBrandEntity);
    			}else{
    				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
    				productBrandEntity2.setName(brandName);
    				productBrandService.save(productBrandEntity2);
    				productEntity.setProductBrandEntity(productBrandEntity2);
    			}	
    		}
	    	if(qualityStatus.equals("all")){
	    		productEntity.setReferencePrice(new BigDecimal(0));
	    		productEntity.setReferencePriceLimit(CurrencyEnum.valueOf("rmb"));
	    		productEntity.setReferencepricetype(PriceUnitEnum.valueOf("yuan")); 
	    	}else{
	    		productEntity.setReferencePrice(referencePrice);
	    		productEntity.setReferencePriceLimit(CurrencyEnum.valueOf(referencePriceLimit));
	    		productEntity.setReferencepricetype(PriceUnitEnum.valueOf(referencepricetype)); 
	    	}
	    	productEntity.setAreaCity(areaCity);
	    	productEntity.setAreaCountry(areaCountry);
	    	productEntity.setAreaProvince(areaProvince);
	    	productEntity.setApprStatus(1);
	    	productEntity.setInforValidity(inforValidity);
	    	if(inforValidity !=null){
	    		Date date = new Date();
	    		Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
	    		productEntity.setInforNumber(date1);
	    	}
//	    	productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
	    	productEntity.setApply(apply);
	    	productEntity.setPrometaTitle(prometaTitle);
	    	productEntity.setProMetaKeywords(proMetaKeywords);
	    	productEntity.setPrometaDescription(prometaDescription);
	    	productEntity.setProOriginal1(proOriginal1);
		    productEntity.setProOriginal2(proOriginal2);
	     	productEntity.setProOriginal3(proOriginal3);
		    productEntity.setProOriginal4(proOriginal4);
		   	productEntity.setProSynopsis(proSynopsis);
		    productEntity.setProSynopsisEn(proSynopsisEn);
		    productEntity.setProContent(proContent);
		    productEntity.setProContentEn(proContentEn);
		    //行业分类
		    String tt1 ="";
		    if(indClassId==null){
		    	productEntity.setIndustryClass(null);
		    }else{
		    	for (int i = 0; i < indClassId.length; i++) {
		    		String string = indClassId[i];
		    		if(!string.isEmpty()){
		    			if(CommonUtil.isNumeric(string)==true){
		    				IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
		    				if(industryClassEntity !=null){
		    					tt1 +=industryClassEntity.getName()+",";
		    				}	
		    			}else{
		    				tt1 += indClassId[i]+",";
		    			}	
		    		}
		    	}
		    	if(tt1!=""){
		    		productEntity.setIndustryClass(tt1);
		    	}	
		    }
	    	ProductclassEntity productclassEntity = productclassService.find(proClassId);
	    	if(productclassEntity !=null){
	    		productEntity.setProductclass(productclassEntity);
	    	}
	    	productEntity.setIsTax(isTax);
	    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
	    	productEntity.setProcostPrice(procostPrice);
	    	productEntity.setProStock(proStock);
	    	if(proStock ==1){
		    	productEntity.setProdNumber(prodNumber);
	    	}else{
	    		productEntity.setProdNumber("");
	    	}
	    	productEntity.setProownaudit(proownaudit);
	    	productEntity.setIsUnit(isUnit);
	     	if(isUnit==0){
	     		proUnit ="";
	     	}    	 
	     	productEntity.setProductType(productTypeService.find(3l));
	     	productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
	     	productEntity.setQualityStatus(QualityStatusEnum.valueOf(qualityStatus));
	     	productEntity.setProUnit(proUnit);
	    	productEntity.setProNo(proNo);
	    	productEntity.setProNameEn(proNameEn);
	    	productEntity.setPoption(poption);
	    	productEntity.setStatus(StatusEnum.valueOf(status));
	    	productEntity.setCycle(cycle);
	    	productEntity.setCycleUnit(DateTypeEnum.valueOf(cycleUnit));
	    	productEntity.setProName(proName);
	    	productEntity.setWeben(weben);
	    	productEntity.setWebzh(webzh);
	    	productEntity.getProductRentEntity().setRentPrice(rentPrice);
	    	productEntity.getProductRentEntity().setRentPriceType(CurrencyEnum.valueOf(rentPriceType));
	    	productEntity.getProductRentEntity().setRentPriceUnit(PriceUnitEnum.valueOf(rentPriceUnit));
       	 		
	    	messageAuditService.update(productEntity);
    	}
  	 		
  	 		
   	 	Map<String, Object> result = new HashMap<String, Object>();
   	 	result.put("result", 1);
   	 	result.put("message", "操作成功");
   	 	return result;	
    }
    
    //修改商品求租
    @ResponseBody
    @RequestMapping(value="/update_editWantRent_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editWantRent_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
    	long brandId= ReqUtil.getLong(request, "brandId",0l);
    	String proNo = ReqUtil.getString(request, "proNo", "");
    	BigDecimal magdebrugBudget = new BigDecimal(ReqUtil.getLong(request, "magdebrugBudget", 0l));
    	String magdebrugBudgetLimit = ReqUtil.getString(request, "magdebrugBudgetLimit", "yuan");
    	String magdebrugBudgetType = ReqUtil.getString(request, "magdebrugBudgetType", "rmb");
    	String status = ReqUtil.getString(request, "status", "");
    	String poption = ReqUtil.getString(request, "poption", "");
    	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
    	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
    	BigDecimal sendCycle = new BigDecimal(ReqUtil.getLong(request, "sendCycle", 0l));
    	String sendCycleUnit = ReqUtil.getString(request, "sendCycleUnit", "");
    	String proUnit = ReqUtil.getString(request, "proUnit", "");
    	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
    	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
    	int webzh =  ReqUtil.getInt(request, "webzh", 0);
    	int weben =  ReqUtil.getInt(request, "weben", 0);
    	int proStock =  ReqUtil.getInt(request, "proStock", 0);
    	int isTax =  ReqUtil.getInt(request, "isTax", 0);
    	String taxRate =  ReqUtil.getString(request, "taxRate", "");
    	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
    	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
    	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
    	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
    	String proSynopsis =  ReqUtil.getString(request,       "proSynopsis", "");
    	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
    	String proContent =  ReqUtil.getString(request, "proContent", "");
    	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
    	long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
    	String apply =  ReqUtil.getString(request, "apply", "");
    	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
    	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
    	String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
//    	BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
//    	String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");
    	Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
    	Date startRent = ReqUtil.getDate(request, "startRent", null);
    	BigDecimal rentperiod = new BigDecimal(ReqUtil.getLong(request, "rentperiod", 0l));
    	String rentperiodunit = ReqUtil.getString(request, "rentperiodunit", "");
    	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String brandName = ReqUtil.getString(request, "brandName", "");
    	String areaProvince = ReqUtil.getString(request, "areaProvince", "");
    	String areaCity = ReqUtil.getString(request, "areaCity", "");
    	String areaCountry = ReqUtil.getString(request, "areaCountry", "");

    	ProductEntity productEntity = messageAuditService.find(id);
    	if(productEntity != null){	
    		if(!brandName.isEmpty()){
    			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    			if(productBrandEntity != null){
    				productEntity.setProductBrandEntity(productBrandEntity);
    			}else{
    				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
    				productBrandEntity2.setName(brandName);
    				productBrandService.save(productBrandEntity2);
    				productEntity.setProductBrandEntity(productBrandEntity2);
    			}	
    		}
    		productEntity.setApprStatus(1);
    		productEntity.setAreaCity(areaCity);
    		productEntity.setAreaCountry(areaCountry);
    		productEntity.setAreaProvince(areaProvince);
    		productEntity.setInforValidity(inforValidity);
        	if(inforValidity !=null){
        		Date date = new Date();
        		Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
        		productEntity.setInforNumber(date1);
        	}
//    		productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
    		productEntity.setApply(apply);
    		productEntity.setPrometaTitle(prometaTitle);
    		productEntity.setProMetaKeywords(proMetaKeywords);
    		productEntity.setPrometaDescription(prometaDescription);
    		productEntity.setProOriginal1(proOriginal1);
    		productEntity.setProOriginal2(proOriginal2);
    		productEntity.setProOriginal3(proOriginal3);
    		productEntity.setProOriginal4(proOriginal4);
    		productEntity.setProSynopsis(proSynopsis);
    		productEntity.setProSynopsisEn(proSynopsisEn);
    		productEntity.setProContent(proContent);
    		productEntity.setProContentEn(proContentEn);
		    //行业分类
		    String tt1 ="";
		    if(indClassId==null){
		    	productEntity.setIndustryClass(null);
		    }else{
		    	for (int i = 0; i < indClassId.length; i++) {
		    		String string = indClassId[i];
		    		if(!string.isEmpty()){
		    			if(CommonUtil.isNumeric(string)==true){
		    				IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
		    				if(industryClassEntity !=null){
		    					tt1 +=industryClassEntity.getName()+",";
		    				}	
		    			}else{
		    				tt1 += indClassId[i]+",";
		    			}	
		    		}
		    	}
		    	if(tt1!=""){
		    		productEntity.setIndustryClass(tt1);
		    	}	
		    }
    		ProductclassEntity productclassEntity = productclassService.find(proClassId);
    		if(productclassEntity !=null){
    			productEntity.setProductclass(productclassEntity);
    		}
    		productEntity.setIsTax(isTax);
    		productEntity.setTaxRate(RateEnum.valueOf(taxRate));
    		productEntity.setProStock(proStock);
    		if(proStock == 1){
    			productEntity.setProdNumber(prodNumber);
    		}else{
    			productEntity.setProdNumber("");
    		}
    		productEntity.setProownaudit(proownaudit);
    		productEntity.setIsUnit(isUnit);
    		if(isUnit==0){
    			proUnit ="";
    		}    	 
    		productEntity.setProductType(productTypeService.find(4l));
    		productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
    		productEntity.setProUnit(proUnit);
    		productEntity.setProNo(proNo);
    		productEntity.setProNameEn(proNameEn);
    		productEntity.setPoption(poption);
    		productEntity.setStatus(StatusEnum.valueOf(status));
    		productEntity.setSendCycle(sendCycle);
    		productEntity.setSendCycleUnit(DateTypeEnum.valueOf(sendCycleUnit));
    		productEntity.setProName(proName);
    		productEntity.setWeben(weben);
    		productEntity.setWebzh(webzh);
    		productEntity.getProductWantRentEntity().setRentperiod(rentperiod);
    		productEntity.getProductWantRentEntity().setRentperiodunit(DateTypeEnum.valueOf(rentperiodunit));
    		productEntity.getProductWantRentEntity().setMagdebrugBudget(magdebrugBudget);
    		productEntity.getProductWantRentEntity().setMagdebrugBudgetLimit(PriceUnitEnum.valueOf(magdebrugBudgetLimit));
    		productEntity.getProductWantRentEntity().setMagdebrugBudgetType(CurrencyEnum.valueOf(magdebrugBudgetType));
    		productEntity.getProductWantRentEntity().setStartRent(startRent); 

    		messageAuditService.update(productEntity);
    	}
  	 		
  	 		
   	 	Map<String, Object> result = new HashMap<String, Object>();
   	 	result.put("result", 1);
   	 	result.put("message", "操作成功");
   	 	return result;	
    }
    
    //修改商品维修
    @ResponseBody
    @RequestMapping(value="/update_editRepair_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editRepair_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
     	long brandId= ReqUtil.getLong(request, "brandId",0l);
     	String proNo = ReqUtil.getString(request, "proNo", "");
     	BigDecimal repairPrice = new BigDecimal(ReqUtil.getLong(request, "repairPrice", 0l));
     	String repairPriceUnit = ReqUtil.getString(request, "repairPriceUnit", "yuan");
     	String repairPriceType = ReqUtil.getString(request, "repairPriceType", "rmb");
     	String poption = ReqUtil.getString(request, "poption", "");
     	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
     	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
     	BigDecimal maintainPeriod = new BigDecimal(ReqUtil.getLong(request, "maintainPeriod", 0l));
     	String maintainPeriodunit = ReqUtil.getString(request, "maintainPeriodunit", "");
     	String proUnit = ReqUtil.getString(request, "proUnit", "");
     	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
     	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
     	int webzh =  ReqUtil.getInt(request, "webzh", 0);
     	int weben =  ReqUtil.getInt(request, "weben", 0);
     	int proStock =  ReqUtil.getInt(request, "proStock", 0);
     	int isTax =  ReqUtil.getInt(request, "isTax", 0);
     	String taxRate =  ReqUtil.getString(request, "taxRate", "");
     	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
     	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
     	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
     	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
     	String proSynopsis =  ReqUtil.getString(request,       "proSynopsis", "");
     	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
     	String proContent =  ReqUtil.getString(request, "proContent", "");
     	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
     	long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
     	String apply =  ReqUtil.getString(request, "apply", "");
     	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
     	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
     	String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
//     	BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
//     	String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");
     	Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
    	String fault = ReqUtil.getString(request, "fault", "");
    	String faultEn = ReqUtil.getString(request, "faultEn", "");
     	String maintaindemo = ReqUtil.getString(request, "maintaindemo", "");
     	String maintaindemoEn = ReqUtil.getString(request, "maintaindemoEn", "");
     	String brandName = ReqUtil.getString(request, "brandName", "");
     	String areaProvince = ReqUtil.getString(request, "areaProvince", "");
     	String areaCity = ReqUtil.getString(request, "areaCity", "");
     	String areaCountry = ReqUtil.getString(request, "areaCountry", "");
     	
    	ProductEntity productEntity = messageAuditService.find(id);
    	if(productEntity != null){
    		if(!brandName.isEmpty()){
    			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    			if(productBrandEntity != null){
    				productEntity.setProductBrandEntity(productBrandEntity);
    			}else{
    				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
    				productBrandEntity2.setName(brandName);
    				productBrandService.save(productBrandEntity2);
    				productEntity.setProductBrandEntity(productBrandEntity2);
    			}	
    		}
	    	productEntity.setApprStatus(1);
	    	productEntity.setRepairPeriod(repairPeriod);
	    	productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
	    	productEntity.setInforValidity(inforValidity);
	    	if(inforValidity !=null){
	    		Date date = new Date();
	    		Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
	    		productEntity.setInforNumber(date1);
	    	}
//	    	productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
	    	productEntity.setApply(apply);
	    	productEntity.setPrometaTitle(prometaTitle);
	    	productEntity.setProMetaKeywords(proMetaKeywords);
	    	productEntity.setPrometaDescription(prometaDescription);
	    	productEntity.setProOriginal1(proOriginal1);
		    productEntity.setProOriginal2(proOriginal2);
	     	productEntity.setProOriginal3(proOriginal3);
		    productEntity.setProOriginal4(proOriginal4);
		   	productEntity.setProSynopsis(proSynopsis);
		    productEntity.setProSynopsisEn(proSynopsisEn);
		    productEntity.setProContent(proContent);
		    productEntity.setProContentEn(proContentEn);
		    //行业分类
		    String tt1 ="";
		    if(indClassId==null){
		    	productEntity.setIndustryClass(null);
		    }else{
		    	for (int i = 0; i < indClassId.length; i++) {
		    		String string = indClassId[i];
		    		if(!string.isEmpty()){
		    			if(CommonUtil.isNumeric(string)==true){
		    				IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
		    				if(industryClassEntity !=null){
		    					tt1 +=industryClassEntity.getName()+",";
		    				}	
		    			}else{
		    				tt1 += indClassId[i]+",";
		    			}	
		    		}
		    	}
		    	if(tt1!=""){
		    		productEntity.setIndustryClass(tt1);
		    	}	
		    }
	    	ProductclassEntity productclassEntity = productclassService.find(proClassId);
	    	if(productclassEntity !=null){
	    		productEntity.setProductclass(productclassEntity);
	    	}
	    	productEntity.setIsTax(isTax);
	    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
	    	productEntity.setProStock(proStock);
	    	productEntity.setProownaudit(proownaudit);
	    	productEntity.setIsUnit(isUnit);
	     	if(isUnit==0){
	     		proUnit ="";
	     	}    	 
	     	productEntity.setAreaCity(areaCity);
	     	productEntity.setAreaCountry(areaCountry);
	     	productEntity.setAreaProvince(areaProvince);
	     	productEntity.setProductType(productTypeService.find(5l));
	     	productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
	     	productEntity.setProUnit(proUnit);
	    	productEntity.setProNo(proNo);
	    	productEntity.setProNameEn(proNameEn);
	    	productEntity.setPoption(poption);
	    	productEntity.setProName(proName);
	    	productEntity.setWeben(weben);
	    	productEntity.setWebzh(webzh);
	    	productEntity.getProductRepairEntity().setRepairPriceType(CurrencyEnum.valueOf(repairPriceType));
	    	productEntity.getProductRepairEntity().setRepairPrice(repairPrice);
	    	productEntity.getProductRepairEntity().setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
	    	productEntity.getProductRepairEntity().setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
	    	productEntity.getProductRepairEntity().setMaintainPeriod(maintainPeriod);
	    	productEntity.getProductRepairEntity().setFault(fault);
	    	productEntity.getProductRepairEntity().setFaultEn(faultEn);
	    	productEntity.getProductRepairEntity().setMaintaindemo(maintaindemo);
	    	productEntity.getProductRepairEntity().setMaintaindemoEn(maintaindemoEn);
       	 		
      	 	messageAuditService.update(productEntity);
    	}
  	 		
  	 		
   	 	Map<String, Object> result = new HashMap<String, Object>();
   	 	result.put("result", 1);
   	 	result.put("message", "操作成功");
   	 	return result;	
    }
    
    //修改商品求修
    @ResponseBody
    @RequestMapping(value="/update_editWantRepair_productAudit.jspx", method={RequestMethod.POST, RequestMethod.GET})
    public Map<String, Object> update_editWantRepair_productAudit(HttpServletRequest request,String[] indClassId,HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 0l);
    	String proName= ReqUtil.getString(request, "proName", "");
     	long brandId= ReqUtil.getLong(request, "brandId",0l);
     	String proNo = ReqUtil.getString(request, "proNo", "");
     	BigDecimal repairPrice = new BigDecimal(ReqUtil.getLong(request, "repairPrice", 0l));
     	String repairPriceUnit = ReqUtil.getString(request, "repairPriceUnit", "yuan");
     	String repairPriceType = ReqUtil.getString(request, "repairPriceType", "rmb");
     	String poption = ReqUtil.getString(request, "poption", "");
     	String prouseAddress = ReqUtil.getString(request, "prouseAddress", "");
     	int isUnit = ReqUtil.getInt(request, "isUnit", 0);
     	BigDecimal maintainPeriod = new BigDecimal(ReqUtil.getLong(request, "maintainPeriod", 0l));
     	String maintainPeriodunit = ReqUtil.getString(request, "maintainPeriodunit", "");
     	String proUnit = ReqUtil.getString(request, "proUnit", "");
     	String proNameEn =  ReqUtil.getString(request, "proNameEn", "");
     	int proownaudit =  ReqUtil.getInt(request, "proownaudit", 0);
     	int webzh =  ReqUtil.getInt(request, "webzh", 0);
     	int weben =  ReqUtil.getInt(request, "weben", 0);
     	int proStock =  ReqUtil.getInt(request, "proStock", 0);
     	int isTax =  ReqUtil.getInt(request, "isTax", 0);
     	String taxRate =  ReqUtil.getString(request, "taxRate", "");
     	String proOriginal1 =  ReqUtil.getString(request, "proOriginal1", "");
     	String proOriginal2 =  ReqUtil.getString(request, "proOriginal2", "");
     	String proOriginal3 =  ReqUtil.getString(request, "proOriginal3", "");
     	String proOriginal4 =  ReqUtil.getString(request, "proOriginal4", "");
     	String proSynopsis =  ReqUtil.getString(request,       "proSynopsis", "");
     	String proSynopsisEn =  ReqUtil.getString(request, "proSynopsisEn", "");
     	String proContent =  ReqUtil.getString(request, "proContent", "");
     	String proContentEn =  ReqUtil.getString(request, "proContentEn", "");
     	long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
     	String apply =  ReqUtil.getString(request, "apply", "");
     	String prometaTitle =  ReqUtil.getString(request, "prometaTitle", "");
     	String proMetaKeywords =  ReqUtil.getString(request, "proMetaKeywords", "");
     	String prometaDescription = ReqUtil.getString(request, "prometaDescription", "");
//     	BigDecimal inforValidity = new BigDecimal(ReqUtil.getLong(request, "inforValidity", 0l));
//     	String inforValidityUnit = ReqUtil.getString(request, "inforValidityUnit", "");
     	Date inforValidity = ReqUtil.getDate(request, "inforValidity", null);
    	String repairPeriod = ReqUtil.getString(request, "repairPeriod", "");
    	String repairPeriodUnit = ReqUtil.getString(request, "repairPeriodUnit", "");
    	String bugloko = ReqUtil.getString(request, "bugloko", "");
    	String buglokoEn = ReqUtil.getString(request, "buglokoEn", "");
    	String faultCause = ReqUtil.getString(request, "faultCause", "");
    	String faultCauseEn = ReqUtil.getString(request, "faultCauseEn", "");
    	String brandName = ReqUtil.getString(request, "brandName", "");
     	String prodNumber = ReqUtil.getString(request, "prodNumber", "");
    	String areaProvince = ReqUtil.getString(request, "areaProvince", "");
    	String areaCity = ReqUtil.getString(request, "areaCity", "");
    	String areaCountry = ReqUtil.getString(request, "areaCountry", "");
    	
    	ProductEntity productEntity = messageAuditService.find(id);
    	if(productEntity != null){	
    		if(!brandName.isEmpty()){
    			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    			if(productBrandEntity != null){
    				productEntity.setProductBrandEntity(productBrandEntity);
    			}else{
    				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
    				productBrandEntity2.setName(brandName);
    				productBrandService.save(productBrandEntity2);
    				productEntity.setProductBrandEntity(productBrandEntity2);
    			}	
    		}
	    	productEntity.setApprStatus(1);
	    	productEntity.setAreaCity(areaCity);
	    	productEntity.setAreaCountry(areaCountry);
	    	productEntity.setAreaProvince(areaProvince);
	    	productEntity.setRepairPeriod(repairPeriod);
	    	productEntity.setRepairPeriodUnit(DateTypeEnum.valueOf(repairPeriodUnit));
	    	productEntity.setInforValidity(inforValidity);
	    	if(inforValidity !=null){
	    		Date date = new Date();
	    		Integer date1 = (int)((inforValidity.getTime() - date.getTime())/1000/60/60/24);
	    		productEntity.setInforNumber(date1);
	    	}
//	    	productEntity.setInforValidityUnit(DateTypeEnum.valueOf(inforValidityUnit));
	    	productEntity.setApply(apply);
	    	productEntity.setPrometaTitle(prometaTitle);
	    	productEntity.setProMetaKeywords(proMetaKeywords);
	    	productEntity.setPrometaDescription(prometaDescription);
	    	productEntity.setProOriginal1(proOriginal1);
		    productEntity.setProOriginal2(proOriginal2);
	     	productEntity.setProOriginal3(proOriginal3);
		    productEntity.setProOriginal4(proOriginal4);
		   	productEntity.setProSynopsis(proSynopsis);
		    productEntity.setProSynopsisEn(proSynopsisEn);
		    productEntity.setProContent(proContent);
		    productEntity.setProContentEn(proContentEn);
		    //行业分类
		    String tt1 ="";
		    if(indClassId==null){
		    	productEntity.setIndustryClass(null);
		    }else{
		    	for (int i = 0; i < indClassId.length; i++) {
		    		String string = indClassId[i];
		    		if(!string.isEmpty()){
		    			if(CommonUtil.isNumeric(string)==true){
		    				IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(string));
		    				if(industryClassEntity !=null){
		    					tt1 +=industryClassEntity.getName()+",";
		    				}	
		    			}else{
		    				tt1 += indClassId[i]+",";
		    			}	
		    		}
		    	}
		    	if(tt1!=""){
		    		productEntity.setIndustryClass(tt1);
		    	}	
		    }
	    	ProductclassEntity productclassEntity = productclassService.find(proClassId);
	    	if(productclassEntity !=null){
	    		productEntity.setProductclass(productclassEntity);
	    	}
	    	productEntity.setIsTax(isTax);
	    	productEntity.setTaxRate(RateEnum.valueOf(taxRate));
	    	productEntity.setProStock(proStock);
	    	if(proStock == 1){
	    		productEntity.setProdNumber(prodNumber);
	    	}else{
	    		productEntity.setProdNumber("");
	    	}
	    	productEntity.setProownaudit(proownaudit);
	    	productEntity.setIsUnit(isUnit);
	     	if(isUnit==0){
	     		proUnit ="";
	     	}    	 
	     	productEntity.setProductType(productTypeService.find(6l));
	     	productEntity.setProuseAddress(ProuseAddressEnum.valueOf(prouseAddress));
	     	productEntity.setProUnit(proUnit);
	    	productEntity.setProNo(proNo);
	    	productEntity.setProNameEn(proNameEn);
	    	productEntity.setPoption(poption);
	    	productEntity.setProName(proName);
	    	productEntity.setWeben(weben);
	    	productEntity.setWebzh(webzh);
	    	productEntity.getProducWanRepairEntity().setRepairPriceType(CurrencyEnum.valueOf(repairPriceType));
	    	productEntity.getProducWanRepairEntity().setRepairPrice(repairPrice);
	    	productEntity.getProducWanRepairEntity().setRepairPriceUnit(PriceUnitEnum.valueOf(repairPriceUnit));
	    	productEntity.getProducWanRepairEntity().setMaintainPeriodunit(DateTypeEnum.valueOf(maintainPeriodunit));
	    	productEntity.getProducWanRepairEntity().setMaintainPeriod(maintainPeriod);
	    	productEntity.getProducWanRepairEntity().setBugloko(bugloko);
	    	productEntity.getProducWanRepairEntity().setBuglokoEn(buglokoEn);
	    	productEntity.getProducWanRepairEntity().setFaultCause(faultCause);
	    	productEntity.getProducWanRepairEntity().setFaultCauseEn(faultCauseEn);      	 		
      	 	messageAuditService.update(productEntity);
    	}
  	 		
  	 		
   	 	Map<String, Object> result = new HashMap<String, Object>();
   	 	result.put("result", 1);
   	 	result.put("message", "操作成功");
   	 	return result;	
    }
    
	//修改会员价格
	@RequestMapping(value="/edit_memberPrice.jspx", method={ RequestMethod.GET, RequestMethod.POST})
	public String edit_memberPrice(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		ProductEntity productEntity = messageAuditService.find(id);
		MemberEntity memberEntity = memberService.find(productEntity.getMemberEntity().getId());
		if(memberEntity !=null){
			model.addAttribute("item", productEntity);
			return TEM_PATH + "/memberPrice/edit_memberPrice";
		}else{
			return null;
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/save_edit_memberPrice.jspx", method={ RequestMethod.POST, RequestMethod.GET})
	public Map<String, Object> save_edit_memberPrice(HttpServletRequest request, HttpServletResponse response, Model model){
		Long id = ReqUtil.getLong(request, "id", 0l);
		//发货周期
		BigDecimal sendCycle = new BigDecimal(ReqUtil.getLong(request, "sendCycle", 0l));
		String sendCycleUnit = ReqUtil.getString(request, "sendCycleUnit", "");
		//来宾价格
		BigDecimal proCustomPrice = new BigDecimal(ReqUtil.getLong(request, "proCustomPrice", 0l));
		String proCustomPriceType = ReqUtil.getString(request, "proCustomPriceType", "");
		String proCustomPriceLimit = ReqUtil.getString(request, "proCustomPriceLimit", "");
		//客户价格
		BigDecimal proMemberPrice = new BigDecimal(ReqUtil.getLong(request, "proMemberPrice", 0l));
		String proMemberPriceType = ReqUtil.getString(request, "proMemberPriceType", "");
		String proMemberPriceLimit = ReqUtil.getString(request, "proMemberPriceLimit", "");
		//同行价格
		BigDecimal proPeer = new BigDecimal(ReqUtil.getLong(request, "proPeer", 0l));
		String proPeerType = ReqUtil.getString(request, "proPeerType", "");
		String proPeerLimit = ReqUtil.getString(request, "proPeerLimit", "");
		//VIP价格
		BigDecimal proVipPrice = new BigDecimal(ReqUtil.getLong(request, "proVipPrice", 0l));
		String proVipPriceType = ReqUtil.getString(request, "proVipPriceType", "");
		String proVipPriceLimit = ReqUtil.getString(request, "proVipPriceLimit", "");
		
		ProductEntity productEntity = messageAuditService.find(id);
		if(productEntity !=null){
			//发货周期
			productEntity.setSendCycle(sendCycle);
			productEntity.setSendCycleUnit(DateTypeEnum.valueOf(sendCycleUnit));
			//客户价格
			productEntity.setProMemberPrice(proMemberPrice);
			productEntity.setProMemberPriceType(CurrencyEnum.valueOf(proMemberPriceType));
			productEntity.setProMemberPriceLimit(PriceUnitEnum.valueOf(proMemberPriceLimit));
			//同行价格
			productEntity.setProPeer(proPeer);
			productEntity.setProPeerType(CurrencyEnum.valueOf(proPeerType));
			productEntity.setProPeerLimit(PriceUnitEnum.valueOf(proPeerLimit));
			//VIP价格
			productEntity.setProVipPrice(proVipPrice);
			productEntity.setProVipPriceType(CurrencyEnum.valueOf(proVipPriceType));
			productEntity.setProVipPriceLimit(PriceUnitEnum.valueOf(proVipPriceLimit));
			//来宾价格
			productEntity.setProCustomPrice(proCustomPrice);
			productEntity.setProCustomPriceType(CurrencyEnum.valueOf(proCustomPriceType));
			productEntity.setProCustomPriceLimit(PriceUnitEnum.valueOf(proCustomPriceLimit));
			messageAuditService.update(productEntity);
			}
		
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
	}
}
