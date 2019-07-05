package net.boocu.project.service;

import java.util.HashMap;

import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface AutoTestService extends BaseService<AutoTestEntity, Long> {
	public Page<AutoTestEntity> findAutoTestPage(Pageable pageable,HashMap<String,Object> htMap) ;
	public AutoTestEntity save(AutoTestEntity autoTestEntity,ProductEntity productEntity);
	public void saveWithPro(AutoTestEntity autoTestEntity, String[] brandName);
	public void updateWithPro(AutoTestEntity autoTestEntity, String[] brandName, Long id);
}
