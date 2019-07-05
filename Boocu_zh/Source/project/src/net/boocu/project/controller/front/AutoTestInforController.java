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

import net.boocu.framework.controller.BaseController;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.bean.InstrumentsBean;
import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.InstrumentEntity;
import net.boocu.project.entity.ModuleEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.enums.productType;
import net.boocu.project.service.AutoTestService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.InstrumentService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Pageable;
import net.boocu.web.controller.common.CommonUtil;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 产品信息自动化测试
 * 
 * @author deng 20160122
 * @version 1.0
 */
@Controller("autoTestInforController")
public class AutoTestInforController extends BaseController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/productInforManage/autoTest";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");

	/** 错误信息 */
	private static final Message ERROR_MESSAGE = Message.error("错误信息!");
	@Resource
	private MessageService messageService;
	@Resource
	AutoTestService autoTestService;

	@Resource
	ProductService productService;

	@Resource
	MemberService memberService;

	@Resource
	HelpService helpService;

	@Resource
	ProductBrandService productBrandService;

	@Resource
	ProducttypeService productTypeService;

	@Resource
	ProductclassService productClassService;

	@Resource
	IndustryClassService industryClassService;

	@Resource
	InstrumentService instrumentService;

	@ModelAttribute
	public void getAutoTest(@RequestParam(required = false, defaultValue = "0") Long id, Model model) {
		AutoTestEntity autoTestEntity = autoTestService.find(id);
		if (autoTestEntity != null) {
			model.addAttribute("editAutoTest", autoTestEntity);
		}
	}

	/**
	 */
	@RequestMapping(value = { "/autoTestInfor", "/autoTestInfor.jhtml" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String userAutoTestInfor(@RequestParam(required = false, defaultValue = "1") int pageNum, // 审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize, // 审核页记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum1, // 待审核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize1, // 待审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum2, // 未审核核页码
			@RequestParam(required = false, defaultValue = "15") int pageSize2, // 未审核记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum3, // 首页推荐页码
			@RequestParam(required = false, defaultValue = "12") int pageSize3, // 首页推荐记录数
			@RequestParam(required = false, defaultValue = "1") int pageNum4, // 下架页码
			@RequestParam(required = false, defaultValue = "15") int pageSize4, // 下架记录数
			HttpServletRequest request, ModelMap model, HttpServletResponse response) {
		// 审核状态
		int appr = ReqUtil.getInt(request, "appr", 1);

		// 获取当前日期
		Date date = new Date();

		model.addAttribute("appr", appr);
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		if (currentMember != null) {
			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("proClass", 0));
			filters.add(Filter.eq("proClass", 1));

			// 有效期
			List<Filter> filters2 = new ArrayList<Filter>();
			filters2.add(Filter.ge("inforValidity", date));
			filters2.add(Filter.eq("longTerm", 1));

			// 已审核
			Pageable pageable = new Pageable(pageNum, pageSize);
			pageable.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable.getFilters().add(Filter.eq("apprStatus", 1));
			pageable.getFilters().add(Filter.eq("productType", productTypeService.find(7l)));
			pageable.getFilters().add(Filter.eq("isDel", 0));
			pageable.getFilters().add(Filter.or(filters));
			pageable.getFilters().add(Filter.or(filters2));
			pageable.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page", productService.findPage(pageable));

			// 待审核
			Pageable pageable1 = new Pageable(pageNum1, pageSize1);
			pageable1.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable1.getFilters().add(Filter.eq("apprStatus", 0));
			pageable1.getFilters().add(Filter.eq("productType", productTypeService.find(7l)));
			pageable1.getFilters().add(Filter.eq("isDel", 0));
			pageable1.getFilters().add(Filter.or(filters));
			pageable1.getFilters().add(Filter.or(filters2));
			pageable1.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page1", productService.findPage(pageable1));

			// 未通过
			Pageable pageable2 = new Pageable(pageNum2, pageSize2);
			pageable2.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable2.getFilters().add(Filter.eq("apprStatus", 2));
			pageable2.getFilters().add(Filter.eq("productType", productTypeService.find(7l)));
			pageable2.getFilters().add(Filter.eq("isDel", 0));
			pageable2.getFilters().add(Filter.or(filters));
			pageable2.getFilters().add(Filter.or(filters2));
			pageable2.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page2", productService.findPage(pageable2));

			// 首页推荐
			Pageable pageable3 = new Pageable(pageNum3, pageSize3);
			pageable3.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable3.getFilters().add(Filter.eq("apprStatus", 1));
			pageable3.getFilters().add(Filter.eq("productType", productTypeService.find(7l)));
			pageable3.getFilters().add(Filter.eq("isDel", 0));
			pageable3.getFilters().add(Filter.eq("isPromSale", 1));
			pageable3.getFilters().add(Filter.or(filters));
			pageable3.getFilters().add(Filter.or(filters2));
			pageable3.getFilters().add(Filter.eq("proownaudit", 1));
			model.addAttribute("page3", productService.findPage(pageable3));

			// 下架
			Pageable pageable4 = new Pageable(pageNum4, pageSize4);
			// 过滤有效期到期 或者 手动下架
			List<Filter> filList = new ArrayList<Filter>();
			filList.add(Filter.eq("proownaudit", 0));
			filList.add(Filter.isNull("inforValidity"));
			filList.add(Filter.le("inforValidity", date));
			List<Filter> fiList = new ArrayList<Filter>();
			fiList.add(Filter.eq("apprStatus", 1));
			fiList.add(Filter.eq("apprStatus", 0));
			pageable4.getFilters().add(Filter.eq("memberEntity", memberService.find(currentMember.getId())));
			pageable4.getFilters().add(Filter.eq("longTerm", 0));
			pageable4.getFilters().add(Filter.or(fiList));
			pageable4.getFilters().add(Filter.eq("productType", productTypeService.find(7l)));
			pageable4.getFilters().add(Filter.eq("isDel", 0));
			pageable4.getFilters().add(Filter.or(filters));
			pageable4.getFilters().add(Filter.or(filList));
			model.addAttribute("page4", productService.findPage(pageable4));

			return TEMPLATE_PATH + "/autoTestInfor";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	// 修改产品信息销售
	@RequestMapping(value = "/edit_autoTestInfor", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit_autoTestInfor(HttpServletRequest request, HttpServletResponse response, Model model) {
		long id = ReqUtil.getLong(request, "id", 0l);

		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);

		if (currentMember != null) {
			// 帮组信息
			model.addAttribute("helps", helpService.findAll());

			// 品牌
			model.addAttribute("brands", productBrandService.findAll());

			ProductEntity productEntity = productService.find(id);
			if (productEntity != null) {
				AutoTestEntity autoTestEntity = autoTestService.find(Filter.eq("productEntity", productEntity));
				model.addAttribute("item", autoTestEntity);
				List<InstrumentEntity> instrumentEntities = instrumentService
						.findList(Filter.eq("autoTest", autoTestEntity));
				// 获取instrumentEntities 第一个元素
				if (instrumentEntities.size() > 0 && instrumentEntities != null) {
					InstrumentEntity instrumentEntity = instrumentEntities.get(0);
					model.addAttribute("instrumentEntity", instrumentEntity);
					instrumentEntities.remove(0);
					// 去除instrumentEntities 第一个元素
					model.addAttribute("instrument", instrumentEntities);
				}
			}
			return TEMPLATE_PATH + "/edit_user_pub_autoTest";
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/*
	 * @ModelAttribute public void
	 * getModule(@RequestParam(required=false,defaultValue="0") Long id ,Model
	 * model){ AutoTestEntity autoTestEntity = autoTestService.find(id);
	 * if(autoTestEntity != null){ model.addAttribute("editAutoTestEntity",
	 * autoTestEntity); }
	 * 
	 * }
	 */

	// 修改客户商品销售
	@ResponseBody
	@RequestMapping(value = "/save_updateEdit_user_pub_autoTest", method = { RequestMethod.POST, RequestMethod.GET })
	public Message save_updateEdit_user_pub_autoTest(HttpServletRequest request, HttpServletResponse response,
			String[] brandName, @ModelAttribute("editAutoTest") AutoTestEntity autoTestEntity,
			InstrumentsBean instrumentsBean) {
		if (autoTestEntity.getProductEntity().getInforValidity() == null
				&& autoTestEntity.getProductEntity().getLongTerm() == 0) {
			return ERROR_MESSAGE;
		}
		List<InstrumentEntity> list = autoTestEntity.getProductItems();
		autoTestEntity.getProductEntity().setApprStatus(0);

		// autoTestEntity.setProductItems(null);
		autoTestService.update(autoTestEntity);
		// 删除对应实体的instruments
		if (list.size() > 0 && list != null) {
			instrumentService.deleteList(list);
		}
		int i = 0;
		for (InstrumentEntity instrument : instrumentsBean.getInstruments()) {
			if (instrument != null && (instrument.getProductBrandEntity() != null || instrument.getProNo() != null
					|| instrument.getProName() != null)) {
				if (brandName != null && brandName.length > 0 && brandName[i] != null) {
					String string = brandName[i];
					if (!string.isEmpty()) {
						ProductBrandEntity productBrand = productBrandService.find(Filter.eq("name", string));
						if (CommonUtil.isNumeric(string) == true) {
							ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(string));
							if (productBrandEntity != null) {
								instrument.setProductBrandEntity(productBrandEntity);
							} else if (productBrand != null) {
								instrument.setProductBrandEntity(productBrand);
							} else {
								ProductBrandEntity productBrandEntity1 = new ProductBrandEntity();
								productBrandEntity1.setName(string);
								productBrandEntity1.setApprStatus(0);
								productBrandService.save(productBrandEntity1);
								instrument.setProductBrandEntity(productBrandEntity1);
							}
						} else if (productBrand != null) {
							instrument.setProductBrandEntity(productBrand);
						} else {
							ProductBrandEntity productBrandEntity = new ProductBrandEntity();
							productBrandEntity.setName(string);
							productBrandEntity.setApprStatus(0);
							productBrandService.save(productBrandEntity);
							instrument.setProductBrandEntity(productBrandEntity);
						}
					}
				}
				if (instrument.getProductclass() == null) {
					instrument.setProductclass(null);
				} else {
					ProductclassEntity productclassEntity = productClassService
							.find(instrument.getProductclass().getId());
					instrument.setProductclass(productclassEntity);
				}
				i++;
				instrument.setAutoTest(autoTestEntity);
				instrumentService.save(instrument);
			}
		}

		return SUCCESS_MESSAGE;

	}
}