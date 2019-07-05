package net.boocu.project.dao;

import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.SalesEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

public interface DataCenterDao extends BaseDao<DataCenterEntity,Long> {
	/**
	 * 列表页面获取数据的公共方法 add by deng 20151024
	 * */
	public Page<DataCenterEntity> findFrontDataCenterPage(Pageable pageable,
			Map map) ; 
}
