package net.boocu.web.controller.admin;


import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.SuMemberEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.SuMemberService;
import net.boocu.project.service.ProductService;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;


/**
 * 


 * @author fang
 *
 * 2015年8月27日
 */
@Controller("suMemberController")
@RequestMapping("/admin/suMember")
public class SuMemberController {
	
	private static final String TEM_PATH ="/template/admin/suMember";
	
	@Resource
	private SuMemberService suMemberService;
	
	@Resource
	private MemberService memberService;
	
	@Resource
	private ProductService productService;
	
	@Resource
	private JdbcTemplate JdbcTemplate;
	
	@Resource(name = "rsaServiceImpl")
    private RSAService rsaService;
	
	@RequestMapping(value="toList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		// 密钥
        RSAPublicKey publicKey = rsaService.generateKey(request);

        model.addAttribute("captchaId", UUID.randomUUID().toString());
        model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
		return TEM_PATH+"/suMemberlist";
	}


	//跳转到实体添加页面
    @RequestMapping(value = "/add_suMember.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_SuMember(HttpServletRequest request, HttpServletResponse response, Model model) {
    	// 密钥
        RSAPublicKey publicKey = rsaService.generateKey(request);

        model.addAttribute("captchaId", UUID.randomUUID().toString());
        model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
    	return TEM_PATH+"/suMember_add";
    	
    } 
    
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public void getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "create_date", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("name", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		/*pageable.getFilters().add(Filter.eq("apprStatus", 1));
		pageable.getFilters().add(Filter.eq("isDel", 0));*/
		Page<SuMemberEntity> page = suMemberService.findPage(pageable);
		List<SuMemberEntity> resultList = page.getCont();
		
		List<Map> resultData = new ArrayList<Map>();
		for (SuMemberEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getId());
    		data.put("ids", item.getId());
    		data.put("name", item.getName());
    		data.put("sort", item.getSort());
    		data.put("username", item.getUsername());
    		data.put("status", item.getStatus());
    		data.put("create_date", item.getCreateDate());
    		data.put("usertype", item.getUsertype());
    		data.put("content", item.getContent());
    		data.put("phone", item.getPhone());
			resultData.add(data);
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultData); 
		RespUtil.renderJson(response, result);
    }
    
    
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	SuMemberEntity topNode =  suMemberService.find(1l);
    	return getNodeData(topNode);
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(SuMemberEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<SuMemberEntity> items = suMemberService.findList(flist, Sequencer.asc("sort"));
		for(SuMemberEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<SuMemberEntity> children = suMemberService.findList(Filter.eq("parentid", item.getId()));
			if(children.size() != 0){
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
    	return resultList;
    }
    
    //获取异步下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotree.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model){
    	Long id = ReqUtil.getLong(request, "id", 1l);
    	SuMemberEntity suMemberEntity = suMemberService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(suMemberEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", suMemberEntity.getId()));
        	List<SuMemberEntity> items = suMemberService.findList(flist, Sequencer.asc("sort"));
    		for(SuMemberEntity item : items){
    			Map<String,Object> map = new HashMap<String, Object>();
    			map.put("id", item.getId());
    			map.put("text", item.getName());
    			map.put("state", "0".equals(item.getLeaf())?"closed":"open");
    			resultList.add(map);
    		}
    	}
    	return resultList;
    }
    
    /**
     * 
     * 方法:将新添加的SuMemberEntity（本身实体 ）保存到数据库中
     * 传入参数:SuMemberEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台SuMemberEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveSuMember(HttpServletRequest request, HttpServletResponse response, Model model,SuMemberEntity suMemberEntity) {
    	/*MemberEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		suMemberEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	if(suMemberEntity.getParentid().isEmpty()){
    		suMemberEntity.setParentid("1");
    	}*/
    	suMemberEntity.setPassword(DigestUtils.md5Hex(suMemberEntity.getPassword()));
    	 // 删除RSA私钥
	     rsaService.removePrivateKey(request);
    	suMemberService.save(suMemberEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中SuMemberEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_suMember.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteSuMember(HttpServletRequest request, HttpServletResponse response, Model model,Long id) {
    	String[] idStrings = request.getParameterValues("id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		for(Long id1 : ids){
    			suMemberService.delete(id1);
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    }}
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editSuMember(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	// 密钥
        RSAPublicKey publicKey = rsaService.generateKey(request);

        model.addAttribute("captchaId", UUID.randomUUID().toString());
        model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
        model.addAttribute("exponent", Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
    	SuMemberEntity suMemberEntity = new SuMemberEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		suMemberEntity = this.suMemberService.find(lid);
    		model.addAttribute("item", suMemberEntity);
    	}
    	return TEM_PATH+"/suMember_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的SuMemberEntity（本身实体 ）
     * 传入参数:SuMemberEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台SuMemberEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/doEdit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditSuMember(HttpServletRequest request, HttpServletResponse response, Model model
    		) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
		String name = ReqUtil.getString(request, "name", "");
		String content = ReqUtil.getString(request, "content", "");
		String username = ReqUtil.getString(request, "username", "");
		String phone = ReqUtil.getString(request, "phone", "");
		String sort = ReqUtil.getString(request, "sort", "");
		String usertype = ReqUtil.getString(request, "usertype", "");
		String password = ReqUtil.getString(request, "password", "");
    	SuMemberEntity OldSuMemberEntity = suMemberService.find(id);
    	//OldSuMemberEntity.setParentid(parentid);
    	OldSuMemberEntity.setContent(content);
    	OldSuMemberEntity.setUsername(username);
    	if (!password.equals(OldSuMemberEntity.getPassword())) { 
    		OldSuMemberEntity.setPassword(DigestUtils.md5Hex(password));
		}
    	OldSuMemberEntity.setName(name);
    	OldSuMemberEntity.setSort(Integer.parseInt(sort));
    	OldSuMemberEntity.setPhone(phone);
    	OldSuMemberEntity.setUsertype(usertype);
    	suMemberService.update(OldSuMemberEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_sumember_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getSuMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, SuMemberEntity memberGradeEntity) {
    	List<SuMemberEntity> memberGradeEntities = suMemberService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(SuMemberEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
    
  //查询用户名是否存在
    @ResponseBody
    @RequestMapping(value="/register.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Message register(String edit_username,String username,HttpServletRequest request,HttpSession session){
    	 if (edit_username != null && edit_username != "" && edit_username.equals(username)) {
    		 return Message.success("true");
		 }else{
			 List<Map<String, Object>> row=suMemberService.register(username);
    		 if (row.size() > 0) {
    			 return Message.success("false");
    		 }
    		 return Message.success("true");
		}
    	}
    
	//根据常用联系人的id组，查询对应name（名称）
		public Map getNodeData2(String rootId) {
			return suMemberService.getNodeData2(rootId);
		}
    
}
