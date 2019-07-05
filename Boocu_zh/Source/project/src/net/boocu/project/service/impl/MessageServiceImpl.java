package net.boocu.project.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import net.boocu.project.dao.MessageDao;
import net.boocu.project.dao.SysMessageDao;
import net.boocu.project.entity.MessageEntity;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.project.service.MessageService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

@Repository("MessageServiceImpl")
public class MessageServiceImpl extends BaseServiceImpl<MessageEntity, Long> implements MessageService {

	@Resource(name = "MessageDaoImpl")
	private MessageDao messageDao;

	@Resource(name = "MessageDaoImpl")
	public void setBaseDao(MessageDao messageDao) {
		super.setBaseDao(messageDao);
	}

	@Override
	public Page<MessageEntity> findPageOrSendList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return messageDao.findPageOrSendList(pageable, map);
	}

	@Override
	public Page<MessageEntity> findPageOrRecList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return messageDao.findPageOrRecByAdminList(pageable, map);
	}
}
