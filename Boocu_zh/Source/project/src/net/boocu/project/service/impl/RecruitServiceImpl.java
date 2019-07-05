package net.boocu.project.service.impl;

import javax.annotation.Resource;

import net.boocu.project.dao.RecruitDao;
import net.boocu.project.entity.RecruitEntity;
import net.boocu.project.service.RecruitService;
import net.boocu.web.service.impl.BaseServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 使用方法:如UserEntity
 * 1.将 recruit replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Recruit replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */

@Service("recruitServiceImpl")
public class RecruitServiceImpl extends BaseServiceImpl<RecruitEntity, Long> implements RecruitService {
	@Resource(name = "recruitDaoImpl")
    private RecruitDao recruitDao;
    
    @Resource(name = "recruitDaoImpl")
    public void setBaseDao(RecruitDao recruitDao) {
    	super.setBaseDao(recruitDao);
    }
}
