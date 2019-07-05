package net.boocu.web.service.impl;

import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import net.boocu.web.controller.admin.McBrandAndModelController;
import net.boocu.web.controller.admin.SuCompanyNameController;
import net.boocu.web.controller.admin.SuContactsController;
import net.boocu.web.controller.admin.SuMemberController;
import net.boocu.web.dao.SuModelCollectionDao;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.service.AdminService;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;


@Service("SumodelCollectionServiceImpl")
public class SuModelCollectionServiceImpl extends BaseServiceImpl<SuModelCollectionEntity, Long> implements SuModelCollectionService {
	@Resource(name = "adminServiceImpl")
	AdminService adminService;
	
	@Resource
	private SuMemberService suMemberService;
	
	@Resource
	private SuContactsController sucontactsController;

	@Resource
	private McBrandAndModelController mcBrandAndModelController;//注入McBrandAndModelController对象
	
	@Resource
	private SuCompanyNameController sucompanynameController;
	
	@Resource
	private SuMemberController sumemberController;

	@Resource
	private ProductclassService productclassService;

	@Resource
	private ProductBrandService productBrandService;

	@Resource
	private IndustryClassService industryClassService;

	@Resource
	private McBrandAndModelService mcBrandAndModelService;//注入McBrandAndModelService对象

	@Resource(name = "suModelCollectionDaoImpl")
    private SuModelCollectionDao suModelCollectionDao;
    
    @Resource(name = "suModelCollectionDaoImpl")
    public void setBaseDao(SuModelCollectionDao memberGradeDao) {
    	super.setBaseDao(memberGradeDao);
    }

	@Override
	public List<Map<String, Object>> querysucompanyrows(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		return suModelCollectionDao.querysucompanyrows(request, response, model);//查询数据条数
	}

