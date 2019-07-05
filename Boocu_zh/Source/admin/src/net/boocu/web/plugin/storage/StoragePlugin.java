/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.storage;

import java.io.File;
import java.util.List;

import net.boocu.framework.plugin.BasePlugin;
import net.boocu.web.FileInfo;
import net.boocu.web.enums.StorageMethodEnum;

/**
 * Plugin - 存储
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public abstract class StoragePlugin extends BasePlugin {

    /** “存储方式名称”属性名称 */
    public static final String STORAGE_NAME_ATTR = "storageName";

    /** “URL前缀”属性名称 */
    public static final String URLPREFIX_ATTR = "urlPrefix";

    /** “描述”属性名称 */
    public static final String DESCRIPTION_ATTR = "description";

    /**
     * 获取存储方式
     * 
     * @return 存储方式
     */
    public abstract StorageMethodEnum getStorageMethod();

    /**
     * 获取存储方式名称
     * 
     * @return 存储方式名称
     */
    public String getStorageName() {
        return getAttribute(STORAGE_NAME_ATTR);
    }

    /**
     * 获取URL前缀
     * 
     * @return URL前缀
     */
    public String getUrlPrefix() {
        return getAttribute(URLPREFIX_ATTR);
    }

    /**
     * 获取描述
     * 
     * @return 描述
     */
    public String getDescription() {
        return getAttribute(DESCRIPTION_ATTR);
    }

    /**
     * 获取URL
     * 
     * @param path
     *            上传路径
     * @return URL
     */
    public abstract String getUrl(String path);

    /**
     * 文件浏览
     * 
     * @param path
     *            浏览路径
     * @return 文件信息
     */
    public abstract List<FileInfo> browser(String path);

    /**
     * 文件上传
     * 
     * @param path
     *            上传路径
     * @param file
     *            上传文件
     * @param contentType
     *            文件类型
     */
    public abstract void upload(String path, File file, String contentType);

}