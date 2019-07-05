/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.enums;

/**
 * Enum - 令牌方式
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public enum TokenMethodEnum {

	/** 登录 */
    auto_login,
    
    /** 注册 */
    user_regist,

    /** 绑定手机 */
    mobile_binding,

    /** 修改手机 */
    mobile_modif,

    /** 绑定邮箱 */
    email_binding,

    /** 修改邮箱 */
    email_modif,

    /** 绑定银行卡 */
    bankcard_binding,

    /** 修改银行卡 */
    bankcard_modif,

    /** 重置登录密码 */
    user_password_rest,
    
    /** 找回登录密码 */
    user_password_find,

    /** 修改登录密码 */
    user_password_modif,

    /** 找回支付密码 */
    account_password_find,

    /** 修改支付密码 */
    account_password_modif,
    
    /** 第三方SDK */
    open_sdk

}
