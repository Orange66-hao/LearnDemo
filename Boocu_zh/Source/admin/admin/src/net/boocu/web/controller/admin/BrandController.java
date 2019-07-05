package net.boocu.web.controller.admin;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.BasedataEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.BasedataService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.impl.ProductBrandServiceImpl;
import net.boocu.web.*;

import net.boocu.web.enums.MessageTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;


/**
 * 商品销售
 * @author fang
 *
 * 2015年8月7日
 */
@Controller("brandController")
@RequestMapping("/admin/basedata/brand")
public class BrandController {
	
	private static final String TEM_PATH ="/template/admin/basedata/brand";
	
	@Resource
	private ProductBrandService productBrandService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private InstrumentService instrumentService;
	
	@Resource
	private BasedataService basedataService;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/brandlist";
	}
	
	@RequestMapping(value="audit.json",method={RequestMethod.POST,RequestMethod.GET})
	public void audit(HttpServletRequest request,HttpServletResponse response,Model model){
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
		
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%"+keyword+"%"));
			List<Filter> flist1 = new ArrayList<Filter>();
			flist1.add(Filter.like("nameEn", "%"+keyword+"%"));

            pageable.getFilters().add(Filter.or(flist));
			pageable.getFilters().add(Filter.or(flist1));
		}

		//pageable.getFilters().add(Filter.eq("apprStatus", 1));
		//pageable.getFilters().add(Filter.eq("isDel", 0));
        Page<Map<String, Object>> page = productBrandService.findPage(pageable, request);
        List<Map<String, Object>> cont = page.getCont();
        List<Map> resultList = new ArrayList<Map>();
        for (Map<String, Object> item : cont) {
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("id", item.get("id"));
            map.put("name", item.get("name"));
            map.put("nameEn", item.get("name_en"));
            map.put("country", item.get("country"));
            map.put("image", item.get("image"));
            map.put("sort", item.get("sort"));
            map.put("area", item.get("area"));
            map.put("isRecommend", Integer.parseInt(item.get("is_recommend").toString())==1?"显示":"不显示");
            map.put("status", Integer.parseInt(item.get("status").toString())==1?"启用":"停用");
            resultList.add(map);
        }
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	@RequestMapping(value="nAudit.json",method={RequestMethod.POST,RequestMethod.GET})
	public void nAudit(HttpServletRequest request,HttpServletResponse response,Model model){
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
		
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("apprStatus", 0));
		Page<ProductBrandEntity> page = productBrandService.findPage(pageable);
		List<ProductBrandEntity> productBrandEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(ProductBrandEntity item : productBrandEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("nameEn", item.getNameEn());
			map.put("country", item.getCountry());
			map.put("image", item.getImage());
			map.put("sort", item.getSort());
			map.put("area", item.getArea());
			map.put("isRecommend", item.getIsRecommend()==1?"显示":"不显示");
			map.put("status", item.getStatus()==1?"启用":"停用");
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}
	
	//跳转到村实体添加页面
    @RequestMapping(value = "/add_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String addAdvert(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/brand_add";
    } 
    
    /**
     * 
     * 方法:将新添加的BrandEntity（本身实体 ）保存到数据库中
     * 传入参数:BrandEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台BrandEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	String nameEn= ReqUtil.getString(request, "nameEn", "");
    	String image = ReqUtil.getString(request, "image", "");
    	String country = ReqUtil.getString(request, "country", "");
    	int sort = ReqUtil.getInt(request, "sort", 1);
    	String area = ReqUtil.getString(request, "area", "");
    	int isRecommend = ReqUtil.getInt(request, "isRecommend", 0);
    	int status = ReqUtil.getInt(request, "status", 0);
    	
    	ProductBrandEntity productBrandEntity = new ProductBrandEntity();
    	productBrandEntity.setName(name);
    	productBrandEntity.setNameEn(nameEn);
    	productBrandEntity.setImage(image);
    	productBrandEntity.setCountry(country);
    	productBrandEntity.setSort(sort);
    	productBrandEntity.setArea(area);
    	productBrandEntity.setIsRecommend(isRecommend);
    	productBrandEntity.setStatus(status);
    	productBrandEntity.setApprStatus(1);
    	
    	productBrandService.save(productBrandEntity);
    	
    	/*//自动添加英文版
    	if (nameEn != null && nameEn != "") {
    		ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
    		productBrandEntity2.setName(nameEn);
    		productBrandEntity2.setNameEn(nameEn);
    		productBrandEntity2.setImage(image);
    		productBrandEntity2.setCountry(country);
    		productBrandEntity2.setSort(sort);
    		productBrandEntity2.setArea(area);
    		productBrandEntity2.setIsRecommend(isRecommend);
    		productBrandEntity2.setStatus(status);
    		productBrandEntity2.setApprStatus(1);
    		
    		productBrandService.save(productBrandEntity2);
		}*/
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中BrandEntity（广告本身实体 ）并更新数据库
     */
    @RequestMapping(value = "/delete_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteBrand(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		//遍历品牌表里的 id
    		for(Long brandId :id){
    			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    			//如果品牌不为空
    			if(productBrandEntity!=null){
    				//判断商品表里是否有这个品牌
        			List<ProductEntity> productEntities = productService.findList(Filter.eq("productBrandEntity", productBrandEntity));
        			//判断仪器表里是否有这个品牌
        			List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("productBrandEntity", productBrandEntity));
        			
        			List<BasedataEntity> basedataEntities = basedataService.findList(Filter.eq("productBrandEntity", productBrandEntity));
        			//如果过滤的品牌在商品表里不为空
        			if(productEntities.size()>0 && productEntities!=null){
        				for(ProductEntity productEntity : productEntities){
        					if(productEntity!=null){
            					productEntity.setIsDel(1);
            					productService.update(productEntity);	
        					}
        				}
        			}
        			//如果仪器表里的这个品牌不为空
        			if(instrumentEntities.size()>0){
        				for(InstrumentEntity instrumentEntity : instrumentEntities){
        					if(instrumentEntity!=null){
            					instrumentEntity.setIsDel(1);
            					instrumentService.update(instrumentEntity);	
        					}
        				}
        			}
        			//如果资料库里的这个品牌不为空
        			if(basedataEntities.size()>0){
        				for(BasedataEntity basedataEntity : basedataEntities){
        					if(basedataEntity!=null){
        						basedataEntity.setIsDel(1);
            					basedataService.update(basedataEntity);
        					}
        				}
        			}
        			productBrandEntity.setIsDel(1);
        			productBrandService.update(productBrandEntity);
    			}
    		}
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "删除成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	ProductBrandEntity item = new ProductBrandEntity();

		if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		item = this.productBrandService.find(lid);
    		model.addAttribute("item", item);
    		System.out.println(item.getCountry());
    	}
    	
    	return TEM_PATH+"/brand_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的BrandEntity（村本身实体 ）
     * 传入参数:BrandEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台BrandEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_brand.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditBrand(HttpServletRequest request, HttpServletResponse response, Model model,ProductBrandEntity productBrandEntity) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	ProductBrandEntity productBrandEntityOle = productBrandService.find(id);
    	if(productBrandEntityOle != null){
    		productBrandEntityOle.setName(productBrandEntity.getName());
    		productBrandEntityOle.setNameEn(productBrandEntity.getNameEn());
    		productBrandEntityOle.setImage(productBrandEntity.getImage());
    		productBrandEntityOle.setCountry(productBrandEntity.getCountry());
    		productBrandEntityOle.setSort(productBrandEntity.getSort());
    		productBrandEntityOle.setArea(productBrandEntity.getArea());
    		productBrandEntityOle.setIsRecommend(productBrandEntity.getIsRecommend());
    		productBrandEntityOle.setStatus(productBrandEntity.getStatus());
    		productBrandService.update(productBrandEntityOle);
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }

    /**取得品牌名的集合*/
    @ResponseBody
	@RequestMapping(value = "/get_brand_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getBrandNames(HttpServletRequest request, HttpServletResponse response, Model model,ProductBrandEntity productBrandEntity) {
    	List<ProductBrandEntity> productBrandEntities = productBrandService.findList(Filter.eq("apprStatus", 1),Filter.eq("isDel", 0));
    	List<Map> resultList = new ArrayList<Map>();
    	for(ProductBrandEntity item : productBrandEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		map.put("nameEn",item.getNameEn());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList) );
    
    }
    
    //审核
    @ResponseBody
    @RequestMapping(value= "/auditBrand.jspx", method={ RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> auditBrand(HttpServletRequest request, HttpServletResponse response, Model model){
    	String[] idString = request.getParameterValues("id");
    	Long[] ids = new Long[idString.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idString[i]);
    	}
    	
    	for(Long id : ids){
    		ProductBrandEntity productBrandEntity = productBrandService.find(id);
    		productBrandEntity.setApprStatus(1);
    		productBrandService.update(productBrandEntity);
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "审核成功");
    	return result;
    }
    
    //反审核
    @ResponseBody
    @RequestMapping(value= "/nAuidt_brand.jspx", method={RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> nAuditBrand(HttpServletRequest request, HttpServletResponse response, Model model){
    	String[] idsString = request.getParameterValues("id");
    	Long[] ids = new Long[idsString.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idsString[i]);
    	}
    	for(Long id : ids){
    		ProductBrandEntity productBrandEntity = productBrandService.find(id);
    		productBrandEntity.setApprStatus(0);
    		productBrandService.update(productBrandEntity);
    	}
    	
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "反审核成功");
    	return result;
    }
	// 导出excel表
	@ResponseBody
	@RequestMapping(value = "/orderExport.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void orderExport(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		productBrandService.orderExport(request, session, response);
	}
}
