package net.boocu.web.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.*;
import net.boocu.project.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.druid.support.json.JSONUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.enums.productType;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.random.random;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.sf.json.JSONObject;

@Controller("userModelCollectionController")
@RequestMapping("/admin/ModelCollection")

public class ModelCollectionController {

	private static final String TEM_PATH = "/template/admin/modelcollection";

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
	ModelCollectionService modelCollectionService;
	@Resource
	McContactsService mcContactsService;
	@Resource
	ProductService productService;
	@Autowired
	private DeleteLogService deleteLogService;
	@Resource
	private McIndustryClassService mcIndustryClassService;

	@Resource
	private McIndustryClassController mcIndustryClassController;

	@Resource
	private McMemberService mcMemberService;

	@Resource
	private McProductClassService mcproductclassService;

	@Resource
	private McProductclassController mcproductclassController;

	@Resource
	private McBrandController mcbrandController;

	@Resource
	private McModelController mcmodelController;

	@Resource
	private McContactsController mccontactsController;

	@Resource
	private McMajorController mcmajorController;

	@Resource
	private McCompanyNameController mccompanynameController;

	@Resource
	private McMemberController mcmemberController;

	@Resource
	ProducttypeService typeService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@Autowired
	private LookValService lookValService;

	@Resource
	private ProductSubscribeService productSubscribeService;

	@Resource
	private ProductclassService productclassService;
	
	@Resource
	private McCompanyNameService mcCompanyNameService;
	
	@Resource
	private MailSignatureService mailSignatureService;

	/**公司管理跳转*/
	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {

		AdminEntity admin = adminService.getCurrent();
		if ((admin.getId() == 1)
				|| (admin.getId() == 75)) {
			// model.addAttribute("showhandle", "false");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		} else {
			model.addAttribute("showhandle", "true");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		}
		return TEM_PATH + "/modelcollectionlist";
	}

