/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.boocu.web.controller.admin;

import com.alibaba.druid.support.json.JSONUtils;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import net.boocu.web.*;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * add by zhang
 * 2018.11.26
 * 添加产品对应的品牌型号
 */
@Controller("pBrandAndModelController")
@RequestMapping("/admin/pBrandAndModel")
public class PbrandAndModelController {
	private Logger logger=Logger.getLogger(PbrandAndModelController.class);
	private static final String TEM_PATH = "/template/admin/basedata/pBrandAndModel";
	@Autowired
	private BrandAndModelService brandAndModelService;
	@Autowired
	private PbrandAndModelService pbrandAndModelService;
	@Autowired
	private ProductclassService productclassService;

	@Resource
	private ProductBrandService productBrandService;

	@Autowired
	private AdminService adminService;
	@Autowired
	private IndustryClassService industryClassService;
	@Resource
	private McBrandService mcBrandService;

	@Resource
	private MemberService memberService;

	@Resource
	private ProductService productService;

	@Resource
	private McProductClassService mcproductclassService;

	@Resource
	private McMajorService mcMajorService;

	@Resource
	private JdbcTemplate JdbcTemplate;
	//跳转到型号列表
	@RequestMapping(value = "toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		String industryClassId = ReqUtil.getString(request, "industryClassId", "undefined");
		String productId = ReqUtil.getString(request, "productId", "undefined");
		model.addAttribute("productId",productId);
		model.addAttribute("industryClassId",industryClassId);
		model.addAttribute("currentUser",adminService.getCurrent());
		return TEM_PATH + "/brandAndModelList";
	}

	// 跳转到实体添加页面
	@RequestMapping(value = "/add_brandAndModel.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_McBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		String model_yincang = request.getParameter("model_yincang");
		request.setAttribute("model_yincang", model_yincang);
		return TEM_PATH + "/brandAndModel_add";
	}
	//根据产品和仪器获取对应的品牌型号
	@ResponseBody
	@RequestMapping(value = "/getBrandAndModelByInduAndPro.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public  List<PbrandAndModelEntity> getBrandAndModelByInduAndPro(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long industryClass = ReqUtil.getLong(request,"industryClass",0L);
		Long productclass = ReqUtil.getLong(request,"productclass",0L);
		IndustryClassEntity industryClassEntity = industryClassService.find(industryClass);
		ProductclassEntity productclassEntity = productclassService.find(productclass);
		List<PbrandAndModelEntity> list =new ArrayList<>();
		if(industryClassEntity!=null&&productclassEntity!=null){
			 list = pbrandAndModelService.findList(
					Filter.eq("productclassEntity", productclassEntity),Filter.eq("industryClassEntity",industryClassEntity));
		}

		return list;
	}

