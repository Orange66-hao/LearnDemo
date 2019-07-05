package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.RecruitDao;
import net.boocu.project.entity.RecruitEntity;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;

/**
 * 使用方法:如UserEntity
 * 1.将 recruit replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Recruit replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Repository("recruitDaoImpl")
public class RecruitDaoImpl extends BaseDaoImpl<RecruitEntity, Long> implements RecruitDao {
}