	/**高校管理跳转*/
	@RequestMapping(value = "/UniversitiesList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String Universities(HttpServletRequest request, HttpServletResponse response, Model model) {

		AdminEntity admin = adminService.getCurrent();
		if ((admin.getId() == 1)
				|| (admin.getId() == 75)) {
			// model.addAttribute("showhandle", "false");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		} else {
			model.addAttribute("showhandle", "true");// 保存指令到会话，屏蔽非管理员会员的新增、删除按钮
		}
		return TEM_PATH + "/universities";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<Map<String, Object>> topNoderows = modelCollectionService.querycompanyrows(request, response, model);// 查询条数
		List<Map> page = modelCollectionService.querycompany(request, response, model);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());// 获取行总数
		result.put("rows", page);
		RespUtil.renderJson(response, result);
	}

	/** 未分组 */
	@RequestMapping(value = "/getNoGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getNoGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		String blame = ReqUtil.getString(request, "blame", "");
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

	/** 公司增加跳转 */
	@RequestMapping(value = "/add_modelcollection.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/modelcollection_add";
	}

	/** 高校增加跳转 */
	@RequestMapping(value = "/add_university.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_university(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/university_add";
	}
	/** 执行增加用户组 *//*
					
					 * 
					 * ModelCollectionEntity entity = new ModelCollectionEntity();
					 * entity.setIds(random.createData(16)); entity.setName(name);
					 * entity.setMc_industry_class(mc_industry_class);
					 * entity.setMajor_product(major_product);
					 * entity.setMc_productclass(mc_productclass); entity.setMc_model(mc_model);
					 * entity.setContact(contact); entity.setBlame(blame);
					 * entity.setCreateuser(adminService.getCurrentId().toString ());
					 * modelC * @RequestMapping(value = "/doAdd_memberGrade.jspx", method = {
					 * RequestMethod.POST, RequestMethod.GET }) public void
					 * doAdd_memberGrade(HttpServletRequest request, HttpServletResponse response,
					 * Model model) { String name = ReqUtil.getString(request, "name", ""); String
					 * mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
					 * String major_product = request.getParameter("major_product"); String
					 * mc_productclass = ReqUtil.getString(request, "mc_productclass", ""); String
					 * mc_model = ReqUtil.getString(request, "mc_model", ""); String contact =
					 * ReqUtil.getString(request, "contact", ""); String blame =
					 * ReqUtil.getString(request, "blame", "");ollectionService.save(entity);
					 * 
					 * //将选择的主营产品、常用仪器存入中间表 String[] ary =major_product.split(","); String[] ary2
					 * =mc_productclass.split(","); String[] ary3 =contact.split(","); for (String
					 * str : ary) { JdbcTemplate.update(
					 * "insert into mc_change_major(mc_company,mc_major) values("
					 * +entity.getIds()+","+str+")"); } for (String str2 : ary2) {
					 * JdbcTemplate.update(
					 * "insert into mc_change_productclass(mc_company,mc_productclass) values("
					 * +entity.getIds()+","+str2+")"); } for (String str3 : ary3) {
					 * JdbcTemplate.update( "update mc_contacts set mc_company=" + entity.getIds() +
					 * " where id=" + str3); } Map<String, Object> result = new HashMap<String,
					 * Object>(); result.put("result", 1); result.put("message", "增加成功");
					 * RespUtil.renderJson(response, result); }
					 */

	/**
	 * 
	 * 方法:将新添加的McModelEntity（本身实体 ）保存到数据库中 传入参数:McModelEntity的字段 传出参数:result（方法结果信息）
	 * 逻辑：接收前台McModelEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveMcModel(HttpServletRequest request, HttpServletResponse response, Model model, ModelCollectionEntity mcModelEntity) {

		mcModelEntity.setIds(random.createData(16));
		mcModelEntity.setCreateuser(adminService.getCurrentId().toString());
		mcModelEntity.setSubscribeStatus("0");

		String[] mc_brands = ReqUtil.getParams(request, "mc_brand6[]");
		String[] mc_models = ReqUtil.getParams(request, "mc_model6[]");
		String[] mc_productclass_numbers = ReqUtil.getParams(request, "mc_productclass_number6[]");
		String[] mc_productclass_names = ReqUtil.getParams(request, "mc_productclass_name6[]");
		String name = mcModelEntity.getName();

		if (mc_brands != null) {
			McBrandAndModelEntity mcBrandAndModelEntity=null;
			for (int i=0;i<mc_brands.length;i++) {
				if (!mc_brands[i].equals("")) {
					mcBrandAndModelEntity= new McBrandAndModelEntity();//多个对象时要重新new，所以放在for循环里面
					mcBrandAndModelEntity.setMc_brand(mc_brands[i]);
					mcBrandAndModelEntity.setMc_model(mc_models[i]);
					mcBrandAndModelEntity.setMc_productclass_number(mc_productclass_numbers[i]);
					mcBrandAndModelEntity.setMc_productclass_name(mc_productclass_names[i]);
					mcBrandAndModelEntity.setMc_company(name);//获取公司名称id并保存
					mcBrandAndModelEntity.setCompany_type(1);
					mcBrandAndModelService.save(mcBrandAndModelEntity);//保存用save方法，用update方法会报空

				}
			}
		}
		String mc_brand_and_model="";//定义一个字符串保存McBrandAndModelEntity对象的id
		if (name != null && !name.equals("")) {//根据公司名称表的id去mc_brand_and_model表查询数据该公司添加过的仪器品牌和型号
			List<McBrandAndModelEntity> mc_companyId = mcBrandAndModelService.findList(Filter.eq("mc_company", name));
			if (mc_companyId.size()!=0) {
				for (int i = 0;i < mc_companyId.size();i++){
					mc_brand_and_model += mc_companyId.get(i).getId()+",";
				}
			}
		}
		if (mc_brand_and_model != null && !mc_brand_and_model.equals("")) {
			mcModelEntity.setMc_brand_and_model(mc_brand_and_model.substring(0,mc_brand_and_model.length()-1));
		}

        String[] contacts = mcModelEntity.getContact().split(",");
        if (contacts.length!=0) {
            McContactsEntity mcContactsEntity = mcContactsService.find(Long.valueOf(contacts[0]));//通过公司对象里的contact的id获取联系人表对象
            mcContactsEntity.setTeacher(request.getParameter("teacher"));
            mcContactsEntity.setMajor(request.getParameter("major"));
            mcContactsEntity.setResume(request.getParameter("resume"));
        }
        if (mcModelEntity.getMajor_product()=="") {//如果添加高校没选研究方向产品名称时设置为null
        	mcModelEntity.setMajor_product(null);
		}
		if (mcModelEntity.getMc_productclass()=="") {//如果添加高校或者公司没选仪器名称/研究方向产品名称时设置为null
			mcModelEntity.setMc_productclass(null);
		}
		if (mcModelEntity.getIntroducer()=="") {
			mcModelEntity.setIntroducer(null);
		}
		if (mcModelEntity.getLaboratory()=="") {
			mcModelEntity.setLaboratory(null);
		}
        modelCollectionService.save(mcModelEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/** 跳转公司修改 */
	@RequestMapping(value = "/edit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		ModelCollectionEntity modelentiy = modelCollectionService.find(id);
		model.addAttribute("item", modelentiy);
		return TEM_PATH + "/modelcollection_edit";
	}

	/** 跳转高校修改 */
	@RequestMapping(value = "/edit_university.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_university(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		ModelCollectionEntity modelentiy = modelCollectionService.find(id);

        String[] contacts = modelentiy.getContact().split(",");
        if (contacts.length!=0) {//联系人非空判断
            //根据id查询对象,再根据对象.get方法获取专业
            McContactsEntity mcContactsEntity = mcContactsService.find(Long.valueOf(contacts[0]));
            if (mcContactsEntity!=null) {
                String teacher = mcContactsEntity.getTeacher();
                String major = mcContactsEntity.getMajor();
                String resume = mcContactsEntity.getResume();
                //通过下面的方式存到model对象里面就可以在前台获取到值了
                model.addAttribute("teacher",teacher);
                model.addAttribute("major",major);
                model.addAttribute("resume",resume);
            }

        }


        model.addAttribute("item", modelentiy);
		return TEM_PATH + "/university_edit";
	}

	/** 查看 */
	@RequestMapping(value = "/look_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String look_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity item = memberService.find(id);
		model.addAttribute("item", item);
		return TEM_PATH + "/memberSub_info";
	}
	public static void main(String[] args) {
		System.out.println(DigestUtils.md5Hex("123456"));
	}
	/** 修改 */
	@RequestMapping(value = "/doEdit_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void save_membersub(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String laboratory = ReqUtil.getString(request, "laboratory", "");
		String website = ReqUtil.getString(request, "website", "");
        String major = ReqUtil.getString(request, "major", "");// 专业
        String teacher = ReqUtil.getString(request, "teacher", "");//老师姓名
        String resume = ReqUtil.getString(request, "resume", "");//简历
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = request.getParameter("major_product");
		String research_direction = request.getParameter("research_direction");//研究方向
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
//		String mc_brand = ReqUtil.getString(request, "mc_brand", "");
//		String mc_model = ReqUtil.getString(request, "mc_model", "");
		String[] mc_brands = ReqUtil.getParams(request, "mc_brand6[]");
		String[] mc_models = ReqUtil.getParams(request, "mc_model6[]");
		String[] mc_productclass_numbers = ReqUtil.getParams(request, "mc_productclass_number6[]");
		String[] mc_productclass_names = ReqUtil.getParams(request, "mc_productclass_name6[]");
		String contact = ReqUtil.getString(request, "contact", "");
		String blame = ReqUtil.getString(request, "blame", "");
		String introducer = ReqUtil.getString(request, "introducer", "");// 介绍人
		String remark = ReqUtil.getString(request, "remark", "");// 备注
		String grade = ReqUtil.getString(request, "grade", "");
		String measurement_time = ReqUtil.getString(request, "measurement_time", "");
		String ckZt = ReqUtil.getString(request, "ckZt", "");
		String rate = ReqUtil.getString(request, "rate", "");
		String subscribe_content=ReqUtil.getString(request, "subscribeContent", "");
		ModelCollectionEntity entity = modelCollectionService.find(id);

		String brandAndModel="";//定义一个字符串保存McBrandAndModelEntity对象的id

		if (mc_brands != null) {
			McBrandAndModelEntity mcBrandAndModelEntity=null;
			for (int i=0;i<mc_brands.length;i++) {
				if (!mc_brands[i].equals("")) {
					mcBrandAndModelEntity= new McBrandAndModelEntity();//多个对象时要重新new，所以放在for循环里面
					mcBrandAndModelEntity.setMc_brand(mc_brands[i]);
					mcBrandAndModelEntity.setMc_model(mc_models[i]);
					mcBrandAndModelEntity.setMc_productclass_number(mc_productclass_numbers[i]);
					mcBrandAndModelEntity.setMc_productclass_name(mc_productclass_names[i]);
					mcBrandAndModelEntity.setMc_company(entity.getName());//获取公司名称id并保存
					mcBrandAndModelEntity.setCompany_type(1);//客户或者高校
					mcBrandAndModelService.save(mcBrandAndModelEntity);//保存用save方法，用update方法会报空
					brandAndModel+=mcBrandAndModelEntity.getId()+",";
				}
			}
		}
		String mc_brand_and_model = entity.getMc_brand_and_model();
		if (mc_brand_and_model == null && brandAndModel != "") {
			//字符串剪切，String.substring(开始索引，结束索引，包头不包尾，把最后的,给剪掉)
			entity.setMc_brand_and_model(brandAndModel.substring(0,brandAndModel.length()-1));
		} else if (brandAndModel != ""){
			//mc_company表中原来的mc_brand_and_model字段末尾是没有逗号,的，那就把有逗号的 brandAndModel 放在前面
			entity.setMc_brand_and_model(brandAndModel+entity.getMc_brand_and_model());
		}
//		for (String mc_brand:mc_brands) {
//			mcBrandAndModelEntity.setMc_brand(mc_brand);
//		}
//		for (String mc_model:mc_models) {
//			mcBrandAndModelEntity.setName(mc_model);
//		}


		String[] contacts =entity.getContact().split(",");
        if (contacts.length!=0) {//联系人非空判断
            //获取联系人对象
            McContactsEntity mcContactsEntity = mcContactsService.find(Long.valueOf(contacts[0]));//取第一个联系人
            mcContactsEntity.setMajor(major);//把专业存到数据库
            mcContactsEntity.setTeacher(teacher);//把老师姓名存到数据库
            mcContactsEntity.setResume(resume);//把简历存到数据库
        }

        entity.setSubscribeContent(subscribe_content);
		entity.setName(name);
		entity.setWebsite(website);//公司网站

		entity.setMc_industry_class(mc_industry_class);

		if (major_product == "") {//如果修改后去除了研究方向产品名称则赋值为null
			entity.setMajor_product(null);
		} else {
			entity.setMajor_product(major_product);
		}
		if (research_direction == "") {
			entity.setResearch_direction(null);
		} else {
			entity.setResearch_direction(research_direction);
		}
		entity.setResearch_direction(research_direction);//研究方向
		if (mc_productclass == "") {
			entity.setMc_productclass(null);//设值为null，为了避免数据库的（mc_company表）mc_productclass字段出现空字符串导致所有客户信息都无法展示
		} else {
			entity.setMc_productclass(mc_productclass);
		}

		if (entity.getIntroducer() == "") {
			entity.setIntroducer(null);
		}
		if (entity.getLaboratory() == "") {
			entity.setLaboratory(null);
		} else {
			entity.setLaboratory(laboratory); // 实验室
		}

		entity.setContact(contact);
		entity.setBlame(blame);
		entity.setIntroducer(introducer); // 介绍人
		entity.setRemark(remark); // 备注
		entity.setGrade(grade);
		entity.setMeasurement_time(measurement_time);
		entity.setModifyuser(adminService.getCurrentId().toString());
		entity.setCkZt(ckZt);
		entity.setRate(rate);

		
		/*
		 * //将选择的主营产品、常用仪器存入中间表 String[] ary =major_product.split(","); String[] ary2
		 * =mc_productclass.split(","); String[] ary3 =contact.split(","); String[] ary4
		 * =mc_industry_class.split(","); if (major_product != "" && major_product !=
		 * null) { for (String str : ary) {
		 * JdbcTemplate.update("delete from mc_change_major where mc_major in("
		 * +str+")"); JdbcTemplate.update(
		 * "insert into mc_change_major(mc_company,mc_major) values("
		 * +entity.getIds()+","+str+")"); } } if (mc_productclass != "" &&
		 * mc_productclass != null) { for (String str2 : ary2) { JdbcTemplate.update(
		 * "delete from mc_change_productclass where mc_productclass in(" +str2+")");
		 * JdbcTemplate.update(
		 * "insert into mc_change_productclass(mc_company,mc_productclass) values("
		 * +entity.getIds()+","+str2+")"); } } if (mc_industry_class != "") { for
		 * (String str4 : ary4) { JdbcTemplate.update(
		 * "insert into mc_change_industry(mc_company,mc_industry) values("
		 * +entity.getIds()+","+str4+")"); } }
		 */
		/*
		 * for (String str3 : ary3) { JdbcTemplate.update(
		 * "update mc_contacts set mc_company=" + entity.getIds() + " where id=" +
		 * str3); }
		 */

		modelCollectionService.update(entity);
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
			String[] type = entity.getProType().split("、");
			String newType = "";
			for (int i = 0; i < type.length; i++) {
				String s = type[i];
				if (i != type.length - 1) {
					newType += typeService.find(Long.valueOf(s)).getTypeName() + "、";
				} else {
					newType += typeService.find(Long.valueOf(s)).getTypeName();
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
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			ModelCollectionEntity modelCollectionEntity=null;
			String name = "";
			String blame = "";
			McCompanyNameEntity mcCompanyNameEntity=null;
			DeleteLogEntity deleteLogEntity = null;
			AdminEntity adminEntity = null;
			for (Long id : gradeId) {
				deleteLogEntity = new DeleteLogEntity();
                modelCollectionEntity = modelCollectionService.find(id);
                if (modelCollectionEntity != null) {
					if (id != null && id != 0) {
						//拿到name属性去公司名称表获得公司名称对象
						name = modelCollectionEntity.getName();
						//公司名称id
						deleteLogEntity.setDelete_mc_company_name_id(name);

						mcCompanyNameEntity = mcCompanyNameService.find(Long.parseLong(name));
						//把公司名存到实体类
						name = mcCompanyNameEntity.getName();
						deleteLogEntity.setDelete_mc_company_name(name);

						//联系人id
						name = modelCollectionEntity.getContact();
						deleteLogEntity.setDelete_mc_contact_id(name);

						//责任人
						name = modelCollectionEntity.getBlame();
						adminEntity = adminService.find(Long.parseLong(name));
						name = adminEntity.getName();
						deleteLogEntity.setDelete_blame(name);

						//操作人
						adminEntity = adminService.getCurrent();
						name = adminEntity.getName();
						deleteLogEntity.setOperator(name);

						//操作人电脑ip地址
						name = InetAddress.getLocalHost().getHostAddress();//获取的是局域网内的ip地址
						//name = ReqUtil.getIpAddr(request);//获取到的是127.0.0.1
						deleteLogEntity.setOperator_address_ip(name);

						//操作人的计算机名
						name = InetAddress.getLocalHost().getHostName();
						deleteLogEntity.setOperator_host_name(name);

						//把数据存到delete_log表里面
						deleteLogService.save(deleteLogEntity);
						modelCollectionService.delete(modelCollectionEntity.getId());
					}
				}
            }
			result.put("result", 1);
			result.put("message", "删除成功");
			RespUtil.renderJson(response, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** 执行分配客户 */
	@RequestMapping(value = "/allocate_customer.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void allocate_customer (@RequestParam Long[] gradeId, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Long id : gradeId) {
			if (id != null && id != 0) {
				ModelCollectionEntity modelCollectionEntity = modelCollectionService.find(id);
				String mode_blame = request.getParameter("mode_blame");//获取搜索框里面的责任人的id
				modelCollectionEntity.setBlame(mode_blame);//设置责任人
				modelCollectionService.update(modelCollectionEntity);//执行更新逻辑
			}
		}
		result.put("result", 1);
		result.put("message", "客户分配成功");
		RespUtil.renderJson(response, result);
	}

	/** 删除 */
	@RequestMapping(value = "/del_membersub.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void del_membersub(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		MemberEntity memberentity = memberService.find(id);
		List<ProductSubscribeEntity> subList = subscribeService.findList(Filter.eq("memberEntity", memberentity));
		subscribeService.deleteList(subList);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
	}

	/** 取得用户等级的集合 */
	@ResponseBody
	@RequestMapping(value = "/get_modelcollection_names.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model,
			MemberGradeEntity memberGradeEntity) {
		List<ModelCollectionEntity> memberGradeEntities = modelCollectionService.findAll();
		List<Map> resultList = new ArrayList<Map>();
		for (ModelCollectionEntity item : memberGradeEntities) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}

	// 查询公司名是否存在
	@ResponseBody
	@RequestMapping(value = "/register.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Message register(String edit_name, String name, HttpServletRequest request, HttpSession session) {
		return modelCollectionService.register(edit_name, name, request, session);
	}

	// 导出excel表
	@ResponseBody
	@RequestMapping(value = "/orderExport.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void orderExport(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		modelCollectionService.orderExport(request, session, response);
	}
	// 批量导入
	@ResponseBody
	@RequestMapping(value = "/doImport.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doImport(@RequestParam("file") MultipartFile file,HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IllegalStateException, IOException {
		//modelCollectionService.orderExport(request, session, response);
	
		
		long  startTime=System.currentTimeMillis();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
       //CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
       //        request.getSession().getServletContext());
       //检查form中是否有enctype="multipart/form-data"
		 // 上传文件 获得文件名(全路径)
		Map<String, Object> map = new HashMap<String, Object>();
	    String fileName = file.getOriginalFilename();
	    if (file.isEmpty()) {
	        map.put("errorMsg", "空文件");
	        JSONObject jo = JSONObject.fromObject(map);
	        response.setContentType("text/html");
	        response.setCharacterEncoding("utf-8");
	        response.getWriter().write(jo.toString());
	        return;
	    }
	    String substring = fileName.substring(fileName.lastIndexOf("."));
	    String blame = fileName.substring(0,fileName.lastIndexOf("."));
	    //负责人
	    List<AdminEntity> findBlameList = adminService.findList(Filter.eq("name", blame));
	    /*if(findBlameList.size()<=0) {
	    	 map.put("errorMsg", "没有"+blame+"这个业务员");
	        JSONObject jo = JSONObject.fromObject(map);
	        response.setContentType("text/html");
	        response.setCharacterEncoding("utf-8");
	        response.getWriter().write(jo.toString());
	        return;
	    }*/
	    Workbook book=null;
	    if(substring.equals(".xlsx")) {
	    	book=new XSSFWorkbook(file.getInputStream());
	    }else {
	    	book=new HSSFWorkbook(file.getInputStream());
	    }
	    Sheet sheetAt = book.getSheetAt(0);
	    for(int i=1;i<sheetAt.getLastRowNum();i++) {
	    	Row row = sheetAt.getRow(i);
	    	if(row!=null&&StringUtils.isNotBlank(parseExcel(row.getCell(1)))) {
	    		//List<ModelCollectionEntity> modelCollectionList = modelCollectionService.findAll();
				List<McCompanyNameEntity> mcCompanyNameList = mcCompanyNameService.findAll();
	    		McCompanyNameEntity newNameEntity=new McCompanyNameEntity();
		    	ModelCollectionEntity newEntity=new ModelCollectionEntity();
		    	McContactsEntity contactsEntity=new McContactsEntity();
	    		newEntity.setMc_industry_class("学校");
	    		String name = parseExcel(row.getCell(0));
	    		if(name!=null&&!(boolean)contains(mcCompanyNameList,name).get("isContains")) {//没有这家公司
	    			newNameEntity.setName(name);
					newNameEntity.setSort(1);
					mcCompanyNameService.save(newNameEntity);
					newEntity.setName(newNameEntity.getId().toString());
	    			String major_product = parseExcel(row.getCell(1));
	    			String teacher = parseExcel(row.getCell(2));
	    			String resume = parseExcel(row.getCell(3));
                    String contact = parseExcel(row.getCell(5));
	    			newEntity.setResearch_direction(contact);
	    			String job = parseExcel(row.getCell(6));
                    newEntity.setMc_productclass(job);
	    			String brand = parseExcel(row.getCell(7));
	    			newEntity.setMc_brand(brand);
	    			String con_name = parseExcel(row.getCell(9));
	    			contactsEntity.setName(con_name);
                    String co_job = parseExcel(row.getCell(10));
	    			contactsEntity.setJob(co_job);
                    contactsEntity.setTeacher(teacher);
	    			contactsEntity.setMajor(major_product);
	    			contactsEntity.setResume(resume);
                    String phone = parseExcel(row.getCell(11));
	    			contactsEntity.setPhone(phone);
                    String email = parseExcel(row.getCell(12));
	    			contactsEntity.setMail(email);
	    			contactsEntity.setSort(1);
	    			contactsEntity.setMc_company(newNameEntity.getId().toString());
	    			mcContactsService.save(contactsEntity);
                    String website = parseExcel(row.getCell(13));
	    			newEntity.setWebsite(website);
	    			newEntity.setContact(contactsEntity.getId().toString());
	    			newEntity.setBlame("1");
	    			newEntity.setGrade("9");
	    			newEntity.setMeasurement_time("1");
	    			newEntity.setRate("30");
	    			newEntity.setCkZt("2");
	    			modelCollectionService.save(newEntity);
	    		}else if(name!=null) {//有这家公司
	    			McCompanyNameEntity findNameEntity = mcCompanyNameService.findList(Filter.eq("name", name)).get(0);
	    			ModelCollectionEntity findEntity = modelCollectionService.find(Filter.eq("name", findNameEntity.getId()));
	    			if(findEntity!=null) {
                        findEntity.setBlame("1");
	    				String contact = parseExcel(row.getCell(2));
		    			String[] contactId = findEntity.getContact().split(",");
		    			int count=0;
		    			for (String id : contactId) {
		    				McContactsEntity find = mcContactsService.find(Long.valueOf(id));
		    				if(find!=null&&find.getName().equals(contact)) {
		    					count++;
		    				}
						}
		    			if(count==0) {//没有这个联系人
		    				McContactsEntity newContact=new McContactsEntity();
			    			String job = parseExcel(row.getCell(10));
			    			String phone = parseExcel(row.getCell(11));
                            String email = parseExcel(row.getCell(12));
                            String teacher = parseExcel(row.getCell(2));
                            String resume = parseExcel(row.getCell(3));
                            String major_product = parseExcel(row.getCell(1));
			    			newContact.setName(contact);
			    			newContact.setJob(job);
			    			newContact.setTeacher(teacher);
			    			newContact.setMajor(major_product);
			    			newContact.setResume(resume);
			    			newContact.setPhone(phone);
			    			newContact.setMail(email);
			    			newContact.setSort(1);
			    			newContact.setMc_company(findNameEntity.getId().toString());
			    			mcContactsService.save(newContact);
			    			findEntity.setContact(findEntity.getContact()+","+newContact.getId());
			    			modelCollectionService.update(findEntity);
		    			}
	    			}
	    		}
	    	}
	    }
       long  endTime=System.currentTimeMillis();
       System.out.println("上传耗时："+String.valueOf(endTime-startTime)+"ms");
       response.sendRedirect("/admin/home/index.jspx");
	}
	private Map contains(List<McCompanyNameEntity> mcCompanyNameList,String value) {
		Map map=new HashMap<>();
		map.put("isContains", false);
		if(value==null) {
			map.put("isContains", false);
			return map;
		}
		if(mcCompanyNameList==null||mcCompanyNameList.size()<=0) {
			map.put("isContains", false);
			return map;
		}
		for (McCompanyNameEntity nameEntity : mcCompanyNameList) {
			if(nameEntity.getName().equals(value)) {
				map.put("isContains", true);
				map.put("company", nameEntity);
				return map;
			}
		}
		return map;
	}
	private String parseExcel(Cell cell) {
		String result = new String();
		if(cell==null) {
			return result;
		}
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
		if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
		SimpleDateFormat sdf = null;
		if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
		.getBuiltinFormat("h:mm")) {
		sdf = new SimpleDateFormat("HH:mm");
		} else {// 日期
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		Date date = cell.getDateCellValue();
		result = sdf.format(date);
		} else if (cell.getCellStyle().getDataFormat() == 58) {
		// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		double value = cell.getNumericCellValue();
		Date date = org.apache.poi.ss.usermodel.DateUtil
		.getJavaDate(value);
		result = sdf.format(date);
		} else {
		double value = cell.getNumericCellValue();
		CellStyle style = cell.getCellStyle();
		DecimalFormat format = new DecimalFormat();
		String temp = style.getDataFormatString();
		// 单元格设置成常规
		if (temp.equals("General")) {
		format.applyPattern("#");
		}
		result = format.format(value);
		}
		break;
		case HSSFCell.CELL_TYPE_STRING:// String类型
		result = cell.getRichStringCellValue().toString();
		break;
		case HSSFCell.CELL_TYPE_BLANK:
		result = "";
		default:
		result = "";
		break;
		}
		return result;
		}
	// 跳转客户审核订阅页面 
	@RequestMapping(value = "/toMccompanyAuditList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		return "/template/admin/mccompany/mccompanyAudit/mccompanyAuditlist";
	}
	//审核订阅内容，确定要给客户推送了
	@RequestMapping(value = "/mcCompanyAudit/invocation.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void check(HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		String[] strid = request.getParameterValues("id");
    	Long[] ids = new Long[strid.length];
    	
    	for(int i=0;i<strid.length;i++){
    		ids[i] = Long.parseLong(strid[i]);
    	}
    	for(int i=0;i<ids.length;i++){
    		ModelCollectionEntity mcModelEntity = modelCollectionService.find(ids[i]);
    		// 审核时根据联系人的邮箱或者手机 ，找到member表里的记录,做相应订阅
    		String[] contactIds = mcModelEntity.getContact().split(",");
    		for (int index=0;index<contactIds.length; index++) {
				McContactsEntity contact = mcContactsService.find(Long.valueOf(contactIds[index]));
				if ((contact != null && StringUtils.isNotBlank(contact.getMail()))
						|| (contact != null && StringUtils.isNotBlank(contact.getPhone()))) {
			 		MemberEntity findByPhone = null;
					MemberEntity findByMail = null;
					if (StringUtils.isNotBlank(contact.getMail())) {
						findByMail = memberService.findByEmail(contact.getMail());
					}
					if (StringUtils.isNotBlank(contact.getPhone())) {
						findByPhone = memberService.findByMobile(contact.getPhone());
					}
					if (findByPhone !=null || findByMail != null) {
						AdminEntity find = adminService.find(Long.valueOf(mcModelEntity.getBlame()));
						List<MailSignatureEntity> findList2 = mailSignatureService.findList(Filter.eq("name", find.getName()));
						MemberEntity memberEntity=findByMail==null?findByPhone:findByMail;
						List<ProductSubscribeEntity> findList = subscribeService.findList(Filter.eq("memberEntity", memberEntity));
						if(findList.size()==0) {//没有订阅的情况下
							String[] mcProductClassList = mcModelEntity.getSubscribeContent().replaceAll("，", ",")
									.split(",");
							List<ProductEntity> ProductEntityList = new ArrayList<>();
							List<ProductclassEntity> ProductClassList = new ArrayList<>();
							for (String mcProductClass : mcProductClassList) {
								if (mcProductClassList!=null&&mcProductClassList.length>0) {
									if(memberEntity.getMemberGradeEntity().getId()==1000342502L) {//国外客户
										ProductEntityList = productService.findListByNameAndProNo(1,true,mcProductClassList);
										if(findList2!=null&&findList2.size()>0) {
											memberEntity.setMailSignatureId(findList2.get(0).getId());
										}else {
											memberEntity.setMailSignatureId(1);
										}	
									}else {
										ProductEntityList = productService.findListByNameAndProNo(1,false,mcProductClassList);
										if(findList2!=null&&findList2.size()>0) {
											memberEntity.setMailSignatureId(findList2.get(0).getId());
										}else {
											memberEntity.setMailSignatureId(1);
										}
									}
								}
								if (ProductEntityList.size() > 0) {
									break;
								}
							}
							if (ProductEntityList.size() != 0) {
								for(ProductEntity p:ProductEntityList) {
									ProductSubscribeEntity entity = new ProductSubscribeEntity();
									entity.setProType("1");
									entity.setProductEntity(p);
									entity.setSubscribeEmail("2-1");
									entity.setSubscribePlatform(2);
									entity.setIsDelete(0);
									entity.setMemberEntity(memberEntity);
									if(memberEntity.getMemberGradeEntity().getId()==1000342502L) {//国外客户
										entity.setSubscribeMobile("0");
									}else{
										entity.setSubscribeMobile("2-1");
									}
									subscribeService.save(entity);
								}
							}
						}
					}
				}
			}
    		mcModelEntity.setSubscribeStatus("1");
    		modelCollectionService.update(mcModelEntity);
    		}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "审核成功");
    	
    	RespUtil.renderJson(response, result);
	}
	
}
