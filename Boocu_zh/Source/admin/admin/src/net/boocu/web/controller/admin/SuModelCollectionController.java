package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.random.random;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.boocu.web.setting.security.SecuritySetting;

@Controller("userSuModelCollectionController")
@RequestMapping("/admin/SuModelCollection")

public class SuModelCollectionController {

	private static final String TEM_PATH = "/template/admin/sumodelcollection";

	@Resource
	private McBrandAndModelService mcBrandAndModelService;

	@Resource
	MemberService memberService;

	@Resource(name = "adminServiceImpl")
	AdminService adminService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	ProductSubscribeService subscribeService;

	@Resource
	SuModelCollectionService suModelCollectionService;

	@Resource
	ProductService productService;

	/*@Resource
	private SuIndustryClassService suIndustryClassService;

	@Resource
	private SuIndustryClassController suIndustryClassController;*/

	@Resource
	private SuMemberService suMemberService;

	/*@Resource
	private SuProductClassService suproductclassService;

	@Resource
	private SuProductclassController suproductclassController;

	@Resource
	private SuBrandController subrandController;

	@Resource
	private SuModelController sumodelController;*/

	@Resource
	private SuContactsController sucontactsController;

	/*@Resource
	private SuMajorController sumajorController;*/

	@Resource
	private SuCompanyNameController sucompanynameController;

	@Resource
	private SuMemberController sumemberController;

