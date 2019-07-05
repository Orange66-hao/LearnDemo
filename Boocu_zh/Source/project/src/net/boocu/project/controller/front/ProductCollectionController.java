package net.boocu.project.controller.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductCollectionEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductCollectionService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

@Controller("collectionController")
@RequestMapping("/collection")
public class ProductCollectionController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/userCenter/orderManage/collection";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");
	/** 失败信息 */
	private static final Message ERROR_MESSAGE = Message.error("操作失败!");
	/** 提示信息 */
	private static final Message INFO_MESSAGE = Message.error("您已经收藏过此商品!");
	@Resource
	private FriendsService friendsService;
	@Resource
	private HelpService helpService;

	@Resource
	private ProductService productService;
	@Resource
	private ProductCollectionService collectionService;
	@Resource
	private MemberService memberService;
	@Resource
	private ProducttypeService productTypeService;
	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private MessageService messageService;

	/**
	 * ajax 收藏商品信息
	 */
	@RequestMapping(value = "/joinColl", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Message collectionProduct(HttpServletRequest request, HttpServletResponse response) {
		Long proId = ReqUtil.getLong(request, "proId", 0l);
		long memberId = ReqUtil.getLong(request, "memberId", 0l);
		List<Filter> filters = new ArrayList<Filter>();
		MemberEntity member = memberService.find(memberId);
		ProductEntity product = productService.find(proId);
		filters.add(Filter.eq("memberEntity", member));
		filters.add(Filter.eq("productEntity", product));
		ProductCollectionEntity codition = collectionService.find(filters);
		if (codition == null) {
			Date date = new Date();
			ProductCollectionEntity entity = new ProductCollectionEntity();
			entity.setIsDel(0);
			entity.setMemberEntity(member);
			entity.setProductEntity(product);
			entity.setCreateDate(date);
			collectionService.saveCollection(entity);
			return SUCCESS_MESSAGE;
		} else {
			return INFO_MESSAGE;
		}
	}

	@RequestMapping(value = "/user_coll", method = { RequestMethod.POST, RequestMethod.GET })
	public String showCollectionList(HttpServletRequest request, HttpServletResponse response, Model model) {
		Pageable pageable = new Pageable(1, 5);
		pageable.getFilters().add(Filter.eq("memberEntity", memberService.getCurrent()));
		pageable.getFilters().add(Filter.eq("isDel", 0));
		model.addAttribute("page", collectionService.findPage(pageable));

		model.addAttribute("typeItem", productTypeService.findAll());

		model.addAttribute("brandItem", productBrandService.findAll());
		// 已登录的会员名
		MemberEntity currentMember = memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		// 获取站内信未读
		Map ms = new HashMap<>();
		ms.put("member", memberService.getCurrent());
		ms.put("state", 0);
		ms.put("type", 1);
		model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
		// model.addAttribute("page", productService.findPage(pageable));
		// model.addAttribute("isBegin", "1");

		// 友情推荐
		model.addAttribute("friends", friendsService.findAll());

		// 帮助信息
		model.addAttribute("helps", helpService.findAll());

		// 真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);

		return TEMPLATE_PATH + "/user_coll";
	}

	@RequestMapping(value = "/searchInfo", method = { RequestMethod.POST, RequestMethod.GET })
	public String dataJson(@RequestParam(required = false, defaultValue = "1") int pageNum, // 页码
			@RequestParam(required = false, defaultValue = "5") int pageSize, // 页记录数
			/*
			 * @RequestParam(required = false) String dateOrder, // 时间排序
			 * 
			 * @RequestParam(required = false) String priceOrder, // 价格排序
			 * 
			 * @RequestParam(required = false) String popuOrder, // 人气排序
			 */ @RequestParam(required = false) String keyword, // 搜索关键字
			@RequestParam(required = false) Long proBrand, // 产品品牌
			@RequestParam(required = false) Long selOType, // 产品分类
			@RequestParam(required = false) Long selOProNo, // 产品型号
			HttpServletRequest request, HttpServletResponse response, Model model) {

		Pageable pageable = new Pageable(pageNum, pageSize);
		List<Filter> flist = new ArrayList<Filter>();
		// 条件放置集合
		Map con = new HashMap<String, Object>();
		/*
		 * con.put("dateOrder", dateOrder); con.put("priceOrder", priceOrder);
		 * con.put("popuOrder", popuOrder);
		 */
		con.put("keyword", keyword);
		con.put("proBrand", proBrand);
		con.put("selOType", selOType);
		con.put("selOProNo", selOProNo);
		Page<ProductCollectionEntity> pages = collectionService.findListCollectionPage(pageable, con);
		model.addAttribute("page", pages);
		return "/template/front/include/dataLists/userColl";
	}

	/** 获取型号 */
	@ResponseBody
	@RequestMapping(value = "/get_proNo.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getProNo(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long brandId = ReqUtil.getLong(request, "brandId", 0l);
		List<Filter> filters = new ArrayList<Filter>();
		ProductBrandEntity brandEntity = productBrandService.find(brandId);
		if (brandEntity != null) {
			List<ProductEntity> productList = productService
					.findList(Filter.eq("productBrandEntity", productBrandService.find(brandId)));
			List<Map> resultList = new ArrayList<Map>();
			for (ProductEntity item : productList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getProNo());
				resultList.add(map);
			}
			return resultList;
		} else {
			return null;
		}

	}

	/** 删除 */
	@RequestMapping(value = "/isDel", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Message isDel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Long[] collId) {
		ProductCollectionEntity entity = null;
		if (collId.length > 0) {
			for (Long id : collId) {
				entity = collectionService.find(id);
				if (entity != null) {
					entity.setIsDel(1);
				}
				collectionService.update(entity);
			}
		}
		if (entity != null) {
			return SUCCESS_MESSAGE;
		} else {
			return ERROR_MESSAGE;
		}
	}

	/** 永久删除 */
	@ResponseBody
	@RequestMapping(value = "/delColl", method = { RequestMethod.POST, RequestMethod.GET })
	public void delColl(HttpServletRequest request, HttpServletResponse response, @RequestParam Long[] collId) {
		if (collId.length > 0) {
			for (Long id : collId) {
				collectionService.delete(id);
			}
		}
	}

}
