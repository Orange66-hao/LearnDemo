/**
 * 
 */
package net.boocu.pay.tenpay.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.tencent.common.MD5;
import com.tencent.common.Util;
import com.tencent.common.XMLParser;

import net.boocu.pay.alipay.config.AlipayConfig;
import net.boocu.pay.tenpay.config.TenConfig;
import net.boocu.pay.tenpay.utils.http.HttpClientConnectionManager;

/**
 * 微信支付工具类
 * @author Administrator
 *
 */
public class TenUtils {
	
	private static final Logger logger = Logger.getLogger(TenUtils.class);
	
    private final static int CONNECT_TIMEOUT = 5000; // in milliseconds
    private final static String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * 把map转换成XML
	 * @param map
	 * @return
	 */
	public String mapParseToXml(Map<String,Object> map){
		
		return null;
	}
	
	  public static DefaultHttpClient httpclient;

	  static
	  {
	    httpclient = new DefaultHttpClient();
	    httpclient = (DefaultHttpClient)HttpClientConnectionManager.getSSLInstance(httpclient);
	  }
	  
	/**
     * 获取本机Ip 
     *  
     *  通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。
     *  获得符合 <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String localIp(){
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();            
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
        	logger.info(e.getMessage(),e);
        }
        return ip;
    }
    
    /**
     * 微信签名算法
     * @param map
     * @return
     */
    public static String getSign(Map<String,Object> map){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + TenConfig.getKey();
        logger.info("Sign Before MD5:" + result);
  	    
        result = MD5Encrypt.MD5Encode(result, "utf-8").toUpperCase();
        return result;
    }
    
    /**
     * 微信签名算法
     * @param map
     * @return
     */
    public static String getSignWithOutUnicode(Map<String,Object> map){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + TenConfig.getKey();
        logger.info("Sign Before MD5:" + result);
  	    
        result = MD5Encrypt.md5(result);
        return result;
    }
    
    public static String postData(String urlStr, String data, String contentType) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            if(contentType!=null && !"".equals(contentType))
                conn.setRequestProperty("content-type", contentType);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), DEFAULT_ENCODING);
            if(data == null)
                data = "";
            writer.write(data); 
            writer.flush();
            writer.close();  
 
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
        	logger.error("Error connecting to " + urlStr + ": " + e.getMessage());
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
    
	/**
	 * 模拟浏览器post提交
	 * 
	 * @param url
	 * @return
	 */
	public static HttpPost getPostMethod(String url) {
		HttpPost pmethod = new HttpPost(url); // 设置响应头信息
		pmethod.addHeader("Connection", "keep-alive");
		pmethod.addHeader("Accept", "*/*");
		pmethod.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		pmethod.addHeader("Host", "api.mch.weixin.qq.com");
		pmethod.addHeader("X-Requested-With", "XMLHttpRequest");
		pmethod.addHeader("Cache-Control", "max-age=0");
		pmethod.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
		return pmethod;
	}
    
	public static Map getPayNo(String url, String xmlParam) {
		Util.log("请求xml是：" + xmlParam);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpPost httpost = getPostMethod(url);
		Map map = null;
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");
			Util.log("下单返回结果是："+resultStr);

			map = XMLParser.getMapFromXML(resultStr);
		} catch (Exception e) {
			Util.log(e.getMessage());
		}
		return map;
	}
    
}
