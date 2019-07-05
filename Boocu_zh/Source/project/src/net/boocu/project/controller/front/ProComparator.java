/*
 * Copyright 2015-2016 嘉惠捷科技. All rights reserved.
 * Support: 嘉惠捷科技
 */
package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.tool.xml.css.parser.state.Unknown;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.NewsService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.SalesService;
import net.boocu.project.service.SysMessageService;
import net.boocu.project.service.WorldAreaService;
import net.boocu.web.Filter;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberService;

/**
 * Controller -商品比较
 * 
 * @author deng
 * @version 1.0 20160519
 */
@Controller("ProCompartor")
@RequestMapping("/ProCompartor")
public class ProComparator {
	public static final String TEMPATH = "/template/front/comparator";

	@Resource
	ProductService productService;

	@Resource
	MemberService memberService;

	@Resource
	HelpService helpService;

	@Resource
	SalesService salesService;

	@Resource
	NewsService newsService;

	@Resource
	ProductclassService productclassService;

	@Resource
	ProductBrandService productBrandService;

	@Resource
	private WorldAreaService worldAreaService;

	@Resource
	private MessageService messageService;

	@RequestMapping(value = "list.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public String ProductCompartorList(HttpServletRequest request, HttpServletResponse response, Model model,
			Long[] ids) {

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		// 获取产品分类信息
		List<Map> products = getNodeData("01");
		model.addAttribute("products", products);

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

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

		for (int i = 0; i < ids.length; i++) {
			ProductEntity productEntity = productService.find(ids[i]);
			if (productEntity != null) {
				model.addAttribute("product" + i, productEntity);
			}
		}

		// 价格类型
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			MemberGradeEntity grader = memberService.getCurrent().getMemberGradeEntity();
			if (grader != null) {
				model.addAttribute("priceType", grader.getPriceType());
			}
		}

		return TEMPATH + "/comparator";
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

	/** 取得型号的集合 **/
	@ResponseBody
	@RequestMapping(value = "/get_proNo.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getProNo(HttpServletRequest request, HttpServletResponse response, Model model, Long brandId) {
		List<Map> resultList = new ArrayList<Map>();
		if (brandId != null) {
			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
			if (productBrandEntity != null) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.eq("apprStatus", 1));
				filters.add(Filter.eq("isDel", 0));
				filters.add(Filter.eq("productBrandEntity", productBrandEntity));
				List<ProductEntity> productEntities = productService.findList(filters);
				Set<String> hashset = new HashSet<String>();
				for (ProductEntity item : productEntities) {
					hashset.add(item.getProNo());
				}
				for (String item : hashset) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", item);
					map.put("text", item);
					resultList.add(map);
				}
			}
		}
		return resultList;
	}

	/** 过滤品牌和型号的商品 */
	@ResponseBody
	@RequestMapping(value = "/get_proCom.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> get_proCom(HttpServletRequest request, HttpServletResponse response, Model model,
			String proNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		Long brandId = ReqUtil.getLong(request, "brandId", 0l);
		if (brandId != null && brandId != 0l) {
			ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
			if (productBrandEntity != null && !proNo.isEmpty()) {
				ArrayList<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.eq("productBrandEntity", productBrandEntity));
				filters.add(Filter.eq("proNo", proNo));
				filters.add(Filter.eq("apprStatus", 1));
				filters.add(Filter.eq("isDel", 0));
				List<ProductEntity> productEntities = productService.findList(filters);
				if (productEntities.get(0) != null) {
					ProductEntity item = productEntities.get(0);
					// 获取所在地
					if (item.getAreaProvince() != null && !item.getAreaProvince().isEmpty()
							&& item.getProductType().getId() < 7) {
						Long provinceId = Long.parseLong(item.getAreaProvince());
						WorldAreaEntity worldAreaEntity = worldAreaService.find(provinceId);
						if (worldAreaEntity.getAREANAME().equals("天津市") || worldAreaEntity.getAREANAME().equals("北京市")
								|| worldAreaEntity.getAREANAME().equals("上海市")
								|| worldAreaEntity.getAREANAME().equals("重庆市")) {
							map.put("worldArea", worldAreaEntity.getAREANAME());
						} else {
							if (item.getAreaCity() != null && !item.getAreaCity().isEmpty()) {
								Long cityId = Long.parseLong(item.getAreaCity());
								WorldAreaEntity worldAreaEntity1 = worldAreaService.find(cityId);
								map.put("worldArea", worldAreaEntity1.getAREANAME());
							}
						}
					}
					String qualityStatus = "";
					if (item.getQualityStatus() != null) {
						switch (item.getQualityStatus()) {
						case all:
							qualityStatus = "全新";
							break;
						case nine:
							qualityStatus = "9成新";
							break;
						case eight:
							qualityStatus = "8成新";
							break;
						case seven:
							qualityStatus = "7成新";
							break;
						case six:
							qualityStatus = "6成新";
							break;
						case five:
							qualityStatus = "5成新";
							break;
						default:
							qualityStatus = "5成新以下";
							break;
						}
					}
					String status = "";
					if (item.getStatus() != null) {
						switch (item.getStatus()) {
						case fine:
							status = "完好";
							break;
						case bad:
							status = "破损";
							break;
						default:
							status = "未知";
							break;
						}
					}
					String taxRate = "";
					if (item.getTaxRate() != null && item.getIsTax() == 1) {
						switch (item.getTaxRate()) {
						case taxSpecialInvoice:
							taxRate = "17%增值税发票";
							break;
						case taxInvoice:
							taxRate = "3%增值税发票";
							break;
						default:
							taxRate = "3%普通发票";
							break;
						}
					}
					map.put("proOriginal", item.getProOriginal1());
					map.put("proName", item.getProName());
					map.put("brand", item.getProductBrandEntity().getName());
					map.put("proNo", item.getProNo());
					map.put("prodNumber", item.getProdNumber());
					map.put("id", item.getId());
					map.put("qualityStatus", qualityStatus);
					map.put("proVipPrice", item.getProVipPrice());
					map.put("proPeer", item.getProPeer());
					map.put("proMemberPrice", item.getProMemberPrice());
					map.put("proCustomPrice", item.getProCustomPrice());
					map.put("proVipPriceLimit", item.getProVipPriceLimit());
					map.put("proPeerLimit", item.getProPeerLimit());
					map.put("proMemberPriceLimit", item.getProMemberPriceLimit());
					map.put("proCustomPriceLimit", item.getProCustomPriceLimit());
					map.put("isTax", item.getIsTax());
					map.put("taxRate", taxRate);
					map.put("sendCycle", item.getSendCycle());
					map.put("sendCycleUnit", Unit(item.getSendCycleUnit()));
					map.put("repairPeriod", item.getRepairPeriod());
					map.put("repairPeriodUnit", Unit(item.getRepairPeriodUnit()));
					map.put("status", status);
					map.put("isUnit", item.getIsUnit());
					if (item.getProductType() != null) {
						map.put("type", item.getProductType().getTypeName());
					}
				}
			}
		}

		map.put("result", 1);
		map.put("message", "success");
		return map;
	}

	// 将日期转换为字符
	private String Unit(DateTypeEnum un) {
		String unit = "";
		if (un != null) {
			switch (un) {
			case day:
				unit = "天";
				break;
			case week:
				unit = "周";
				break;
			default:
				unit = "月";
				break;
			}
		}
		return unit;
	}

}
