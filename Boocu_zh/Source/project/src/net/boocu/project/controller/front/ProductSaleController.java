/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

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
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.enums.productType;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.LookValService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.WorldAreaService;
import net.boocu.project.util.OrderUtils;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.SysConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.support.json.JSONUtils;

/**
 * Controller -
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontProductSaleController")
@RequestMapping("/productSale")
public class ProductSaleController {

	private static final String TEMPATH = "/template/front/sale";

	@Resource
	private ProductSaleService productSaleService;

	@Resource
	private HelpService helpService;

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
	private MemberService memberService;

	@Resource
	private WorldAreaService worldAreaService;

	@Resource
	private FriendsService friendsService;
	
	@Resource
	private SysConfigService sysConfigService;

	// 服务类型ID
	private static final long TYPEID = Long.parseLong("" + productType.sale.getValue());

	@Resource
	private ProductBrandService productBrandService;
	
	@Resource
	private ProductBuyService proBuyService;
	
	@Autowired
	private LookValService lookValService;
	
	@RequestMapping(value="/getSaleNames.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public void getSaleNames(HttpServletRequest request, HttpServletResponse response, Model model,
			ProductSaleEntity saleEntity){
		String keyword = ReqUtil.getString(request, "keyword", "");
		int date = ReqUtil.getInt(request, "date", 0);
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("kw", keyword);
		
		if(date > 0){
			java.util.Date time = new java.util.Date(System.currentTimeMillis());	
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);
			calendar.add(Calendar.DAY_OF_MONTH,-date);
		
			time = calendar.getTime();
			params.put("date", time);
		}
		
		List<ProductSaleEntity> sale = productSaleService.getSaleInfo(params);
    	List<Map> resultList = new ArrayList<Map>();
    	for(ProductSaleEntity item : sale){
			if(item.getProductEntity() != null){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
	    		map.put("text", item.getProductEntity().getProductBrandEntity()==null?"":
	    			item.getProductEntity().getProductBrandEntity().getName()+item.getProductEntity().getProNo());
	    		resultList.add(map);
			}
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList) );
	}
	
	@RequestMapping(value="/saleMatchBuy.json",method={RequestMethod.POST,RequestMethod.GET})
	public void saleMatchBuy(HttpServletRequest request,HttpServletResponse response,Model model){
		
		//keyword是销售的id
		String keyword = ReqUtil.getString(request, "keyword", "0");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		Page<ProductBuyEntity> pse = null;
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		if("0".equals(keyword)){
			params.put("index", 0);
			//查询所有
			pse = proBuyService.getProductBuyPage(pageable, params);
		}else{
			//查询销售的品牌和型号
			ProductSaleEntity ps = productSaleService.find(Long.parseLong(keyword));
			
			/**
			 * 此处是求购匹配销售
			 * 匹配规则是 商品型号+品牌Id(productProNo+brandId)
			 */
			//拿到品牌和型号
			long brandId = ps.getProductEntity().getProductBrandEntity().getId();
			String proNo = ps.getProductEntity().getProNo();
			
			//根据品牌和型号分页查询查询销售信息
			params.put("brandId", brandId);
			params.put("proNo", proNo);
			params.put("index", 1);
			pse = proBuyService.getProductBuyPage(pageable, params);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(ProductBuyEntity item : pse.getCont()){
			if(item.getProductEntity() != null){
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("id", item.getId());
				map.put("name", item.getProductEntity().getProductBrandEntity()==null?"":
					item.getProductEntity().getProductBrandEntity().getName()+item.getProductEntity().getProNo());
				map.put("poption", item.getProductEntity().getPoption());
				map.put("qualityStatus", item.getProductEntity().getQualityStatus());
				map.put("repairPeriod", item.getProductEntity().getRepairPeriod());
				map.put("prouseAddress", item.getProductEntity().getProuseAddress());
				map.put("price", item.getProductEntity().getPrice());
				if(item.getProductEntity().getMemberEntity() != null){
					map.put("userName", item.getProductEntity().getMemberEntity().getUsername());
					map.put("linkUp", item.getProductEntity().getMemberEntity().getId()); //沟通
				}else{
					map.put("userName", "");
					map.put("linkUp", ""); //沟通
				}
				map.put("quote", item.getProductEntity().getId());  //报价
				resultList.add(map);
			}
		}
		result.put("total",pse.getTotal());
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	@RequestMapping(value = "list.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String index(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "7") int pageSize, // 页记录数
			@RequestParam(required = false) String popuOrder, // 人气排序
			@RequestParam(required = false) String priceOrder, // 价格排序
			@RequestParam(required = false) String keyword, // 搜索关键字
			@RequestParam(required = false) Long[] serTypeIds, // 服务类型
			@RequestParam(required = false) Long[] indClass, // 行业分类
			@RequestParam(required = false) Long[] proClass, // 产品分类
			@RequestParam(required = false) Long[] brands, // 品牌
			@RequestParam(required = false) String isSelf, // 是否自营
			@RequestParam(required = false) String isNew, // 是否全新
			@RequestParam(required = false) String brandste, // 是否全新
			@RequestParam(required = false) String proClasste, // 产品分类名称
			@RequestParam(required = false) String indClasste, // 行业分类名称
			@RequestParam(required = false) String serTypeIdste, // 服务类型名称
			HttpServletRequest request, HttpServletResponse response, Model model) {

		// 获取当前日期
		Date date = new Date();

		// 有效期
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.ge("inforValidity", date));
		filters.add(Filter.eq("longTerm", 1));

		Pageable pageable = new Pageable(pageNum, pageSize);
		pageable.getFilters().add(Filter.eq("productType", producttypeService.find(TYPEID)));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("webzh", 1));
		pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.or(filters));
		pageable.getFilters().add(Filter.eq("proownaudit", 1));
		
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 产品搜索 20160425
		Boolean isProductSearch = ReqUtil.getBoolean(request, "isProductSearch", Boolean.FALSE);
		Page<ProductEntity> pages;
		/*if (keyword != null && keyword != "") {
			// 条件放置集合
			Map con = new HashMap<String, Object>();
				con.put("keyword", keyword);
				con.put("popuOrder", popuOrder);
				con.put("priceOrder", priceOrder);
				con.put("isNew", isNew);
				con.put("isSelf", isSelf);
				con.put("isProductSearch", isProductSearch);
				pages = productService.findFrontProductPage(pageable, con);
		}else{
				pages = productService.findPage(pageable);
		}*/
		// 条件放置集合
					Map con = new HashMap<String, Object>();
					con.put("popuOrder", popuOrder);
					con.put("priceOrder", priceOrder);
					con.put("keyword", keyword);
					con.put("serTypeIds", serTypeIds);
					con.put("indClass", indClass);
					con.put("proClass", proClass);
					con.put("brands", brands);
					con.put("isSelf", isSelf);
					con.put("isNew", isNew);
					con.put("isProductSearch", isProductSearch);
						pages = productService.findFrontProductPage(pageable, con);
						

						if (brands != null && brands.length > 0) {
							model.addAttribute("brands2", brands[0]+"");
							model.addAttribute("brandste2", brandste);
						}
						if (proClass != null && proClass.length > 0) {
							model.addAttribute("proClass2", proClass[0]+"");
							model.addAttribute("proClasste2", proClasste);
						}
						if (indClass != null && indClass.length > 0) {
							model.addAttribute("indClass2", indClass[0]+"");
							model.addAttribute("indClasste2", indClasste);
						}
						if (serTypeIds != null && serTypeIds.length > 0) {
							model.addAttribute("serTypeIds2", serTypeIds[0]+"");
							model.addAttribute("serTypeIdste2", serTypeIdste);
						}
		model.addAttribute("page", pages);
		model.addAttribute("keyword", keyword);
		request.setAttribute("popuOrder", popuOrder);
		request.setAttribute("priceOrder", priceOrder);
		request.setAttribute("isNew", isNew);
		request.setAttribute("isSelf", isSelf);
		model.addAttribute("serTypeIds", serTypeIds);
		/*model.addAttribute("indClass", indClass);
		model.addAttribute("proClass", proClass);*/
		model.addAttribute("pageNum", pageNum);
		// 是否开始搜索 为了网站可以连接起来---seo add by fang 20160223
		model.addAttribute("isBegin", "1");
		
		model.addAttribute("isProductSearch", isProductSearch);
		if (isProductSearch) {
			model.addAttribute("topIndustryClasses", industryClassService.findList(Filter.eq("parentid", "1")));
			List<String> parentIndustryIds = new ArrayList<String>();
			for (ProductEntity productEnty : pages.getCont()) {
				if (productEnty.getIndustryClass() != null && !"".equals(productEnty.getIndustryClass())) {
					List<Map<String, Object>> parentIdList = industryClassService
							.getParentIds(productEnty.getIndustryClass().split(","));
					String parentIdsStr = "";
					for (Map<String, Object> map : parentIdList) {
						parentIdsStr += map.get("parentId") + ",";
					}
					parentIndustryIds.add(parentIdsStr);
				}
			}
			model.addAttribute("parentIndustryIds", parentIndustryIds);
		}

		// 存放服务类型id
		model.addAttribute("typeId", TYPEID);

		// 友情推荐
		model.addAttribute("friends", friendsService.findAll());

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		
		return TEMPATH + "/list_sale";
	}

	@RequestMapping(value = "/{id}.jhtml", method = { RequestMethod.POST, RequestMethod.GET })
	public String proSaleDetail(@PathVariable long id, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		ProductSaleEntity productSaleEntity = productSaleService.find(id);
		
		// 筛选当前商品分类的上级名称
		if (productSaleEntity.getProductEntity().getProductclass() != null
				&& !productSaleEntity.getProductEntity().getProductclass().getParentid().equals("01")
				&& !productSaleEntity.getProductEntity().getProductclass().getMenuid().equals("01")) {
			ProductclassEntity productclassEntity = productclassService
					.find(Filter.eq("menuid", productSaleEntity.getProductEntity().getProductclass().getParentid()));
			model.addAttribute("productClass2", productclassEntity);
			if(productclassEntity!=null){
				ProductclassEntity productclassEntity2 = productclassService
						.find(Filter.eq("menuid", productclassEntity.getParentid()));
				if (productclassEntity2 != null && !productclassEntity2.getMenuid().equals("01")
						&& !productclassEntity2.getParentid().equals("0")) {
					model.addAttribute("productClass1", productclassEntity2);
				}
			}
		}

		// 同类热门仪器
		if (productSaleEntity.getProductEntity().getProductclass() != null
				&& !productSaleEntity.getProductEntity().getProductclass().getParentid().equals("01")
				&& !productSaleEntity.getProductEntity().getProductclass().getMenuid().equals("01")) {
			List<Filter> proFilters = new ArrayList<Filter>();
			proFilters.add(Filter.eq("isDel", 0));
			proFilters.add(Filter.eq("productclass", productSaleEntity.getProductEntity().getProductclass()));
			List<ProductEntity> proclasses = productService.findList(50, proFilters, Sequencer.desc("lookTime"));
			List<ProductEntity> proclasses2 = new ArrayList<>();
			if (proclasses.size()<1) {
				model.addAttribute("proclasses", null);
			}else{
				
				//产生随机数
				for (int i = 0; i < 6; i++) {
					int x=(int)(Math.random()*proclasses.size());
					proclasses2.add(proclasses.get(x));
				}
				model.addAttribute("proclasses", proclasses2);
			}
		}
		// 获取所在地
		if (productSaleEntity.getProductEntity().getAreaProvince() != null
				&& !productSaleEntity.getProductEntity().getAreaProvince().equals("")) {
			Long provinceId = Long.parseLong(productSaleEntity.getProductEntity().getAreaProvince());

			WorldAreaEntity worldAreaEntity = worldAreaService.find(provinceId);
			if (worldAreaEntity.getAREANAME().equals("天津市") || worldAreaEntity.getAREANAME().equals("北京市")
					|| worldAreaEntity.getAREANAME().equals("上海市") || worldAreaEntity.getAREANAME().equals("重庆市")) {
				model.addAttribute("worldArea", worldAreaEntity.getAREANAME());
			} else {
				if (productSaleEntity.getProductEntity().getAreaCity() != null
						&& !productSaleEntity.getProductEntity().getAreaCity().equals("")
						&& CommonUtil.isNumeric(productSaleEntity.getProductEntity().getAreaCity()) == true) {
					Long cityId = Long.parseLong(productSaleEntity.getProductEntity().getAreaCity());
					WorldAreaEntity worldAreaEntity1 = worldAreaService.find(cityId);
					model.addAttribute("worldArea", worldAreaEntity1.getAREANAME());
				}
			}
		}
		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 获取产品分类信息
		List<Map> products = getNodeData("01");
		model.addAttribute("products", products);

		// 新闻资料
		List<Filter> newsFList = new ArrayList<Filter>();
		newsFList.add(Filter.eq("isrecommend", 1));
		model.addAttribute("newsList", newsService.findList(10, newsFList));

		/*
		 * //促销图片 model.addAttribute("imageList", salesService.findList(3));
		 */
		// 促销图片
		List<Filter> saleFilters = new ArrayList<Filter>();
		saleFilters.add(Filter.eq("isDel", 0));
		saleFilters.add(Filter.eq("isPromSale", 1));
		saleFilters.add(Filter.eq("display", 1));
		model.addAttribute("imageList", salesService.findList(3, saleFilters, Sequencer.desc("sort")));

		if (productSaleEntity != null) {
			// 浏览次数+1 add by fang 20150909
			productSaleEntity.getProductEntity().setLookTime(productSaleEntity.getProductEntity().getLookTime() + 1);
			productSaleEntity.getProductEntity().setLookDate(new Date());
			productSaleService.update(productSaleEntity);
			
			//保存每天的浏览量
			String sql = "INSERT INTO t_look_val(pro_id,brand_id,pro_no,brand_name,type) VALUES("+productSaleEntity.getProductEntity().getId()+","+
			productSaleEntity.getProductEntity().getProductBrandEntity().getId()+",'"+productSaleEntity.getProductEntity().getProNo()+
			"','"+productSaleEntity.getProductEntity().getProductBrandEntity().getName()+"',1)";
			lookValService.addLookVal(sql);
			
			//判断请求用户是否登录
			MemberEntity member = (MemberEntity) request.getSession().getAttribute("loginUser");
			if (member != null) {
				ProductEntity pe = new OrderUtils().DollarToRmb(member,productSaleEntity.getProductEntity(),sysConfigService);
				if(pe != null){
					productSaleEntity.setProductEntity(pe);
				}
				if(member.getMemberGradeEntity()!=null&&member.getMemberGradeEntity().getName().equals("内部员工")) {
					model.addAttribute("showIndustryClass", true);
				}else {
					model.addAttribute("showIndustryClass", false);
				}
			}
			model.addAttribute("productSale", productSaleEntity);
		}
		// 价格类型
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			MemberGradeEntity grader = memberService.getCurrent().getMemberGradeEntity();
			if (grader != null) {
				model.addAttribute("priceType", grader.getPriceType());
			}
		}

		return TEMPATH + "/list_sale_details";
	}

	// 递归取得商品分类信息
	public List<Map> getNodeData(String rootId) {
		ProductclassEntity topNode = productclassService.find(Filter.eq("menuid", rootId));
		if (topNode == null) {
			return null;
		}
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getMenuid()));
		List<ProductclassEntity> items = productclassService.findList(flist, Sequencer.asc("sort"));
		for (ProductclassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			if ("0".equals(item.getLeaf())) {
				map.put("children", getNodeData(item.getMenuid()));
			}
			resultList.add(map);
		}
		return resultList;
	}
}