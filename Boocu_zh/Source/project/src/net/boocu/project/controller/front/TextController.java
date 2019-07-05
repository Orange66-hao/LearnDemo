/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.project.controller.front;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.boocu.project.util.ConfigUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 短信
 * 
 * @author fang 20160316
 * @version 1.0
 */
@Controller("frontTextController")
@RequestMapping("/text")
public class TextController {
	
    /**
     *发送注册短信	
     * @throws UnsupportedEncodingException 
     */
    //@RequestMapping(value = {"/sendRegist"}, method = RequestMethod.POST)
    @RequestMapping(value = {"/sendRegistTwo"}, method = RequestMethod.POST)
    public @ResponseBody
    void sendRegist(String username,String validate,HttpSession session) throws UnsupportedEncodingException {
    	if (validate != null && validate.equals("register")) {
    		String captchatext ="000000"+ ((int)(Math.random()*1000000))+"";
    		captchatext = captchatext.substring(captchatext.length()-6,captchatext.length());
    		try {
    			System.out.println("返回信息:"+ sendSMS(username,ConfigUtil.getConfig("text.content").replace("captcha", captchatext),""));
    			session.setAttribute(username, captchatext);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.err.println("发送短信异常,请联系短信接口人员!");
    		}
    		System.err.println("验证码:"+session.getAttribute(username));
		}
    }
    /**
     *发送登录短信
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = {"/sendLoginTwo"}, method = RequestMethod.POST)
    public @ResponseBody
    void sendLogin(String username,String validate,HttpSession session) throws UnsupportedEncodingException {
    	if (validate != null && validate.equals("login")) {
    		String captchatext ="000000"+ ((int)(Math.random()*1000000))+"";
    		captchatext = captchatext.substring(captchatext.length()-6,captchatext.length());
    		try {
    			System.out.println("返回信息:"+ sendSMS(username,ConfigUtil.getConfig("text.logincontent").replace("captcha", captchatext),""));
    			session.setAttribute(username, captchatext);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.err.println("发送短信异常,请联系短信接口人员!");
    		}
    		System.err.println("验证码:"+session.getAttribute(username));
    	}
    }
    /**
     *发送修改短信
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = {"/sendupdateTwo"}, method = RequestMethod.POST)
    public @ResponseBody
    void sendupdate(String username,String validate,HttpSession session) throws UnsupportedEncodingException {
    	if (validate != null && validate.equals("login")) {
    		String captchatext ="000000"+ ((int)(Math.random()*1000000))+"";
    		captchatext = captchatext.substring(captchatext.length()-6,captchatext.length());
    		try {
    			System.out.println("返回信息:"+ sendSMS(username,ConfigUtil.getConfig("text.updatecontent").replace("captcha", captchatext),""));
    			session.setAttribute(username, captchatext);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.err.println("发送短信异常,请联系短信接口人员!");
    		}
    		System.err.println("验证码:"+session.getAttribute(username));
    	}
    }
    
    public int sendSMS(String Mobile,String Content,String send_time) throws MalformedURLException, UnsupportedEncodingException {
		URL url = null;
		String CorpID=ConfigUtil.getConfig("text.account_id");//账户名
		
		String Pwd=ConfigUtil.getConfig("text.pwd");//密码
		String send_content=URLEncoder.encode(Content.replaceAll("<br/>", " "), "GBK");//发送内容
		url = new URL("http://mb345.com/WS/BatchSend.aspx?CorpID="+CorpID+"&Pwd="+Pwd+"&Mobile="+Mobile+"&Content="+send_content+"&Cell=&SendTime="+send_time);
		BufferedReader in = null;
		int inputLine = 0;
		try {
			System.out.println("开始发送短信手机号码为 ："+Mobile);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			inputLine = new Integer(in.readLine()).intValue();
		} catch (Exception e) {
			System.out.println("网络异常,发送短信失败！");
			inputLine=-2;
		}
		System.out.println("结束发送短信返回值：  "+inputLine);
		return inputLine;
	}
    public static void main(String[] args) throws Exception{
    	URL url = null;
		String CorpID=ConfigUtil.getConfig("text.account_id");//账户名
		
		String Pwd=ConfigUtil.getConfig("text.pwd");//密码
		String send_content=URLEncoder.encode(ConfigUtil.getConfig("text.subscribecontent"), "GBK");//发送内容
		
		//url = new URL("http://yzm.mb345.com/ws/BatchSend2.aspx?CorpID="+CorpID+"&Pwd="+Pwd+"&Mobile=13725555462&Content="+send_content+"&SendTime=&cell=");
		url = new URL("http://mb345.com/WS/BatchSend2.aspx?CorpID="+CorpID+"&Pwd="+Pwd+"&Mobile="+"13725555462"+"&Content="+send_content+"&Cell=&SendTime="+"");
		BufferedReader in = null;
		int inputLine = 0;
		try {
			System.out.println("开始发送短信手机号码为 ：13725555462");
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			inputLine = new Integer(in.readLine()).intValue();
		} catch (Exception e) {
			System.out.println("网络异常,发送短信失败！");
			inputLine=-2;
		}
		System.out.println("结束发送短信返回值：  "+inputLine);
    	
	}
    
    


}