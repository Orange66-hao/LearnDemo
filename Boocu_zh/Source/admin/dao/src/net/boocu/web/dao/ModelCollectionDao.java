package net.boocu.web.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.McBrandAndModelEntity;
import org.springframework.ui.Model;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.web.Message;
import net.boocu.web.entity.MemberGradeEntity;

public interface ModelCollectionDao extends BaseDao<ModelCollectionEntity,Long> {
	/**
	 * 查询公司名是否存在
	 * @param edit_name
	 * @param name
	 * @param request
	 * @param session
	 * @return
	 */
	 public List<Map<String, Object>> register(String edit_name,String name,HttpServletRequest request,HttpSession session);
	 /**
	  * 当进行条件过滤查询条数
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querycompanyrows(HttpServletRequest request, HttpServletResponse response, Model model);
	 /**
	  * 当进行条件过滤查询
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querycompany(HttpServletRequest request, HttpServletResponse response, Model model);
}
