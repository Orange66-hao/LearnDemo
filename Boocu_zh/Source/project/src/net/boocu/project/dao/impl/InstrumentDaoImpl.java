package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.InstrumentDao;
import net.boocu.project.entity.InstrumentEntity;

import org.springframework.stereotype.Repository;

@Repository("instrumentDaoImpl")
public class InstrumentDaoImpl extends BaseDaoImpl<InstrumentEntity, Long> implements InstrumentDao {


	
}
