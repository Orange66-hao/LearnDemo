package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.service.MailSignatureService;
import net.boocu.web.dao.MailSignatureDao;
import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberGradeService;

import org.springframework.stereotype.Service;


@Service("mailSignatureServiceImpl")
public class MailSignatureServiceImpl extends BaseServiceImpl<MailSignatureEntity, Long> implements MailSignatureService {
	@Resource(name = "mailSignatureDaoImpl")
    private MailSignatureDao moduleDao;
    
    @Resource(name = "mailSignatureDaoImpl")
    public void setBaseDao(MailSignatureDao memberGradeDao) {
    	super.setBaseDao(memberGradeDao);
    }
}
