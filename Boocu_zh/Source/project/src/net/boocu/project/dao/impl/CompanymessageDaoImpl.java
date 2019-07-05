package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.CompanymessageDao;
import net.boocu.project.entity.CompanymessageEntity;

import org.springframework.stereotype.Repository;

@Repository("companymessageDaoImpl")
public class CompanymessageDaoImpl extends BaseDaoImpl<CompanymessageEntity, Long> implements CompanymessageDao {
	
}
