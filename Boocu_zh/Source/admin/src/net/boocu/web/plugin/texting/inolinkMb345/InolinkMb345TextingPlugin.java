/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.texting.inolinkMb345;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.enums.RequestMethodEnum;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.service.PluginConfigService;

import org.springframework.stereotype.Component;

/**
 * Plugin - 凌凯Mb345短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Component("inolinkMb345TextingPlugin")
public class InolinkMb345TextingPlugin extends TextingPlugin {

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;
    
    @Override
    public String getName() {
        return "凌凯Mb345短信";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getAuthor() {
        return "bee";
    }

    @Override
    public String getSiteUrl() {
        return "http://www.mb345.com";
    }

    @Override
    public String getInstallUrl() {
        return "inolink_mb345/install";
    }

    @Override
    public String getUninstallUrl() {
        return "inolink_mb345/uninstall";
    }

    @Override
    public String getSettingUrl() {
        return "inolink_mb345/setting";
    }

    @Override
    public RequestMethodEnum getRequestMethod() {
        return RequestMethodEnum.get;
    }

    @Override
    public String getRequestCharset() {
        return "GBK";
    }

    @Override
    public String getRequestUrl() {
        return "http://mb345.com/WS/BatchSend.aspx";
    }

    @Override
    public void send(String toMobile, String content) throws Exception {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("CorpID", getPartner());
        parameterMap.put("Pwd", getKey());
        parameterMap.put("Mobile", toMobile);
        parameterMap.put("Content", content);
        parameterMap.put("Cell", null);
        parameterMap.put("SendTime", null);
        get(getRequestUrl(), parameterMap);
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