package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.UserlevelDao;
import net.boocu.project.entity.UserlevelEntity;

import org.springframework.stereotype.Repository;

@Repository("userlevelDaoImpl")
public class UserlevelDaoImpl extends BaseDaoImpl<UserlevelEntity, Long> implements UserlevelDao {
	
}
