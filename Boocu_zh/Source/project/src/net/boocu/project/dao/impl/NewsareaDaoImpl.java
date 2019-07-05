package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.NewsareaDao;
import net.boocu.project.entity.NewsareaEntity;

import org.springframework.stereotype.Repository;


@Repository("newsareaDaoImpl")
public class NewsareaDaoImpl extends BaseDaoImpl<NewsareaEntity, Long> implements NewsareaDao {
	
}
