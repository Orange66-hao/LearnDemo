package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 商品类型管理
 * 
 * @author dengwei
 *
 *         2015年8月12日
 */
@Controller("producttypeController")
@RequestMapping("/admin/basedata/producttype")
public class ProducttypeController {

	private static final String TEM_PATH = "/template/admin/basedata/producttype";
	
	@Resource
	private ProducttypeService producttypeService;

	@RequestMapping(value = "toProducttypeList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/producttypelist";
	}

	@RequestMapping(value = "data.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void dataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");

		Pageable pageable = new Pageable(pagenumber, rows);
		if (!sortValue.isEmpty()) {
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) : Sequencer.asc(sortValue);
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}

		if (!keyword.isEmpty()) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("typeName", "%" + keyword + "%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<ProducttypeEntity> page = producttypeService.findPage(pageable);
		List<ProducttypeEntity> producttypeEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();

		for (ProducttypeEntity item : producttypeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("typeName", item.getTypeName());
			map.put("status", item.getStatus());
			map.put("creatuser", item.getCreatuser());
			map.put("updateuser", item.getUpdateuser());
			resultList.add(map);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	// 跳转到村实体添加页面
	@RequestMapping(value = "/add_producttype.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_Producttype(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/producttype_add";

	}

	/**
	 * 
	 * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中 传入参数:ProductSaleEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/save_producttype.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {

		// 获取到页面的基本信息
		String typeName = ReqUtil.getString(request, "typeName", "");
		int status = ReqUtil.getInt(request, "status", 1);
		String creatuser = ReqUtil.getString(request, "creatuser", "");
		String updateuser = ReqUtil.getString(request, "updateuser", "");

		ProducttypeEntity producttypeEntity = new ProducttypeEntity();

		producttypeEntity.setTypeName(typeName);
		producttypeEntity.setStatus(status);
		producttypeEntity.setCreatuser(creatuser);
		producttypeEntity.setUpdateuser(updateuser);
		producttypeService.save(producttypeEntity);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中ProductSaleEntity（广告本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_producttype.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id) {
		if (id != null && id.length > 0) {
			this.producttypeService.deleteList(id);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "操作成功");

		RespUtil.renderJson(response, result);
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_producttype.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");

		ProducttypeEntity producttypeEntity = new ProducttypeEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			producttypeEntity = this.producttypeService.find(lid);
			model.addAttribute("item", producttypeEntity);
		}

		return TEM_PATH + "/producttype_edit";

	}

	/**
	 * 
	 * 方法:保存更新之后的ProductSaleEntity（村本身实体 ） 传入参数:ProductSaleEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/save_edit_producttype.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditProducttype(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String typeName = ReqUtil.getString(request, "typeName", "");
		int status = ReqUtil.getInt(request, "status", 1);
		String creatuser = ReqUtil.getString(request, "creatuser", "");
		String updateuser = ReqUtil.getString(request, "updateuser", "");

		ProducttypeEntity producttypeEntityOle = producttypeService.find(id);
		if (producttypeEntityOle != null)
			producttypeEntityOle.setTypeName(typeName);
		producttypeEntityOle.setStatus(status);
		producttypeEntityOle.setCreatuser(creatuser);
		producttypeEntityOle.setUpdateuser(updateuser);
		producttypeService.update(producttypeEntityOle);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 取得商品类型的下拉信息 add by fang 20150906
	 */
	@ResponseBody
	@RequestMapping(value = "/dataJson.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map<String, Object>> getProductTypeDataJson(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<ProducttypeEntity> entities = producttypeService.findAll();
		for (ProducttypeEntity item : entities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getTypeName());
			resultList.add(map);
		}
		return resultList;
	}
}
