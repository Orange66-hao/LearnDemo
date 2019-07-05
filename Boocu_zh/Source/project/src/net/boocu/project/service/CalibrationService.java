package net.boocu.project.service;

import net.boocu.project.entity.CalibrationEntity;
import net.boocu.web.service.BaseService;

/**
 * 计量校准     add by deng  20160118
 * */
public interface CalibrationService extends BaseService<CalibrationEntity, Long> {
	public void saveWithCalibration(CalibrationEntity calibrationEntity, String[] brandName, String brand);
	
}
