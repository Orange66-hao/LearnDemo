/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.storage.file;

import javax.annotation.Resource;

import net.boocu.framework.controller.BaseAdminController;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.storage.StoragePlugin;
import net.boocu.web.service.PluginConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 本地文件存储
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminFileStorageController")
@RequestMapping("/admin/storage_plugin/file")
public class FileStorageController extends BaseAdminController {

    /** 索引重定向URL */
    private static final String INDEX_REDIRECT_URL = "redirect:/admin/storage_plugin";

    @Resource(name = "fileStoragePlugin")
    private FileStoragePlugin fileStoragePlugin;

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(ModelMap model, RedirectAttributes redirectAttributes) {
        model.addAttribute("plugin", fileStoragePlugin);
        return "/com/bee/web/plugin/storage/file/setting";
    }

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String setting(FileStorageBean fileBean, RedirectAttributes redirectAttributes) {

        // Bean Validation
        if (!verify(fileBean)) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        // 验证本地文件存储
        PluginConfigEntity pPluginConfig = fileStoragePlugin.getPluginConfig();
        if (pPluginConfig == null) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        pPluginConfig.setAttribute(StoragePlugin.STORAGE_NAME_ATTR, fileBean.getStorageName());
        pPluginConfig.setAttribute(StoragePlugin.URLPREFIX_ATTR, fileBean.getUrlPrefix());
        pPluginConfig.setAttribute(StoragePlugin.DESCRIPTION_ATTR, fileBean.getDescription());
        pPluginConfig.setEnabled(fileBean.getEnabled());
        pPluginConfig.setOrder(fileBean.getOrder());
        pluginConfigService.update(pPluginConfig);

        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

    /**
     * 安装
     */
    @RequestMapping(value = "/install", method = RequestMethod.GET)
    public String install(RedirectAttributes redirectAttributes) {
        if (!fileStoragePlugin.getInstalled()) {
            PluginConfigEntity pPluginConfig = new PluginConfigEntity();
            pPluginConfig.setPlugin(fileStoragePlugin.getId());
            pPluginConfig.setEnabled(false);
            pluginConfigService.save(pPluginConfig);
        }
        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

    /**
     * 卸载
     */
    @RequestMapping(value = "/uninstall", method = RequestMethod.GET)
    public String uninstall(RedirectAttributes redirectAttributes) {
        if (fileStoragePlugin.getInstalled()) {
            pluginConfigService.delete(fileStoragePlugin.getPluginConfig());
        }
        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

}