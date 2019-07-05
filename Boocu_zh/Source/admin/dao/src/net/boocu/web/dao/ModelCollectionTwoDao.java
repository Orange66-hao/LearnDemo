package net.boocu.web.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.project.entity.ModelCollectionTwoEntity;
import net.boocu.web.entity.MemberGradeEntity;

public interface ModelCollectionTwoDao extends BaseDao<ModelCollectionTwoEntity,Long> {
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
	public List<Map<String, Object>> querycompanytwo(HttpServletRequest request, HttpServletResponse response, Model model);
	
	public List<Map<String, Object>> register(String name);
	
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
