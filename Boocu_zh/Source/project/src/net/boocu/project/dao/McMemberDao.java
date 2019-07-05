package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McMemberEntity;

public interface McMemberDao extends BaseDao<McMemberEntity,Long> {

	List<Map<String, Object>> getParentIds(String param);
	
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
