/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.storage.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import net.boocu.web.FileInfo;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.enums.StorageMethodEnum;
import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.service.PluginConfigService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

/**
 * Plugin - 本地文件存储
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Component("fileStoragePlugin")
public class FileStoragePlugin extends StoragePlugin implements ServletContextAware {
 
    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;

    /** servletContext */
    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
 
    public String getName() {
        return "本地文件存储";
    }

  
    public StorageMethodEnum getStorageMethod() {
        return StorageMethodEnum.offline;
    }
 
    public String getVersion() {
        return "3.0";
    }

  
    public String getAuthor() {
        return "bee";
    }

    public String getSiteUrl() {
        return "http://www.四季时科技";
    }

    @Override
    public String getInstallUrl() {
        return "file/install";
    }

    @Override
    public String getUninstallUrl() {
        return "file/uninstall";
    }

    @Override
    public String getSettingUrl() {
        return "file/setting";
    }

    @Override
    public String getUrl(String path) {
        String urlPrefix = getUrlPrefix();
        if (StringUtils.isNotBlank(urlPrefix)) {
            return urlPrefix + path;
        }
        return null;
    }

    @Override
    public List<FileInfo> browser(String path) {
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        File directory = new File(servletContext.getRealPath(path));
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file.getName());
                fileInfo.setUrl(getUrl(path) + file.getName());
                fileInfo.setIsDirectory(file.isDirectory());
                fileInfo.setSize(file.length());
                fileInfo.setLastModified(new Date(file.lastModified()));
                fileInfos.add(fileInfo);
            }
        }
        return fileInfos;
    }

    @Override
    public void upload(String path, File file, String contentType) {
        try {
            File destFile = new File(servletContext.getRealPath(path));
            FileUtils.moveFile(file, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public String getAttribute(String name) {
		PluginConfigEntity pPluginConfig = getPluginConfig();
	    return pPluginConfig != null ? (pPluginConfig).getAttribute(name) : null;
	}


	@Override
	public boolean getEnabled() { 
		PluginConfigEntity pPluginConfig = getPluginConfig();
	  	return pPluginConfig != null ? (pPluginConfig).getEnabled() : false;
	}

	@Override
	public Integer getOrder() {
		PluginConfigEntity pPluginConfig = getPluginConfig();
		return pPluginConfig != null ? ( pPluginConfig).getOrder() : null;
	}
 
	@Override
	public PluginConfigEntity getPluginConfig() {
		return (PluginConfigEntity) pluginConfigService.findByPlugin(getId());
	}
 

	@Override
	public boolean getInstalled() {
		return pluginConfigService.pluginExists(getId());
	}

}