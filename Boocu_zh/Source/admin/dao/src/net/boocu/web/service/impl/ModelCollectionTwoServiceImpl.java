package net.boocu.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.ModelCollectionTwoEntity;
import net.boocu.project.service.ModelCollectionTwoService;
import net.boocu.web.Message;
import net.boocu.web.controller.admin.McBrandController;
import net.boocu.web.controller.admin.McMajorController;
import net.boocu.web.controller.admin.McProductclassController;
import net.boocu.web.dao.ModelCollectionTwoDao;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@Service("modelCollectionTwoServiceImpl")
public class ModelCollectionTwoServiceImpl extends BaseServiceImpl<ModelCollectionTwoEntity, Long> implements ModelCollectionTwoService {
	@Resource(name = "modelCollectionTwoDaoImpl")
    private ModelCollectionTwoDao modelCollectionTwoDao;
    
	@Resource
	private McProductclassController mcproductclassController;
	
	@Resource
	private McBrandController mcbrandController;
	
	@Resource
	private McMajorController mcmajorController;
	
    @Resource(name = "modelCollectionTwoDaoImpl")
    public void setBaseDao(ModelCollectionTwoDao memberGradeDao) {
    	super.setBaseDao(memberGradeDao);
    }

	@Override
	public List<Map> querycompanytwo(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> topNode=modelCollectionTwoDao.querycompanytwo(request, response, model);//查询数据
		List<Map> resultList2 = new ArrayList<>();
		for (Map<String, Object> entity : topNode) {
			
			Map<String, Object> item = new HashMap<String, Object>();
			
			Map	major = null;
			if (!(String.valueOf(entity.get("major_product")).isEmpty())   && entity.get("major_product") != null) {
				major=mcmajorController.getNodeData2(String.valueOf(entity.get("major_product")));//根据产品id组，查询对应name（名称）及其信息
			}
			Map	products = null;
			if (!(String.valueOf(entity.get("mc_productclass")).isEmpty())  && entity.get("mc_productclass") != null) {
				products=mcproductclassController.getNodeData2(String.valueOf(entity.get("mc_productclass")));//根据产品id组，查询对应name（名称）及其信息
			}
			Map	brand = null;
			if (!(String.valueOf(entity.get("mc_brand")).isEmpty())  && entity.get("mc_brand") != null) {
				brand=mcbrandController.getNodeData2(String.valueOf(entity.get("mc_brand")));//根据产品id组，查询对应name（名称）及其信息
			}
			
			if (entity.get("id") != null && entity.get("id") != "") {
				item.put("id", entity.get("id"));
			}
			Object major_product_two2="";
			Object major_product_name2="";
			if (major != null && major.get("id") != null && major.get("name") != "") {
				major_product_two2=major.get("id");
				major_product_name2=major.get("name");
			}
			item.put("major_product", major_product_two2);
			item.put("major_product_name", major_product_name2);
			
		
			Object mc_productclass2="";
			Object mc_productclass_name2="";
			if (products != null && products.get("id") != null && products.get("name") != "") {
				mc_productclass2=products.get("id");
				mc_productclass_name2=products.get("name");
			}
			item.put("mc_productclass", mc_productclass2);
			item.put("mc_productclass_name", mc_productclass_name2);
			Object brand2="";
			Object brand_name2="";
			if (brand != null && brand.get("id") != null && brand.get("name") != "") {
				brand2 = brand.get("id");
				brand_name2 = brand.get("name");
			}
				item.put("brand", brand2);
				item.put("brand_name", brand_name2);
			
			if (entity.get("create_date") != null && entity.get("create_date") != "") {
				item.put("create_date", entity.get("create_date"));
			}
			resultList2.add(item);
		}
		return resultList2;
	}

	@Override
	public List<Map<String, Object>> querycompanytworows(HttpServletRequest request, HttpServletResponse response, Model model) {
			return modelCollectionTwoDao.querycompanytworows(request, response, model);
	}

	@Override
	public Message register(String edit_name, String name, HttpServletRequest request, HttpSession session) {
		if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
   		 return Message.success("true");
		 }else{
			 List<Map<String, Object>> row=modelCollectionTwoDao.register(name);
   		 if (row.size() > 0) {
   			 return Message.success("false");
   		 }
   		 return Message.success("true");
		}
	}

	@Override
	public int delete_mc_change_major(String mc_productclass) {
		return modelCollectionTwoDao.delete_mc_change_major(mc_productclass);
	}

	@Override
	public int insert_mc_change_major(String ids, String mc_productclass) {
		return modelCollectionTwoDao.insert_mc_change_major(ids,mc_productclass);
	}

	@Override
	public int delete_mc_change_productclass(String mc_productclass) {
		return modelCollectionTwoDao.delete_mc_change_productclass(mc_productclass);
	}

	@Override
	public int insert_mc_change_productclass(String ids, String mc_productclass) {
		return modelCollectionTwoDao.insert_mc_change_productclass(ids,mc_productclass);
	}

	@Override
	public int delete_mc_change_brand(String mc_brand) {
		return modelCollectionTwoDao.delete_mc_change_brand(mc_brand);
	}

	@Override
	public int insert_mc_change_brand(String ids, String mc_brand) {
		return modelCollectionTwoDao.insert_mc_change_brand(ids,mc_brand);
	}
}
