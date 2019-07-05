/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.boocu.web.dao.RoleDao;
import net.boocu.web.entity.RoleEntity;
import net.boocu.web.service.RoleService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 角色
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("roleServiceImpl")
public class RoleServiceImpl extends BaseServiceImpl<RoleEntity, Long> implements RoleService {

    @Resource(name = "roleDaoImpl")
    public void setBaseDao(RoleDao roleDao) {
        super.setBaseDao(roleDao);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public RoleEntity save(RoleEntity role) {
        return super.save(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public RoleEntity update(RoleEntity role) {
        return super.update(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public RoleEntity update(RoleEntity role, String... ignoreProperties) {
        return super.update(role, ignoreProperties);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public void delete(RoleEntity role) {
        super.delete(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public void deleteList(Long... ids) {
        super.deleteList(ids);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public void deleteList(List<RoleEntity> roles) {
        super.deleteList(roles);
    }

}