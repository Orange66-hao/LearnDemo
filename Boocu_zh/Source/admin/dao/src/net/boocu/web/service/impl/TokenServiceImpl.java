/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import net.boocu.framework.util.SettingUtils;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.dao.TokenDao;
import net.boocu.web.entity.TokenEntity;
import net.boocu.web.enums.TokenMethodEnum;
import net.boocu.web.service.TokenService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.alibaba.druid.util.Base64;

/**
 * Service - 令牌
 * 
 * @author 
 * @version 1.0
 */
@Service("tokenServiceImpl")
public class TokenServiceImpl extends BaseServiceImpl<TokenEntity, Long> implements TokenService {

    @Resource(name = "tokenDaoImpl")
    private TokenDao tokenDao;

    @Resource(name = "tokenDaoImpl")
    public void setBaseDao(TokenDao tokenDao) {
        super.setBaseDao(tokenDao);
    }

    @Override
    @Transactional
    public TokenEntity build(TokenMethodEnum tokenMethod, String addr) throws Exception {

    	 // 获取安全设置
        SecuritySetting setting = SettingUtils.get().getSecurity();
        
       return buildToken(tokenMethod,addr,setting.getTokenExpiry());
    }

    public TokenEntity buildToken(TokenMethodEnum tokenMethod, String addr ,Date expiry ) throws Exception {

        // 参数验证
        if (tokenMethod == null || StringUtils.isBlank(addr)) {
            throw new RuntimeException();
        }

        // 获取安全设置
        SecuritySetting setting = SettingUtils.get().getSecurity();

        // TODO 重发令牌
        TokenEntity pToken = tokenDao.findByAddr(addr);
        if (pToken != null) {
        	
            if (!pToken.verifyRetry(tokenMethod)) {
                return pToken;
            }
            pToken.setType(setting.getTokenType());
            pToken.setMethod(tokenMethod);
            pToken.setAddr(addr);
            pToken.setCode(setting.getTokenCode());
            pToken.setExpiry(expiry);
            pToken.setRetry(setting.getTokenRetry());
            tokenDao.merge(pToken);
            return pToken;
        }

        // TODO 新发令牌
        TokenEntity token = new TokenEntity();
        token.setType(setting.getTokenType());
        token.setMethod(tokenMethod);
        token.setAddr(addr);
        token.setCode(setting.getTokenCode());
        token.setExpiry(setting.getTokenExpiry());
        token.setRetry(setting.getTokenRetry());
        tokenDao.persist(token);
        return token;
    }
    
    @Override
    @Transactional
    public boolean verify(TokenMethodEnum tokenMethod, String addr, String code, boolean isDelete) {
        TokenEntity pToken = tokenDao.findByAddr(addr);
        if (pToken != null && pToken.verify(tokenMethod, addr, code)) {
            if (isDelete) {
                tokenDao.remove(pToken);
            }
            return true;
        }
        return false;
    }
    
    /**
     * 生成效验码
     * @param userId
     * @param tokenEntity
     * @return
     */
    public String getValidateCode(Long userId,TokenEntity tokenEntity){
    	if(tokenEntity == null)
    		return null;
    	String vcodeData = MessageUtil.getInstance()
				.push("id", userId)
				.push("addr", tokenEntity.getAddr())
				.push("code", tokenEntity.getCode())
				.push("method", tokenEntity.getMethod())
				.push("token_id", tokenEntity.getId())
				.toString();
    	return DigestUtils.md5DigestAsHex(vcodeData.getBytes());
    	
    }
    
    /**
     * 生成token
     * @param userId
     * @param tokenEntity
     * @return
     */
    public String getTokenString(Long userId,TokenEntity tokenEntity){
    	if(tokenEntity == null)
    		return null;
    	String tokenString = MessageUtil.getInstance()
				.push("id", userId)
				.push("addr", tokenEntity.getAddr())
				.push("code", tokenEntity.getCode())
				.push("method", tokenEntity.getMethod().ordinal())
				.push("vcode", getValidateCode(userId,tokenEntity) )
				.toString();
    	return Base64.byteArrayToBase64(tokenString.getBytes());
    	
    }
    /**
     * 验证效验码
     * 
     */
    public boolean verifyToken(TokenMethodEnum tokenMethod, String addr, String code, String vcode, Long userId) {
    	
    	TokenEntity pToken = tokenDao.findByAddr(addr);
    	
        if (pToken != null && pToken.verify(tokenMethod, addr, code)) {
    		//验证效验码
    		if(vcode.equals(getValidateCode(userId, pToken)))
    			return true;
            return false;
        }

        return false;
    }

    @Override
    @Transactional
    public boolean verify(TokenMethodEnum tokenMethod, String addr, String code) {
        TokenEntity pToken = tokenDao.findByAddr(addr);
        return pToken != null && pToken.verify(tokenMethod, addr, code);
    }

    
    public MessageUtil getDataFromTokenString(String tokenString){
    	String tokenJSON = new String(Base64.base64ToByteArray(tokenString));
    	return MessageUtil.fromJson(tokenJSON);
    }
    
    
    public boolean validate(String tokenString , TokenMethodEnum tokenMethod){
    	
    	MessageUtil tokenData = getDataFromTokenString(tokenString);
    	
    	String username = tokenData.getItemValue("addr").toString();
    	String code = tokenData.getItemValue("code").toString();
    	String vcode = tokenData.getItemValue("vcode").toString() ;
    	Long  userId = Long.parseLong(tokenData.getItemValue("id").toString());
    	    	
    	if(username == null || code==null || vcode==null || userId == null )
    		return false;
    	    	
    	return verifyToken(
    			tokenMethod , username, code ,vcode ,userId
    			);
    	
    }
}