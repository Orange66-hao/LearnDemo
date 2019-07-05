/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.boocu.web.enums.TokenMethodEnum;
import net.boocu.web.plugin.texting.TextingPlugin;
import net.boocu.web.service.PluginService;
import net.boocu.web.service.TemplateService;
import net.boocu.web.service.TextingService;
import net.boocu.web.service.TokenService;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service - 短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("textingServiceImpl")
public class TextingServiceImpl implements TextingService {

    @Resource(name = "pluginServiceImpl")
    private PluginService pluginService;

    @Resource(name = "freeMarkerConfigurer")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Resource(name = "taskExecutor")
    private TaskExecutor taskExecutor;

    @Resource(name = "templateServiceImpl")
    private TemplateService templateService;

    @Resource(name = "tokenServiceImpl")
    private TokenService tokenService;

    @Override
    public void send(String toMobile, String templatePath, Map<String, Object> model, boolean async) throws Exception {
        Assert.hasText(toMobile);
        Assert.notNull(templatePath);
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate(templatePath);
            String cont = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            if (async) {
                addSendTask(toMobile, cont);
            } else {
                send(toMobile, cont);
            }
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String toMobile, String templatePath, Map<String, Object> model) throws Exception {
        send(toMobile, templatePath, model, true);
    }

    @Override
    public void sendUserRegistCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.user_regist, toMobile));
        net.boocu.web.Template template = templateService.get("user_regist_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendMobileBindingCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.mobile_binding, toMobile));
        net.boocu.web.Template template = templateService.get("mobile_binding_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendMobileModifCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.mobile_modif, toMobile));
        net.boocu.web.Template template = templateService.get("mobile_modif_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendMobileModifNotice(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        net.boocu.web.Template template = templateService.get("mobile_modif_notice_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendBankcardBindingCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.bankcard_binding, toMobile));
        net.boocu.web.Template template = templateService.get("bankcard_binding_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendBankcardModifCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.bankcard_modif, toMobile));
        net.boocu.web.Template template = templateService.get("bankcard_modif_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendBankcardModifNotice(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        net.boocu.web.Template template = templateService.get("bankcard_modif_notice_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendUserPasswordFindCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.user_password_find, toMobile));
        net.boocu.web.Template template = templateService.get("user_password_find_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendUserPasswordModifCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.user_password_modif, toMobile));
        net.boocu.web.Template template = templateService.get("user_password_modif_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendUserPasswordModifNotice(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        net.boocu.web.Template template = templateService.get("user_password_modif_notice_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendAccountPasswordFindCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.account_password_find, toMobile));
        net.boocu.web.Template template = templateService.get("account_password_find_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendAccountPasswordModifCaptcha(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.account_password_modif, toMobile));
        net.boocu.web.Template template = templateService.get("account_password_modif_captcha_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    @Override
    public void sendAccountPasswordModifNotice(String toMobile, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        net.boocu.web.Template template = templateService.get("account_password_modif_notice_texting");
        send(toMobile, template.getTemplatePath(), model);
    }

    /**
     * 发送短信
     * 
     * @param toMobile
     *            收件人手机号码
     * @param cont
     *            短信内容
     */
    private void send(String toMobile, String cont) throws Exception {
        TextingPlugin textingPlugin = pluginService.getDefaultTextingPlugin();
        if (textingPlugin != null) {
            textingPlugin.send(toMobile, cont);
        }
    }

    /**
     * 添加短信发送任务
     * 
     * @param mimeMessage
     *            MimeMessage
     */
    private void addSendTask(final String toMobile, final String cont) {
        taskExecutor.execute(new Runnable() {
            public void run() {
                try {
                    send(toMobile, cont);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}