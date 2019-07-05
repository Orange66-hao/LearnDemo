package net.boocu.project.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.SubscribeInfoDao;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.service.SubscribeInfoService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.impl.BaseServiceImpl;

@Service("subscribeInfoServiceImpl")
public class SubscribeInfoServiceImpl extends BaseServiceImpl<SubscribeInfoEntity, Long>
		implements SubscribeInfoService {
	@Resource(name = "subscribeInfoDaoImpl")
	private SubscribeInfoDao subscribeInfoDao;

	@Resource(name = "subscribeInfoDaoImpl")
	public void setBaseDao(SubscribeInfoDao subscribeInfoDao) {
		super.setBaseDao(subscribeInfoDao);
	}

	public List<Object> getModel(Long id) {
		// TODO Auto-generated method stub
		return subscribeInfoDao.getModel(id);
	}

	@Override
	public Page<SubscribeInfoEntity> findFrontSubscribePage(Pageable pageable, Map map) {
		// TODO Auto-generated method stub
		return subscribeInfoDao.findFrontSubscribePage(pageable, map);
	}

	@Override
	public List<Long> findDistinctMemberList() {
		// TODO Auto-generated method stub
		 return subscribeInfoDao.findDistinctMemberList();
	}

	@Override
	public List<Map<String, Object>> getInfoList(Long id) {
		// TODO Auto-generated method stub
		return subscribeInfoDao.getInfoList(id);
	}

	@Override
	public void deleteById(Long valueOf) {
		// TODO Auto-generated method stub
		subscribeInfoDao.deleteById(valueOf);
	}


}
