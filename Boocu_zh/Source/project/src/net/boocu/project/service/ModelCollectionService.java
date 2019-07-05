package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.*;
import org.springframework.ui.Model;

import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ModelCollectionService  extends BaseService<ModelCollectionEntity, Long>{

	//Page<ModelCollectionEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);
	 /**
	  * 当进行条件过滤查询条数
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querycompanyrows(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 查询所有
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public List<Map> querycompany(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 导出excel表
	 * @param request
	 * @param session
	 * @param response
	 */
	public void orderExport(HttpServletRequest request,HttpSession session, HttpServletResponse response);
	/**
	 * 查询公司名是否存在
	 * @param edit_name
	 * @param name
	 * @param request
	 * @param session
	 * @return
	 */
	 public Message register(String edit_name,String name,HttpServletRequest request,HttpSession session);
}
