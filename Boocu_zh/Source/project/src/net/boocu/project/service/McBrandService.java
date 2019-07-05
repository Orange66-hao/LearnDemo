package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McBrandEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.web.service.BaseService;

public interface McBrandService extends BaseService<McBrandEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McBrandEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param id
	 * @return
	 */
	public Map queryall(String id);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param id
	 * @return
	 */
	public Map getNodeData2(String id);
	/**
	 * 取得用户等级的集合
	 * @param reqeust
	 * @param response
	 * @param model
	 * @param memberGradeEntity
	 */
	public List<Map> getMcBrandGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McBrandEntity memberGradeEntity);
}
