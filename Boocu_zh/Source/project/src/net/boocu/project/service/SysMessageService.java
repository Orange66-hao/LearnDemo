package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.SysMessageEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface SysMessageService extends BaseService<SysMessageEntity, Long> {
	public Page<SysMessageEntity> findPageOrSendList(Pageable pageable, Map map);
	
	public Page<SysMessageEntity> findPageOrRecList(Pageable pageable, Map map);
}
