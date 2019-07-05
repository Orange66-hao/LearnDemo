package net.boocu.project.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.McProductClassEntity;
/**
 * 使用方法:如UserEntity
 * 1.将 Productclass replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface McProductClassDao extends BaseDao<McProductClassEntity,Long> {
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> getNodeData2(String productclass_id);
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> queryall_pro(String id);
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> queryall_id();
	/**
	 * 根据id查询常用仪器信息
	 * @param productclass_id
	 * @return
	 */
	public List<Map<String, Object>> queryall_inid(String id);
}
