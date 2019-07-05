package net.boocu.project.interceptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Sequencer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * @author fang
 *
 * 2015年9月9日   主要是向列表中输出同样的内容 
 */
public class ProductControllerInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	@Resource
	private ProductSaleService productSaleService;
	
	@Resource
	private SalesService salesService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private NewsService newsService;
	
	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private ProducttypeService producttypeService;
	
	@Resource
	private IndustryClassService industryClassService;
	
	@Resource
	private ProductBrandService productBrandService;
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView model) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("ProductControllerInterceptor--postHandle--拦截器");
/*		//促销图片
		model.addObject("imageList", salesService.findList(6));*/
		//促销图片
		List<Filter> saleFilters = new ArrayList<Filter>();
		Date date=new Date();
		saleFilters.add(Filter.eq("isDel", 0));
		saleFilters.add(Filter.eq("isPromSale", 1));
		saleFilters.add(Filter.eq("display", 1));
		saleFilters.add(Filter.ge("inforValidity", date));
		model.addObject("imageList", salesService.findList(6,saleFilters, Sequencer.desc("sort")));
		
		//商品类型 add by fang 201509
		model.addObject("proTypes", producttypeService.findList(null, Sequencer.asc("id")));
		
		//行业分类
		model.addObject("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
		
		//产品分类
		List<Filter> fList2 = new ArrayList<Filter>();
		fList2.add(Filter.eq("parentid", "01"));
		List<ProductclassEntity> list=null;
		try {
			list= (List<ProductclassEntity>) redisTemplate.boundValueOps("topProClasses").get();
			if(list==null){
                list= productclassService.findList(fList2, Sequencer.asc("id"));
                redisTemplate.boundValueOps("topProClasses").set(list);
            }
		} catch (Exception e) {
			e.printStackTrace();
			list= productclassService.findList(fList2, Sequencer.asc("id"));
		}

		model.addObject("topProClasses",list );
		
		//品牌
		List<Filter> brandFilters = new ArrayList<Filter>();
		brandFilters.add(Filter.eq("isDel", 0));
		brandFilters.add(Filter.eq("apprStatus", 1));
		model.addObject("brands", productBrandService.findList(11, brandFilters, Sequencer.asc("area")));
		
		/*Boolean isProductSearch = (Boolean) model.getModel().get("isProductSearch");
		Page<ProductEntity> pages =   (Page<ProductEntity>) model.getModel().get("page");
		if(isProductSearch != null && isProductSearch){
			model.addObject("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
			List<String> parentIndustryIds = new ArrayList<String>();
			for(ProductEntity productEnty : pages.getCont()){
				if(productEnty.getIndustryClass() != null && !"".equals( productEnty.getIndustryClass())){
					List<Map<String, Object>> parentIdList = industryClassService.getParentIds(productEnty.getIndustryClass().split(","));
					String parentIdsStr = "";
					for(Map<String, Object> map : parentIdList){
						parentIdsStr += map.get("parentId")+",";
					}
					parentIndustryIds.add(parentIdsStr);
				}
			}
			model.addObject("parentIndustryIds", parentIndustryIds);
		}*/
		System.out.println("end--拦截器");
		super.postHandle(request, response, handler, model);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		return super.preHandle(request, response, handler);
	}
	
}
