/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.storage.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import net.boocu.web.FileInfo;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.enums.StorageMethodEnum;
import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.service.PluginConfigService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;

/**
 * Plugin - 阿里云存储
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Component("ossStoragePlugin")
public class OssStoragePlugin extends StoragePlugin {

    /** “ACCESS ID”属性名称 */
    public static final String ACCESS_ID_ATTR = "accessId";

    /** “ACCESS KEY”属性名称 */
    public static final String ACCESS_KEY_ATTR = "accessKey";

    /** “BUCKET名称”属性名称 */
    public static final String BUCKET_NAME_ATTR = "bucketName";

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;
    
    @Override
    public String getName() {
        return "阿里云存储";
    }

    @Override
    public StorageMethodEnum getStorageMethod() {
        return StorageMethodEnum.online;
    }

    @Override
    public String getVersion() {
        return "1.0.12";
    }

    @Override
    public String getAuthor() {
        return "bee";
    }

    @Override
    public String getSiteUrl() {
        return "http://www.四季时科技";
    }

    @Override
    public String getInstallUrl() {
        return "oss/install";
    }

    @Override
    public String getUninstallUrl() {
        return "oss/uninstall";
    }

    @Override
    public String getSettingUrl() {
        return "oss/setting";
    }

    /**
     * 获取ACCESS ID
     * 
     * @return ACCESS ID
     */
    public String getAccessId() {
        return getAttribute(ACCESS_ID_ATTR);
    }

    /**
     * 获取ACCESS KEY
     * 
     * @return ACCESS KEY
     */
    public String getAccessKey() {
        return getAttribute(ACCESS_KEY_ATTR);
    }

    /**
     * 获取BUCKET名称
     * 
     * @return BUCKET名称
     */
    public String getBucketName() {
        return getAttribute(BUCKET_NAME_ATTR);
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
        String accessId = getAccessId();
        String accessKey = getAccessKey();
        String bucketName = getBucketName();
        String urlPrefix = getUrlPrefix();
        try {
            OSSClient ossClient = new OSSClient(accessId, accessKey);
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix(StringUtils.removeStart(path, "/"));
            listObjectsRequest.setDelimiter("/");
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            for (String commonPrefix : objectListing.getCommonPrefixes()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(StringUtils.substringAfterLast(StringUtils.removeEnd(commonPrefix, "/"), "/"));
                fileInfo.setUrl(urlPrefix + "/" + commonPrefix);
                fileInfo.setIsDirectory(true);
                fileInfo.setSize(0L);
                fileInfos.add(fileInfo);
            }
            for (OSSObjectSummary ossObjectSummary : objectListing.getObjectSummaries()) {
                if (ossObjectSummary.getKey().endsWith("/")) {
                    continue;
                }
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(StringUtils.substringAfterLast(ossObjectSummary.getKey(), "/"));
                fileInfo.setUrl(urlPrefix + "/" + ossObjectSummary.getKey());
                fileInfo.setIsDirectory(false);
                fileInfo.setSize(ossObjectSummary.getSize());
                fileInfo.setLastModified(ossObjectSummary.getLastModified());
                fileInfos.add(fileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileInfos;
    }

    @Override
    public void upload(String path, File file, String contentType) {
        String accessId = getAccessId();
        String accessKey = getAccessKey();
        String bucketName = getBucketName();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            OSSClient ossClient = new OSSClient(accessId, accessKey);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(file.length());
            ossClient.putObject(bucketName, StringUtils.removeStart(path, "/"), inputStream, objectMetadata);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
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