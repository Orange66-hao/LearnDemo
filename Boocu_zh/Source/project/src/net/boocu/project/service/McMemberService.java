package net.boocu.project.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.McIndustryClassEntity;
import net.boocu.project.entity.McMemberEntity;
import net.boocu.web.Message;
import net.boocu.web.service.BaseService;

public interface McMemberService extends BaseService<McMemberEntity, Long> {
	//取得节点下的所有子节点,包括本身
	public List<McMemberEntity> getChildren(Long[] id);
	//取得节点下的所有子节点,包括本身
	public List<Map<String, Object>> getParentIds(String[] industryClass);
	
	/**
	 * 查询用户名是否存在
	 * @param edit_username
	 * @param username
	 * @param request
	 * @param session
	 * @return
	 */
	public List<Map<String, Object>> register(String edit_username,String username,HttpServletRequest request,HttpSession session);
	/**
	 * 根据常用联系人的id组，查询对应name（名称）
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String rootId);
	
	/**
	 * 查询负责人名称
	 * @param rootId
	 * @return
	 */
	public List<Map<String, Object>> quaryblame(String blame_id);
}
