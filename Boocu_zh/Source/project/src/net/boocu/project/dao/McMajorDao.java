package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMajorEntity;
import net.boocu.project.entity.McModelEntity;

public interface McMajorDao extends BaseDao<McMajorEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	/**
	 * 根据公司ids查询主营产品信息
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String rootId); 
	/**
	 * 根据id查询行业分类信息
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryall(String id);
	/**
	 * 取得常用仪器数据集合
	 * @param reqeust
	 * @param response
	 * @param model
	 * @param memberGradeEntity
	 */
	public List<Map<String, Object>> getMcModelGradeNames(String mc_industry_id);

	public List<Map<String, Object>> queryallid();

	public List<Map<String, Object>> queryallinid(String mc_industry_id);
}
