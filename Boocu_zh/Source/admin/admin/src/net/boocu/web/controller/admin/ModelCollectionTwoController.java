package net.boocu.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.project.entity.McProductClassEntity;
import net.boocu.project.entity.ModelCollectionTwoEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.LookValService;
import net.boocu.project.service.McBrandService;
import net.boocu.project.service.McIndustryClassService;
import net.boocu.project.service.McMemberService;
import net.boocu.project.service.McModelService;
import net.boocu.project.service.McProductClassService;
import net.boocu.project.service.ModelCollectionTwoService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProducttypeService;
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

@Controller("userModelCollectionTwoController")
@RequestMapping("/admin/ModelCollectionTwo")

public class ModelCollectionTwoController {

	private static final String TEM_PATH = "/template/admin/modelcollectiontwo";

	@Resource
	MemberService memberService;
	
    @Resource(name = "adminServiceImpl")
	AdminService adminService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	ProductSubscribeService subscribeService;
	
	@Resource
	ModelCollectionTwoService modelCollectiontwoService;

	@Resource
	ProductService productService;
	
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
	
	@RequestMapping(value = "/toList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/modelcollectiontwolist";
	}

	/** 已分组 */
	@RequestMapping(value = "/getGroupData.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void getGroupData(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Map<String, Object>> topNoderows=modelCollectiontwoService.querycompanytworows(request, response, model);//查询条数
		List<Map> page=modelCollectiontwoService.querycompanytwo(request, response, model);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());//获取行总数
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
	@RequestMapping(value = "/add_modelcollectiontwo.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0l);
		model.addAttribute("item", memberGradeService.find(id));
		return TEM_PATH + "/modelcollectiontwo_add";
	}
	
