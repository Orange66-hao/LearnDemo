package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberGradeService;

import org.springframework.stereotype.Service;


@Service("memberGradeServiceImpl")
public class MemberGradeServiceImpl extends BaseServiceImpl<MemberGradeEntity, Long> implements MemberGradeService {
	@Resource(name = "memberGradeDaoImpl")
    private MemberGradeDao moduleDao;
    
    @Resource(name = "memberGradeDaoImpl")
    public void setBaseDao(MemberGradeDao memberGradeDao) {
    	super.setBaseDao(memberGradeDao);
    }
}
