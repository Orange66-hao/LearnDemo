/**
 * 
 */
package net.boocu.web.service;

import java.util.List;
import java.util.Map;

import net.boocu.web.entity.SysConfigEntity;

/**
 * @author Administrator
 *
 */
public interface SysConfigService  extends BaseService<SysConfigEntity, Long>{

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
	public String getValByKey(String key);
}
