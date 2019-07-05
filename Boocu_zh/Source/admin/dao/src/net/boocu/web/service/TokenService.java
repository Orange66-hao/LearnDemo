/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.Date;

import net.boocu.project.util.MessageUtil;
import net.boocu.web.entity.TokenEntity;
import net.boocu.web.enums.TokenMethodEnum;

/**
 * Service - 令牌
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface TokenService extends BaseService<TokenEntity, Long> {

    /**
     * 生成令牌
     * 
     * @param tokenMethod
     *            令牌方式
     * @param addr
     *            通讯地址
     * @return 生成成功的令牌
     * @throws 生成失败的异常
     */
    TokenEntity build(TokenMethodEnum tokenMethod, String addr) throws Exception;

    /**
     * 生成令牌
     * 
     * @param tokenMethod
     *            令牌方式
     * @param addr
     *            通讯地址
     * @param addr
     *            到期时间
     *            
     * @return 生成成功的令牌
     * @throws 生成失败的异常
     */
    TokenEntity buildToken(TokenMethodEnum tokenMethod, String addr ,Date expiry ) throws Exception;
    /**
     * 验证令牌
     * 
     * @param tokenMethod
     *            令牌方式
     * @param addr
     *            通讯地址
     * @param code
     *            令牌代码
     * @param isDelete
     *            是否删除
     * @return 验证是否通过
     */
    boolean verify(TokenMethodEnum tokenMethod, String addr, String code, boolean isDelete);

    /**
     * 验证令牌
     * 
     * @param tokenMethod
     *            令牌方式
     * @param addr
     *            通讯地址
     * @param code
     *            令牌代码
     * @return 验证是否通过
     */
    boolean verify(TokenMethodEnum tokenMethod, String addr, String code);

    /**
     * 
     * @param autoLogin  令牌方式
     * @param username  通讯地址(用户名)
     * @param code    令牌代码
     * @param vcode  令牌效验码
     * @param userId  用户id
     * @return
     */
	boolean verifyToken(TokenMethodEnum tokenMethod, String username,
			String code, String vcode, Long userId);


	/**
	 * 生成绑定用户id的效验码
	 * @param userId
	 * @param tokenEntity
	 * @return
	 */
	public String getValidateCode(Long userId,TokenEntity tokenEntity);


	
	/**
     * 生成token
     * @param userId
     * @param tokenEntity
     * @return
     */
    public String getTokenString(Long userId,TokenEntity tokenEntity);
	
    MessageUtil getDataFromTokenString(String tokenString);
    
    boolean validate(String tokenString , TokenMethodEnum tokenMethod);
}


