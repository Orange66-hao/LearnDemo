package net.boocu.project.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import net.boocu.project.dao.WorldAreaDao;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.WorldAreaEntity;
import net.boocu.project.service.WorldAreaService;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("worldAreaServiceImpl")
public class WorldAreaServiceImpl extends BaseServiceImpl<WorldAreaEntity, Long> implements WorldAreaService {
	@Resource(name = "worldAreaDaoImpl")
    private WorldAreaDao worldAreaDao;
    
    @Resource(name = "worldAreaDaoImpl")
    public void setBaseDao(WorldAreaDao worldAreaDao) {
    	super.setBaseDao(worldAreaDao);
    }
    

}
