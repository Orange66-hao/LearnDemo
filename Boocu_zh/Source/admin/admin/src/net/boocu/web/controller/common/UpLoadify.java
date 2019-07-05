package net.boocu.web.controller.common;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UpLoadify extends HttpServlet{
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
//		doPost(request,response);
	}
	
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		if(request.getParameter("folder")== null || request.getParameter("folder")== ""){
			return;
		}
//		
//		response.setContentType("text/html");
//		response.setCharacterEncoding("UTF-8");
//		String path = this.getServletContext().getRealPath("/");
//		String fileD = request.getParameter("folder");
//		String sourcePath = path +"upload/source/";
//		path = path + "upload/" + fileD + "/";
//		File folder = new File(path);
//		File sourceFolder = new File(sourcePath);
//		
//		if(!folder.exists()){
//			folder.mkdirs();
//		}
//		
//		if(!sourcePath.exists()){
//			sourcePath.mkdirs();
//		}
//		
//		ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
//		sfu.setHeaderEncoding("UTF-8");
//		try{
//			List<?> fileList = sfu.parseRequest(request);
//			String sourceName = "";
//			String extName = "";
//			String name = "";
//			String sfileName = "";
//			for(int i= 0;){
//				
//			}
//		}
	}
}
