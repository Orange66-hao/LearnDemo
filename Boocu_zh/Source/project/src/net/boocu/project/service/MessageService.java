package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface MessageService extends BaseService<MessageEntity, Long> {
	public Page<MessageEntity> findPageOrSendList(Pageable pageable, Map map);

	public Page<MessageEntity> findPageOrRecList(Pageable pageable, Map map);
}
