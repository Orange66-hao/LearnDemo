package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.FriendsDao;
import net.boocu.project.entity.FriendsEntity;

import org.springframework.stereotype.Repository;


@Repository("friendsDaoImpl")
public class FriendsDaoImpl extends BaseDaoImpl<FriendsEntity, Long> implements FriendsDao {
	
}
