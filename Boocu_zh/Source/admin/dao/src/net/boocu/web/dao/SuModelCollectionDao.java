package net.boocu.web.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.project.entity.SuModelCollectionEntity;
import net.boocu.web.entity.MemberGradeEntity;

public interface SuModelCollectionDao extends BaseDao<SuModelCollectionEntity,Long> {
	 /**
	  * 当进行条件过滤查询条数
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querysucompanyrows(HttpServletRequest request, HttpServletResponse response, Model model);
	 /**
	  * 当进行条件过滤查询
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querysucompany(HttpServletRequest request, HttpServletResponse response, Model model);
	 /**
		 * 查询是否存在
		 * @param request
		 * @param response
		 * @param model
		 * @return
		 */
	 public List<Map<String, Object>> register(String name);
}
