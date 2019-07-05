package net.boocu.web.controller.admin;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.ProducWantRepairEntity;
import net.boocu.project.entity.ProductBuyEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductRentEntity;
import net.boocu.project.entity.ProductRepairEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.ProductWantRentEntity;
import net.boocu.project.service.MailSignatureService;
import net.boocu.project.service.McMemberService;
import net.boocu.project.service.ProducWantRepairService;
import net.boocu.project.service.ProductBuyService;
import net.boocu.project.service.ProductRentService;
import net.boocu.project.service.ProductRepairService;
import net.boocu.project.service.ProductSaleService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductWantRentService;
import net.boocu.project.util.JsonUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.controller.admin.productmng.ProductWantRentSelfController;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberEntity.MemberShipEnum;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.random.random;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tencent.common.MD5;


/**
 * 会员审核
 * 
 * @author deng
 *
 * 2015年10月29日     
 */
@Controller("memberInviteController")
@RequestMapping("/admin/member/memberInvite")
public class MemberInviteController {
	
	private static final String TEM_PATH ="/template/admin/member/memberInvite";
	
	@Resource
	MemberService memberService;
	
	@Resource
	MemberGradeService memberGradeService;
	
	@Resource
	ProductService productService;
	
	@Resource
	MailSignatureService mailSignatureService;
	
	@Resource
	McMemberService mcMemberService;
	
	@Resource
	ProductSaleService productSaleService;
	
	@Resource
	ProductBuyService productBuyService;
	
	@Resource
	ProductRentService productRentService;
	
	@Resource
	ProductWantRentService productWantRentService;
	
	@Resource
	ProductRepairService productRepairService;
	
	@Resource
	ProducWantRepairService productWantRepairService;	
	
	@RequestMapping(value="toMemberInviteList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/memberInvitelist";
	}
	
	
	
	@RequestMapping(value="inviteData.json",method={RequestMethod.POST,RequestMethod.GET})
	public void auditData(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		//int enabled = ReqUtil.getInt(request, "enabled", 5);
		//String gride = ReqUtil.getString(request, "gride", "");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			sortValue = sortValue.replace("item", "memberEntity");
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		//params.put("enabled", enabled);
		//params.put("gride", gride);
		params.put("invite", 1);
		Page<MemberEntity> page = memberService.findMemberAuditPage(pageable, params);
		List<MemberEntity> memberEntities = page.getCont();
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		for(MemberEntity item : memberEntities){
			Map<String, Object> map = new HashMap<String,Object>();
				JsonConfig jsonConfig = new JsonConfig();
				jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
					@Override
					public boolean apply(Object source, String username, Object arg2) {
						return arg2==null || "memberEntity".equals(username) ;
					}
				});
				map.put("item",JsonUtil.getJsonObjFor121(item, jsonConfig));
				
				//查询邮件签名信息
				if (item.getMailSignatureId() != 0) {
					map.put("mailSignatureId",mailSignatureService.find(item.getMailSignatureId()).getName());
				}else{
					map.put("mailSignatureId", "");
				}
				
				//查询邀请人信息
				if (item.getBlame() != 0) {
					map.put("blame",mcMemberService.find(item.getBlame()).getName());
				}else{
					map.put("blame", "");
				}
				
