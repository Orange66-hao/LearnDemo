package net.boocu.project.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.boocu.project.dao.NewsEnDao;
import net.boocu.project.entity.NewsEnEntity;
import net.boocu.project.service.NewsEnService;
import net.boocu.web.service.impl.BaseServiceImpl;

@Service("newsEnServiceImpl")
public class NewsEnServiceImpl extends BaseServiceImpl<NewsEnEntity, Long> implements NewsEnService {
	@Resource(name = "newsEnDaoImpl")
	private NewsEnDao newsEnDao;

	@Resource(name = "newsEnDaoImpl")
	public void setBaseDao(NewsEnDao newsEnDao) {
		super.setBaseDao(newsEnDao);
	}
}
