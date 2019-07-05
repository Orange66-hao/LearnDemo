/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.Map;

/**
 * Service - 短信
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface TextingService {

    /**
     * 发送短信
     * 
     * @param toMobile
     *            收信人手机号码
     * @param templatePath
     *            模板路径
     * @param model
     *            数据
     * @param async
     *            是否异步
     * @throws 发送失败的异常
     */
    void send(String toMobile, String templatePath, Map<String, Object> model, boolean async) throws Exception;

    /**
     * 发送短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param templatePath
     *            模板路径
     * @param model
     *            数据
     * @throws 发送失败的异常
     */
    void send(String toMobile, String templatePath, Map<String, Object> model) throws Exception;

    /**
     * 发送注册验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendUserRegistCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送绑定手机验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendMobileBindingCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送修改手机验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendMobileModifCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送手机修改通知短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendMobileModifNotice(String toMobile, String username) throws Exception;

    /**
     * 发送绑定银行卡验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendBankcardBindingCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送修改银行卡验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendBankcardModifCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送银行卡修改通知短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendBankcardModifNotice(String toMobile, String username) throws Exception;

    /**
     * 发送找回登录密码验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendUserPasswordFindCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送修改登录密码验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendUserPasswordModifCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送登陆密码修改通知短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendUserPasswordModifNotice(String toMobile, String username) throws Exception;

    /**
     * 发送找回支付密码验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendAccountPasswordFindCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送修改支付密码验证码短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendAccountPasswordModifCaptcha(String toMobile, String username) throws Exception;

    /**
     * 发送支付密码修改通知短信（异步）
     * 
     * @param toMobile
     *            收信人手机号码
     * @param username
     *            用户名
     * @throws 发送失败的异常
     */
    void sendAccountPasswordModifNotice(String toMobile, String username) throws Exception;

}