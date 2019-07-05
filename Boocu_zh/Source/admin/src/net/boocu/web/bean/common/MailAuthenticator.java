/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.common;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;










/**  
 * 用户名和密码的验证  
 * @author hualong_wu  
 *  
 */  
public class MailAuthenticator extends Authenticator{   
    private  String username="xxxxx";      
    private  String password="xxxxx";      
       
    public MailAuthenticator() {   
        super();   
    }   
       
    /**  
     * 设置验证的用户名和密码  
     */  
    public MailAuthenticator(String userName , String password) {   
        super();   
        this.username = userName;   
        this.password = password;   
    }   
    protected PasswordAuthentication getPasswordAuthentication()   
    {      
        return new PasswordAuthentication(this.username,this.password);      
    }      
}  