	/** 执行增加用户组 *//*
	@RequestMapping(value = "/doAdd_memberGrade.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void doAdd_memberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
		String name = ReqUtil.getString(request, "name", "");
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = request.getParameter("major_product");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mc_model = ReqUtil.getString(request, "mc_model", "");
		String contact = ReqUtil.getString(request, "contact", "");
		String blame = ReqUtil.getString(request, "blame", "");
		
		ModelCollectionTwoEntity entity = new ModelCollectionTwoEntity();
		entity.setIds(random.createData(16));
		entity.setName(name);
		entity.setMc_industry_class(mc_industry_class);
		entity.setMajor_product(major_product);
		entity.setMc_productclass(mc_productclass);
		entity.setMc_model(mc_model);
		entity.setContact(contact);
		entity.setBlame(blame);
		entity.setCreateuser(adminService.getCurrentId().toString());
		modelCollectiontwoService.save(entity);
		
		//将选择的主营产品、常用仪器存入中间表
         String[] ary =major_product.split(",");
         String[] ary2 =mc_productclass.split(",");
         String[] ary3 =contact.split(",");
         for (String str : ary) {
        	 JdbcTemplate.update("insert into mc_change_major(mc_company,mc_major) values("+entity.getIds()+","+str+")");
		}
         for (String str2 : ary2) {
        	 JdbcTemplate.update("insert into mc_change_productclass(mc_company,mc_productclass) values("+entity.getIds()+","+str2+")");
		}
		for (String str3 : ary3) {
			JdbcTemplate.update("update mc_contacts set mc_company=" + entity.getIds() + " where id=" + str3);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "增加成功");
		RespUtil.renderJson(response, result);
	}*/
	   /**
     * 
     * 方法:将新添加的McModelEntity（本身实体 ）保存到数据库中
     * 传入参数:McModelEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台McModelEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveMcModel(HttpServletRequest request, HttpServletResponse response, Model model,ModelCollectionTwoEntity mcModelEntity) {
    	mcModelEntity.setIds(random.createData(16));
    	modelCollectiontwoService.save(mcModelEntity);
    	//将选择的主营产品、常用仪器存入中间表
        String[] ary =mcModelEntity.getMajor_product().split(",");
        String[] ary2 =mcModelEntity.getMc_productclass().split(",");
        //String[] ary3 =mcModelEntity.getContact().split(",");
       // String[] ary4 =mcModelEntity.getMc_industry_class().split(",");
        String[] ary5 =mcModelEntity.getMc_brand().split(",");
        
       if (mcModelEntity.getMajor_product() != "") {
        	for (String str : ary) {
        		int hr2=modelCollectiontwoService.insert_mc_change_major(mcModelEntity.getIds(),str);
        	}
		}
        if (mcModelEntity.getMc_productclass() != "") {
        	for (String str2 : ary2) {
        		int hr2=modelCollectiontwoService.insert_mc_change_productclass(mcModelEntity.getIds(),str2);
        	}
        }
        if (mcModelEntity.getMc_brand() != "") {
        	for (String str4 : ary5) {
        		int hr2=modelCollectiontwoService.insert_mc_change_brand(mcModelEntity.getIds(),str4);
        	}
        }
        /*if (mcModelEntity.getMc_industry_class() != "") {
        	for (String str4 : ary4) {
        		JdbcTemplate.update("insert into mc_change_industry(mc_company,mc_industry) values("+mcModelEntity.getIds()+","+str4+")");
        	}
        }*/
       /* if (mcModelEntity.getContact() != "") {
        	for (String str3 : ary3) {
        		JdbcTemplate.update("update mc_contacts set mc_company=" + mcModelEntity.getIds() + " where id=" + str3);
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
		ModelCollectionTwoEntity modelentiy=	modelCollectiontwoService.find(id);
		model.addAttribute("item", modelentiy);
		return TEM_PATH + "/modelcollectiontwo_edit";
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
		String mc_industry_class = ReqUtil.getString(request, "mc_industry_class", "");
		String major_product = request.getParameter("major_product");
		String mc_productclass = ReqUtil.getString(request, "mc_productclass", "");
		String mc_brand = ReqUtil.getString(request, "mc_brand", "");
		String mc_model = ReqUtil.getString(request, "mc_model", "");
		String contact = ReqUtil.getString(request, "contact", "");
		String blame = ReqUtil.getString(request, "blame", "");
		
		
		ModelCollectionTwoEntity entity = modelCollectiontwoService.find(id);
		//entity.setName(name);
		//entity.setMc_industry_class(mc_industry_class);
		entity.setMajor_product(major_product);
		entity.setMc_productclass(mc_productclass);
		entity.setMc_brand(mc_brand);
		//entity.setMc_model(mc_model);
		//entity.setContact(contact);
		//entity.setBlame(blame);
		entity.setModifyuser(adminService.getCurrentId().toString());
		
		//将选择的主营产品、常用仪器存入中间表
		String[] ary =major_product.split(",");
		String[] ary2 =mc_productclass.split(",");
		//String[] ary3 =contact.split(",");
		 //String[] ary4 =mc_industry_class.split(",");
		 String[] ary5 =mc_brand.split(",");
		if (major_product != "" && major_product != null) {
			for (String str : ary) {
				int hr=modelCollectiontwoService.delete_mc_change_major(str);
				int hr2=modelCollectiontwoService.insert_mc_change_major(entity.getIds(),str);
			}
		}
		if (mc_productclass != "" && mc_productclass != null) {
			for (String str2 : ary2) {
				int hr=modelCollectiontwoService.delete_mc_change_productclass(str2);
				int hr2=modelCollectiontwoService.insert_mc_change_productclass(entity.getIds(),str2);
			}
		}
		if (mc_brand != "" && mc_brand != null) {
			for (String str3 : ary5) {
				int hr=modelCollectiontwoService.delete_mc_change_brand(str3);
				int hr2=modelCollectiontwoService.insert_mc_change_brand(entity.getIds(),str3);
			}
		}
		/* if (mc_industry_class != "") {
	        	for (String str4 : ary4) {
	        		JdbcTemplate.update("insert into mc_change_industry(mc_company,mc_industry) values("+entity.getIds()+","+str4+")");
	        	}
	        }*/
		/*for (String str3 : ary3) {
			JdbcTemplate.update("update mc_contacts set mc_company=" + entity.getIds() + " where id=" + str3);
		}*/
		
		modelCollectiontwoService.update(entity);
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
					modelCollectiontwoService.delete(id);
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
    @RequestMapping(value="/get_modelcollectiontwo_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, MemberGradeEntity memberGradeEntity) {
    	List<ModelCollectionTwoEntity> memberGradeEntities = modelCollectiontwoService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(ModelCollectionTwoEntity item : memberGradeEntities){
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
    	return modelCollectiontwoService.register(edit_name, name, request, session);
    	}
	
}
