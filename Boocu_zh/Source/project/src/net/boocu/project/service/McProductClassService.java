package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;

import net.boocu.project.entity.McProductClassEntity;
import net.boocu.web.service.BaseService;

public interface McProductClassService extends BaseService<McProductClassEntity, Long> {
	public void testFullName();
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public Map getNodeData2(String productclass_id);
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public Map queryall_pro(String id);
	
	/**
	 * 取得用户等级集合
	 * @param reqeust
	 * @param response
	 * @param model
	 * @param memberGradeEntity
	 */
	public List<Map> getMcProductClass(HttpServletRequest reqeust, HttpServletResponse response, Model model, McProductClassEntity memberGradeEntity);
}
