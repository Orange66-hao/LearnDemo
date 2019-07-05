/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;


import net.boocu.web.service.MailService;
/**
 * Service - 日志
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface EMailService extends MailService {

	void sendFindPassword(String toMail, String username ,String tokenUrl) throws Exception;

	void sendRegist(String toMail, String username, String tokenUrl) throws Exception;

}