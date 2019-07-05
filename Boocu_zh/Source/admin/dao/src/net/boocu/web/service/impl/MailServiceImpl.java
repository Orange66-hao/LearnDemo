/*
 * Copyright 2014-2015 bee-lending.com. All rights reserved.
 * Support: http://www.bee-lending.com
 */
package net.boocu.web.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import net.boocu.framework.util.SettingUtils;
import net.boocu.web.enums.TokenMethodEnum;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.TemplateService;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.comm.CommSetting;

import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Service - 邮件
 * 
 * @author 蜜蜂贷
 * @version 1.0
 */
@Service("mailServiceImpl")
public class MailServiceImpl implements EMailService {

    @Resource(name = "freeMarkerConfigurer")
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Resource(name = "javaMailSender")
    private JavaMailSenderImpl javaMailSender;

    @Resource(name = "taskExecutor")
    private TaskExecutor taskExecutor;

    @Resource(name = "templateServiceImpl")
    private TemplateService templateService;

    @Resource(name = "tokenServiceImpl")
    private TokenService tokenService;

    @Override
    public void send(String smtpFromMail, String smtpHost, Integer smtpPort, String smtpUsername, String smtpPassword,
            String toMail, String subject, String templatePath, Map<String, Object> model, boolean async)
            throws Exception {
        Assert.hasText(smtpFromMail);
        Assert.hasText(smtpHost);
        Assert.notNull(smtpPort);
        Assert.hasText(smtpUsername);
        Assert.hasText(smtpPassword);
        Assert.hasText(toMail);
        Assert.hasText(subject);
        Assert.hasText(templatePath);

        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Template template = configuration.getTemplate(templatePath);
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        javaMailSender.setHost(smtpHost);
        javaMailSender.setPort(smtpPort);
        javaMailSender.setUsername(smtpUsername);
        javaMailSender.setPassword(smtpPassword);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        mimeMessageHelper.setFrom(MimeUtility.encodeWord(model.get("siteName").toString()) + " <"
                + smtpFromMail + ">");
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setTo(toMail);
        mimeMessageHelper.setText(text, true);
        if (async) {
            addSendTask(mimeMessage);
        } else {
            javaMailSender.send(mimeMessage);
        }
    }

    @Override
    public void send(String toMail, String subject, String templatePath, Map<String, Object> model, boolean async)
            throws Exception {
        CommSetting setting = SettingUtils.get().getComm();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(),
                setting.getSmtpPassword(), toMail, subject, templatePath, model, async);
    }

    @Override
    public void send(String toMail, String subject, String templatePath, Map<String, Object> model) throws Exception {
        CommSetting setting = SettingUtils.get().getComm();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(),
                setting.getSmtpPassword(), toMail, subject, templatePath, model, true);
    }

    @Override
    public void send(String toMail, String subject, String templatePath) throws Exception {
        CommSetting setting = SettingUtils.get().getComm();
        send(setting.getSmtpFromMail(), setting.getSmtpHost(), setting.getSmtpPort(), setting.getSmtpUsername(),
                setting.getSmtpPassword(), toMail, subject, templatePath, null, true);
    }

    @Override
    public void sendRegist(String toMail, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.user_regist, toMail));
        net.boocu.web.Template template = templateService.get("registMail");
        send(toMail, template.getName(), template.getTemplatePath(), model);
    }
    @Override
    public void sendRegist(String toMail, String username,String tokenUrl) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("email", toMail);
        model.put("tokenUrl", tokenUrl);
        net.boocu.web.Template template = templateService.get("registByUrlMail");
        send(toMail, template.getName(), template.getTemplatePath(), model);
    }
    
    @Override
    public void sendBinding(String toMail, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.email_binding, toMail));
        net.boocu.web.Template template = templateService.get("bindingMail");
        send(toMail, template.getName(), template.getTemplatePath(), model);
    }

    @Override
    public void sendFindPassword(String toMail, String username) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("token", tokenService.build(TokenMethodEnum.user_password_find, toMail));
        net.boocu.web.Template template = templateService.get("findPasswordMail");
        send(toMail, template.getName(), template.getTemplatePath(), model);
    }
    
    @Override
    public void sendFindPassword(String toMail, String username ,String tokenUrl) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("tokenUrl", tokenUrl);
        net.boocu.web.Template template = templateService.get("findPasswordByUrlMail");
        send(toMail, template.getName(), template.getTemplatePath(), model);
    }
    /**
     * 添加邮件发送任务
     * 
     * @param mimeMessage
     *            MimeMessage
     */
    private void addSendTask(final MimeMessage mimeMessage) {
        try {
            taskExecutor.execute(new Runnable() {
                public void run() {
                    javaMailSender.send(mimeMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}