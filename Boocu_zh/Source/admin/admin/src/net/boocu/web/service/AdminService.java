/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

//import net.boocu.web.bean.admin.ProfileSettingBean;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.shiro.ShiroPrincipal;

/**
 * Service - 管理员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface AdminService extends BaseService<AdminEntity, Long> {

    /**
     * 根据ID查找权限
     * 
     * @param id
     *            ID
     * @return 权限，不存在时返回NULL
     */
    List<String> findAuths(Long id);

    /**
     * 根据管理员查找权限
     * 
     * @param admin
     *            管理员
     * @return 权限，不存在时返回NULL
     */
    List<String> findAuths(AdminEntity admin);

    /**
     * 设置管理员
     * 
     * @param settingBean
     *            账户设置Bean
     * @param admin
     *            管理员
     * @throws Exception
     *             设置失败的异常
     */
//    void set(ProfileSettingBean profileSettingBean, AdminEntity admin) throws Exception;

    /**
     * 判断管理员是否登录
     * 
     * @return 管理员是否登录
     */
    boolean authorized();

    /**
     * 获取当前管理员
     * 
     * @return 当前管理员，不存在时返回NULL
     */
    AdminEntity getCurrent();

    /**
     * 获取当前身份信息
     * 
     * @return 身份信息，不存在时返回NULL
     */
    ShiroPrincipal getCurrentPrincipal();

    /**
     * 获取当前管理员的ID
     * 
     * @return 当前管理员的ID，不存在时返回NULL
     */
    Long getCurrentId();

    /**
     * 获取当前管理员的用户名
     * 
     * @return 当前登管理员的录用户名，不存在时返回NULL
     */
    String getCurrentUsername();

	List<String> getAuthoritiesName(Long id);
 
	List<String> getRolesName(Long id);

}