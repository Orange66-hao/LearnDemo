package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.NewsDao;
import net.boocu.project.entity.NewsEntity;

import org.springframework.stereotype.Repository;


@Repository("newsDaoImpl")
public class NewsDaoImpl extends BaseDaoImpl<NewsEntity, Long> implements NewsDao {
	
}
