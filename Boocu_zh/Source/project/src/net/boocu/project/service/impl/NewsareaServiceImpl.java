package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.NewsareaDao;
import net.boocu.project.entity.NewsareaEntity;
import net.boocu.project.service.NewsareaService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;


@Service("newsareaServiceImpl")
public class NewsareaServiceImpl extends BaseServiceImpl<NewsareaEntity, Long> implements NewsareaService {
	@Resource(name = "newsareaDaoImpl")
    private NewsareaDao newsareaDao;
    
    @Resource(name = "newsareaDaoImpl")
    public void setBaseDao(NewsareaDao newsareaDao) {
        super.setBaseDao(newsareaDao);
    }
}