				if(item.getLoginDate() !=null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String loginDate = sdf.format(item.getLoginDate());
					map.put("loginDate", loginDate);
				}else{
					map.put("loginDate", "");
				}
				map.put("enabled", item.getEnabled());
			resultList.add(map);
		}
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}
    
	//跳转到实体添加页面
    @RequestMapping(value = "/add_mcInvite.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_McMajor(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/memberInvite_add";
    	
    } 
	
    //新增
	 @ResponseBody
	 @RequestMapping(value = "/doAdd_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	 public Map<String,Object> saveMcMajor(HttpServletRequest request, HttpServletResponse response, Model model,MemberEntity memberEntity) {

		 String userType = ReqUtil.getString(request, "userType", "");
		 String memberShip = ReqUtil.getString(request, "memberShip", "");
	     long memberGradeId = ReqUtil.getLong(request, "memberGradeId", 0l);
	     
		 // 新建会员
	     MemberEntity member = new MemberEntity();
	     memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()));
	     memberEntity.setEnabled(3);
	     memberEntity.setInvite(1);
	     memberEntity.setFirst(1);
	     memberEntity.setUserType(UserTypeEnum.valueOf(userType));
	     memberEntity.setMemberShip(MemberShipEnum.valueOf(memberShip));
	     MemberGradeEntity memberGradeEntity = memberGradeService.find(memberGradeId);
 		if(memberGradeEntity !=null){
 			memberEntity.setMemberGradeEntity(memberGradeEntity);
 		}  
	     BeanUtils.copyProperties(memberEntity, member);
	     memberService.save(member);
		 
	    Map<String, Object> result = new HashMap<String, Object>(); 
	    result.put("result", 1);
	    result.put("message", "保存成功");
	     return result;
	    } 
	
	
	
	
    
    //查看会员详细信息
    @RequestMapping(value = "/look_memberInvite.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String lookmemberAudit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	MemberEntity memberEntity = memberService.find(id);
    	if(memberEntity.getMemberShip() ==MemberShipEnum.personal){
    		model.addAttribute("item", memberEntity);
        	return TEM_PATH+"/memberInvite_personal";
    	}else{
    		model.addAttribute("item", memberEntity);
        	return TEM_PATH+"/memberInvite_enterprise";
    	}
    } 
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberInvite.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editMemberAudit(HttpServletRequest request, HttpServletResponse response, Model model) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	MemberEntity memberEntity = memberService.find(id);
    	if(memberEntity != null){
    		model.addAttribute("item", memberEntity);
    	}
    	
    	return TEM_PATH+"/memberInvite_edit";
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductRepairEntity（村本身实体 ）
     * 传入参数:ProductRepairtEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductRepairEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_memberInvite.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditAutoTest(HttpServletRequest request, HttpServletResponse response, Model model,MemberEntity memberEntity) {
    	long id = ReqUtil.getLong(request, "id", 0l);
    	//获取到页面的基本信息
    	String username = ReqUtil.getString(request, "username", "");
    	String realName = ReqUtil.getString(request, "realName", "");
    	String userType = ReqUtil.getString(request, "userType", "");
    	long memberGradeId = ReqUtil.getLong(request, "memberGradeId", 0l);
    	long mailSignatureId = ReqUtil.getLong(request, "mailSignatureId", 0l);
    	String password = ReqUtil.getString(request, "password", "");
    	
    	MemberEntity memberEntity2 = memberService.find(id);
    	if(memberEntity2 != null){
    		memberEntity.setUsername(username);
    		memberEntity.setRealName(realName);
    		memberEntity.setUserType(UserTypeEnum.valueOf(userType));
    		memberEntity.setMailSignatureId(mailSignatureId);
    		memberEntity.setPassword(DigestUtils.md5Hex(password));
    		memberEntity.setEnabled(3);
    		memberEntity.setInvite(1);
    		MemberGradeEntity memberGradeEntity = memberGradeService.find(memberGradeId);
    		if(memberGradeEntity !=null){
    			memberEntity.setMemberGradeEntity(memberGradeEntity);
    		}   		
        	memberService.update(memberEntity);
    	}
    	
    	Map<String,Object> result = new HashMap<String, Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	return result;
    } 
    
    //删除
    @RequestMapping(value="/delete_memberInvite.jspx",method={RequestMethod.POST, RequestMethod.GET})
    public void delete_memberAudit(HttpServletRequest request, HttpServletResponse response, Model model){
    	String[] idStrings = request.getParameterValues("item.id");
    	Long[] ids = new Long[idStrings.length];
    	
    	for(int i=0; i<ids.length; i++){
    		ids[i] = Long.parseLong(idStrings[i]);
    	}
    	
    	if(ids !=null && ids.length>0){
    		//删除会员所发布的数据
    		for(Long id : ids){
    			MemberEntity memberEntity = memberService.find(id);
    			if(memberEntity !=null){
    				List<ProductEntity> productEntities = productService.findList(Filter.eq("memberEntity", memberEntity));
        			if(productEntities !=null && productEntities.size() !=0){
        				productService.deleteList(productEntities);	
        				//删除其余表里的数据
            			for(ProductEntity item : productEntities){
            				//删除销售表里的数据
            				ProductSaleEntity productSaleEntity = productSaleService.find(item.getId());
            				if(productSaleEntity !=null){
                				productSaleService.delete(productSaleEntity.getId());	
            				}
            				//删除求购表里的数据
            				ProductBuyEntity productBuyEntity = productBuyService.find(item.getId());
            				if(productBuyEntity !=null){
            					productBuyService.delete(productBuyEntity.getId());	
            				}
            				//删除租赁表里的数据
            				ProductRentEntity productRentEntity = productRentService.find(item.getId());
            				if(productRentEntity !=null){
            					productRentService.delete(productRentEntity.getId());	
            				}
            				//删除求租里的数据
            				ProductWantRentEntity productWantRentEntity = productWantRentService.find(item.getId());
            				if(productWantRentEntity !=null){
            					productWantRentService.delete(productWantRentEntity.getId());	
            				}
            				//删除维修里的数据
            				ProductRepairEntity productRepairEntity = productRepairService.find(item.getId());
            				if(productRepairEntity !=null){
            					productRepairService.delete(productRepairEntity.getId());	
            				}
            				//删除求修里的数据
            				ProducWantRepairEntity productWantRepairEntity = productWantRepairService.find(item.getId());
            				if(productWantRepairEntity !=null){
            					productWantRepairService.delete(productWantRepairEntity.getId());
            				}
            			}    						
        			}
    	    		//删除会员表
    	    		memberService.deleteList(ids);
    			}   			
    		}
    	}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "删除成功");
		RespUtil.renderJson(response, result);
    }
}
