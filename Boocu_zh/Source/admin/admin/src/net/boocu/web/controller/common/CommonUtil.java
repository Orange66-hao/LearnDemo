package net.boocu.web.controller.common;

/**
 * 判读字符是否是数字
 *
 * @author  
 */
public final class CommonUtil {
	public static boolean isNumeric(String str){
		  for (int i = 0; i < str.length(); i++){
		   if (!Character.isDigit(str.charAt(i))){
			   return false;
		   	  }
		  }
		  return true;
	 }
}