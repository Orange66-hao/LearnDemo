package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.BasedataEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BasedataService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import nl.siegmann.epublib.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.common.comm.ServiceClient.Request;

/**
 * 资料库管理
 * 
 * @author deng
 *
 *         2015年8月15日
 */
@Controller("basedataController")
@RequestMapping("/admin/basedata/basedata")
public class BasedataController {

	private static final String TEM_PATH = "/template/admin/basedata/basedata";

	@Resource
	private BasedataService basedataService;

	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private IndustryClassService industryClassService;

	@RequestMapping(value = "toBasedataList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/basedatalist";
	}

	@RequestMapping(value = "data.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void dataJson(HttpServletRequest request, HttpServletResponse response) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		String model = ReqUtil.getString(request, "model", "");
        Long brandId = ReqUtil.getLong(request, "brandId", 0L);
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

		Filter filter=null;
		List<Filter> filterList = new ArrayList<Filter>();
		if (!model.isEmpty()) {
			filterList.add(Filter.eq("proNo", model,true));
		}
        if (brandId!=0L) {
            ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
            if(productBrandEntity!=null){
                filterList.add(Filter.eq("productBrandEntity",productBrandEntity));
            }
        }
		if (!keyword.isEmpty()) {
			pageable.getFilters().add(Filter.like("proName","%"+keyword+"%"));
		}

		filter= new Filter(filterList);
        pageable.getFilters().add(filter);
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("apprStatus", 1));
		Page<BasedataEntity> page = basedataService.findPage(pageable);
		List<BasedataEntity> basedataEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();

		for (BasedataEntity item : basedataEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("brandId", item.getProductBrandEntity() == null ? "" : item.getProductBrandEntity().getName());
			map.put("proNo", item.getProNo());
			map.put("proName", item.getProName());
			map.put("proNameEn", item.getProNameEn());
			map.put("modify_date", item.getModifyDate());
			String industryString="";
            if(StringUtils.isNotBlank(item.getIndustryClass())){
                String[] induIds = item.getIndustryClass().split(",");
                for (String induId : induIds) {
                    IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(induId));
                    if(industryClassEntity!=null){
                        industryString+=industryClassEntity.getName()+",";
                    }
                }
            }
			map.put("industryClass", industryString);
			map.put("proClassId", item.getProductclass() == null ? "" : item.getProductclass().getName());
			map.put("image", item.getImage());
			map.put("proSynopsis", item.getProSynopsis());
			map.put("proSynopsisEn", item.getProSynopsisEn());
			map.put("proContent", item.getProContent());
			map.put("proContentEn", item.getProContentEn());
			resultList.add(map);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	// 未审核的资料库数据
	@RequestMapping(value = "/nAppr", method = { RequestMethod.POST, RequestMethod.GET })
	public void nAppr(HttpServletRequest request, HttpServletResponse response) {
		String keyword = ReqUtil.getString(request, "keyword", "");
        String model = ReqUtil.getString(request, "model", "");
        Long brandId = ReqUtil.getLong(request, "brandId", 0L);
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
			pageable.getFilters().add(Filter.like("proName","%"+keyword+"%"));
		}

