package net.boocu.project.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.boocu.framework.dao.BaseDao;
import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;

public interface SubscribeInfoDao extends BaseDao<SubscribeInfoEntity,Long>{

	public List<Object> getModel(Long id);
	
	/**
	 * 
	 * 概述:前台分页方法
	 * 传入:分页对象pageable,分页参数map
	 */
	public Page<SubscribeInfoEntity> findFrontSubscribePage(Pageable pageable,
			Map map);

	public List<Long> findDistinctMemberList();

	public List<Map<String, Object>> getInfoList(Long id);

	public void deleteById(Long valueOf);
}
