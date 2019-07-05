/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.boocu.framework.util.WebUtils;
import net.boocu.project.util.MessageUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.constant.Constants;
import net.boocu.web.dao.MemberDao;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.enums.TokenMethodEnum;
import net.boocu.web.service.MemberInfoService;
import net.boocu.web.service.MemberLogService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.TokenService;
import net.boocu.web.shiro.ShiroPrincipal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Service - 会员
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service("memberServiceImpl")
public class MemberServiceImpl extends BaseServiceImpl<MemberEntity, Long> implements MemberService {

    @Resource(name = "memberDaoImpl")
    private MemberDao memberDao;

    @Resource(name = "memberInfoServiceImpl")
    private MemberInfoService memberInfoService;
 
    @Resource(name = "memberLogServiceImpl")
    private MemberLogService memberLogService;

    @Resource
    private HttpServletRequest request;
    
    @Resource
    private TokenService tokenService;
    
    @Resource(name = "memberDaoImpl")
    public void setBaseDao(MemberDao memberDao) {
        super.setBaseDao(memberDao);
    }
    
	@Override
	public Page<MemberEntity> findMemberAuditPage(Pageable pageable,
			HashMap<String, Object> htMap) {
		return memberDao.findMemberAuditPage(pageable, htMap);
	}

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return memberDao.usernameExists(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameUnique(String previousUsername, String currentUsername) {
        if (StringUtils.equalsIgnoreCase(previousUsername, currentUsername)) {
            return true;
        } else {
            if (memberDao.usernameExists(currentUsername)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean idNoExists(String idNo) {
        return memberDao.idNoExists(idNo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean idNoUnique(String previousIdNo, String currentIdNo) {
        if (StringUtils.equalsIgnoreCase(previousIdNo, currentIdNo)) {
            return true;
        } else {
            if (memberDao.idNoExists(currentIdNo)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return memberDao.emailExists(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailUnique(String previousEmail, String currentEmail) {
        if (StringUtils.equalsIgnoreCase(previousEmail, currentEmail)) {
            return true;
        } else {
            if (memberDao.emailExists(currentEmail)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean mobileExists(String mobile) {
        return memberDao.mobileExists(mobile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean mobileUnique(String previousMobile, String currentMobile) {
        if (StringUtils.equals(previousMobile, currentMobile)) {
            return true;
        } else {
            if (memberDao.mobileExists(currentMobile)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByUsername(String username) {
        return memberDao.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MemberEntity showopenid(String openid) {
    	return memberDao.showopenid(openid);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MemberEntity showwxopenid(String wxopenid) {
    	return memberDao.showwxopenid(wxopenid);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByEmail(String email) {
        return memberDao.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity findByMobile(String mobile) {
        return memberDao.findByMobile(mobile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberEntity> findHasIdentityList() {
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(Filter.isNotNull("idNo"));
        return findList(filters);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authorized() {
        return getCurrentPrincipal() != null;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberEntity getCurrent() {
    	Long currentId = getCurrentId();
        if (currentId != null) {
            return memberDao.find(currentId);
        }
        return null;
    }

    private ShiroPrincipal getShiroPrincipalFromCookie(HttpSession session){
    	
    	String tokenString = WebUtils.getCookie(request, MemberEntity.AUTOLOGIN_COOKIE_NAME);
    	if(tokenString == null)
    		return null;
    	
    	MessageUtil tokenData = tokenService.getDataFromTokenString(tokenString);
    	if(tokenData.getItems().size() == 0)
    		return null;
    	String username = (String) tokenData.getItemValue("addr");
    	String code = (String)tokenData.getItemValue("code");
    	String id = tokenData.getItemValue("id").toString() ;    	
    	String vcode = (String)tokenData.getItemValue("vcode") ;
    	
    	if(username == null || code==null || vcode==null || id== null)
    		return null;
    	
    	Long userId = Long.parseLong(id) ;
    	
    	boolean check= tokenService.verifyToken(
    				TokenMethodEnum.auto_login , username, code ,vcode ,userId
    			);
    	if(check){    		    		
	            ShiroPrincipal shiroPrincipal = new ShiroPrincipal(userId, username);
	            session.setAttribute(
	            		Constants.PRINCIPAL_ATTR_NAME, shiroPrincipal
	            		);
	           
	            return shiroPrincipal;
    	}
    	return null;
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public ShiroPrincipal getCurrentPrincipal() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            ShiroPrincipal shiroPrincipal =  (ShiroPrincipal) request.getSession().getAttribute(Constants.PRINCIPAL_ATTR_NAME);
            //尝试从cookie 获取
            if( shiroPrincipal == null){
            	shiroPrincipal = getShiroPrincipalFromCookie(request.getSession());
            }
            return shiroPrincipal;
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentId() {
    	ShiroPrincipal currentPrincipal = getCurrentPrincipal();
        if (currentPrincipal != null) {
            return currentPrincipal.getId();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String getCurrentUsername() {
    	ShiroPrincipal currentPrincipal = getCurrentPrincipal();
        if (currentPrincipal != null) {
            return currentPrincipal.getUsername();
        }
        return null;
    }

	/* (non-Javadoc)
	 * @see net.boocu.web.service.MemberService#findById(java.lang.Long)
	 */
	@Override
	public MemberEntity findById(Long id) {
		return memberDao.findById(id);
	}

}