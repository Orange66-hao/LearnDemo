package net.boocu.web.controller.admin.productmng;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;

/**
 * 商品管理
 * 
 * 
 * */
@Controller("productController")
@RequestMapping("/admin/productMng/product")
public class ProductController {

	@Resource
	private ProductService productService;
	@Resource
	private ProducttypeService producttypeService;
	
	/**
	 * 商品恢复页面
	 * */
	@RequestMapping(value="toLitterBox.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toLitterBox(HttpServletRequest request,HttpServletResponse response ,Model model){
		return "template/admin/litterBox/proBox/list";
	}
	
	
	/**所有被删除的商品*/
	@RequestMapping(value="delDatas.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public void toDelPro(HttpServletRequest request,HttpServletResponse response ,Model model){
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item.", "");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		pageable.getFilters().add(Filter.eq("isDel", 1));
		Page<ProductEntity> page=  productService.findPage(pageable);
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : page.getCont()){
			Map map = new HashMap<String,Object>();
			map.put("item", JsonUtil.getJsonObj(item));
			resultList.add(map);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}
	
	
	
	
	/**
	 * 商品恢复  
	 * */
	@RequestMapping(value="reverseDelPro.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public void reverseDelPro(HttpServletRequest request,HttpServletResponse response ,Model model){
		String[] strids =  request.getParameterValues("item.id");
		String err = "";
    	Long[] ids = new Long[strids.length];
    	for(int i=0;i<strids.length ;i++){
    		ids[i] = Long.parseLong(strids[i]);
    	}
    	Map<String,Object> result = new HashMap<String,Object>();
    	if(ids != null && ids.length>0){
    		if(productService.reversePro(ids, err)){
    			System.out.println("id:"+ids[0]);
    			result.put("result", 1);
    	    	result.put("message", "操作成功");
    		}else{
    			result.put("result", 0);
    	    	result.put("message", err);
    		}
    	}
    	
    	RespUtil.renderJson(response, result);
	}
	
    /**取得产品名称的集合*/
    @ResponseBody
    @RequestMapping(value = "/get_product_proNames.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getProNos(HttpServletRequest request, HttpServletResponse response, Model model,ProductEntity productEntity) {
    	List<ProductEntity> productEntities = productService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(ProductEntity item : productEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getProName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList) );
    
    }
    @RequestMapping(value="/list.jspx", method = {
			RequestMethod.POST, RequestMethod.GET })
	public String listDate(@RequestParam(required=false,defaultValue="1") int pageNum,//页码
			@RequestParam(required=false , defaultValue="7") int pageSize,//页记录数
 			HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
	/*	Pageable pageable = new Pageable(pageNum,pageSize);
		pageable.getFilters().add(Filter.eq("isDel", 0));
		model.addAttribute("page", productService.findPage(pageable));*/
		
		return "/template/admin/prodoctMng/list";
	}
	@RequestMapping(value="/data.json",method={RequestMethod.POST,RequestMethod.GET})
	public void dataJson(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		long productTypeId = ReqUtil.getLong(request, "productTypeId", 0l);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("productTypeId", productTypeId);
		Page<ProductEntity> page = productService.findListProductPage(pageable,params);
		List<ProductEntity> productSaleEntities = page.getCont();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductEntity item : productSaleEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			if(item.getMemberEntity() != null){
				map.put("id", item.getId());
				map.put("proName", item.getProName());
				map.put("brandName", item.getProductBrandEntity()==null?"":item.getProductBrandEntity().getName());
				map.put("proNo", item.getProNo());
				map.put("status", item.getStatus());
				map.put("prodNumber", item.getProdNumber());
				map.put("qualityStatus", item.getQualityStatus());
				resultList.add(map);
			}
		}
		result.put("total",page.getTotal() );
		System.out.println("page.getTotal().............."+page.getTotal());
		result.put("rows",resultList); 
		System.out.println("resultList+++++++++++"+resultList);
		RespUtil.renderJson(response, result);
	}
}
