package net.boocu.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.project.entity.SuModelCollectionEntity;
import net.boocu.web.service.BaseService;

public interface SuModelCollectionService  extends BaseService<SuModelCollectionEntity, Long>{

	//Page<ModelCollectionEntity> findAllSubByMember(Pageable pageable, Map<String, Object> htMap);
	 /**
	  * 当进行条件过滤查询条数
	  * @param request
	  * @param response
	  * @param model
	  * @return
	  */
	 public List<Map<String, Object>> querysucompanyrows(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 查询所有
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public List<Map> querysucompany(HttpServletRequest request, HttpServletResponse response, Model model);
	/**
	 * 查询是否存在
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public List<Map<String, Object>> register(String name);
}
