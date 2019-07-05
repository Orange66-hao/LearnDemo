/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.project.entity.HelpEntity;
import net.boocu.project.service.HelpService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("frontHelpController")
@RequestMapping("/help")
public class HelpController {
	
	private static final String TEMPATH = "/template/front/help";

	
	@Resource
	private HelpService helpService;
	
	@RequestMapping(value="/{id}.jhtml", method = {
			RequestMethod.POST, RequestMethod.GET })
	public String help(@PathVariable long id,HttpServletRequest request,HttpServletResponse response, Model model) {		
		
		model.addAttribute("helps", helpService.findAll());
		HelpEntity helpEntity = helpService.find(id);
		
		//真实路径
		String proLink = request.getSession().getServletContext().getRealPath("/");
		model.addAttribute("proLink", proLink);
		
		if(helpEntity != null){
			model.addAttribute("help",helpEntity);
		}
		
		return TEMPATH+"/help";
	}
}