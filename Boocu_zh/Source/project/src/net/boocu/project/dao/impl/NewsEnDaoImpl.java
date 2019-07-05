package net.boocu.project.dao.impl;

import org.springframework.stereotype.Repository;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.NewsEnDao;
import net.boocu.project.entity.NewsEnEntity;

@Repository("newsEnDaoImpl")
public class NewsEnDaoImpl extends BaseDaoImpl<NewsEnEntity, Long> implements NewsEnDao {

}
