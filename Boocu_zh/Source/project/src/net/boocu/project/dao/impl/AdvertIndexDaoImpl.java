package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.AdvertIndexDao;
import net.boocu.project.entity.AdvertIndexEntity;

import org.springframework.stereotype.Repository;


@Repository("advertIndexDaoImpl")
public class AdvertIndexDaoImpl extends BaseDaoImpl<AdvertIndexEntity, Long> implements AdvertIndexDao {
	
}
