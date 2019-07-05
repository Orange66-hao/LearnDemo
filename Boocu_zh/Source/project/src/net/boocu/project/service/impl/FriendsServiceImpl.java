package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.FriendsDao;
import net.boocu.project.entity.FriendsEntity;
import net.boocu.project.service.FriendsService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;



@Service("friendsServiceImpl")
public class FriendsServiceImpl extends BaseServiceImpl<FriendsEntity, Long> implements FriendsService {
	@Resource(name = "friendsDaoImpl")
    private FriendsDao friendsDao;
    
    @Resource(name = "friendsDaoImpl")
    public void setBaseDao(FriendsDao friendsDao) {
        super.setBaseDao(friendsDao);
    }
}
