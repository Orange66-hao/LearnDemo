package net.boocu.web.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.web.dao.MailSignatureDao;
import net.boocu.web.dao.MemberGradeDao;
import net.boocu.web.entity.MemberGradeEntity;

import org.springframework.stereotype.Repository;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Repository("mailSignatureDaoImpl")
public class MailSignatureDaoImpl extends BaseDaoImpl<MailSignatureEntity, Long> implements MailSignatureDao {
}