        List<Filter> filterList = new ArrayList<Filter>();
        if (!model.isEmpty()) {
            pageable.getFilters().add(Filter.eq("proNo", model,true));
        }
        if (brandId!=0L) {
            ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
            if(productBrandEntity!=null){
                pageable.getFilters().add(Filter.eq("productBrandEntity",productBrandEntity));
            }
        }
		if (!keyword.isEmpty()) {
			pageable.getFilters().add(Filter.like("proName","%"+keyword+"%"));
		}
		pageable.getFilters().add(Filter.eq("isDel", 0));
		pageable.getFilters().add(Filter.eq("apprStatus", 0));
		Page<BasedataEntity> page = basedataService.findPage(pageable);
		List<BasedataEntity> basedataEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		for (BasedataEntity item : basedataEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("brandId", item.getProductBrandEntity() == null ? "" : item.getProductBrandEntity().getName());
			map.put("proNo", item.getProNo());
			map.put("proName", item.getProName());
			map.put("proNameEn", item.getProNameEn());
			String industryString="";
			if(StringUtils.isNotBlank(item.getIndustryClass())){
				String[] induIds = item.getIndustryClass().split(",");
				for (String induId : induIds) {
					IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(induId));
					if(industryClassEntity!=null){
						industryString+=industryClassEntity.getName()+",";
					}
				}
			}
			map.put("industryClass", industryString);
			map.put("proClassId", item.getProductclass() == null ? "" : item.getProductclass().getName());
			map.put("image", item.getImage());
			map.put("proSynopsis", item.getProSynopsis());
			map.put("proSynopsisEn", item.getProSynopsisEn());
			map.put("proContent", item.getProContent());
			//map.put("proContentEn", item.getProContentEn());
			map.put("modify_date", item.getModifyDate());
			resultList.add(map);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	// 跳转到村实体添加页面
	@RequestMapping(value = "/add_basedata.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_Basedata(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/basedata_add";

	}

