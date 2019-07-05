/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.dao.AuthorityDao;
import net.boocu.web.dao.ResourceDao;
import net.boocu.web.entity.AuthorityEntity;
import net.boocu.web.entity.ResourceEntity;
import net.boocu.web.service.ResourceService;

import org.springframework.stereotype.Service;

 
/**
 * Service - 管理员
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("adminResourceImpl")
public class ResourceServiceImpl extends BaseServiceImpl<ResourceEntity, Long> implements ResourceService {

    @Resource(name = "resourceDaoImpl")
    private ResourceDao resourceDao;

    @Resource(name = "resourceDaoImpl")
    public void setBaseDao(ResourceDao resourceDao) {
        super.setBaseDao(resourceDao);
    }
    
    @Resource 
    private AuthorityDao authorityDao;
    
    
    /**
	 * 根据主键ID删除资源实体
	 * @param id
	 */
	public void delete(Long id) {
		List<AuthorityEntity> authorities = authorityDao.findList(null, null, null, null);
		
		for(AuthorityEntity auth : authorities) {
			for(ResourceEntity resource : auth.getResources()) {
				if(resource.getId().equals(id) ) {
					auth.getResources().remove(resource);
					authorityDao.merge(auth);
					break;
				}
			}
		}
		ResourceEntity resource = this.resourceDao.find(id);
		
		resourceDao.remove(resource);
	}
 
	 
	/**
	 * 根据分页对象、过滤集合参数，分页查询资源列表
	 * @param page
	 * @param filters
	 * @return
	 */
	public Page<ResourceEntity> findPage( Pageable page, final List<Filter> filters) {
		return resourceDao.findPage(page);
		
		
	}
	
	/**
	 * 查询所有资源记录
	 * @return
	 */
	public List<ResourceEntity> getAll() {
		return resourceDao.findList(null, null, null, null);
	}
	
	/**
	 * 根据用户ID查询该用户具有权限访问的资源与不需要授权的资源
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ResourceEntity> getAuthorizedResource(Long userId) {
		
		return resourceDao.getAuthorizedResource(userId);
	 
	}
	
}