package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.HelpDao;
import net.boocu.project.entity.HelpEntity;

import org.springframework.stereotype.Repository;

@Repository("helpDaoImpl")
public class HelpDaoImpl extends BaseDaoImpl<HelpEntity, Long> implements HelpDao {
	
}
