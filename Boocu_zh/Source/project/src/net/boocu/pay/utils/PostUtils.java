/**
 * 
 */
package net.boocu.pay.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;
/**
 * @author Administrator
 *
 */

public class PostUtils
{
  private static Logger logger = Logger.getLogger(PostUtils.class);
  
  public static String sendRequest(String urlStr, String requestObj)
  {
    try
    {
      StringBuffer buffer = new StringBuffer();
      URL url = new URL(urlStr);
      URLConnection rulConnection = url.openConnection();
      HttpURLConnection httpUrlConnection = (HttpURLConnection)rulConnection;

      httpUrlConnection.setDoOutput(true);
      
      httpUrlConnection.setDoInput(true);
      
      httpUrlConnection.setUseCaches(false);
      
      httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
      
      httpUrlConnection.setRequestMethod("POST");
      
      httpUrlConnection.connect();
      PrintWriter out = new PrintWriter(new OutputStreamWriter(httpUrlConnection.getOutputStream(), "utf-8"));
      out.flush();
      BufferedReader reader = null;
      reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));
      String line = null;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
      logger.error(buffer.toString());
      return buffer.toString();
    }
    catch (Exception e)
    {
      logger.error(e.getMessage(), e);
    }
    return "";
  }
  
  public static String getResultPost(String urlString, String code)
  {
    StringBuffer buffer = new StringBuffer();
    try
    {
      URL url = new URL(urlString);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("GET");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      
      con.setRequestProperty("Connection", "Keep-Alive");
      con.setRequestProperty("Charset", code);
      con.setRequestProperty("Referer", "http://www.kuaidi100.com/index_old.shtml");
      BufferedReader reader = null;
      
      reader = new BufferedReader(new InputStreamReader(con.getInputStream(), code));
      String line = null;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
    }
    catch (Exception e)
    {
      logger.error(e.getMessage(), e);
    }
    return buffer.toString();
  }
  
  public static String getResultPost(String urlString, String code, String referer)
  {
    StringBuffer buffer = new StringBuffer();
    try
    {
      URL url = new URL(urlString);
      HttpURLConnection con = (HttpURLConnection)url.openConnection();
      con.setRequestMethod("POST");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      
      con.setRequestProperty("Connection", "Keep-Alive");
      con.setRequestProperty("Charset", code);
      con.setRequestProperty("Referer", referer);
      BufferedReader reader = null;
      
      reader = new BufferedReader(new InputStreamReader(con.getInputStream(), code));
      String line = null;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
    }
    catch (Exception e)
    {
      logger.error(e.getMessage(), e);
    }
    return buffer.toString();
  }
  
  public static void main(String[] args)
  {
	  String sb = "";
	  for(int i=0;i<1000;i+=10){
		  sb += getResultPost("http://game.qq.com/cf/clan/clanrank/clanrank_0_"+i+"_1.js", "GBK")+"\n";
	  }
	  System.out.println(sb);
  }
}