	// 产品型号列表数据查询
	@ResponseBody
	@RequestMapping(value = "getData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {

		List<Map<String, Object>> topNoderows=pbrandAndModelService.queryrows(request, response, model);//查询条数
		List<Map> page=pbrandAndModelService.query(request, response, model);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());//获取行总数
		result.put("rows", page);
		RespUtil.renderJson( response, result);
	}

	// 获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
		McBrandEntity topNode = mcBrandService.find(1l);
		return getNodeData(topNode);
	}

	// 递归查找树形结构信息
	public List<Map> getNodeData(McBrandEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getId()));
		List<McBrandEntity> items = mcBrandService.findList(flist, Sequencer.asc("sort"));
		for (McBrandEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<McBrandEntity> children = mcBrandService.findList(Filter.eq("parentid", item.getId()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}

	// 获取异步下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotree.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 1l);
		McBrandEntity mcBrandEntity = mcBrandService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (mcBrandEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", mcBrandEntity.getId()));
			List<McBrandEntity> items = mcBrandService.findList(flist, Sequencer.asc("sort"));
			for (McBrandEntity item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName());
				// map.put("state", "0".equals(item.getLeaf())?"closed":"open");
				resultList.add(map);
			}
		}
		return resultList;
	}

	/**
	 * 
	 * 方法:将新添加的McBrandEntity（本身实体 ）保存到数据库中 传入参数:McBrandEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台McBrandEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
		public Map<String, Object> saveMcBrand(HttpServletRequest request, HttpServletResponse response, Model model,
				PbrandAndModelEntity pbrandAndModelEntity) {
			Map<String, Object> result = new HashMap<String, Object>();
		try {
            String[] models = ReqUtil.getParams(request, "model");
            Long productclass = ReqUtil.getLong(request, "mc_productclass", 0L);
            Long industryClass = ReqUtil.getLong(request, "industryClass", 0L);
            //品牌
            for (int i=0;i<models.length; i++ ) {
            	if(StringUtils.isBlank(models[i])){
            		//如果有一个型号为空  则跳过这个数据添加后面的
            		continue;
				}
               PbrandAndModelEntity pbEntity=new PbrandAndModelEntity();
                pbEntity.setModel(models[i]);
                if( pbrandAndModelEntity.getSort()==null){
                    pbEntity.setSort("1");
                }else{
                    pbEntity.setSort(pbrandAndModelEntity.getSort());
                }
                pbEntity.setRemark(pbrandAndModelEntity.getRemark());
                ProductclassEntity productclassEntity = productclassService.find(productclass);
                IndustryClassEntity industryClassEntity = industryClassService.find(industryClass);
                if(productclassEntity==null||industryClassEntity==null){
                    result.put("result", 0);
                    result.put("message", "请选择下拉数据集");
                    return result;
                }
                pbEntity.setIndustryClassEntity(industryClassEntity);
                pbEntity.setProductclassEntity(productclassEntity);
                pbrandAndModelService.save(pbEntity);
            }
			result.put("result", 1);
			result.put("message", "保存成功");
		} catch (Exception e) {
			result.put("result", 0);
			result.put("message", "服务器出错了");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 方法:删除选中McBrandEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组） 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_brandAndModel.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {

		String[] pbmIdLists = request.getParameterValues("pbmIdList[]");
		Long[] ids = new Long[pbmIdLists.length];

		for (int i = 0; i < ids.length; i++) {
			ids[i] = Long.parseLong(pbmIdLists[i]);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ids != null && ids.length > 0) {
                for (Long id : ids) {
                    pbrandAndModelService.delete(id);
                }
            }
			result.put("result", 1);
			result.put("message", "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 0);
			result.put("message", "删除失败");
		}
		RespUtil.renderJson(response, result);
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String brand_type = request.getParameter("brand_type");
		McBrandEntity mcBrandEntity = new McBrandEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			mcBrandEntity = this.mcBrandService.find(lid);
			McProductClassEntity proen = this.mcproductclassService
					.find(Long.parseLong(mcBrandEntity.getMc_productclass()));
			McMajorEntity itemMajor = null;
			if (proen != null && !proen.isEmpty()) {
				itemMajor = mcMajorService.find(Long.parseLong(proen.getMc_major()));
			}

			model.addAttribute("itemmajor", itemMajor);
			model.addAttribute("item", mcBrandEntity);
		}

		if (brand_type != null && brand_type.equals("brandshow")) {
			return TEM_PATH + "/mcBrand_enterprise";
		} else {
			return TEM_PATH + "/mcBrand_edit";
		}

	}

	/**
	 * 
	 * 方法:保存更新之后的McBrandEntity（本身实体 ） 传入参数:McBrandEntity的字段 id（更新实体的id）
	 * 传出参数:result（方法结果信息） 逻辑：查询该id的实体，存在则读取前台McBrandEntity（村本身实体
	 * ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditMcBrand(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String remark = ReqUtil.getString(request, "remark", "");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		// String mc_company = ReqUtil.getString(request, "mc_company", "");
		McBrandEntity OldMcBrandEntity = mcBrandService.find(id);
		// OldMcBrandEntity.setParentid(parentid);
		OldMcBrandEntity.setSort(sort);
		// OldMcBrandEntity.setMc_company(mc_company);
		OldMcBrandEntity.setName(name);
		OldMcBrandEntity.setRemark(remark);
		OldMcBrandEntity.setMc_productclass(mc_productclass);
		mcBrandService.update(OldMcBrandEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}
	/** 增加跳转 */
	@RequestMapping(value = "/deleteModel.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String deleteModel(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long productClassId = ReqUtil.getLong(request, "productClassId", 0l);
		Long industryClassId = ReqUtil.getLong(request, "industryClassId", 0l);
		model.addAttribute("industryClassId", industryClassId);
		model.addAttribute("productClassId", productClassId);
		return TEM_PATH + "/brandAndModel_edit";
	}
	/**
	 * 根据产品id和仪器Id查找对应的型号集合展示在前台
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getModelListByProductClsssIdAndIndustryClassId.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map<String,Object>> getModelListByProductClsssIdAndIndustryClassId(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long productClassId = ReqUtil.getLong(request, "productClassId", 0l);
		Long industryClassId = ReqUtil.getLong(request, "industryClassId", 0l);
		ProductclassEntity productclassEntity = productclassService.find(productClassId);
		IndustryClassEntity industryClassEntity = industryClassService.find(industryClassId);
		List<PbrandAndModelEntity> list = pbrandAndModelService.findList(
				Filter.eq("productclassEntity", productclassEntity),
				Filter.eq("industryClassEntity", industryClassEntity));
		if(list!=null&&list.size()>0){
			List<Map<String,Object>> list1=new ArrayList<>();
			for (PbrandAndModelEntity brandAndModelEntity : list) {
				Map map=new HashMap();
				map.put("id",brandAndModelEntity.getId());
				map.put("model",brandAndModelEntity.getModel());
				list1.add(map);
			}
			return list1;
		}else{
			return new ArrayList<>();
		}
	}
    @ResponseBody
    @RequestMapping(value = "/doDelete.jspx", method = { RequestMethod.POST, RequestMethod.GET })
    public Message doDelete(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            Long productClassId = ReqUtil.getLong(request, "productClassId", 0l);
            Long industryClassId = ReqUtil.getLong(request, "industryClassId", 0l);
            String[] brandModelLists = ReqUtil.getParams(request, "brandModelList[]");
            if(brandModelLists!=null&&brandModelLists.length>0){
                for (String id : brandModelLists) {
                    pbrandAndModelService.delete(Long.parseLong(id));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return  new Message(MessageTypeEnum.error,"删除失败,服务器出错了");
        }
        return  new Message(MessageTypeEnum.success,"删除成功");
    }
}
