/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import net.boocu.framework.dao.impl.BaseDaoImpl;
import net.boocu.web.dao.PluginConfigDao;
import net.boocu.web.entity.PluginConfigEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * Dao - 插件配置
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("pluginConfigDaoImpl")
public class PluginConfigDaoImpl extends BaseDaoImpl<PluginConfigEntity, Long> implements PluginConfigDao {

    @Override
    public boolean pluginExists(String plugin) {
        if (StringUtils.isBlank(plugin)) {
            return false;
        }
        String jpql = "select count(*) from PluginConfigEntity pluginConfigs where pluginConfigs.plugin = :plugin";
        Long count = entityManager.createQuery(jpql, Long.class).setFlushMode(FlushModeType.COMMIT)
                .setParameter("plugin", plugin).getSingleResult();
        return count > 0;
    }

    @Override
    public PluginConfigEntity findByPlugin(String plugin) {
        if (StringUtils.isBlank(plugin)) {
            return null;
        }
        try {
            String jpql = "select pluginConfigs from PluginConfigEntity pluginConfigs where pluginConfigs.plugin = :plugin";
            return entityManager.createQuery(jpql, PluginConfigEntity.class).setFlushMode(FlushModeType.COMMIT)
                    .setParameter("plugin", plugin).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}