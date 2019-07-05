/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.PluginConfigEntity;

/**
 * Dao - 插件配置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface PluginConfigDao extends BaseDao<PluginConfigEntity, Long> {

    /**
     * 判断插件是否存在
     * 
     * @param plugin
     *            插件
     * @return 插件是否存在
     */
    boolean pluginExists(String plugin);

    /**
     * 查找插件配置
     * 
     * @param plugin
     *            插件
     * @return 插件配置，不存在时返回NULL
     */
    PluginConfigEntity findByPlugin(String plugin);

}