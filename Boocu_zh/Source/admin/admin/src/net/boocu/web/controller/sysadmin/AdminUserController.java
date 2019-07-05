/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.sysadmin;

import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.framework.util.SettingUtils;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.RoleEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;
import net.boocu.web.service.RoleService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
 

/**
 * Controller - 会员管理
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminUserController")
@RequestMapping("/admin/core/admin_user")
public class AdminUserController {
 
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource(name = "rsaServiceImpl")
    private RSAService rsaService;

    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;
    
    @Resource(name = "memberServiceImpl")
    private MemberService memberService;

    @Resource
    private RoleService roleService;
    
    
   
    @RequestMapping(value = "/list.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String list(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return "/admin/core/admin/admin_user_list";
    	
    }
    
//    {
//        "total": 7,
//        "rows": [
//            {
//                "authList": [],
//                "dateline": 0,
//                "fields": {},
//                "founder": 1,
//                "password": "21232f297a57a5a743894a0e4a801fc3",
//                "remark": "",
//                "roleids": [],
//                "siteid": 0,
//                "state": 1,
//                "userdept": "", 
//                "userno": ""
//            }
//    }
    
    @RequestMapping(value = "/list/data.json",method ={ RequestMethod.POST, RequestMethod.GET})
    public void list_data(HttpServletRequest request, HttpServletResponse response, Model model) {
    	JSONObject json = new JSONObject();
		List<Map> adminList = new ArrayList<Map>();
		
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, 10);
    	
		Page<AdminEntity> page = adminService.findPage(pageable);
		List<AdminEntity> resultList = page.getCont();
		

		for(AdminEntity m : resultList){
			Map map = new HashMap();
			map.put("userid", m.getId());
			map.put("username", m.getUsername());
			map.put("realname", m.getName());
			map.put("lastlogin", m.getLoginDate());
//			map.put("logincount", m.getUsername());
	 
			adminList.add(map);
		}
		
		Map data = new HashMap();
		data.put("total",page.getTotal() );
		
		data.put("rows",adminList); 
		
		RespUtil.renderJson(response, data);
    }
    
    
    @RequestMapping(value = "/add_admin_user.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String addAdminUser(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return "/admin/core/admin/add_admin_user";
    	
    } 
    
    @ResponseBody
    @RequestMapping(value = "/save_admin_user.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveAdminUser(HttpServletRequest request, HttpServletResponse response, Model model,AdminEntity admin) {
 
//    	adminUser.username:test
//    	adminUser.password:test
//    	adminUser.founder:0
//    	adminUser.state:1
//    	adminUser.realname:test
//    	adminUser.userno:test
//    	adminUser.userdept:test
//    	adminUser.remark:test
 
    	String password = admin.getPassword();
    	
    	 
        if(password!=null && password.length()>0){
        	String pwd = DigestUtils.md5Hex(password); 
        	admin.setPassword(pwd);
        }
        
        this.adminService.save(admin);
        
    	Map<String,Object> result = new HashMap();
    	result.put("result", 1);
    	result.put("message", "操作成功");
  
    	return result;
    } 
    
    
    
    @RequestMapping(value = "/edit_admin_user.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String edit_admin_user(HttpServletRequest request, HttpServletResponse response, Model model) {

    	Long id = ReqUtil.getLong(request, "id", 0L);
    	
    	AdminEntity adminUser = this.adminService.find(id);
    	model.addAttribute("adminUser", adminUser);

    	
    	List<RoleEntity> rolesList = this.roleService.findAll();
    	
    	model.addAttribute("rolesList", rolesList);
    	
    	return "/admin/core/admin/edit_admin_user";
    } 

    
    @ResponseBody
    @RequestMapping(value = "/save_edit_admin_user.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditAdminUser(HttpServletRequest request, HttpServletResponse response, Model model,AdminEntity m) {
 
    	Long id = ReqUtil.getLong(request, "id", 0L);
    	String password = ReqUtil.getString(request, "newPassword", "123456");
    	//String password = m.getPassword();
    	
    	AdminEntity admin = this.adminService.find(id);
    	admin.setUsername(m.getUsername());
    	
        if(password!=null && password.length()>0){
        	String pwd = DigestUtils.md5Hex(password); 
        	admin.setPassword(pwd);
        }
        
 
        admin.setEmail(m.getEmail());
        admin.setName(m.getName());
      
        
        
        String[] roleids = ReqUtil.getParams(request, "roleids");
        Long[] roleidss = new Long[roleids.length];
        if(roleids!=null) {
        	for(int i =0;i<roleids.length;i++){
            	roleidss[i] = Long.valueOf(roleids[i]);
            }
        }
        
        if(roleids != null){
        	List<RoleEntity> rolesList = this.roleService.findList(roleidss);

        	Set<RoleEntity> set = new HashSet<RoleEntity> ();
        	
        	for(RoleEntity role : rolesList){
        		set.add(role);
        	}
        	
        	admin.setRoles(set);
        }
        	
    	
    	this.adminService.update(admin);
    	
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	
    	result.put("result", 1);
    	result.put("message","操作成功");
  
    	return result;
    } 
    
    //修改操作者本身的密码 add by fang 20150701
    @RequestMapping("updCurAdminPwd.jspx")
    public String toUpdAdminPwd(HttpServletRequest request, HttpServletResponse response, Model model){
    	//秘钥
    	RSAPublicKey publicKey = rsaService.generateKey(request);
    	model.addAttribute("modulus", Base64.encodeBase64String(publicKey.getModulus().toByteArray()));
    	model.addAttribute("exponent",Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray()));
    	
    	return "/admin/sys_platmng/list";
    }
    
    
    @ResponseBody
    @RequestMapping(value="save_edit_admin_pwd.jspx",method={RequestMethod.POST,RequestMethod.GET})
    public Map<String, Object> updAdminPwd(HttpServletRequest request, HttpServletResponse response, Model model){
    	//取得原密码,新密码1,新密码2
    	String strOldPwd = rsaService.decryptParameter("oldPwd", request);
    	String strNewPwd1 = rsaService.decryptParameter( "newPwd", request);
    	String strNewPwd2 = rsaService.decryptParameter( "newPwd2", request);
	
    	Map<String, Object> result = new HashMap<String, Object>();
    	AdminEntity adminEntity = adminService.getCurrent();
    	//验证旧密码, 新密码1,2 和新密码格式
    	if(!adminEntity.getPassword().equals(DigestUtils.md5Hex(strOldPwd))){
    		result.put("result", 0);
    		result.put("message", "原密码输入错误!");
    		return result;
    	}
    	if(!strNewPwd1.equals(strNewPwd2)){
    		result.put("result", 0);
    		result.put("message", "两次输入的新密码不一致!");
    		return result;
    	}
    	SecuritySetting setting = SettingUtils.get().getSecurity();
        if (!setting.verifyPasswordLength(strNewPwd1)) {
        	result.put("result", 0);
    		result.put("message", "新密码格式不准确");
    		return result;
        }
    	//upd
    	adminEntity.setPassword(DigestUtils.md5Hex(strNewPwd1));
    	adminService.update(adminEntity);
    	result.put("result", 1);
		result.put("message", "更改成功!");
		return result;

    }
    
}