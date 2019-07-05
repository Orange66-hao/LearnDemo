/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.plugin.texting.inolinkMb345;

import javax.annotation.Resource;

import net.boocu.framework.controller.BaseAdminController;
import net.boocu.web.entity.PluginConfigEntity;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.service.PluginConfigService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 凌凯Mb345短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("adminInolinkMb345TextingController")
@RequestMapping("/admin/texting_plugin/inolink_mb345")
public class InolinkMb345TextingController extends BaseAdminController {

    /** 索引重定向URL */
    private static final String INDEX_REDIRECT_URL = "redirect:/admin/texting_plugin";

    @Resource(name = "inolinkMb345TextingPlugin")
    private InolinkMb345TextingPlugin inolinkMb345TextingPlugin;

    @Resource(name = "pluginConfigServiceImpl")
    private PluginConfigService pluginConfigService;

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(ModelMap model, RedirectAttributes redirectAttributes) {
        model.addAttribute("plugin", inolinkMb345TextingPlugin);
        return "/com/bee/web/plugin/texting/inolinkMb345/setting";
    }

    /**
     * 设置
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String setting(InolinkMb345TextingBean inolinkTextingBean, RedirectAttributes redirectAttributes) {

        // Bean Validation
        if (!verify(inolinkTextingBean)) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        // 验证凌凯Mb345短信
        PluginConfigEntity pPluginConfig = inolinkMb345TextingPlugin.getPluginConfig();
        if (pPluginConfig == null) {
            // addFlashMessage(redirectAttributes, ERROR_MESSAGE);
            return INDEX_REDIRECT_URL;
        }

        pPluginConfig.setAttribute(TextingPlugin.PARTNER_ATTR, inolinkTextingBean.getPartner());
        pPluginConfig.setAttribute(TextingPlugin.KEY_ATTR, inolinkTextingBean.getKey());
        pPluginConfig.setAttribute(TextingPlugin.DESCRIPTION_ATTR, inolinkTextingBean.getDescription());
        pPluginConfig.setEnabled(inolinkTextingBean.getEnabled());
        pPluginConfig.setOrder(inolinkTextingBean.getOrder());
        pluginConfigService.update(pPluginConfig);

        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

    /**
     * 安装
     */
    @RequestMapping(value = "/install", method = RequestMethod.GET)
    public String install(RedirectAttributes redirectAttributes) {
        if (!inolinkMb345TextingPlugin.getInstalled()) {
            PluginConfigEntity pPluginConfig = new PluginConfigEntity();
            pPluginConfig.setPlugin(inolinkMb345TextingPlugin.getId());
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
        if (inolinkMb345TextingPlugin.getInstalled()) {
            pluginConfigService.delete(inolinkMb345TextingPlugin.getPluginConfig());
        }
        // addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
        return INDEX_REDIRECT_URL;
    }

}