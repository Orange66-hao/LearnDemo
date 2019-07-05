/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;

import net.boocu.framework.enums.CaptchaTypeEnum;
import net.boocu.framework.util.SettingUtils;
import net.boocu.web.service.CaptchaService;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * Service - 验证码
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Service("captchaServiceImpl")
public class CaptchaServiceImpl implements CaptchaService {

    @Resource(name = "imageCaptchaService")
    private ImageCaptchaService imageCaptchaService;

    @Override
    public BufferedImage buildImage(String captchaId) {
        try {
             return imageCaptchaService.getImageChallengeForID(captchaId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verify(CaptchaTypeEnum captchaType, String captchaId, String captcha) {
        // 验证验证码范围
        if (captchaType == null
                || ArrayUtils.contains(SettingUtils.get().getSecurity().getCaptchaScopes(), captchaType)) {
            return verify(captchaId, captcha);
        } else {
            return true;
        }
    }

    @Override
    public boolean verify(String captchaId, String captcha) {
        // 验证验证ID、验证码
        if (StringUtils.isNotBlank(captchaId) && StringUtils.isNotBlank(captcha)) {
            try {
                // 验证验证码
                 return imageCaptchaService.validateResponseForID(captchaId, StringUtils.upperCase(captcha));
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

}