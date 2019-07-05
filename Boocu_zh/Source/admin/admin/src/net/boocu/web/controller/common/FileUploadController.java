/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.controller.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.enums.FileTypeEnum;
import net.boocu.framework.util.ReqUtil;
import net.boocu.web.service.FileService;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.jhlabs.image.SmartBlurFilter;

/**
 * Controller - 图书管理
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Controller("fileUploadController")
@RequestMapping("/fileUpload")
public class FileUploadController {
	@Resource(name = "fileServiceImpl")
	private FileService fileService;
	
	private static String webUrl ="E:\\wwwroot\\img.wl95.com\\upload\\";

	/**
	 * 上传图片
	 */
	@RequestMapping(value = "/img.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void image(MultipartFile img, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		System.out.println("img" + img);
		resultMap.put("success", false);
		if (!fileService.verify(FileTypeEnum.image, img)) {
			resultMap.put("msg", "上传图片格式或大小不正确");
			System.err.println("上传图片格式或大小不正确");
		} else {
			/*
			 * String url = fileService.uploadLocal(FileTypeEnum.image, img); if
			 * (url == null) { resultMap.put("msg", "上传图片出现错误");
			 * System.err.println("上传图片出现错误"); } else { resultMap.put("success",
			 * true); int Runtime = 1; while(true){ File file = new File(url);
			 * if(file.exists()){ break; } Runtime ++; Thread.sleep(1000);
			 * if(Runtime ==15){ break; } } resultMap.put("url", url);
			 * resultMap.put("msg", "上传成功"); System.err.println("图片上传:"+url);
			 */
			String serverFileName = "";
			// 获取web应用根路径,也可以直接指定服务器任意盘符路径
			String savePath = webUrl+"image\\";
			// String savePath = "d:/upload/";
			// 检查路径是否存在,如果不存在则创建之
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdir();
			}
			// 文件按天归档
			savePath = savePath + getCurDate() + "/";
			File file1 = new File(savePath);
			if (!file1.exists()) {
				file1.mkdir();
			}

			String ctxPath = "http://img.wl95.com/upload/image/" + getCurDate() + "/";

			String xmid1 = "";
			String xmid2 = "";
			if (img != null) {
				String name = "";
				String extName = "";

				if (isNotEmpty(img)) {
					// 真实文件名
					String fileName1 = img.getOriginalFilename();
					String ext1 = FileUtils.getExName(fileName1);
					xmid1 = FileNameUtils.genFileName() + "." + ext1;

					xmid2 = FileNameUtils.genFileName() + "2." + ext1;
					// 我们一般会根据某种命名规则对其进行重命名
					// String fileName = ;
					File fileToCreate1 = new File(savePath, xmid1);

					// 检查同名文件是否存在,不存在则将文件流写入文件磁盘系统
					if ((fileName1 != null) && (!fileName1.equals("")) && !fileToCreate1.exists()) {
						FileOutputStream os = new FileOutputStream(fileToCreate1);
						os.write(img.getBytes());
						os.flush();
						os.close();
					}

					if ((fileName1 != null) && (!fileName1.equals(""))) {
						serverFileName = ctxPath + xmid1;
					} else {
					}
				} else {
				}

				FileUtil.createPath(savePath, getCurDate());
				File file2 = new File(savePath + xmid1);
				if (file2.length() > 200 * 1024) {

					ImageUtils.reduceImg(savePath + xmid1, savePath + xmid2, 1000, 0);

					// 删除原图
					File fileToCreate2 = new File(savePath, xmid1);
					FileUtil.removeFile(fileToCreate2);

					// 加水印
					WaterMarkUtil.waterText("", savePath + xmid2, savePath + xmid1, "Dialog", Font.PLAIN, Color.RED, 72,
							WaterMarkUtil.CENTER, 0.3f);

					// 生成水印后，删除原图
					File fileToCreate3 = new File(savePath, xmid2);
					FileUtil.removeFile(fileToCreate3);
					resultMap.put("success", true);
					resultMap.put("url", serverFileName);
					resultMap.put("msg", "上传成功");
				} else {

					resultMap.put("success", true);
					resultMap.put("url", serverFileName);
					resultMap.put("msg", "上传成功");
				}
			}
		}

		String resultJson = JSON.toJSONString(resultMap);
		response.setContentType("text/html");
		response.getWriter().write(resultJson);

	}

	/**
	 * 编辑器上传图片
	 */
	@RequestMapping(value = "/eidtimg.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void editImg(MultipartFile img, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		System.out.println("img" + img);
		resultMap.put("success", false);
		if (!fileService.verify(FileTypeEnum.image, img)) {
			resultMap.put("message", "上传图片格式或大小不正确");
			System.err.println("上传图片格式或大小不正确");
		} else {
			String serverFileName = "";
			// 获取web应用根路径,也可以直接指定服务器任意盘符路径
			String savePath =webUrl+"image\\";
			// String savePath = "d:/upload/";
			// 检查路径是否存在,如果不存在则创建之
			File file = new File(savePath);
			if (!file.exists()) {
				file.mkdir();
			}
			// 文件按天归档
			savePath = savePath + getCurDate() + "/";
			File file1 = new File(savePath);
			if (!file1.exists()) {
				file1.mkdir();
			}

			String ctxPath = "http://img.wl95.com/upload/image/" + getCurDate() + "/";

			String xmid1 = "";
			String xmid2 = "";
			if (img != null) {
				String name = "";
				String extName = "";

				if (isNotEmpty(img)) {
					// 真实文件名
					String fileName1 = img.getOriginalFilename();
					String ext1 = FileUtils.getExName(fileName1);
					xmid1 = FileNameUtils.genFileName() + "." + ext1;

					xmid2 = FileNameUtils.genFileName() + "2." + ext1;
					// 我们一般会根据某种命名规则对其进行重命名
					// String fileName = ;
					File fileToCreate1 = new File(savePath, xmid1);

					// 检查同名文件是否存在,不存在则将文件流写入文件磁盘系统
					if ((fileName1 != null) && (!fileName1.equals("")) && !fileToCreate1.exists()) {
						FileOutputStream os = new FileOutputStream(fileToCreate1);
						os.write(img.getBytes());
						os.flush();
						os.close();
					}

					if ((fileName1 != null) && (!fileName1.equals(""))) {
						serverFileName = ctxPath + xmid1;
					} else {
					}
				} else {
				}

				FileUtil.createPath(savePath, getCurDate());
				File file2 = new File(savePath + xmid1);
				if (file2.length() > 200 * 1024) {

					ImageUtils.reduceImg(savePath + xmid1, savePath + xmid2, 1000, 0);

					// 删除原图
					File fileToCreate2 = new File(savePath, xmid1);
					FileUtil.removeFile(fileToCreate2);

					// 加水印
					WaterMarkUtil.waterText("", savePath + xmid2, savePath + xmid1, "Dialog", Font.PLAIN, Color.RED, 72,
							WaterMarkUtil.CENTER, 0.3f);

					// 生成水印后，删除原图
					File fileToCreate3 = new File(savePath, xmid2);
					FileUtil.removeFile(fileToCreate3);
					resultMap.put("error", 0);
					resultMap.put("success", true);
					resultMap.put("url", serverFileName);
					resultMap.put("msg", "上传成功");
				} else {
					resultMap.put("error", 0);
					resultMap.put("success", true);
					resultMap.put("url", serverFileName);
					resultMap.put("msg", "上传成功");
				}
			}
		}
		String resultJson = JSON.toJSONString(resultMap);
		response.setContentType("text/html");
		response.getWriter().write(resultJson);

	}

	/**
	 * 上传文件
	 */
	@RequestMapping(value = "/file.jspx", method = { RequestMethod.GET, RequestMethod.POST })
	public void File(MultipartFile text, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);
		if (!fileService.verify(FileTypeEnum.file, text)) {
			resultMap.put("msg", "上传文件格式或大小不正确");
		} else {
			String url = fileService.uploadLocal(FileTypeEnum.file, text);
			// System.out.println(text.getOriginalFilename());
			if (url == null) {
				resultMap.put("msg", "上传文件出现错误");
			} else {
				resultMap.put("originalFilename", text.getOriginalFilename());
				resultMap.put("success", true);
				int Runtime = 1;
				while (true) {
					File file = new File(url);
					if (file.exists()) {
						break;
					}
					Runtime++;
					Thread.sleep(1000);
					if (Runtime == 10) {
						break;
					}
				}
				resultMap.put("url", url);
				resultMap.put("msg", "上传成功");
			}
		}
		String resultJson = JSON.toJSONString(resultMap);
		response.setContentType("text/html");
		response.getWriter().write(resultJson);
	}

	/**
	 * ck上传图片
	 */
	@RequestMapping(value = "/CKimg.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void uplImage(HttpServletRequest request, MultipartFile upload, HttpServletResponse response)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);
		if (!fileService.verify(FileTypeEnum.image, upload)) {
			resultMap.put("msg", "上传图片格式或大小不正确");
		} else {
			String url = fileService.uploadLocal(FileTypeEnum.image, upload);
			if (url == null) {
				resultMap.put("msg", "上传图片出现错误");
			} else {
				resultMap.put("success", true);
				resultMap.put("url", url);
				resultMap.put("msg", "上传成功");
			}
			PrintWriter out = response.getWriter();
			String callback = ReqUtil.getString(request, "CKEditorFuncNum", "");
			out.println("<script type=\"text/javascript\">");
			out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + url + "','')");
			out.println("</script>");

		}
		String resultJson = JSON.toJSONString(resultMap);
		response.setContentType("text/html");
		response.getWriter().write(resultJson);
	}

	/**
	 * 返回当前字符串型日期
	 * 
	 * @return String 返回的字符串型日期
	 */
	public String getCurDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = simpledateformat.format(calendar.getTime());
		return strDate;
	}

	/**
	 * 判断对象是否为NotEmpty(!null或元素>0)<br>
	 * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
	 * 
	 * @param pObj
	 *            待检查对象
	 * @return boolean 返回的布尔值
	 */
	public boolean isNotEmpty(Object pObj) {
		if (pObj == null)
			return false;
		if (pObj == "")
			return false;
		if (pObj instanceof String) {
			if (((String) pObj).length() == 0) {
				return false;
			}
		} else if (pObj instanceof Collection) {
			if (((Collection) pObj).size() == 0) {
				return false;
			}
		} else if (pObj instanceof Map) {
			if (((Map) pObj).size() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 上传图片
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/imgUpload.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void imgUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String serverFileName = "";
		// 获取web应用根路径,也可以直接指定服务器任意盘符路径
		String savePath = webUrl+"image\\";;
		// 检查路径是否存在,如果不存在则创建之
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdir();
		}
		// 文件按天归档
		savePath = savePath + getCurDate() + "/";
		File file1 = new File(savePath);
		if (!file1.exists()) {
			file1.mkdir();
		}

		String ctxPath = "http://img.wl95.com/upload/image/" + getCurDate() + "/";

		String xmid1 = "";
		String xmid2 = "";

		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");
		List fileList = null;

		MultipartFile myFile1 = ((DefaultMultipartHttpServletRequest) request).getFileMap().get("Filedata");
		if (myFile1 != null) {
			String name = "";
			String extName = "";

			if (isNotEmpty(myFile1)) {
				// 真实文件名
				String fileName1 = myFile1.getOriginalFilename();
				String ext1 = FileUtils.getExName(fileName1);
				xmid1 = FileNameUtils.genFileName() + "." + ext1;

				xmid2 = FileNameUtils.genFileName() + "2." + ext1;
				// 我们一般会根据某种命名规则对其进行重命名
				// String fileName = ;
				File fileToCreate1 = new File(savePath, xmid1);

				// 检查同名文件是否存在,不存在则将文件流写入文件磁盘系统
				if ((fileName1 != null) && (!fileName1.equals("")) && !fileToCreate1.exists()) {
					FileOutputStream os = new FileOutputStream(fileToCreate1);
					os.write(myFile1.getBytes());
					os.flush();
					os.close();
				}

				if ((fileName1 != null) && (!fileName1.equals(""))) {
					serverFileName = ctxPath + xmid1;
				} else {
				}
			} else {
			}

			FileUtil.createPath(savePath, getCurDate());
			File file2 = new File(savePath + xmid1);
			ImageUtils.reduceImg(savePath + xmid1, savePath + xmid2, 1000, 0);

			// 删除原图
			File fileToCreate2 = new File(savePath, xmid1);
			FileUtil.removeFile(fileToCreate2);

			// 加水印
			WaterMarkUtil.waterText("http://wl95.com/", savePath + xmid2, savePath + xmid1, "Dialog", Font.PLAIN,
					Color.RED, 72, WaterMarkUtil.CENTER, 0.3f);

			// 生成水印后，删除原图
			File fileToCreate3 = new File(savePath, xmid2);
			FileUtil.removeFile(fileToCreate3);

			response.getWriter().print(serverFileName);
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadifyFile.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void uploadifyFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String serverFileName = "";
		// 获取web应用根路径,也可以直接指定服务器任意盘符路径
		String savePath = webUrl+"file\\";

		String realFileName = "";

		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdir();
		}
		// 文件按天归档
		savePath = savePath + getCurDate() + "/";
		File file1 = new File(savePath);
		if (!file1.exists()) {
			file1.mkdir();
		}
		String ctxPath = "http://img.wl95.com/upload/file/" + getCurDate() + "/";

		String xmid1 = "";
		String xmid2 = "";

		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		upload.setHeaderEncoding("utf-8");

		MultipartFile myFile1 = ((DefaultMultipartHttpServletRequest) request).getFileMap().get("Filedata");
		if (myFile1 != null) {
			if (isNotEmpty(myFile1)) {
				// 真实文件名
				String fileName1 = myFile1.getOriginalFilename();
				String ext1 = FileUtils.getExName(fileName1);
				xmid1 = FileNameUtils.genFileName() + "." + ext1;

				xmid2 = FileNameUtils.genFileName() + "2." + ext1;
				// 我们一般会根据某种命名规则对其进行重命名
				// String fileName = ;
				File fileToCreate1 = new File(savePath, xmid1);

				// 检查同名文件是否存在,不存在则将文件流写入文件磁盘系统
				if ((fileName1 != null) && (!fileName1.equals("")) && !fileToCreate1.exists()) {
					FileOutputStream os = new FileOutputStream(fileToCreate1);
					os.write(myFile1.getBytes());
					os.flush();
					os.close();
				}

				if ((fileName1 != null) && (!fileName1.equals(""))) {
					serverFileName = ctxPath + xmid1;
					realFileName = fileName1;
				} else {
				}
			} else {
			}

			FileUtil.createPath(savePath, getCurDate());

			response.getWriter().print(serverFileName + "*" + realFileName);
		}
	}
}