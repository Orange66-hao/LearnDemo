package net.boocu.project.dao;

import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface MessageDao extends BaseDao<MessageEntity, Long> {
	public Page<MessageEntity> findPageOrSendList(Pageable pageable,
			Map map);
	public Page<MessageEntity> findPageOrRecByAdminList(Pageable pageable,
			Map map);
}
