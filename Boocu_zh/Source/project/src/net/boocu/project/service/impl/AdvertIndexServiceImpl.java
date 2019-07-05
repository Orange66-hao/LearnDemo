package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.AdvertIndexDao;
import net.boocu.project.entity.AdvertIndexEntity;
import net.boocu.project.service.AdvertIndexService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("advertIndexServiceImpl")
public class AdvertIndexServiceImpl extends BaseServiceImpl<AdvertIndexEntity, Long> implements AdvertIndexService {
	@Resource(name = "advertIndexDaoImpl")
    private AdvertIndexDao AdvertIndexDao;
    
    @Resource(name = "advertIndexDaoImpl")
    public void setBaseDao(AdvertIndexDao AdvertIndexDao) {
        super.setBaseDao(AdvertIndexDao);
    }
}
