/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.texting.inolinkEucp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.framework.enums.RequestMethodEnum;
import net.boocu.framework.util.XmlUtils;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.plugin.texting.inolinkEucp.bean.CommandBean;
import net.boocu.web.plugin.texting.inolinkEucp.bean.MessageBean;
import net.boocu.web.service.PluginConfigService;

import org.springframework.stereotype.Component;

/**
 * Plugin - 凌凯Eucp短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Component("inolinkEucpTextingPlugin")
public class InolinkEucpTextingPlugin extends TextingPlugin {

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;

    @Override
    public String getName() {
        return "凌凯Eucp短信";
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
        return "http://www.inolink.com";
    }

    @Override
    public String getInstallUrl() {
        return "inolink_eucp/install";
    }

    @Override
    public String getUninstallUrl() {
        return "inolink_eucp/uninstall";
    }

    @Override
    public String getSettingUrl() {
        return "inolink_eucp/setting";
    }

    @Override
    public RequestMethodEnum getRequestMethod() {
        return RequestMethodEnum.post;
    }

    @Override
    public String getRequestCharset() {
        return "GBK";
    }

    @Override
    public String getRequestUrl() {
        return "http://eucp.inolink.com/Api/Execute";
    }

    @Override
    public void send(String toMobile, String content) throws Exception {
        // 消息
        MessageBean messageBean = new MessageBean();
        messageBean.setType("2");
        messageBean.setTitle(getPartner());
        messageBean.setReceiver(toMobile);
        messageBean.setContent(content);
        // 命令
        CommandBean commandBean = new CommandBean();
        commandBean.setFunction("SendMessage");
        commandBean.setUsername(getPartner());
        commandBean.setPassword(getKey());
        commandBean.setSign("");
        commandBean.setMessage(messageBean);
        // 发送短信
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("Command", XmlUtils.convertString(commandBean));
        post(getRequestUrl(), parameterMap);
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