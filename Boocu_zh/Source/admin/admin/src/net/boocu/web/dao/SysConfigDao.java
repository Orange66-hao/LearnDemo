/**
 * 
 */
package net.boocu.web.dao;

import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.SysConfigEntity;

/**
 * @author Administrator
 *
 */
public interface SysConfigDao extends BaseDao<SysConfigEntity, Long> {

	/**
	 * 修改配置
	 * @param sce
	 * @return
	 */
	public int updateConfig(SysConfigEntity sce);
	
	/**
	 * 初始化系统配置
	 */
	public void updateInitConfig();

	/**
	 * 查询所有的系统配置
	 * @return
	 */
	public List<SysConfigEntity> getSysConfigLists();

	/**
	 * 根据key获取value的值
	 * @param key
	 * @return
	 */
	public String getConfigByKey(String key);
}
