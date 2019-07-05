package net.boocu.project.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.MessageDao;
import net.boocu.project.dao.ProducWantRepairDao;
import net.boocu.project.dao.SysMessageDao;
import net.boocu.project.entity.SysMessageEntity;
import net.boocu.project.service.SysMessageService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

@Service("SysMessageServiceImpl")
public class SysMessageServiceImpl extends BaseServiceImpl<SysMessageEntity, Long> implements SysMessageService {
	@Resource(name = "SysMessageDaoImpl")
	private SysMessageDao sysmessageDao;
	@Resource
	MessageDao messageDao;
	@Resource(name = "SysMessageDaoImpl")
	public void setBaseDao(SysMessageDao messageDao) {
		super.setBaseDao(messageDao);
	}

	@Override
	public Page<SysMessageEntity> findPageOrSendList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return sysmessageDao.findPageOrSendList(pageable, map);
	}

	@Override
	public Page<SysMessageEntity> findPageOrRecList(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return sysmessageDao.findPageOrRecList(pageable, map);
	}
}
