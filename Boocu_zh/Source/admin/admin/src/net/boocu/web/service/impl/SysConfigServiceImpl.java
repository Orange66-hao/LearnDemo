/**
 * 
 */
package net.boocu.web.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boocu.web.dao.SysConfigDao;
import net.boocu.web.entity.SysConfigEntity;
import net.boocu.web.service.SysConfigService;

/**
 * @author Administrator
 *
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigEntity, Long> 
	implements SysConfigService{
	
	@Autowired
	private SysConfigDao sysConfigDao;

	/* (non-Javadoc)
	 * @see net.boocu.web.service.SysConfigService#updateConfig(net.boocu.web.entity.SysConfigEntity)
	 */
	@Override
	public int updateConfig(SysConfigEntity sce) {
		return sysConfigDao.updateConfig(sce);
	}

	/* (non-Javadoc)
	 * @see net.boocu.web.service.SysConfigService#updateInitConfig()
	 */
	@Override
	public void updateInitConfig() {
		sysConfigDao.updateInitConfig();
	}

	/* (non-Javadoc)
	 * @see net.boocu.web.service.SysConfigService#getSysConfigLists()
	 */
	@Override
	public List<SysConfigEntity> getSysConfigLists() {
		return sysConfigDao.getSysConfigLists();
	}

	/* (non-Javadoc)
	 * @see net.boocu.web.service.SysConfigService#getValByKey(java.lang.String)
	 */
	@Override
	public String getValByKey(String key) {
		return sysConfigDao.getConfigByKey(key);
	}
	
}
