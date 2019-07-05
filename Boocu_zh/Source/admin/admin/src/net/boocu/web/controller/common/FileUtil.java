/** 
 * Description:文件上传类
 * Date:2011-11-20
 * Author:Along	
*/
package net.boocu.web.controller.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil
{
	
	/**
	 * 判断目标目录是否存在，如果不存在，就创建该目录
	 * @param filePath
	 */
  public static void checkPath(String filePath){
	  File folder = new File(filePath);
	  try{
	  if (!folder.exists()) {
		  folder.mkdir();
		  }}catch(Exception e){
			e.printStackTrace();
          }	  
          }
  /**
   * 判断目标目录是否存在，如果不存在，就创建该目录
   * @param basePath 根目录，一定存在的目录
   * @param newFolder 子目录，可以是多个层次的，以逗号分开
   */
  public static void createPath(String basePath,String newFolder){
	  String []floders=newFolder.split(",");
	  String tempFloder=basePath;
	  if(basePath.endsWith(File.separator))
	  {
		  tempFloder=tempFloder.substring(0, tempFloder.length()-2);
	  }
	  for(int i=0;i<floders.length;i++)
	  {
		  tempFloder=tempFloder+File.separator+floders[i];
		  checkPath(tempFloder);
	  }	  
     }
  
  /**
   * 删除目录下的所有文件，除了exceptFile之外的
   * @param filePath
   * @param exceptFile
   */
  public static void deleteAllFile(String filePath,String []exceptFile)
  {
	  File goodsPath = new File(filePath);
	  if (goodsPath.isDirectory()) 
	  {
		 File[] files=goodsPath.listFiles();
		 for(int i=0;i<files.length;i++)
		 {
			 boolean delete=true;
			 for(int j=0;j<exceptFile.length;j++)
			 {
			 if(files[i].getName().startsWith(exceptFile[j]))
			 {
				 delete=false;
				 break;
			 }
			 }
			 if(delete)
			 {
				 files[i].delete(); 
				 
			 }
			 
		 }
		  
	  }	  	  
  }
  /**
   * 删除目录下的所有文件，除了exceptFile之外的
   * @param filePath
   * @param exceptFile
   */
  public static void deleteAllFile(String filePath,List exceptFile)
  {
	  File goodsPath = new File(filePath);
	  if (goodsPath.isDirectory()) 
	  {
		 File[] files=goodsPath.listFiles();
		 for(int i=0;i<files.length;i++)
		 {
			 boolean delete=true;
			 for(int j=0;j<exceptFile.size();j++)
			 {
			 if(files[i].getName().startsWith(exceptFile.get(j).toString()))
			 {
				 delete=false;
				 break;
			 }
			 }
			 if(delete)
			 {
				 files[i].delete(); 
				 
			 }
			 
		 }
		  
	  }	  	  
  }
  /**
   * 删除目录下的后缀名符合的所有文件
   * @param filePath
   * @param exceptFile
   */
  public static void deleteExtFile(String filePath,String suffix)
  {
	  File goodsPath = new File(filePath);
	  if (goodsPath.isDirectory()) 
	  {
		 File[] files=goodsPath.listFiles();
		 for(int i=0;i<files.length;i++)
		 {
			
			 if(files[i].getName().endsWith(suffix))
			 {
				
				 files[i].delete(); 
				 
			 }
			 
		 }
		  
	  }	  	  
  }
  
  /**
   * 删除指定目录下文件
   * @param filePath
   * @param exceptFile
   */
  public static void deleteFile(String fileurl)
  {
	  File goodsURL = new File(fileurl);
	  goodsURL.delete();	  
  }
  
  /**
   * 删除目录下的所有文件，除了html中包含的之外的
   * @param filePath
   * @param exceptFile
   */
  public static void deleteAllFile(String filePath,String html)
  {
	    List fileNames=new ArrayList();
		String regex="src=\"([^\"]+)\""; 
		Pattern pa=Pattern.compile(regex);
		Matcher ma=pa.matcher(html);
		while(ma.find()){  
			String temp=ma.group();
			String fileName=temp.substring(temp.lastIndexOf("/")+1,temp.length()-1);
			fileNames.add(fileName);
			}
		deleteAllFile(filePath,fileNames);
  }
  
  /**
   * 复制文件
   * @param srcFile 源文件File
   * @param destDir 目标目录File
   * @param newFileName 新文件名
   * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
   */
  public static long copyFile(File srcFile,File destDir,String newFileName){
   long copySizes = 0;
   if(!srcFile.exists()){
    System.out.println("源文件不存在");
    copySizes = -1;
   }
   else if(!destDir.exists()){
    System.out.println("目标目录不存在");
    copySizes = -1;
   }
   else if(newFileName == null){
    System.out.println("文件名为null");
    copySizes = -1;
   }
   else{
    try {
     BufferedInputStream bin = new BufferedInputStream(
       new FileInputStream(srcFile));
     BufferedOutputStream bout = new BufferedOutputStream(
       new FileOutputStream(new File(destDir,newFileName)));
     int b = 0 ,i = 0;
     long t1 = System.currentTimeMillis();
     while((b = bin.read()) != -1){
      bout.write(b);
      i++;
     }
     long t2 = System.currentTimeMillis();
     bout.flush();
     bin.close();
     bout.close();
     copySizes = i;
     long t = t2-t1;
     //System.out.println("复制了" + i + "个字节\n" + "时间" + t);
    } catch (FileNotFoundException e) {    
     e.printStackTrace();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
   return copySizes;
}
  /** 读取一个文件到字符串里.     
   * @param sFileName  文件名    
   * @param sEncode   String     
   * @return 文件内容     */    
  public static String readTextFile(String sFileName, String sEncode)    {    
	  StringBuffer sbStr = new StringBuffer();        
	  try{            
		  File ff = new File(sFileName);            
		  InputStreamReader read = new InputStreamReader(new FileInputStream(ff),sEncode);
		  BufferedReader ins = new BufferedReader(read);            
		  String dataLine = "";           
		  while (null != (dataLine = ins.readLine()))            
		  {                
			  sbStr.append(dataLine);                         
		   }  
		  ins.close();        
		  }        
	    catch (Exception e)        
		{}        
	    return sbStr.toString();    
  }
  
  public static String[] listFile(String folderName)
  {
	  File goodsPath = new File(folderName);
	  String[] fileNames=null;
	  if (goodsPath.isDirectory()) 
	  {
		 File[] files=goodsPath.listFiles();
		 fileNames=new String[files.length];
		 for(int i=0;i<files.length;i++)
		 {
			 fileNames[i]=files[i].getName();
			 
		 }
	  }
	  
	return fileNames;  
  }
  
  /**
   * 尝试删除文件(非目录)尝试3次
   * @param file
   */
  public static void removeFile(File file) {
          int maxTry = 3;
          while (maxTry > 0) {
                  maxTry--;
                  if (file.isFile()) {
                          if (file.delete())
                                  return;
                          else
                                  continue;
                  } else {
                          return;
                  }
          }
  }


  }