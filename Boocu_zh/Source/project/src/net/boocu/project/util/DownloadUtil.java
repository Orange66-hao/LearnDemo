package net.boocu.project.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件下载工具类
 * 
 * @author xyf
 *
 */
public class DownloadUtil {
	/**
	 * 完成文件下载，支持断点续传
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 *            下载显示出来的文件名
	 * @param location
	 *            文件位置
	 */
	public static void downloadFile(HttpServletRequest request,
			HttpServletResponse response, String fileName, String location) {
		downloadFile(request, response, fileName, new File(location));
	}

	/**
	 * 完成文件下载，支持断点续传
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 *            下载显示出来的文件名
	 * @param file
	 *            文件
	 */
	public static void downloadFile(HttpServletRequest request,
			HttpServletResponse response, String fileName, File file) {

		try {
			if (file.exists()) {
				long fileLength = file.length();
				Date fileTime = new Date(file.lastModified());
				InputStream ins = new FileInputStream(file);

				downloadFile(request, response, fileName, ins, fileLength,
						fileTime);
			} else {
				System.out.println("Error: file " + file.getName()
						+ " not found.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 完成文件下载，支持断点续传
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 *            下载显示出来的文件名
	 * @param ins
	 *            文件流
	 * @param fileLength
	 *            文件大小
	 * @param fileTime
	 *            文件时间
	 */
	public static void downloadFile(HttpServletRequest request,
			HttpServletResponse response, String fileName, InputStream ins,
			long fileLength, Date fileTime) {
		BufferedInputStream bis = null;
		long p = 0L;
		long toLength = 0L;
		long contentLength = 0L;
		int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
		String rangBytes = "";

		try {
			// get file content
			bis = new BufferedInputStream(ins);

			// tell the client to allow accept-ranges
			response.reset();
			response.setHeader("Content-Encoding", "");
			response.setHeader("Accept-Ranges", "bytes");

			// client requests a file block download start byte
			String range = request.getHeader("Range");
			if (range != null && range.trim().length() > 0
					&& !"null".equals(range)) {
				response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
				rangBytes = range.replaceAll("bytes=", "");
				if (rangBytes.endsWith("-")) { // bytes=270000-
					rangeSwitch = 1;
					p = Long.parseLong(rangBytes.substring(0,
							rangBytes.indexOf("-")));
					contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
				} else { // bytes=270000-320000
					rangeSwitch = 2;
					String temp1 = rangBytes.substring(0,
							rangBytes.indexOf("-"));
					String temp2 = rangBytes.substring(
							rangBytes.indexOf("-") + 1, rangBytes.length());
					p = Long.parseLong(temp1);
					toLength = Long.parseLong(temp2);
					contentLength = toLength - p + 1; // 客户端请求的是
														// 270000-320000
														// 之间的字节
				}
			} else {
				contentLength = fileLength;
			}

			// 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
			// Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
			response.setHeader("Content-Length", String.valueOf(fileLength));
			response.setDateHeader("Last-Modified", fileTime.getTime());
			// 断点开始
			// 响应的格式是:
			// Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
			if (rangeSwitch == 1) {
				String contentRange = new StringBuffer("bytes ").append(p)
						.append("-").append(fileLength - 1).append("/")
						.append(fileLength).toString();
				response.setHeader("Content-Range", contentRange);
				bis.skip(p);
			} else if (rangeSwitch == 2) {
				String contentRange = range.replace("=", " ") + "/"
						+ String.valueOf(fileLength);
				response.setHeader("Content-Range", contentRange);
				bis.skip(p);
			} else {
				String contentRange = new StringBuffer("bytes ").append("0-")
						.append(fileLength - 1).append("/").append(fileLength)
						.toString();
				response.setHeader("Content-Range", contentRange);
			}

			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);

			OutputStream out = response.getOutputStream();
			int n = 0;
			long readLength = 0;
			int bsize = 1024;
			byte[] bytes = new byte[bsize];
			if (rangeSwitch == 2) {
				// 针对 bytes=27000-39000 的请求，从27000开始写数据
				while (readLength <= contentLength - bsize) {
					n = bis.read(bytes);
					readLength += n;
					out.write(bytes, 0, n);
				}
				if (readLength <= contentLength) {
					n = bis.read(bytes, 0, (int) (contentLength - readLength));
					out.write(bytes, 0, n);
				}
			} else {
				while ((n = bis.read(bytes)) != -1) {
					out.write(bytes, 0, n);
				}
			}
			out.flush();
			out.close();
			bis.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void downloadFileByUrl(HttpServletRequest request,
			HttpServletResponse response, String fileName, String fileUrl) {
		ServletContext servletContext = request.getServletContext();
		File f = new File(servletContext.getRealPath(fileUrl));
		if (f != null && f.exists()) {
			downloadFile(request, response, fileName, f);
		} else {
			try {
				InputStream ins = servletContext.getResourceAsStream(fileUrl);
				long fileLength = ins.available();
				Date fileTime = new Date();
				downloadFile(request, response, fileName, ins, fileLength,
						fileTime);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
