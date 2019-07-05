/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.texting.zucp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.enums.RequestMethodEnum;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.service.PluginConfigService;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Plugin - 漫道短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Component("zucpTextingPlugin")
public class ZucpTextingPlugin extends TextingPlugin {

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;
    
    @Override
    public String getName() {
        return "漫道短信";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public String getAuthor() {
        return "bee";
    }

    @Override
    public String getSiteUrl() {
        return "http://www.zucp.net";
    }

    @Override
    public String getInstallUrl() {
        return "zucp/install";
    }

    @Override
    public String getUninstallUrl() {
        return "zucp/uninstall";
    }

    @Override
    public String getSettingUrl() {
        return "zucp/setting";
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
        return "http://sdk2.entinfo.cn:8061/mdsmssend.ashx";
    }

    @Override
    public void send(String toMobile, String content) throws Exception {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("sn", getPartner());
        parameterMap.put("pwd", StringUtils.upperCase(DigestUtils.md5Hex(getPartner() + getKey())));
        parameterMap.put("mobile", toMobile);
        parameterMap.put("content", content);
        parameterMap.put("ext", null);
        parameterMap.put("stime", null);
        parameterMap.put("rrid", null);
        parameterMap.put("msgfmt", null);
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