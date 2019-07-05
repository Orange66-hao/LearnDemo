/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.web.dao.AreaDao;
import net.boocu.web.entity.AreaEntity;
import net.boocu.web.service.AreaService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Service - 地址区域
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("areaServiceImpl")
public class AreaServiceImpl extends BaseServiceImpl<AreaEntity, Long> implements AreaService {
	@Resource(name = "areaDaoImpl")
    private AreaDao areaDao;

    @Resource(name = "areaDaoImpl")
    public void setBaseDao(AreaDao areaDao) {
        super.setBaseDao(areaDao);
    }
	@Override
	public boolean nameExists(String parentId, String name) {
		return areaDao.nameExists(parentId, name);
	}

	@Override
	public boolean nameUnique(String parentId, String previousName,
			String currentName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AreaEntity> findRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AreaEntity> findChildren(String parentId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Map<String, Object>> findChildrenBySql(Long parentId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Map<String, Object>> findChildrenBySql(String parentName) {
		// TODO Auto-generated method stub
		return null;
	}
	

}