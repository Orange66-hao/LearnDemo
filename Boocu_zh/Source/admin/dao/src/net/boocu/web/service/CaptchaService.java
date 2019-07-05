/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.awt.image.BufferedImage;

import net.boocu.framework.enums.CaptchaTypeEnum;

/**
 * Service - 验证码
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface CaptchaService {

    /**
     * 生成验证码图片
     * 
     * @param captchaId
     *            验证ID
     * @return 验证码图片
     */
    BufferedImage buildImage(String captchaId);

    /**
     * 验证码验证
     * 
     * @param captchaType
     *            验证码类型
     * @param captchaId
     *            验证ID
     * @param captcha
     *            验证码（忽略大小写）
     * @return 验证码验证是否通过
     */
    boolean verify(CaptchaTypeEnum captchaType, String captchaId, String captcha);

    /**
     * 验证码验证
     * 
     * @param captchaId
     *            验证ID
     * @param captcha
     *            验证码（忽略大小写）
     * @return 验证码验证是否通过
     */
    boolean verify(String captchaId, String captcha);

}