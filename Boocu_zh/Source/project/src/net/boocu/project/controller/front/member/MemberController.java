/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front.member;

import java.text.ParseException;
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
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.RSAService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 

/**
 * Controller - 用户
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontMemberController")
@RequestMapping("/member")
public class MemberController {
 
    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    @Resource(name = "rsaServiceImpl")
    private RSAService rsaService;

    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;
    
    @Resource
    private MemberService memberService;
    
    @Autowired
	private ProductSubscribeService productSubscribeService;
    @RequestMapping(value = { "/index.jhtml", }, method = { RequestMethod.GET })
	public String orderPage(HttpServletRequest request,HttpServletResponse response, Model model) {		
		
		model.addAttribute("routeName", "order");
		return "/front/member/user_order"; 
	}
    
    //设置个人信息 昵称,性别,生日,头像url
    @RequestMapping(value="/baseSetting",method=RequestMethod.POST)
    public void baseSetting(@RequestParam(required=false,defaultValue="") String nickName,
    		@RequestParam(required=false,defaultValue="") String gender,
    		@RequestParam(required=false,defaultValue="") Date birthDate,
    		@RequestParam(required=false,defaultValue="") String headImgUrl){
    	
    }
    @RequestMapping(value = { "/currentMember.jspx", }, method = { RequestMethod.GET ,RequestMethod.POST})
	public void getCurrent(HttpServletRequest request,HttpServletResponse response, Model model) {	
    	MemberEntity current = memberService.getCurrent();
    	Map<String,Object> map=new HashMap<>();
    	map.put("member", current);
		RespUtil.renderJson(response, map);
	}
    @RequestMapping(value = { "/getList.json", }, method = { RequestMethod.GET ,RequestMethod.POST})
	public void getList(HttpServletRequest request,HttpServletResponse response, Model model) {	
    	int pageNumber=ReqUtil.getInt(request, "page", 1);
    	int pageSize=ReqUtil.getInt(request, "rows", 5);
    	String time=ReqUtil.getString(request, "time", "");
    	String param = ReqUtil.getString(request, "params", "");
    	Pageable pageable=new Pageable(pageNumber, pageSize);
    	MemberEntity entity=new MemberEntity();
    	Map<String,Object> params = new HashMap<String,Object>();
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
    	//处理时间
		String startTimeStr=params.get("startTime").toString();
		String endTimeStr=params.get("endTime").toString();
		Page<MemberEntity> page = null;
		List<MemberEntity> resultList = new ArrayList<>();
		try {
			String format = new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss");
			Date strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTimeStr.equals("")?format:startTimeStr);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTimeStr.equals("")?format:endTimeStr);
			List<Sequencer> list=new ArrayList<>();
			list.add(Sequencer.desc("createDate"));
			if(StringUtils.isNotBlank(param)&&param.equals("totalSubscribe")) {
				 page = productSubscribeService.findSubScribeAllMember
						(startTimeStr.equals("")?null:startTimeStr, endTimeStr.equals("")?null:endTimeStr,pageable);
				 resultList=page.getCont();
				
			}else if(StringUtils.isNotBlank(param)&&param.equals("emailCloseSubscribe")) {
				page = productSubscribeService.getCloseSubscribe(startTimeStr, endTimeStr,pageable);
				resultList=page.getCont();
			}else if(StringUtils.isNotBlank(param)&&param.equals("mobileCloseSubscribe")) {
				page = productSubscribeService.getCloseMobileSubscribe(startTimeStr, endTimeStr,pageable);
				resultList=page.getCont();
			}else{
				if(StringUtils.isNotBlank(param)&&param.equals("newMember")) {
					pageable.getFilters().add(Filter.ge("createDate", strDate));
					pageable.getFilters().add(Filter.le("createDate", endDate));
				}
				if(StringUtils.isNotBlank(param)&&param.equals("loginCell")) {
					pageable.getFilters().add(Filter.ge("loginDate", strDate));
					pageable.getFilters().add(Filter.le("loginDate", endDate));
				}
				pageable.setSequencers(list);
				page=memberService.findPage(pageable);
				resultList=page.getCont();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Map> resultData = new ArrayList<Map>();
		for (MemberEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("username", item.getUsername());
			data.put("realName", item.getRealName());
			data.put("mobile", item.getMobile());
			data.put("loginDate", item.getLoginDate());
			data.put("createDate", item.getCreateDate());
			data.put("contacts", item.getContacts());
			data.put("userType", item.getMemberShip());
			data.put("id", item.getId());
			resultData.add(data);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", page.getTotal());
		result.put("rows", resultData);
		RespUtil.renderJson(response, result);
	}
     
}