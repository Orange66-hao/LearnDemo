/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.CalibrationEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.enums.productType;
import net.boocu.project.service.CalibrationService;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.service.WorldAreaService;
import net.boocu.project.util.OrderUtils;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.SysConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller -产品测试 add by deng
 * 
 * @author deng
 * @version 1.0 20150119
 */
@Controller("frontProductCalibrationController")
@RequestMapping("/productCalibration")
public class ProductCalibrationController {

	private static final String TEMPATH = "/template/front/calibration";

	@Resource
	private CalibrationService calibrationService;

	@Resource
	private HelpService helpService;

	@Resource
	private ProducttypeService producttypeService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private ProductService productService;

	@Resource
	private NewsService newsService;

	@Resource
	private SalesService salesService;

	@Resource
	private MemberService memberService;

	@Resource
	private WorldAreaService worldAreaService;

	@Resource
	private InstrumentService instrumentService;

	@Resource
	private FriendsService friendsService;

	@Resource
	private IndustryClassService industryClassService;
	
	@Resource
	private SysConfigService sysConfigService;
	
	@Resource
	private MessageService messageService;
	
	//服务类型ID
	private static final long TYPEID  = Long.parseLong(""+productType.calibration.getValue());
	
	@RequestMapping( value="/list.jspx",method = {
			RequestMethod.POST, RequestMethod.GET })
	public String index(@RequestParam(required=false,defaultValue="1") int pageNum,//页码
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
		//获取当前日期
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
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		// 友情推荐
		model.addAttribute("friends", friendsService.findAll());

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		model.addAttribute("page", productService.findPage(pageable));
		model.addAttribute("isBegin", "1");
		// 存放服务类型id
		model.addAttribute("typeId", TYPEID);
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
		return TEMPATH + "/list_calibration";
	}

	@RequestMapping(value = "/{id}.jhtml", method = { RequestMethod.POST, RequestMethod.GET })
	public String proBuyDetail(@PathVariable long id, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		CalibrationEntity calibrationEntity = calibrationService.find(id);
		if (calibrationEntity != null) {
			calibrationEntity.getProductEntity().setLookTime(calibrationEntity.getProductEntity().getLookTime() + 1);
			calibrationService.update(calibrationEntity);
			
			//判断请求用户是否登录
			MemberEntity member = (MemberEntity) request.getSession().getAttribute("loginUser");
			ProductEntity pe = new OrderUtils().DollarToRmb(member,calibrationEntity.getProductEntity(),sysConfigService);
			if(pe != null){
				calibrationEntity.setProductEntity(pe);
			}
			model.addAttribute("calibrationEntity", calibrationEntity);
			
			//仪器列表
			List<InstrumentEntity> instrumentEntities = instrumentService.findList(Filter.eq("calibration", calibrationEntity));
			model.addAttribute("instrument", instrumentEntities);

			if (instrumentEntities.size() != 0 && instrumentEntities != null) {
				InstrumentEntity instrumentEntity = instrumentEntities.get(0);
				if (instrumentEntity != null) {
					if (instrumentEntity.getProductclass() != null
							&& !instrumentEntity.getProductclass().getParentid().equals("01")
							&& !instrumentEntity.getProductclass().getMenuid().equals("01")) {
						List<Filter> proFilters = new ArrayList<Filter>();
						proFilters.add(Filter.eq("isDel", 0));
						proFilters.add(Filter.eq("productclass", instrumentEntity.getProductclass()));
						List<ProductEntity> proclasses = productService.findList(6, proFilters,
								Sequencer.desc("lookTime"));
						model.addAttribute("proclasses", proclasses);
					}
				}
			}
		}

		// 筛选当前商品分类的上级名称
		if (calibrationEntity.getProductEntity().getProductclass() != null) {
			if (!calibrationEntity.getProductEntity().getProductclass().getParentid().equals("01")
					&& !calibrationEntity.getProductEntity().getProductclass().getMenuid().equals("01")) {
				ProductclassEntity productclassEntity = productclassService.find(
						Filter.eq("menuid", calibrationEntity.getProductEntity().getProductclass().getParentid()));
				model.addAttribute("productClass2", productclassEntity);
				ProductclassEntity productclassEntity2 = productclassService
						.find(Filter.eq("menuid", productclassEntity.getParentid()));
				if (productclassEntity2 != null && !productclassEntity2.getMenuid().equals("01")
						&& !productclassEntity2.getParentid().equals("0")) {
					model.addAttribute("productClass1", productclassEntity2);
				}
			}
		}

		// 获取所在地
		if (calibrationEntity.getProductEntity().getAreaProvince() != null
				&& !calibrationEntity.getProductEntity().getAreaProvince().equals("")) {
			Long provinceId = Long.parseLong(calibrationEntity.getProductEntity().getAreaProvince());

			WorldAreaEntity worldAreaEntity = worldAreaService.find(provinceId);
			if (worldAreaEntity.getAREANAME().equals("天津市") || worldAreaEntity.getAREANAME().equals("北京市")
					|| worldAreaEntity.getAREANAME().equals("上海市") || worldAreaEntity.getAREANAME().equals("重庆市")) {
				model.addAttribute("worldArea", worldAreaEntity.getAREANAME());
			} else {
				if (calibrationEntity.getProductEntity().getAreaCity() != null
						&& !calibrationEntity.getProductEntity().getAreaCity().equals("")) {
					Long cityId = Long.parseLong(calibrationEntity.getProductEntity().getAreaCity());
					WorldAreaEntity worldAreaEntity1 = worldAreaService.find(cityId);
					model.addAttribute("worldArea", worldAreaEntity1.getAREANAME());
				}

			}
		}

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

		// 促销信息
		List<Filter> saleFilters = new ArrayList<Filter>();
		saleFilters.add(Filter.eq("isDel", 0));
		saleFilters.add(Filter.eq("isPromSale", 1));
		saleFilters.add(Filter.eq("display", 1));
		model.addAttribute("imageList", salesService.findList(3, saleFilters, Sequencer.desc("sort")));

		// 价格类型
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			MemberGradeEntity grader = memberService.getCurrent().getMemberGradeEntity();
			if (grader != null) {
				model.addAttribute("priceType", grader.getPriceType());
			}
			if(memberEntity.getMemberGradeEntity()!=null&&memberEntity.getMemberGradeEntity().getName().equals("内部员工")) {
				model.addAttribute("showIndustryClass", true);
			}else {
				model.addAttribute("showIndustryClass", false);
			}
		}

		return TEMPATH + "/list_calibration_details";
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