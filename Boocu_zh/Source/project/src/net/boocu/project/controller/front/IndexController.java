/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.WebUtils;
import net.boocu.project.entity.*;
import net.boocu.project.service.AdvertIndexService;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.RecruitService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.interceptor.MemberInterceptor;
import net.boocu.web.service.MemberService;
import net.boocu.web.shiro.ShiroPrincipal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller -
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontIndexController")
public class IndexController {

	private static final int PAGE_ITEM_COUNT = PageItemName.values().length;

	public enum PageItemName {
		RECOMMEND_HARD, HOT, FREE, DISCOUNT, NEW, RECOMMEND_MAGAZINE;
		public int getIndex() {
			return ordinal();
		}
	}
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@Resource
	private NewsService newsService;

	@Resource
	private ProductService productService;

	@Resource
	private ProductSaleService productSaleService;

	@Resource
	private ProductBuyService productBuyService;

	@Resource
	private ProductRentService productRentService;

	@Resource
	private ProductWantRentService productWantRentService;

	@Resource
	private ProductRepairService productRepairService;

	@Resource
	private ProducWantRepairService productWantRepairService;

	@Resource
	private AdvertIndexService advertIndexService;

	@Resource
	private RecruitService recruitService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private SalesService salesService;

	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private HelpService helpService;

