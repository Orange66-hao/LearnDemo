/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.shiro;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.boocu.framework.enums.CaptchaTypeEnum;
import net.boocu.framework.util.SettingUtils;
import net.boocu.web.Filter;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.enums.AccountLockTypeEnum;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.CaptchaService;
import net.boocu.web.setting.security.SecuritySetting;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.web.bind.annotation.ControllerAdvice;


/**
 * 授权认证
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class AuthenticationRealm extends AuthorizingRealm {

	private static Logger log = Logger.getLogger(AuthenticationRealm.class);
	
    @Resource(name = "captchaServiceImpl")
    private CaptchaService captchaService;

    @Resource(name = "adminServiceImpl")
    private AdminService adminService;

    /**
     * 获取认证信息
     * 
     * @param token 令牌
     * @return 认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) {

        // 获取登录令牌
        AuthenticationToken authenticationToken = (AuthenticationToken) token;

        // 获取登录信息
        String username = authenticationToken.getUsername();
        String password = new String(authenticationToken.getPassword());
        String captchaId = authenticationToken.getCaptchaId();
        String captcha = authenticationToken.getCaptcha();
        String ip = authenticationToken.getHost();
    
        // 启用管理员登录验证码时，验证验证码
        if (!captchaService.verify(CaptchaTypeEnum.adminLogin, captchaId, captcha)) {
            log.info(" 【异常：无效令牌】......");
            // 异常：无效令牌
            throw new UnsupportedTokenException();
        }
 
        // 用户名、密码验证
        if (username != null && password != null) {
        	 
            // 根据用户名获取管理员（验证用户名）
            AdminEntity admin = adminService.find(Filter.eq("username", username, true));
            // 管理员不存在时
            if (admin == null) {
                // 异常：账户不存在
                throw new UnknownAccountException();
            }
            // 管理员未启用时
            if (!admin.getEnabled()) {
                // 异常：账户未启用
                throw new DisabledAccountException();
            }

            // 读取安全设置
            SecuritySetting setting = SettingUtils.get().getSecurity();

            // 管理员被锁定时
            if (admin.getLocked()) {
                // 账户锁定范围：管理员
                if (ArrayUtils.contains(setting.getAccountLockScopes(), AccountLockTypeEnum.admin)) {

                    // 获取自动解锁时间
                    int loginFailureLockTime = setting.getAccountLockTime();
                    // 自动解锁时间为0时永久锁定
                    if (loginFailureLockTime == 0) {
                        // 异常：账户被锁定
                        throw new LockedAccountException();
                    }

                    // 获取管理员锁定日期
                    Date lockedDate = admin.getLockedDate();
                    // 获取管理员解锁日期
                    Date unlockDate = DateUtils.addMinutes(lockedDate, loginFailureLockTime);

                    // 当前日期超出管理员解锁日期时
                    if (new Date().after(unlockDate)) {
                        // 解锁管理员
                        admin.setLoginFailureCount(0);
                        admin.setLocked(false);
                        admin.setLockedDate(null);
                        adminService.update(admin);
                    }
                    // 当前日期未超出管理员解锁日期时
                    else {
                        // 异常：账户被锁定
                        throw new LockedAccountException();
                    }

                }
                // 账户锁定类型：非管理员
                else {
                    // 解锁管理员
                    admin.setLoginFailureCount(0);
                    admin.setLocked(false);
                    admin.setLockedDate(null);
                    adminService.update(admin);
                }
            }

            // 密码错误时
            if (!StringUtils.equals(DigestUtils.md5Hex(password), admin.getPassword())) {

                // 计算登录失败次数
                int loginFailureCount = admin.getLoginFailureCount() + 1;
                // 登录失败次数 >= 账户锁定计数，时
                if (loginFailureCount >= setting.getAccountLockCount()) {
                    // 锁定管理员
                    admin.setLocked(true);
                    admin.setLockedDate(new Date());
                }
                admin.setLoginFailureCount(loginFailureCount);
                adminService.update(admin);

                log.warn("异常：认证错误");
                // 异常：认证错误
                throw new IncorrectCredentialsException();
            }

            // 管理员登录
            admin.setLoginIp(ip);
            admin.setLoginDate(new Date());
            admin.setLoginFailureCount(0);
            adminService.update(admin);

//            // 返回认证信息
//            return new SimpleAuthenticationInfo(new Principal(admin.getId(), username), password, getName());
//            
        	log.info("用户【" + username + "】登录成功");
//    		byte[] salt = EncodeUtils.hexDecode(admin.getSalt());
    		ShiroPrincipal subject = new ShiroPrincipal(admin.getId(),admin.getUsername());
    		List<String> authorities = adminService.getAuthoritiesName(admin.getId());
    		List<String> rolelist = adminService.getRolesName(admin.getId());
    		subject.setAuthorities(authorities);
    		subject.setRoles(rolelist);
    		subject.setAuthorized(true);
    		log.info("用户【" + username + "】授权初始化成功......");
			log.info("用户【" + username + "】 角色列表为：" + subject.getRoles());
			log.info("用户【" + username + "】 权限列表为：" + subject.getAuthorities());
    		return new SimpleAuthenticationInfo(subject, password, getName());
    		
        }

        // 异常：账户不存在
        throw new UnknownAccountException();
    }

    /**
     * 获取授权信息
     * 
     * @param principals
     *            principals
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		//获取当前登录的用户名
		ShiroPrincipal subject = (ShiroPrincipal)super.getAvailablePrincipal(principals);
		String username = subject.getUsername();
		Long userId = subject.getId();
		log.info("用户【" + username + "】授权开始......");

		try {
			if(!subject.isAuthorized()) {
            //根据用户名称，获取该用户所有的权限列表
            List<String> authorities = adminService.getAuthoritiesName(userId);
            List<String> rolelist = adminService.getRolesName(userId);
            subject.setAuthorities(authorities);
            subject.setRoles(rolelist);
            subject.setAuthorized(true);
            log.info("用户【" + username + "】授权初始化成功......");
            log.info("用户【" + username + "】 角色列表为：" + subject.getRoles());
            log.info("用户【" + username + "】 权限列表为：" + subject.getAuthorities());
        } else {
            log.info("用户【" + username + "】已授权......");
        }
    } catch(RuntimeException e) {
        throw new AuthorizationException("用户【" + username + "】授权失败");
    }
		//给当前用户设置权限
		info.addStringPermissions(subject.getAuthorities());
		info.addRoles(subject.getRoles());
		
		return info; 
    }

}