	@Override
	public List<Map> querysucompany(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> topNode=suModelCollectionDao.querysucompany(request, response, model);//查询数据
		
		List<Map> resultList2 = new ArrayList<>();
		for (Map<String, Object> entity : topNode) {
			
			Map<String, Object> item = new HashMap<String, Object>();
			
			//SuIndustryClassEntity suindustry = null;
			/*Map suindustry = null;
			if (!(String.valueOf(entity.get("su_industry_class")).isEmpty()) && entity.get("su_industry_class") != null) {
				//suindustry=suIndustryClassService.find(Long.parseLong(String.valueOf(entity.get("su_industry_class"))));//通过行业分类id查询其信息
				suindustry=suIndustryClassController.getNodeData2(String.valueOf(entity.get("su_industry_class")));//根据产品id组，查询对应name（名称）及其信息
			}*/
			Map	sumember1 = null;
            Map	sumember2 = null;
			if (!(String.valueOf(entity.get("blame")).isEmpty())  && entity.get("blame") != null) {
				sumember1=sumemberController.getNodeData2(String.valueOf(entity.get("blame")));//根据常用责任人的id组，查询对应name（名称）
			}
            if (!(String.valueOf(entity.get("blame_buy")).isEmpty())  && entity.get("blame_buy") != null) {
                sumember2=sumemberController.getNodeData2(String.valueOf(entity.get("blame_buy")));//根据常用责任人的id组，查询对应name（名称）
            }
			
			
			/*Map	major = null;
			if (!(String.valueOf(entity.get("major_product")).isEmpty())   && entity.get("major_product") != null) {
				major=sumajorController.getNodeData2(String.valueOf(entity.get("major_product")));//根据产品id组，查询对应name（名称）及其信息
			}
			Map	products = null;
			if (!(String.valueOf(entity.get("su_productclass")).isEmpty())  && entity.get("su_productclass") != null) {
				products=suproductclassController.getNodeData2(String.valueOf(entity.get("su_productclass")));//根据产品id组，查询对应name（名称）及其信息
			}*/
		/*	Map	brand = null;
			if (!(String.valueOf(entity.get("su_brand")).isEmpty())  && entity.get("su_brand") != null) {
				brand=subrandController.getNodeData2(String.valueOf(entity.get("su_brand")));//根据产品id组，查询对应name（名称）及其信息
			}
			Map	model_two22 = null;
			if (!(String.valueOf(entity.get("su_model")).isEmpty())  && entity.get("su_model") != null) {
				model_two22=sumodelController.getNodeData2(String.valueOf(entity.get("su_model")));//根据产品id组，查询对应name（名称）及其信息
			}*/
			Map	contacts = null;
			if (!(String.valueOf(entity.get("contact")).isEmpty()) && entity.get("contact") != null) {
				contacts=sucontactsController.getNodeData2(String.valueOf(entity.get("contact")));//根据常用联系人的id组，查询对应name（名称）
			}
			Map mc_brand_and_models = null;
			if (entity.get("su_brand_and_model") != null && !(String.valueOf(entity.get("su_brand_and_model")).isEmpty())) {
				String su_brand_and_model = String.valueOf(entity.get("su_brand_and_model"));
				mc_brand_and_models = mcBrandAndModelController.getNodeData2(su_brand_and_model);//根据仪器品牌和型号表的id组，查询对应数据
			}
			Map	companyname = null;
			if (!(String.valueOf(entity.get("name")).isEmpty()) && entity.get("name") != null) {
				companyname=sucompanynameController.getNodeData2(String.valueOf(entity.get("name")));//根据常用联系人的id组，查询对应name（名称）
			}
			
			
			
			
			if (entity.get("id") != null && entity.get("id") != "") {
				item.put("id", entity.get("id"));
			}
			if (companyname != null && companyname.get("id") != null && companyname.get("id") != "") {
				item.put("name_id", companyname.get("id"));
			}
			if (companyname != null && companyname.get("name") != null && companyname.get("name") != "") {
				item.put("name", companyname.get("name"));
			}
			
			/*Object major_product_two2="";
			Object major_product_name2="";
			if (major != null && major.get("id") != null && major.get("name") != "") {
				major_product_two2=major.get("id");
				major_product_name2=major.get("name");
			}
			item.put("major_product", major_product_two2);
			item.put("major_product_name", major_product_name2);
			
		
			Object su_productclass2="";
			Object su_productclass_name2="";
			if (products != null && products.get("id") != null && products.get("name") != "") {
				su_productclass2=products.get("id");
				su_productclass_name2=products.get("name");
			}
			item.put("su_productclass", su_productclass2);
			item.put("su_productclass_name", su_productclass_name2);*/
			
			/*Object su_suindustry2="";
			Object su_suindustry_name2="";
			if (suindustry != null && suindustry.get("id") != null && suindustry.get("name") != "") {
				su_suindustry2=suindustry.get("id");
				su_suindustry_name2=suindustry.get("name");
			}*/
			//item.put("su_industry_class", su_suindustry2);
			//item.put("su_industry_class_name", su_suindustry_name2);


		/*	Object su_model2="";
			Object su_model_name2="";
			if (model_two22 != null && model_two22.get("id") != null && model_two22.get("name") != "") {
				su_model2= model_two22.get("id");
				su_model_name2  = model_two22.get("name");
			}
				item.put("su_model", su_model2);
				item.put("su_model_name", su_model_name2);
			
			
			Object brand2="";
			Object brand_name2="";
			if (brand != null && brand.get("id") != null && brand.get("name") != "") {
				brand2 = brand.get("id");
				brand_name2 = brand.get("name");
			}
				item.put("brand", brand2);
				item.put("brand_name", brand_name2);*/
			
			
			
			/*if (entity.get("su_industry_class") != null && entity.get("su_industry_class") != "") {
				item.put("su_industry_class", entity.get("su_industry_class"));
			}
			if (suindustry != null && suindustry.getName() != null && suindustry.getName() != "") {
				item.put("su_industry_class_name", suindustry.getName());
			}*/
			
			if (entity.get("su_industry_class") != null && entity.get("su_industry_class") != "") {//供应商的行业分类
				item.put("su_industry_class_name", entity.get("su_industry_class"));
			}
			if (entity.get("type") != null && entity.get("type") != "") {//性质
				item.put("type", entity.get("type"));
			}
//		    if ((entity.get("major_product") != null) && (entity.get("major_product") != "")) {//主营产品名称
//		        //item.put("major_product_name", entity.get("major_product"));
//				Object major_product = entity.get("major_product");
//				String[] split = major_product.toString().split(",");
//				String name = "";
//				for (int i = 0; i<split.length; i++) {
//					String s = split[i];
//					boolean numeric = isNumeric(s);
//					if (numeric) {//是否都为数字
//						ProductclassEntity productclassEntity = productclassService.find(Long.parseLong(s));
//						name += productclassEntity.getName()+",";
//					}
//				}
//				item.put("major_product_name", name);
//
//
//			}
			if ((entity.get("major_product_type") != null) && (entity.get("major_product_type") != "")) {//主营产品型号
				item.put("major_product_type", entity.get("major_product_type"));
			}
		    if ((entity.get("su_productclass") != null) && (entity.get("su_productclass") != "")) {//常用仪器名称
		        item.put("su_productclass_name", entity.get("su_productclass"));
		    }
			if ((entity.get("su_productclass_type") != null) && (entity.get("su_productclass_type") != "")) {//常用仪器型号
				item.put("su_productclass_type", entity.get("su_productclass_type"));
			}

			String su_brand_and_model = (String) entity.get("su_brand_and_model");//这里需要强制转换成String类型
			if (su_brand_and_model != null && !su_brand_and_model.equals("")) {//字符串与字符串之间用.equals()判断,不能使用 ==
				String mcBrandAndModel = "";
				String suproductclass_name="";
				String suproductclass_nameId="";
				String su_companyId = "";
				ProductBrandEntity productBrandEntity =null;
				ProductclassEntity productclassEntity =null;

				String[] split = su_brand_and_model.toString().split(",");
				HashMap<String, String> productclassMap = new HashMap<>();
				for (int i = 0;i<split.length;i++) {
					String s = split[i];
					if (isNumeric(s)) {
						McBrandAndModelEntity mcBrandAndModelEntity = mcBrandAndModelService.find(Long.parseLong(s));
						String su_brand = mcBrandAndModelEntity.getMc_brand();

						suproductclass_nameId = mcBrandAndModelEntity.getMc_productclass_name();
						su_companyId = mcBrandAndModelEntity.getMc_company();
						productclassMap.put(suproductclass_nameId,su_companyId);

						productBrandEntity = productBrandService.find(Long.parseLong(su_brand));
						String su_productclass_number = mcBrandAndModelEntity.getMc_productclass_number();
						if (!su_productclass_number.equals("1")) {
							mcBrandAndModel += productBrandEntity.getName() +"  "+ mcBrandAndModelEntity.getMc_model() +"、"+ su_productclass_number +" ; ";
						} else {
							mcBrandAndModel += productBrandEntity.getName() +"  "+ mcBrandAndModelEntity.getMc_model() +",";

						}
					}
				}
				if (suproductclass_nameId != null && !suproductclass_nameId.equals("")) {
					Set<Map.Entry<String, String>> entries = productclassMap.entrySet();
					Iterator<Map.Entry<String, String>> iterator = entries.iterator();
					String key = "";
					while (iterator.hasNext()) {
						key = iterator.next().getKey();
						productclassEntity = productclassService.find(Long.parseLong(key));
						suproductclass_name += productclassEntity.getName()+"; ";
					}
				}

				item.put("su_brand_and_models", mcBrandAndModel);
				item.put("suproductclass_name",suproductclass_name);
			}

			if (entity.get("su_brand") != null && entity.get("su_brand") != "") {
				//item.put("su_brand", entity.get("su_brand"));
				//item.put("major_product_name", entity.get("major_product"));
				Object su_brand = entity.get("su_brand");
				String[] split = su_brand.toString().split(",");
				String name = "";
				for (int i = 0; i<split.length; i++) {
					String s = split[i];
					boolean numeric = isNumeric(s);
					if (numeric) {//是否都为数字
						ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(s));
						name += productBrandEntity.getName()+",";
					}
				}
				item.put("brand_name", name);

			}

			if (entity.get("su_model") != null && entity.get("su_model") != "") {
				item.put("su_model", entity.get("su_model"));
			}

			if (entity.get("subscribe_content") != null && entity.get("subscribe_content") != "") {//订阅内容
				item.put("su_subscribe_content", entity.get("subscribe_content"));
			}

			if (contacts != null && contacts.get("id") != null && contacts.get("id") != "") {
				item.put("contact", contacts.get("id"));
			}
			if (contacts != null && contacts.get("name") != null && contacts.get("name") != "") {
				item.put("contact_name", contacts.get("name"));
			}

			if (mc_brand_and_models != null && !mc_brand_and_models.get("id").equals("")) {
				item.put("mc_brand_and_models",mc_brand_and_models.get("id"));
			}
			if (mc_brand_and_models != null && !mc_brand_and_models.get("mc_model").equals("")){
				String[] mc_brands = (String[]) mc_brand_and_models.get("mc_brand");
				ArrayList<String> mc_brand_name = new ArrayList<>();
				for (int i=0;i<mc_brands.length;i++) {
					ProductBrandEntity productBrandEntity = productBrandService.find(Long.parseLong(mc_brands[i]));
					mc_brand_name.add(productBrandEntity.getName());
				}
				item.put("mc_brand_name", mc_brand_name);
				item.put("mc_model", mc_brand_and_models.get("mc_model"));
			}
//			if (entity.get("blame_buy") != null && entity.get("blame_buy") != "") {//采购负责人
//				item.put("blame_buy", entity.get("blame_buy"));
//			}
//			if (entity.get("blame") != null && entity.get("blame") != "") {//跟单负责人
//				item.put("blame", entity.get("blame"));
//			}
			if (sumember2 != null && sumember2.get("name") != null && sumember2.get("name") != "") {//采购负责人
				item.put("blame_buy", sumember2.get("name"));
			}
			if (sumember1 != null && sumember1.get("name") != null && sumember1.get("name") != "") {//跟单负责人
				item.put("blame_name", sumember1.get("name"));
			}

			if (entity.get("createuser") != null && entity.get("createuser") != "") {
				item.put("createuser", entity.get("createuser"));
			}
			if (entity.get("create_date") != null && entity.get("create_date") != "") {
				item.put("create_date", entity.get("create_date"));
			}

			if (entity.get("modify_date") != null && entity.get("modify_date") != "") {
				item.put("modify_date", entity.get("modify_date"));
			}
			
			if (entity.get("is_accept_email") != null && entity.get("is_accept_email") != "") {
				item.put("is_accept_email", entity.get("is_accept_email"));
			}
			if (entity.get("grade") != null && entity.get("grade") != "") {
				item.put("grade", entity.get("grade"));
			}
			if (entity.get("measurement_time") != null && entity.get("measurement_time") != "") {
				item.put("measurement_time", entity.get("measurement_time"));
			}
			if (entity.get("rate") != null && entity.get("rate") != "") {
				item.put("rate", entity.get("rate"));
			}
			if (entity.get("website") != null && entity.get("website") != "") {
				item.put("su_website", entity.get("website"));
			}
			resultList2.add(item);
		}
		return resultList2;
	}

	public static boolean isNumeric(String str) {//判断是否为数字的字符串的方法,用于区分主营产品是修改后的id字符串还是修改前的文本
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}


	@Override
	public List<Map<String, Object>> register(String name) {
		return suModelCollectionDao.register(name);
	}
}
