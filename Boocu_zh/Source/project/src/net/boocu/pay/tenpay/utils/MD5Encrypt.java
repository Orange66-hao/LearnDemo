package net.boocu.pay.tenpay.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encrypt
{
  public static String md5(String s)
  {
    byte[] input = s.getBytes();
    String output = null;
    
    char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
    try
    {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(input);
      


      byte[] tmp = md.digest();
      char[] str = new char[32];
      byte b = 0;
      for (int i = 0; i < 16; i++)
      {
        b = tmp[i];
        str[(2 * i)] = hexChar[(b >>> 4 & 0xF)];
        str[(2 * i + 1)] = hexChar[(b & 0xF)];
      }
      output = new String(str);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    return output;
  }
  
  private static String byteArrayToHexString(byte[] b)
  {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      resultSb.append(byteToHexString(b[i]));
    }
    return resultSb.toString();
  }
  
  private static String byteToHexString(byte b)
  {
    int n = b;
    if (n < 0) {
      n += 256;
    }
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigits[d1] + hexDigits[d2];
  }
  
  public static String MD5Encode(String origin, String charsetname)
  {
    String resultString = null;
    try
    {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      if ((charsetname == null) || ("".equals(charsetname))) {
        resultString = byteArrayToHexString(md.digest(resultString
          .getBytes()));
      } else {
        resultString = byteArrayToHexString(md.digest(resultString
          .getBytes(charsetname)));
      }
    }
    catch (Exception localException) {}
    return resultString;
  }
  
  private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", 
    "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
  
}
