package net.boocu.project.service;

import java.util.HashMap;

import net.boocu.project.entity.AutoTestEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProjectNeedEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

public interface ProjectNeedService extends BaseService<ProjectNeedEntity, Long> {
	public Page<ProjectNeedEntity> findProjectNeedPage(Pageable pageable,HashMap<String,Object> htMap) ;
	public ProjectNeedEntity save(ProjectNeedEntity projectNeedEntity,ProductEntity productEntity);
	public void saveWithNeed(ProjectNeedEntity projectNeedEntity, String[] brandName);
}
