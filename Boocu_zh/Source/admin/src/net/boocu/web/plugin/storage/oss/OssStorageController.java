/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.storage.oss;

import java.math.BigDecimal;

import javax.annotation.Resource;

import net.boocu.framework.controller.BaseAdminController;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.service.PluginConfigService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 阿里云存储
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminOssStorageController")
@RequestMapping("/admin/storage_plugin/oss")
public class OssStorageController extends BaseAdminController {

	
    /** 索引重定向URL */
    private static final String INDEX_REDIRECT_URL = "redirect:/admin/storage_plugin";

    @Resource(name = "ossStoragePlugin")
    private OssStoragePlugin ossStoragePlugin;

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(ModelMap model, RedirectAttributes redirectAttributes) {
        model.addAttribute("plugin", ossStoragePlugin);
        return "/com/bee/web/plugin/storage/oss/setting";
    }

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String setting(OssStorageBean ossBean, RedirectAttributes redirectAttributes) {

        // Bean Validation
        if (!verify(ossBean)) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        // 验证阿里云存储
        PluginConfigEntity pPluginConfig = ossStoragePlugin.getPluginConfig();
        if (pPluginConfig == null) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        pPluginConfig.setAttribute(StoragePlugin.STORAGE_NAME_ATTR, ossBean.getStorageName());
        pPluginConfig.setAttribute(OssStoragePlugin.ACCESS_ID_ATTR, ossBean.getAccessId());
        pPluginConfig.setAttribute(OssStoragePlugin.ACCESS_KEY_ATTR, ossBean.getAccessKey());
        pPluginConfig.setAttribute(OssStoragePlugin.BUCKET_NAME_ATTR, ossBean.getBucketName());
        pPluginConfig.setAttribute(StoragePlugin.URLPREFIX_ATTR, ossBean.getUrlPrefix());
        pPluginConfig.setAttribute(StoragePlugin.DESCRIPTION_ATTR, ossBean.getDescription());
        pPluginConfig.setEnabled(ossBean.getEnabled());
        pPluginConfig.setOrder(ossBean.getOrder());
        pluginConfigService.update(pPluginConfig);

        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

    /**
     * 安装
     */
    @RequestMapping(value = "/install", method = RequestMethod.GET)
    public String install(RedirectAttributes redirectAttributes) {
        String specificationVersion = System.getProperty("java.specification.version");
        if (StringUtils.isNotBlank(specificationVersion)) {
            BigDecimal version = new BigDecimal(specificationVersion);
            if (version.compareTo(new BigDecimal("1.6")) < 0) {
                // addFlashMessage(redirectAttributes, Message.error("当前JDK版本不支持该插件"));
                return INDEX_REDIRECT_URL;
            }
        }
        if (!ossStoragePlugin.getInstalled()) {
            PluginConfigEntity pluginConfig = new PluginConfigEntity();
            pluginConfig.setPlugin(ossStoragePlugin.getId());
            pluginConfig.setEnabled(false);
            pluginConfigService.save(pluginConfig);
        }
        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

    /**
     * 卸载
     */
    @RequestMapping(value = "/uninstall", method = RequestMethod.GET)
    public String uninstall(RedirectAttributes redirectAttributes) {
        if (ossStoragePlugin.getInstalled()) {
            pluginConfigService.delete(ossStoragePlugin.getPluginConfig());
        }
        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

}