package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.NewsDao;
import net.boocu.project.entity.NewsEntity;
import net.boocu.project.service.NewsService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;



@Service("newsServiceImpl")
public class NewsServiceImpl extends BaseServiceImpl<NewsEntity, Long> implements NewsService {
	@Resource(name = "newsDaoImpl")
    private NewsDao newsDao;
    
    @Resource(name = "newsDaoImpl")
    public void setBaseDao(NewsDao newsDao) {
        super.setBaseDao(newsDao);
    }
}