	@Resource
	ProducttypeService typeService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@Autowired
	private LookValService lookValService;

	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/sumodelcollectionlist";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> topNoderows=suModelCollectionService.querysucompanyrows(request, response, model);//查询条数
		List<Map> page=suModelCollectionService.querysucompany(request, response, model);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());
		result.put("rows", page);
		RespUtil.renderJson( response, result);

	}

	/** 未分组 */
	@RequestMapping(value = "/getNoGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getNoGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		Pageable pageable = new Pageable(pagenumber, rows);
		Map<String, Object> htMap = new HashMap<String, Object>();
		htMap.put("appr", 2);
		Page<ProductSubscribeEntity> page = subscribeService.findAllSubByMember(pageable, htMap);
		List<MemberEntity> newList = new ArrayList<>();
		List<ProductSubscribeEntity> memberEntities = page.getCont();
		List<Map> resultList = new ArrayList<>();
		for (ProductSubscribeEntity entity : memberEntities) {
			if (newList.contains(entity.getMemberEntity())) {
				continue;
			}
			newList.add(entity.getMemberEntity());
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", entity.getId());
			item.put("userId", entity.getMemberEntity().getId());
			item.put("username", entity.getMemberEntity().getUsername());
			item.put("email", entity.getMemberEntity().getEmail());
			item.put("loginDate", DateTimeUtil.formatDatetoString(entity.getMemberEntity().getLoginDate()));
			resultList.add(item);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}

	/** 增加跳转 */
	@RequestMapping(value = "/add_sumodelcollection.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/sumodelcollection_add";
	}

	/** 执行增加用户组 *//*
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doAdd_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = ReqUtil.getString(request, "name", "");
		String su_industry_class = ReqUtil.getString(request, "su_industry_class", "");
		String major_product = request.getParameter("major_product");
		String su_productclass = ReqUtil.getString(request, "su_productclass", "");
		String su_model = ReqUtil.getString(request, "su_model", "");
		String contact = ReqUtil.getString(request, "contact", "");
		String blame = ReqUtil.getString(request, "blame", "");

		SuModelCollectionEntity entity = new SuModelCollectionEntity();
		entity.setIds(random.createData(16));
		entity.setName(name);
		entity.setSu_industry_class(su_industry_class);
		entity.setMajor_product(major_product);
		entity.setSu_productclass(su_productclass);
		entity.setSu_model(su_model);
		entity.setContact(contact);
		entity.setBlame(blame);
		entity.setCreateuser(adminService.getCurrentId().toString());
		suModelCollectionService.save(entity);

		//将选择的主营产品、常用仪器存入中间表
         String[] ary =major_product.split(",");
         String[] ary2 =su_productclass.split(",");
         String[] ary3 =contact.split(",");
         for (String str : ary) {
        	 JdbcTemplate.update("insert into su_change_major(su_company,su_major) values("+entity.getIds()+","+str+")");
		}
         for (String str2 : ary2) {
        	 JdbcTemplate.update("insert into su_change_productclass(su_company,su_productclass) values("+entity.getIds()+","+str2+")");
		}
		for (String str3 : ary3) {
			JdbcTemplate.update("update su_contacts set su_company=" + entity.getIds() + " where id=" + str3);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "增加成功");
		RespUtil.renderJson(response, result);
	}*/
	/**
	 *
	 * 方法:将新添加的SuModelEntity（本身实体 ）保存到数据库中
	 * 传入参数:SuModelEntity的字段
	 * 传出参数:result（方法结果信息）
	 * 逻辑：接收前台SuModelEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	public Map<String,Object> saveSuModel(HttpServletRequest request, HttpServletResponse response, Model model,SuModelCollectionEntity suModelEntity) {
		suModelEntity.setIds(random.createData(16));
		suModelEntity.setIsAcceptEmail("0");
		suModelEntity.setRate("2");

		String[] su_brands = ReqUtil.getParams(request, "su_brand6[]");
		String[] su_models = ReqUtil.getParams(request, "su_model6[]");
		String[] su_productclass_numbers = ReqUtil.getParams(request, "su_productclass_number6[]");
		String[] su_productclass_names = ReqUtil.getParams(request, "su_productclass_name6[]");
		String name = suModelEntity.getName();

		if (su_brands != null) {
			McBrandAndModelEntity mcBrandAndModelEntity=null;
			for (int i=0;i<su_brands.length;i++) {
				if (!su_brands[i].equals("")) {
					mcBrandAndModelEntity= new McBrandAndModelEntity();//多个对象时要重新new，所以放在for循环里面
					mcBrandAndModelEntity.setMc_brand(su_brands[i]);
					mcBrandAndModelEntity.setMc_model(su_models[i]);
					mcBrandAndModelEntity.setMc_productclass_number(su_productclass_numbers[i]);
					mcBrandAndModelEntity.setMc_productclass_name(su_productclass_names[i]);
					mcBrandAndModelEntity.setMc_company(suModelEntity.getName());//获取公司名称id并保存
					mcBrandAndModelEntity.setCompany_type(2);
					mcBrandAndModelService.save(mcBrandAndModelEntity);//保存用save方法，用update方法会报空
				}
			}
		}
		String su_brand_and_model="";//定义一个字符串保存McBrandAndModelEntity对象的id
		if (name != null && !name.equals("")) {//根据公司名称表的id去mc_brand_and_model表查询数据该公司添加过的仪器品牌和型号
			List<McBrandAndModelEntity> su_companyId = mcBrandAndModelService.findList(Filter.eq("mc_company", name));
			if (su_companyId.size()!=0) {
				for (int i = 0;i < su_companyId.size();i++){
					su_brand_and_model += su_companyId.get(i).getId()+",";
				}
			}
		}
		if (su_brand_and_model != null && !su_brand_and_model.equals("")) {
			suModelEntity.setSu_brand_and_model(su_brand_and_model.substring(0,su_brand_and_model.length()-1));
		}

		if (suModelEntity.getSu_brand()=="") {//品牌判空
			suModelEntity.setSu_brand(null);
		}
		if (suModelEntity.getMajor_product()=="") {//主营产品名称判空
			suModelEntity.setMajor_product(null);
		}
		suModelCollectionService.save(suModelEntity);
		//将选择的主营产品、常用仪器存入中间表
       /* String[] ary =suModelEntity.getMajor_product().split(",");
        String[] ary2 =suModelEntity.getSu_productclass().split(",");
        String[] ary3 =suModelEntity.getContact().split(",");
        String[] ary4 =suModelEntity.getSu_industry_class().split(",");*/

       /* if (suModelEntity.getMajor_product() != "") {
        	for (String str : ary) {
        		JdbcTemplate.update("insert into su_change_major(su_company,su_major) values("+suModelEntity.getIds()+","+str+")");
        	}
		}
        if (suModelEntity.getSu_productclass() != "") {
        	for (String str2 : ary2) {
        		JdbcTemplate.update("insert into su_change_productclass(su_company,su_productclass) values("+suModelEntity.getIds()+","+str2+")");
        	}
        }
        if (suModelEntity.getSu_industry_class() != "") {
        	for (String str4 : ary4) {
        		JdbcTemplate.update("insert into su_change_industry(su_company,su_industry) values("+suModelEntity.getIds()+","+str4+")");
        	}
        }*/
       /* if (suModelEntity.getContact() != "") {
        	for (String str3 : ary3) {
        		JdbcTemplate.update("update su_contacts set su_company=" + suModelEntity.getIds() + " where id=" + str3);
        	}
        }*/



		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}





	/** 跳转修改 */
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		SuModelCollectionEntity modelentiy= suModelCollectionService.find(id);
		model.addAttribute("item", modelentiy);
		return TEM_PATH + "/sumodelcollection_edit";
	}

	/** 查看 */
	@RequestMapping(value = "/look_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity item=memberService.find(id);
		model.addAttribute("item", item);
		return TEM_PATH + "/memberSub_info";
	}

	/** 修改 */
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void save_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String website = ReqUtil.getString(request, "website", "");//公司网站
		String su_industry_class = ReqUtil.getString(request, "su_industry_class", "");
		String type = ReqUtil.getString(request, "type", "");//性质
		String major_product = request.getParameter("major_product");
		String major_product_type = request.getParameter("major_product_type");//主营设备的型号
		String su_productclass = ReqUtil.getString(request, "su_productclass", "");
		String su_productclass_type = ReqUtil.getString(request, "su_productclass_type", "");//常用仪器型号
		String su_brand = ReqUtil.getString(request, "su_brand", "");
		String subscribe_content = ReqUtil.getString(request, "subscribe_content", "");//订阅内容
		String su_model = ReqUtil.getString(request, "su_model", "");
		String[] su_brands = ReqUtil.getParams(request, "su_brand6[]");
		String[] su_models = ReqUtil.getParams(request, "su_model6[]");
		String[] su_productclass_numbers = ReqUtil.getParams(request, "su_productclass_number6[]");
		String[] su_productclass_names = ReqUtil.getParams(request, "su_productclass_name6[]");
		String contact = ReqUtil.getString(request, "contact", "");
		String blame_buy = ReqUtil.getString(request, "blame_buy", "");//采购责任人
		String blame = ReqUtil.getString(request, "blame", "");
		String grade = ReqUtil.getString(request, "grade", "");//等级
		String measurement_time = ReqUtil.getString(request, "measurement_time", "");//计量时间
		String isAcceptEmail = ReqUtil.getString(request, "isAcceptEmail", "0");
		String rate = ReqUtil.getString(request, "rate", "2");


		SuModelCollectionEntity entity = suModelCollectionService.find(id);
		String brandAndModel="";//定义一个字符串保存McBrandAndModelEntity对象的id
		McBrandAndModelEntity mcBrandAndModelEntity=null;
		if (su_brands != null) {
			for (int i=0;i<su_brands.length;i++) {
				if (!su_brands[i].equals("")) {
					mcBrandAndModelEntity= new McBrandAndModelEntity();//多个对象时要重新new，所以放在for循环里面
					mcBrandAndModelEntity.setMc_brand(su_brands[i]);
					mcBrandAndModelEntity.setMc_model(su_models[i]);
					mcBrandAndModelEntity.setMc_productclass_number(su_productclass_numbers[i]);
					mcBrandAndModelEntity.setMc_productclass_name(su_productclass_names[i]);
					mcBrandAndModelEntity.setMc_company(entity.getName());//获取公司名称id并保存
					mcBrandAndModelEntity.setCompany_type(2);
					mcBrandAndModelService.save(mcBrandAndModelEntity);//保存用save方法，用update方法会报空
					brandAndModel+=mcBrandAndModelEntity.getId()+",";
				}
			}
		}
		String mc_brand_and_model = entity.getSu_brand_and_model();
		if (mc_brand_and_model == null && brandAndModel != "") {
			//字符串剪切，String.substring(开始索引，结束索引);包头不包尾，把最后的,给剪掉
			entity.setSu_brand_and_model(brandAndModel.substring(0,brandAndModel.length()-1));
		} else if (brandAndModel != ""){
			//mc_company表中原来的mc_brand_and_model字段末尾是没有逗号,的，那就把有逗号的 brandAndModel 放在前面
			entity.setSu_brand_and_model(brandAndModel+entity.getSu_brand_and_model());
		}
		entity.setName(name);
		entity.setWebsite(website);//公司网站
		entity.setSu_industry_class(su_industry_class);
		entity.setType(type);//性质保存到数据库

		if (major_product=="") {//主营产品判空,如果修改后没选主营设备名称则赋值为null
			entity.setMajor_product(null);
		} else {
			entity.setMajor_product(major_product);
		}
		entity.setMajor_product_type(major_product_type);//主营产品型号
		entity.setSu_productclass(su_productclass);
		entity.setSu_productclass_type(su_productclass_type);//常用仪器型号
		if (su_brand.equals("")) {
			entity.setSu_brand(null);
		} else {
			entity.setSu_brand(su_brand);
		}
		entity.setSubscribe_content(subscribe_content);//订阅内容
		entity.setSu_model(su_model);
		entity.setContact(contact);
		entity.setBlame_buy(blame_buy);//采购责任人
		entity.setBlame(blame);
		entity.setGrade(grade);//等级
		entity.setMeasurement_time(measurement_time);//计量时间
		entity.setIsAcceptEmail(isAcceptEmail);
		entity.setRate(rate);
		entity.setModifyuser(adminService.getCurrentId().toString());
		
		/*//将选择的主营产品、常用仪器存入中间表
		String[] ary =major_product.split(",");
		String[] ary2 =su_productclass.split(",");
		String[] ary3 =contact.split(",");
		 String[] ary4 =su_industry_class.split(",");
		if (major_product != "" && major_product != null) {
			for (String str : ary) {
				JdbcTemplate.update("delete from su_change_major where su_major in("+str+")");
				JdbcTemplate.update("insert into su_change_major(su_company,su_major) values("+entity.getIds()+","+str+")");
			}
		}
		if (su_productclass != "" && su_productclass != null) {
			for (String str2 : ary2) {
				JdbcTemplate.update("delete from su_change_productclass where su_productclass in("+str2+")");
				JdbcTemplate.update("insert into su_change_productclass(su_company,su_productclass) values("+entity.getIds()+","+str2+")");
			}
		}
		 if (su_industry_class != "") {
	        	for (String str4 : ary4) {
	        		JdbcTemplate.update("insert into su_change_industry(su_company,su_industry) values("+entity.getIds()+","+str4+")");
	        	}
	        }*/
		/*for (String str3 : ary3) {
			JdbcTemplate.update("update su_contacts set su_company=" + entity.getIds() + " where id=" + str3);
		}*/

		suModelCollectionService.update(entity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "修改成功");
		RespUtil.renderJson(response, result);

	}


	/** 查看订阅信息 */
	@RequestMapping(value = "/look_subscribe.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_subscribe(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		List<ProductSubscribeEntity> subEntity = subscribeService
				.findList(Filter.eq("memberEntity", memberService.find(id)));

		for (ProductSubscribeEntity entity : subEntity) {
			String[] type =entity.getProType().split("、");
			String newType="";
			for (int i = 0; i < type.length; i++) {
				String s = type[i];
				if(i!=type.length-1){
					newType+=typeService.find(Long.valueOf(s)).getTypeName()+"、";
				}else{
					newType+=typeService.find(Long.valueOf(s)).getTypeName();
				}
			}
			entity.setProType(newType);
		}
		model.addAttribute("items", subEntity);
		return TEM_PATH + "/subscripbeView/user_subscribe_edit";
	}


	/** 执行删除用户组 */
	@RequestMapping(value = "/del_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_memberGrade(@RequestParam Long[] gradeId, HttpServletRequest request, HttpServletResponse response,
								Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		for (Long id : gradeId) {
			if (id != null && id != 0) {
				suModelCollectionService.delete(id);
			}
		}
		result.put("result", 1);
		result.put("message", "删除成功");

		RespUtil.renderJson(response, result);
	}



	/** 删除 */
	@RequestMapping(value = "/del_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberentity = memberService.find(id);
		List<ProductSubscribeEntity> subList= subscribeService.findList(Filter.eq("memberEntity", memberentity));
		subscribeService.deleteList(subList);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}

	/** 取得用户等级的集合*/
	@ResponseBody
	@RequestMapping(value="/get_sumodelcollection_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	public void getMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, MemberGradeEntity memberGradeEntity) {
		List<SuModelCollectionEntity> memberGradeEntities = suModelCollectionService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for(SuModelCollectionEntity item : memberGradeEntities){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
	}


	//查询公司名是否存在
	@ResponseBody
	@RequestMapping(value="/register.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	public Message register(String edit_name,String name,HttpServletRequest request,HttpSession session){
		if (edit_name != null && edit_name != "" && edit_name.equals(name)) {
			return Message.success("true");
		}else{
			List<Map<String, Object>> row=suModelCollectionService.register(name);
			if (row.size() > 0) {
				return Message.success("false");
			}
			return Message.success("true");
		}
	}

}
