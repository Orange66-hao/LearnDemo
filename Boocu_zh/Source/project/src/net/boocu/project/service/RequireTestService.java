package net.boocu.project.service;

import net.boocu.project.entity.RequireTestEntity;
import net.boocu.web.service.BaseService;

/**
 * 需求测试     add by deng  20160118
 * */
public interface RequireTestService extends BaseService<RequireTestEntity, Long> {
	public void saveWithRequireTest(RequireTestEntity requireTestEntity, String[] brandName);
	
}
