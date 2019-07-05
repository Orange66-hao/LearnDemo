package net.boocu.project.dao;

import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface SysMessageDao extends BaseDao<SysMessageEntity, Long>{

	public Page<SysMessageEntity> findPageOrSendList(Pageable pageable,
			Map map);
	public Page<SysMessageEntity> findPageOrRecList(Pageable pageable,
			Map map);
}
