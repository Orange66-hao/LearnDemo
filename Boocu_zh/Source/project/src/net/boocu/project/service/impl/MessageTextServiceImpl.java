package net.boocu.project.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.MessageTextDao;
import net.boocu.project.dao.SysMessageDao;
import net.boocu.project.entity.MessageTextEntity;
import net.boocu.project.service.MessageTextService;
import net.boocu.web.service.impl.BaseServiceImpl;

@Service("MessageTextServiceImpl")
public class MessageTextServiceImpl extends BaseServiceImpl<MessageTextEntity, Long>
		implements MessageTextService {
	@Resource(name = "MessageTextDaoImpl")
	private MessageTextDao messageTextDao;

	@Resource(name = "MessageTextDaoImpl")
	public void setBaseDao(MessageTextDao messageTextDao) {
		super.setBaseDao(messageTextDao);
	}
}