	@Resource
	private FriendsService friendsService;
	@Resource
	private MemberService memberService;
	@Resource
	private MessageService messageService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "", "/index.jhtml", "/index" }, method = { RequestMethod.POST, RequestMethod.GET })
	public String index(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam(required = false, defaultValue = "0") Long prodId) {
		String email = ReqUtil.getString(request, "email", "");

		if (email != null && email != "") {
			MemberEntity memberEntity1 = memberService.find(Filter.eq("email", email));
			if (memberEntity1 != null) {
				memberEntity1.setEnabled(2);
				memberService.update(memberEntity1);
			}
			// 重构Session
			WebUtils.refactorSession(request);
			HttpSession session = request.getSession();

			// 判断会员是否已登录
			if (memberService.authorized()) {
				session.removeAttribute(memberEntity1.PRINCIPAL_ATTR_NAME);
				WebUtils.removeCookie(request, response, memberEntity1.USERNAME_COOKIE_NAME);
			}
			if(memberEntity1!=null) {
				session.setAttribute(memberEntity1.PRINCIPAL_ATTR_NAME,
						new ShiroPrincipal(memberEntity1.getId(), memberEntity1.getUsername()));
				WebUtils.addCookie(request, response, memberEntity1.USERNAME_COOKIE_NAME, memberEntity1.getUsername());
			}
		}

		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		List<Map> products = null;
		try {
			products = (List<Map>)redisTemplate.boundValueOps("products").get();
		} catch (Exception e) {
			e.printStackTrace();
			products = getNodeData("01");
		}
		// 获取产品分类信息
		if(products==null){
			products = getNodeData("01");
			redisTemplate.boundValueOps("products").set(products);
		}
		model.addAttribute("products", products);

		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		request.getSession().setAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		String strListUrl = "";
		// model.addAttribute("page",
		// productSaleService.findFrontProductSalePage(pageable,con));

		List<NewsEntity> newsList= null;
		try {
			newsList = (List<NewsEntity>)redisTemplate.boundValueOps("newsList").get();
		} catch (Exception e) {
			e.printStackTrace();
			List<Filter> newsFList = new ArrayList<Filter>();
			newsFList.add(Filter.eq("isrecommend", 1));
			newsList=newsService.findList(10, newsFList, Sequencer.desc("sort"));
		}
		if(newsList==null){
			// 新闻资料
			List<Filter> newsFList = new ArrayList<Filter>();
			newsFList.add(Filter.eq("isrecommend", 1));
			newsList=newsService.findList(10, newsFList, Sequencer.desc("sort"));
			redisTemplate.boundValueOps("newsList").set(newsList);
		}
		model.addAttribute("newsList", newsList);

		Map con = new HashMap<String, Object>();
		if (prodId != 0l) {
			con.put("proClass", new Long[] { prodId });
		}
		// 销售信息
		con.put("serTypeIds", new Long[] { 1l });
		Page<ProductEntity> p = productService.findFrontProductPage(new Pageable(1, 8), con);
		model.addAttribute("proSaleList", productService.findFrontProductPage(new Pageable(1, 8), con));
		// model.addAttribute("proSaleList", productSaleService.findList(8));

		// 求购信息
		// model.addAttribute("proBuyList", productBuyService.findList(8));
		con.put("serTypeIds", new Long[] { 2l });
		model.addAttribute("proBuyList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 租赁信息
		// model.addAttribute("proRentList", productRentService.findList(8));
		con.put("serTypeIds", new Long[] { 3l });
		model.addAttribute("proRentList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 求租信息
		// model.addAttribute("proWantRentList",
		// productWantRentService.findList(8));
		con.put("serTypeIds", new Long[] { 4l });
		model.addAttribute("proWantRentList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 维修信息
		// model.addAttribute("proRepairList",
		// productRepairService.findList(8));
		con.put("serTypeIds", new Long[] { 5l });
		model.addAttribute("proRepairList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 求修信息
		// model.addAttribute("proWantRepairList",
		// productWantRepairService.findList(8));
		con.put("serTypeIds", new Long[] { 6l });
		model.addAttribute("proWantRepairList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 自动化测试方案
		con.put("serTypeIds", new Long[] { 7l });
		model.addAttribute("proAutoTestList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 自动化测试需求
		con.put("serTypeIds", new Long[] { 8l });
		model.addAttribute("projectNeedList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 产品测试
		con.put("serTypeIds", new Long[] { 11l });
		model.addAttribute("productTestList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 测试需求
		con.put("serTypeIds", new Long[] { 12l });
		model.addAttribute("proRequireTestList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 自动化测试需求
		con.put("serTypeIds", new Long[] { 13l });
		model.addAttribute("proCalibrationList", productService.findFrontProductPage(new Pageable(1, 8), con));

		// 广告信息
		List<AdvertIndexEntity> adverts = null;
		try {
			adverts = (List<AdvertIndexEntity>)redisTemplate.boundValueOps("adverts").get();
		} catch (Exception e) {
			e.printStackTrace();
			adverts=advertIndexService.findList(5);
		}
		if(adverts==null){
			adverts=advertIndexService.findList(5);
			redisTemplate.boundValueOps("adverts").set(adverts);
		}
		model.addAttribute("adverts",adverts);
		// 企业招聘
		List<RecruitEntity> recruitList = null;
		try {
			recruitList = (List<RecruitEntity>)redisTemplate.boundValueOps("recruitList").get();
		} catch (Exception e) {
			e.printStackTrace();
			List<Filter> recruitFList = new ArrayList<Filter>();
			recruitFList.add(Filter.eq("status", 1));
			recruitList = recruitService.findList(10, recruitFList, Sequencer.desc("sort"));
		}
		if(recruitList==null){
			List<Filter> recruitFList = new ArrayList<Filter>();
			recruitFList.add(Filter.eq("status", 1));
			recruitList = recruitService.findList(10, recruitFList, Sequencer.desc("sort"));
			redisTemplate.boundValueOps("recruitList").set(recruitList);
		}
		model.addAttribute("recruitList",recruitList );


		// 促销图片
		List<Filter> saleFilters = new ArrayList<Filter>();
		saleFilters.add(Filter.eq("isDel", 0));
		saleFilters.add(Filter.eq("isPromSale", 1));
		saleFilters.add(Filter.eq("display", 1));
		model.addAttribute("sales", salesService.findList(saleFilters, Sequencer.desc("sort")));

		// 品牌推荐 add by fang 20150906
		List<ProductBrandEntity> brands = null;
		try {
			brands = (List<ProductBrandEntity>)redisTemplate.boundValueOps("brands").get();
		} catch (Exception e) {
			e.printStackTrace();
			List<Filter> brandFilters = new ArrayList<Filter>();
			brandFilters.add(Filter.eq("isRecommend", 1));
			brands = productBrandService.findList(14, brandFilters, Sequencer.desc("sort"));
		}
		if(brands==null){
			List<Filter> brandFilters = new ArrayList<Filter>();
			brandFilters.add(Filter.eq("isRecommend", 1));
			brands = productBrandService.findList(14, brandFilters, Sequencer.desc("sort"));
			redisTemplate.boundValueOps("brands").set(brands);
		}
		model.addAttribute("brands",brands);

		// 友情推荐 add by fang 20150906
		List<FriendsEntity> friends = null;
		try {
			friends = (List<FriendsEntity>)redisTemplate.boundValueOps("friends").get();
		} catch (Exception e) {
			e.printStackTrace();
			friends = friendsService.findAll();
		}
		if(friends==null){
			friends = friendsService.findAll();
			redisTemplate.boundValueOps("friends").set(friends);
		}
		model.addAttribute("friends", friends);
		// 帮助信息
		List<HelpEntity> helps= null;
		try {
			helps = (List<HelpEntity>)redisTemplate.boundValueOps("helps").get();
		} catch (Exception e) {
			e.printStackTrace();
			helps = helpService.findAll();
		}
		if(helps==null){
			helps = helpService.findAll();
			redisTemplate.boundValueOps("helps").set(helps);
		}
		model.addAttribute("helps",helps);

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		// 价格类型
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			MemberGradeEntity grader = memberService.getCurrent().getMemberGradeEntity();
			if (grader != null) {
				model.addAttribute("priceType", grader.getPriceType());
			}
		}

		return "/template/front/index/index";
	}
	public static void main(String[] args) {
		System.out.println(MemberInterceptor.class.getName() + ".PRINCIPAL");
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