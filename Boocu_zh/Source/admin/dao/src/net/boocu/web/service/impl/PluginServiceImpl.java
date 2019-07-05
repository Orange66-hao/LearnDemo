/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.service.PluginService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.springframework.stereotype.Service;

/**
 * Service - 插件
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("pluginServiceImpl")
public class PluginServiceImpl implements PluginService {

 
    @Resource
    private List<StoragePlugin> storagePlugins = new ArrayList<StoragePlugin>();

    @Resource
    private List<TextingPlugin> textingPlugins = new ArrayList<TextingPlugin>();
 
    @Resource
    private Map<String, StoragePlugin> storagePluginMap = new HashMap<String, StoragePlugin>();

    @Resource
    private Map<String, TextingPlugin> textingPluginMap = new HashMap<String, TextingPlugin>();

    
    @Override
    public List<StoragePlugin> getStoragePlugins() {
        Collections.sort(storagePlugins);
        return storagePlugins;
    }

    @Override
    public List<TextingPlugin> getTextingPlugins() {
        Collections.sort(textingPlugins);
        return textingPlugins;
    }

 

    @Override
    public List<StoragePlugin> getStoragePlugins(final boolean enabled) {
        List<StoragePlugin> result = new ArrayList<StoragePlugin>();
        CollectionUtils.select(storagePlugins, new Predicate() {
            public boolean evaluate(Object object) {
                StoragePlugin storagePlugin = (StoragePlugin) object;
                return storagePlugin.getEnabled() == enabled;
            }
        }, result);
        Collections.sort(result);
        return result;
    }

    @Override
    public List<TextingPlugin> getTextingPlugins(final boolean enabled) {
        List<TextingPlugin> result = new ArrayList<TextingPlugin>();
        CollectionUtils.select(textingPlugins, new Predicate() {
            public boolean evaluate(Object object) {
                TextingPlugin textingPlugin = (TextingPlugin) object;
                return textingPlugin.getEnabled() == enabled;
            }
        }, result);
        Collections.sort(result);
        return result;
    }

 
    @Override
    public StoragePlugin getDefaultStoragePlugin() {
        List<StoragePlugin> storagePlugins = getStoragePlugins(true);
        return storagePlugins.size() > 0 ? storagePlugins.get(0) : null;
    }

    @Override
    public TextingPlugin getDefaultTextingPlugin() {
        List<TextingPlugin> textingPlugins = getTextingPlugins(true);
        return textingPlugins.size() > 0 ? textingPlugins.get(0) : null;
    }
 
    @Override
    public StoragePlugin getStoragePlugin(String id) {
        return storagePluginMap.get(id);
    }

    @Override
    public TextingPlugin getTextingPlugin(String id) {
        return textingPluginMap.get(id);
    }

 

}