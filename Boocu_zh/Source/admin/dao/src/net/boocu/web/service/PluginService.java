/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.plugin.texting.TextingPlugin;

/**
 * Service - 插件
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface PluginService {
 
    /**
     * 获取存储插件
     * 
     * @return 存储插件
     */
    List<StoragePlugin> getStoragePlugins();

    /**
     * 获取短信插件
     * 
     * @return 短信插件
     */
    List<TextingPlugin> getTextingPlugins();

 
    /**
     * 获取存储插件
     * 
     * @param enabled
     *            是否启用
     * @return 存储插件
     */
    List<StoragePlugin> getStoragePlugins(boolean enabled);

    /**
     * 获取短信插件
     * 
     * @param enabled
     *            是否启用
     * @return 短信插件
     */
    List<TextingPlugin> getTextingPlugins(boolean enabled);

 
    /**
     * 获取默认存储插件
     * 
     * @return 默认存储插件
     */
    StoragePlugin getDefaultStoragePlugin();

    /**
     * 获取默认短信插件
     * 
     * @return 默认短信插件
     */
    TextingPlugin getDefaultTextingPlugin();

 
    /**
     * 获取存储插件
     * 
     * @param id
     *            ID
     * @return 存储插件
     */
    StoragePlugin getStoragePlugin(String id);

    /**
     * 获取短信插件
     * 
     * @param id
     *            ID
     * @return 短信插件
     */
    TextingPlugin getTextingPlugin(String id);

}