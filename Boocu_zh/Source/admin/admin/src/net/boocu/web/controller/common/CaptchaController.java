/*
 * Copyright 2010-2014 forgtime.com. All rights reserved.
 * Support: http://www.forgtime.com
 */
package net.boocu.web.controller.common;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.boocu.web.service.CaptchaService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller - 验证码
 * 
 * @author 四季时科技
 * @version 3.0
 */
@Controller("captchaController")
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;

    /**
     * 验证码
     */
    @RequestMapping(method = RequestMethod.GET)
    public void image(String captchaId, HttpSession session, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(captchaId)) {
            captchaId = session.getId();
        }
        response.addHeader("Powered-By", "forgtime.com");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setDateHeader("Expires", 0L);
        response.setContentType("image/jpeg");

        ServletOutputStream servletOutputStream = null;
        try {
            servletOutputStream = response.getOutputStream();
            BufferedImage bufferedImage = captchaService.buildImage(captchaId);
            ImageIO.write(bufferedImage, "jpg", servletOutputStream);
            servletOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(servletOutputStream);
        }
    }

}