	/**
	 * 
	 * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中 传入参数:ProductSaleEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/save_basedata.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model,
			Long[] indClassId) {
		// 获取到页面的基本信息
		String brandName = ReqUtil.getString(request, "brandId", "");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		String proName = ReqUtil.getString(request, "proName", "");
		String proNameEn = ReqUtil.getString(request, "proNameEn", "");
		Long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
		String image = ReqUtil.getString(request, "image", "");
		String proSynopsis = ReqUtil.getString(request, "proSynopsis", "");
		String proSynopsisEn = ReqUtil.getString(request, "proSynopsisEn", "");
		String proContent = ReqUtil.getString(request, "proContent", "");
		String proContentEn = ReqUtil.getString(request, "proContentEn", "");
		String industryClass = ReqUtil.getString(request, "industryClass", "");

		BasedataEntity basedataEntity = new BasedataEntity();
		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
		if (productBrandEntity != null) {
			basedataEntity.setProductBrandEntity(productBrandEntity);
		} else {
			ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
			productBrandEntity2.setName(brandName);
			productBrandService.save(productBrandEntity2);
			basedataEntity.setProductBrandEntity(productBrandEntity2);
		}
		ProductclassEntity productclassEntity = productclassService.find(proClassId);
		if (productclassEntity != null) {
			basedataEntity.setProductclass(productclassEntity);
		}
		for (int i = 0; i < indClassId.length; i++) {
			IndustryClassEntity industryClassEntity = industryClassService.find(indClassId[i]);
			industryClass += industryClassEntity.getName() + ",";
		}
		basedataEntity.setIndustryClass(industryClass);
		basedataEntity.setProNo(proNo);
		basedataEntity.setProName(proName);
		;
		basedataEntity.setProNameEn(proNameEn);
		basedataEntity.setImage(image);
		basedataEntity.setProSynopsis(proSynopsis);
		basedataEntity.setProSynopsisEn(proSynopsisEn);
		basedataEntity.setProContent(proContent);
		basedataEntity.setProContentEn(proContentEn);
		basedataEntity.setApprStatus(1);
		basedataService.save(basedataEntity);

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
	@RequestMapping(value = "/delete_basedata.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteBasedata(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id) {
		if (id != null && id.length > 0) {
			/* this.basedataService.deleteList(id); */
			List<BasedataEntity> basedataEntities = basedataService.findList(id);
			if (basedataEntities.size() > 0) {
				for (BasedataEntity item : basedataEntities) {
					item.setIsDel(1);
					basedataService.update(item);
				}
			}

		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "操作成功");

		RespUtil.renderJson(response, result);
	}

	// 审核
	@RequestMapping(value = "/audit.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void auditBasedata(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id) {
		if (id != null && id.length > 0) {
			List<BasedataEntity> basedataEntities = basedataService.findList(id);
			if (basedataEntities.size() > 0) {
				for (BasedataEntity item : basedataEntities) {
					item.setApprStatus(1);
					basedataService.update(item);
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "审核成功");
		RespUtil.renderJson(response, result);
	}

	// 反审核
	@RequestMapping(value = "/reject.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void rejectBasedata(HttpServletRequest request, HttpServletResponse response, Model model, Long[] id) {
		if (id != null && id.length > 0) {
			List<BasedataEntity> basedataEntities = basedataService.findList(id);
			if (basedataEntities.size() > 0) {
				for (BasedataEntity item : basedataEntities) {
					item.setApprStatus(0);
					basedataService.update(item);
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "反审核成功");
		RespUtil.renderJson(response, result);
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_basedata.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");

		BasedataEntity basedataEntity = new BasedataEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			basedataEntity = this.basedataService.find(lid);
			model.addAttribute("item", basedataEntity);
		}

		return TEM_PATH + "/basedata_edit";

	}

	/**
	 * 
	 * 方法:保存更新之后的ProductSaleEntity（村本身实体 ） 传入参数:ProductSaleEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/save_edit_basedata.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditBasedata(HttpServletRequest request, HttpServletResponse response, Model model) {
		long id = ReqUtil.getLong(request, "id", 0l);
		String brandName = ReqUtil.getString(request, "brandId", "");
		long brandId = ReqUtil.getLong(request, "brandId", 0l);
		String proNo = ReqUtil.getString(request, "proNo", "");
		String proName = ReqUtil.getString(request, "proName", "");
		String proNameEn = ReqUtil.getString(request, "proNameEn", "");
		long proClassId = ReqUtil.getLong(request, "proClassId", 0l);
		String image = ReqUtil.getString(request, "image", "");
		String proSynopsis = ReqUtil.getString(request, "proSynopsis", "");
		String proSynopsisEn = ReqUtil.getString(request, "proSynopsisEn", "");
		String proContent = ReqUtil.getString(request, "proContent", "");
		String proContentEn = ReqUtil.getString(request, "proContentEn", "");
		String[] indClassIds = ReqUtil.getParams(request, "indClassId");

		BasedataEntity basedataEntityOle = basedataService.find(id);
		ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
		ProductclassEntity productclassEntity = productclassService.find(proClassId);
		if (basedataEntityOle != null) {
			if (productBrandEntity != null) {
				basedataEntityOle.setProductBrandEntity(productBrandEntity);
			} else {
				ProductBrandEntity productBrandEntity2 = new ProductBrandEntity();
				productBrandEntity2.setName(brandName);
				productBrandService.save(productBrandEntity2);
				basedataEntityOle.setProductBrandEntity(productBrandEntity2);
			}
			String industryClass="";
			if(indClassIds!=null&&indClassIds.length>0){
				for (String indClassId : indClassIds) {
					industryClass+=indClassId+",";
				}
			}
			basedataEntityOle.setIndustryClass(industryClass);
			basedataEntityOle.setProNo(proNo);
			basedataEntityOle.setProName(proName);
			basedataEntityOle.setProNameEn(proNameEn);
			if (productclassEntity != null) {
				basedataEntityOle.setProductclass(productclassEntity);
			}
			basedataEntityOle.setImage(image);
			basedataEntityOle.setProSynopsis(proSynopsis);
			basedataEntityOle.setProSynopsisEn(proSynopsisEn);
			basedataEntityOle.setProContent(proContent);
			basedataEntityOle.setProContentEn(proContentEn);
			basedataService.update(basedataEntityOle);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	// 更新
	@ResponseBody
	@RequestMapping(value = "/freshData", method = RequestMethod.POST)
	public Map<String, Object> freshProName(HttpServletRequest request, Model model) {
		String brandName = request.getParameter("brand");
		String proNo = request.getParameter("proNo");
		String proName = request.getParameter("proName");
		Long id = ReqUtil.getLong(request, "id", 0l);
		String proSynopsis = request.getParameter("proSynopsis");
		String proSynopsisEn = request.getParameter("proSynopsisEn");
		String proContent = request.getParameter("proContent");
		String proContentEn = request.getParameter("proContentEn");
		String indClassId = request.getParameter("indClassId");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("productBrandEntity", productBrandService.find(Filter.eq("name", brandName))));
		filters.add(Filter.eq("proNo", proNo));

		Map<String, Object> result = new HashMap<String, Object>();
		List<BasedataEntity> basedataEntities = basedataService.findList(filters);
		if (basedataEntities.size() > 0) {
			BasedataEntity basedataEntity = basedataEntities.get(0);
			if (proName != null) {
				basedataEntity.setProName(proName);
			}
			if (id != 0l) {
				ProductclassEntity productclassEntity = productclassService.find(id);
				if (productclassEntity != null) {
					basedataEntity.setProductclass(productclassEntity);
				}
			}
			if (proSynopsis != null) {
				basedataEntity.setProSynopsis(proSynopsis);
			}
			if (proSynopsisEn != null) {
				basedataEntity.setProSynopsisEn(proSynopsisEn);
			}
			if (proContent != null) {
				basedataEntity.setProContent(proContent);
			}
			if (proContentEn != null) {
				basedataEntity.setProContentEn(proContentEn);
			}
			if (indClassId != null) {
				basedataEntity.setIndustryClass(indClassId);
			}
			basedataService.update(basedataEntity);
			result.put("result", 1);
			result.put("success", "更新成功");
		} else {
			result.put("result", 0);
			result.put("error", "资料库没有");
		}
		return result;
	}

	/*
	 * //更新设备类目
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value="/freshProClass",method=RequestMethod.POST) public
	 * Map<String, Object> freshProClass(HttpServletRequest request, Model
	 * model) { String brandName = request.getParameter("brand"); String proNo =
	 * request.getParameter("proNo"); Long id=
	 * Long.parseLong(request.getParameter("proClass"));
	 * 
	 * List<Filter> filters = new ArrayList<Filter>();
	 * filters.add(Filter.eq("productBrandEntity",
	 * productBrandService.find(Filter.eq("name", brandName))));
	 * filters.add(Filter.eq("proNo", proNo));
	 * 
	 * Map<String, Object> result = new HashMap<String, Object>();
	 * List<BasedataEntity> basedataEntities =
	 * basedataService.findList(filters); if(basedataEntities.size()>0){
	 * BasedataEntity basedataEntity = basedataEntities.get(0);
	 * ProductclassEntity productclassEntity = productclassService.find(id);
	 * if(productclassEntity !=null){
	 * basedataEntity.setProductclass(productclassEntity); }
	 * basedataService.update(basedataEntity); result.put("result", 1);
	 * result.put("success", "更新成功"); }else{ result.put("result", 0);
	 * result.put("error", "资料库没有"); } return result; }
	 * 
	 * //更新产品描述
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value="/freshProSynopsis", method=RequestMethod.POST)
	 * public Map<String, Object> freshProSynopsis(HttpServletRequest request) {
	 * String brand = request.getParameter("brand"); String proNo =
	 * request.getParameter("proNo"); String proSynopsis =
	 * request.getParameter("proSynopsis");
	 * 
	 * Map<String, Object> result = new HashMap<String, Object>(); List<Filter>
	 * filters = new ArrayList<Filter>();
	 * filters.add(Filter.eq("productBrandEntity",
	 * productBrandService.find(Filter.eq("name", brand))));
	 * filters.add(Filter.eq("proNo", proNo)); List<BasedataEntity>
	 * basedataEntities = basedataService.findList(filters);
	 * 
	 * if(basedataEntities.size()>0){ BasedataEntity basedataEntity =
	 * basedataEntities.get(0); basedataEntity.setProSynopsis(proSynopsis);
	 * basedataService.update(basedataEntity); result.put("result", 1);
	 * result.put("success", "更新成功"); }else{ result.put("result", 0);
	 * result.put("error", "资料库没有"); } return result; }
	 * 
	 * //更新英文描述
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value="/freshProSynopsisEn", method=RequestMethod.POST)
	 * public Map<String, Object> freshProSynopsisEn(HttpServletRequest request)
	 * { String brand = request.getParameter("brand"); String proNo =
	 * request.getParameter("proNo"); String proSynopsisEn =
	 * request.getParameter("proSynopsisEn");
	 * 
	 * Map<String, Object> result = new HashMap<String, Object>(); List<Filter>
	 * filters = new ArrayList<Filter>();
	 * filters.add(Filter.eq("productBrandEntity",
	 * productBrandService.find(Filter.eq("name", brand))));
	 * filters.add(Filter.eq("proNo", proNo)); List<BasedataEntity>
	 * basedataEntities = basedataService.findList(filters);
	 * 
	 * if(basedataEntities.size()>0){ BasedataEntity basedataEntity =
	 * basedataEntities.get(0); basedataEntity.setProSynopsisEn(proSynopsisEn);
	 * basedataService.update(basedataEntity); result.put("result", 1);
	 * result.put("success", "更新成功"); }else{ result.put("result", 0);
	 * result.put("error", "资料库没有"); } return result; }
	 * 
	 * //更新详细信息
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value="/freshProContent", method=RequestMethod.POST)
	 * public Map<String, Object> freshProContent(HttpServletRequest request) {
	 * String brand = request.getParameter("brand"); String proNo =
	 * request.getParameter("proNo"); String proContent =
	 * request.getParameter("proContent");
	 * 
	 * Map<String, Object> result = new HashMap<String, Object>(); List<Filter>
	 * filters = new ArrayList<Filter>();
	 * filters.add(Filter.eq("productBrandEntity",
	 * productBrandService.find(Filter.eq("name", brand))));
	 * filters.add(Filter.eq("proNo", proNo)); List<BasedataEntity>
	 * basedataEntities = basedataService.findList(filters);
	 * 
	 * if(basedataEntities.size()>0){ BasedataEntity basedataEntity =
	 * basedataEntities.get(0); basedataEntity.setProContent(proContent);
	 * basedataService.update(basedataEntity); result.put("result", 1);
	 * result.put("success", "更新成功"); }else{ result.put("result", 0);
	 * result.put("error", "资料库没有"); } return result; }
	 */
	/*
	 * //同步数据库
	 * 
	 * @RequestMapping(value="/Tong", method={RequestMethod.POST,
	 * RequestMethod.GET}) public void saleTong(HttpServletRequest request,
	 * HttpServletResponse response, Model model){ long brandId =
	 * ReqUtil.getLong(request, "brandId", 0l); String proNo =
	 * ReqUtil.getString(request, "proNo", "");
	 * 
	 * List<Filter> filters = new ArrayList<Filter>();
	 * filters.add(Filter.eq("isDel", 0));
	 * filters.add(Filter.eq("productBrandEntity",
	 * productBrandService.find(brandId))); filters.add(Filter.eq("proNo",
	 * proNo)); List<BasedataEntity> basedataEntities =
	 * basedataService.findList(filters); Map<String, Object> result = new
	 * HashMap<String, Object>(); if(basedataEntities !=null){
	 * for(BasedataEntity item : basedataEntities){ result.put("result", 1);
	 * result.put("proName", item.getProName()); result.put("proNameEn",
	 * item.getProNameEn()); result.put("image", item.getImage());
	 * result.put("proSynopsis", item.getProSynopsis());
	 * result.put("proSynopsisEn", item.getProSynopsisEn());
	 * result.put("proContent", item.getProContent());
	 * result.put("proContentEn", item.getProContentEn());
	 * result.put("proClassId", item.getProductclass().getId());
	 * result.put("indClassId", item.getIndustryClass()); } }
	 * RespUtil.renderJson(response, result); }
	 */
}
