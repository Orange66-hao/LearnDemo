package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.AdminareaDao;
import net.boocu.project.entity.AdminareaEntity;

import org.springframework.stereotype.Repository;

@Repository("adminareaDaoImpl")
public class AdminareaDaoImpl extends BaseDaoImpl<AdminareaEntity, Long> implements AdminareaDao {
	
}
