/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

//import net.boocu.web.bean.admin.ProfileSettingBean;
import net.boocu.web.dao.AdminDao;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.AuthorityEntity;
import net.boocu.web.entity.RoleEntity;
import net.boocu.web.service.AdminService;
import net.boocu.web.shiro.ShiroPrincipal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 管理员
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("adminServiceImpl")
public class AdminServiceImpl extends BaseServiceImpl<AdminEntity, Long> implements AdminService {

    @Resource(name = "adminDaoImpl")
    private AdminDao adminDao;

    @Resource(name = "adminDaoImpl")
    public void setBaseDao(AdminDao adminDao) {
        super.setBaseDao(adminDao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAuths(Long id) {
        return findAuths(adminDao.find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAuths(AdminEntity admin) {
        List<String> auths = new ArrayList<String>();
        if (admin != null) {
            for (RoleEntity role : admin.getRoles()) {
//                auths.addAll(role.getAuths());
            }
        }
        return auths;
    }

//    @Override
//    @Transactional
//    public void set(ProfileSettingBean profileSettingBean, AdminEntity admin) throws Exception {
//        adminDao.lock(admin, LockModeType.PESSIMISTIC_WRITE);
//        if (StringUtils.isNotBlank(profileSettingBean.getPassword())) {
//            admin.setPassword(profileSettingBean.getPassword());
//        }
//        admin.setEmail(profileSettingBean.getEmail());
//        adminDao.merge(admin);
//    }

    @Override
    @Transactional(readOnly = true)
    public boolean authorized() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            return subject.isAuthenticated();
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminEntity getCurrent() {
    	Long currentId = getCurrentId();
        if (currentId != null) {
            return adminDao.find(currentId);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public ShiroPrincipal getCurrentPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            return (ShiroPrincipal) subject.getPrincipal();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentId() {
    	ShiroPrincipal currentPrincipal = getCurrentPrincipal();
        if (currentPrincipal != null) {
            return currentPrincipal.getId();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String getCurrentUsername() {
    	ShiroPrincipal currentPrincipal = getCurrentPrincipal();
        if (currentPrincipal != null) {
            return currentPrincipal.getUsername();
        }
        return null;
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public AdminEntity save(AdminEntity admin) {
        return super.save(admin);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public AdminEntity update(AdminEntity admin) {
        return super.update(admin);
    }

    @Override
    @Transactional
    @CacheEvict(value = "authorization", allEntries = true)
    public AdminEntity update(AdminEntity admin, String... ignoreProperties) {
        return super.update(admin, ignoreProperties);
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
    public void delete(AdminEntity admin) {
        super.delete(admin);
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
    public void deleteList(List<AdminEntity> admins) {
        super.deleteList(admins);
    }

	@Override
	public List<String> getAuthoritiesName(Long id) {
		AdminEntity admin = this.find(id);
		
		List<String> authorityNameList = new ArrayList<String>();
		
		for(AuthorityEntity anth : admin.getAuthorities()){
			authorityNameList.add( anth.getName());
		}
		
		for(RoleEntity role : admin.getRoles()){
	 
			for(AuthorityEntity anth : role.getAuthorities()){
				
				authorityNameList.add(anth.getName());
			}
			
		}
		
		return authorityNameList;
	}

	@Override
	public List<String> getRolesName(Long id) {
		AdminEntity admin = this.find(id);
		
		List<String> rolesNameList = new ArrayList<String>();
		
		for(RoleEntity role : admin.getRoles()){
			rolesNameList.add(role.getName());
		}
		
		return rolesNameList;
	}

}