package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.UserlevelDao;
import net.boocu.project.entity.UserlevelEntity;
import net.boocu.project.service.UserlevelService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("userlevelServiceImpl")
public class UserlevelServiceImpl extends BaseServiceImpl<UserlevelEntity, Long> implements UserlevelService {
	@Resource(name = "userlevelDaoImpl")
    private UserlevelDao userlevelDao;
    
    @Resource(name = "userlevelDaoImpl")
    public void setBaseDao(UserlevelDao userlevelDao) {
        super.setBaseDao(userlevelDao);
    }
}
