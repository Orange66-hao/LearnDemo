package net.boocu.project.dao.impl;

import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.MessageTextDao;
import net.boocu.project.entity.MessageTextEntity;
@Repository("MessageTextDaoImpl")
public class MessageTextDaoImpl extends BaseDaoImpl<MessageTextEntity, Long> implements MessageTextDao{

}
