package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionTwoEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ModelCollectionTwoService  extends BaseService<ModelCollectionTwoEntity, Long>{

	//Page<ModelCollectionTwoEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);
	/**
	 * 查询所有条数
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public List<Map<String, Object>> querycompanytworows(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 查询所有
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public List<Map> querycompanytwo(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 查询公司名是否存在
	 * @param edit_name
	 * @param name
	 * @param request
	 * @param session
	 * @return
	 */
	 public Message register(String edit_name,String name,HttpServletRequest request,HttpSession session);
	 /**
	  * 删除mc_change_major表数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  delete_mc_change_major(String mc_major);
	 /**
	  * 为中间表mc_change_major添加数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  insert_mc_change_major(String ids,String mc_major);
	 /**
	  * 删除mc_change_productclass表数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  delete_mc_change_productclass(String mc_productclass);
	 /**
	  * 为中间表mc_change_productclass添加数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  insert_mc_change_productclass(String ids,String mc_productclass);
	 /**
	  * 删除mc_change_brand表数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  delete_mc_change_brand(String mc_brand);
	 /**
	  * 为中间表mc_change_brand添加数据
	  * @param edit_name
	  * @param name
	  * @param request
	  * @param session
	  * @return
	  */
	 public int  insert_mc_change_brand(String ids,String mc_brand);
}
