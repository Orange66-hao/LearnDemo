package net.boocu.project.service;

import java.util.Map;

import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.SalesEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.service.BaseService;

/**
 * 使用方法:如UserEntity
 * 1.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
public interface DataCenterService extends BaseService<DataCenterEntity, Long> {
	public Page<DataCenterEntity> findFrontDataCenterPage(Pageable pageable,
			Map map) ;
	public DataCenterEntity save(DataCenterEntity dataEntity,ProductEntity productEntity);
}
