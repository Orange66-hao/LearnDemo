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
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.project.entity.McModelEntity;
import net.boocu.web.service.BaseService;

public interface McMajorService extends BaseService<McMajorEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McMajorEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	/**
	 * 根据公司ids查询主营产品信息
	 * @param rootId
	 * @return
	 */
	public Map getNodeData2(String rootId); 
	/**
	 * 根据id查询行业分类信息
	 * @param id
	 * @return
	 */
	public Map queryall(String id);
	/**
	 * 取得常用仪器数据集合
	 * @param reqeust
	 * @param response
	 * @param model
	 * @param memberGradeEntity
	 */
	public List<Map> getMcModelGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McModelEntity memberGradeEntity);
	/**
	 * 取得常用仪器数据集合
	 * @param reqeust
	 * @param response
	 * @param model
	 * @param memberGradeEntity
	 */
	public List<Map> getMcMajorGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, McMajorEntity memberGradeEntity);
	
}
