package net.boocu.project.dao.impl;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.project.dao.CalibrationDao;
import net.boocu.project.entity.CalibrationEntity;

import org.springframework.stereotype.Repository;

/**
 * 计量校准   add by  deng 20160118
 * */
@Repository("calibrationDaoImpl")
public class CalibrationDaoImpl extends BaseDaoImpl<CalibrationEntity, Long> implements CalibrationDao {
}
