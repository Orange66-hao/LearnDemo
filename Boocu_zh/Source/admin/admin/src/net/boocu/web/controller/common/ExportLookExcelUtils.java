package net.boocu.web.controller.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportLookExcelUtils {
	
	public static final String exportSubExport(List<Map<String, Object>> lookList, String path) {
		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet("订阅最多分类");

		sheet.setDefaultColumnWidth(9);

		CellStyle style = wb.createCellStyle();

		style.setWrapText(true);

		setCommonStyle(wb, style);

		Row rowHeaderOne = sheet.createRow(0);
		rowHeaderOne.setHeightInPoints(14.25F);

		Row rowOne = sheet.createRow(0);

		rowOne.setHeightInPoints(14.25F);
		for (int i = 0; i < 5; i++) {
			rowOne.createCell(i);
			if (i == 0) {
				sheet.setColumnWidth(i, 9600);
			} else {
				sheet.setColumnWidth(i, 4800);
			}
		}
		rowOne.setHeightInPoints(14.25F);
		rowOne.getCell(0).setCellValue("分类名称");
		rowOne.getCell(1).setCellValue("类型");
		rowOne.getCell(2).setCellValue("订阅用户数");
		rowOne.getCell(3).setCellValue("邮箱接收用户数");
		rowOne.getCell(4).setCellValue("系统接收用户数");

		for (int i = 0; i < 5; i++) {
			rowOne.getCell(i).setCellStyle(style);
		}
		int len = lookList == null ? 0 : lookList.size();
		int nowRow = 1;
		for (int i = nowRow; i < nowRow + len; i++) {
			Map<String, Object> look = (Map) lookList.get(i - 1);
			Row tempRow = sheet.createRow(i);
			tempRow.setHeightInPoints(35.0F);
			for (int k = 0; k < 5; k++) {
				tempRow.createCell(k).setCellStyle(style);
			}
			for (int j = 0; j < 5; j++) {
				CellRangeAddress region = new CellRangeAddress(i - 1, i - 1, j, j);
				sheet.addMergedRegion(region);
				Row row = sheet.getRow(i);
				row.getCell(j).setCellStyle(style);
				String value = "";
				if (j == 0) {
					value = look.get("name").toString();
				} else if (j == 1) {
					value = look.get("type").toString();
				} else if (j == 2) {
					value = look.get("userCount").toString();
				} else if (j == 3) {
					value = look.get("emailRecv").toString();
				} else if (j == 4) {
					value = look.get("systemRecv").toString();
				}
				row.getCell(j).setCellValue(value);
			}
		}
		String excelName = "exportExcel/Sub_" + System.currentTimeMillis() + ".xls";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		writeFile(path + excelName, wb);
		return path + excelName;
	}
	
	public static final String exportTypeExport(List<Map<String, Object>> lookList, String path) {
		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet("最热门分类");

		sheet.setDefaultColumnWidth(9);

		CellStyle style = wb.createCellStyle();

		style.setWrapText(true);

		setCommonStyle(wb, style);

		Row rowHeaderOne = sheet.createRow(0);
		rowHeaderOne.setHeightInPoints(14.25F);

		Row rowOne = sheet.createRow(0);

		rowOne.setHeightInPoints(14.25F);
		for (int i = 0; i < 5; i++) {
			rowOne.createCell(i);
			if (i == 0) {
				sheet.setColumnWidth(i, 9600);
			} else {
				sheet.setColumnWidth(i, 4800);
			}
		}
		rowOne.setHeightInPoints(14.25F);
		rowOne.getCell(0).setCellValue("品牌和型号");
		rowOne.getCell(1).setCellValue("类型");
		rowOne.getCell(2).setCellValue("分类名称");
		rowOne.getCell(3).setCellValue("发布人");
		rowOne.getCell(4).setCellValue("最新点击时间");

		for (int i = 0; i < 5; i++) {
			rowOne.getCell(i).setCellStyle(style);
		}
		int len = lookList == null ? 0 : lookList.size();
		int nowRow = 1;
		for (int i = nowRow; i < nowRow + len; i++) {
			Map<String, Object> look = (Map) lookList.get(i - 1);
			Row tempRow = sheet.createRow(i);
			tempRow.setHeightInPoints(35.0F);
			for (int k = 0; k < 5; k++) {
				tempRow.createCell(k).setCellStyle(style);
			}
			for (int j = 0; j < 5; j++) {
				CellRangeAddress region = new CellRangeAddress(i - 1, i - 1, j, j);
				sheet.addMergedRegion(region);
				Row row = sheet.getRow(i);
				row.getCell(j).setCellStyle(style);
				String value = "";
				if (j == 0) {
					value = look.get("proInfo").toString();
				} else if (j == 1) {
					value = look.get("type").toString();
				} else if (j == 2) {
					value = look.get("typeName").toString();
				} else if (j == 3) {
					value = look.get("userName").toString();
				} else if (j == 4) {
					value = look.get("lookDate").toString();
				}
				row.getCell(j).setCellValue(value);
			}
		}
		String excelName = "exportExcel/Type_" + System.currentTimeMillis() + ".xls";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		writeFile(path + excelName, wb);
		return path + excelName;
	}
	
	
	public static final String exportAddMostExport(List<Map<String, Object>> lookList, String path) {
		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet("发布最多的商品");

		sheet.setDefaultColumnWidth(9);

		CellStyle style = wb.createCellStyle();

		style.setWrapText(true);

		setCommonStyle(wb, style);

		Row rowHeaderOne = sheet.createRow(0);
		rowHeaderOne.setHeightInPoints(14.25F);

		Row rowOne = sheet.createRow(0);

		rowOne.setHeightInPoints(14.25F);
		for (int i = 0; i < 4; i++) {
			rowOne.createCell(i);
			if (i == 0) {
				sheet.setColumnWidth(i, 9600);
			} else {
				sheet.setColumnWidth(i, 4800);
			}
		}
		rowOne.setHeightInPoints(14.25F);
		rowOne.getCell(0).setCellValue("品牌和型号");
		rowOne.getCell(1).setCellValue("类型");
		rowOne.getCell(2).setCellValue("商品总数");
		rowOne.getCell(3).setCellValue("发布用户数");

		for (int i = 0; i < 4; i++) {
			rowOne.getCell(i).setCellStyle(style);
		}
		int len = lookList == null ? 0 : lookList.size();
		int nowRow = 1;
		for (int i = nowRow; i < nowRow + len; i++) {
			Map<String, Object> look = (Map) lookList.get(i - 1);
			Row tempRow = sheet.createRow(i);
			tempRow.setHeightInPoints(35.0F);
			for (int k = 0; k < 4; k++) {
				tempRow.createCell(k).setCellStyle(style);
			}
			for (int j = 0; j < 4; j++) {
				CellRangeAddress region = new CellRangeAddress(i - 1, i - 1, j, j);
				sheet.addMergedRegion(region);
				Row row = sheet.getRow(i);
				row.getCell(j).setCellStyle(style);
				String value = "";
				if (j == 0) {
					value = look.get("proInfo").toString();
				} else if (j == 1) {
					value = look.get("type").toString();
				} else if (j == 2) {
					value = look.get("proCount").toString();
				} else if (j == 3) {
					value = look.get("userCount").toString();
				}
				row.getCell(j).setCellValue(value);
			}
		}
		String excelName = "exportExcel/addMost_" + System.currentTimeMillis() + ".xls";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		writeFile(path + excelName, wb);
		return path + excelName;
	}

	public static final String exportLookExport(List<Map<String, Object>> lookList, String path) {
		Workbook wb = new HSSFWorkbook();

		Sheet sheet = wb.createSheet("点击量最多型号");

		sheet.setDefaultColumnWidth(9);

		CellStyle style = wb.createCellStyle();

		style.setWrapText(true);

		setCommonStyle(wb, style);

		Row rowHeaderOne = sheet.createRow(0);
		rowHeaderOne.setHeightInPoints(14.25F);

		Row rowOne = sheet.createRow(0);

		rowOne.setHeightInPoints(14.25F);
		for (int i = 0; i < 5; i++) {
			rowOne.createCell(i);
			if (i == 0) {
				sheet.setColumnWidth(i, 9600);
			} else {
				sheet.setColumnWidth(i, 4800);
			}
		}
		rowOne.setHeightInPoints(14.25F);
		rowOne.getCell(0).setCellValue("品牌和型号");
		rowOne.getCell(1).setCellValue("类型");
		rowOne.getCell(2).setCellValue("点击数量");
		rowOne.getCell(3).setCellValue("最大点击量");
		rowOne.getCell(4).setCellValue("商品总数");

		for (int i = 0; i < 5; i++) {
			rowOne.getCell(i).setCellStyle(style);
		}
		int len = lookList == null ? 0 : lookList.size();
		int nowRow = 1;
		for (int i = nowRow; i < nowRow + len; i++) {
			Map<String, Object> look = (Map) lookList.get(i - 1);
			Row tempRow = sheet.createRow(i);
			tempRow.setHeightInPoints(35.0F);
			for (int k = 0; k < 5; k++) {
				tempRow.createCell(k).setCellStyle(style);
			}
			for (int j = 0; j < 5; j++) {
				CellRangeAddress region = new CellRangeAddress(i - 1, i - 1, j, j);
				sheet.addMergedRegion(region);
				Row row = sheet.getRow(i);
				row.getCell(j).setCellStyle(style);
				String value = "";
				if (j == 0) {
					value = look.get("proInfo").toString();
				} else if (j == 1) {
					value = look.get("type").toString();
				} else if (j == 2) {
					value = look.get("lookVal").toString();
				} else if (j == 3) {
					value = look.get("maxLook").toString();
				} else if (j == 4) {
					value = look.get("proCount").toString();
				}
				row.getCell(j).setCellValue(value);
			}
		}
		String excelName = "exportExcel/look_" + System.currentTimeMillis() + ".xls";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		writeFile(path + excelName, wb);
		return path + excelName;
	}

	private static void setCommonStyle(Workbook wb, CellStyle cellStyle) {
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderBottom((short) 1);
		cellStyle.setBorderLeft((short) 1);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderRight((short) 1);
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop((short) 1);
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

		cellStyle.setAlignment((short) 2);
		cellStyle.setVerticalAlignment((short) 1);
		cellStyle.setWrapText(true);

		Font font = wb.createFont();
		font.setFontName("宋体");
		cellStyle.setFont(font);
	}

	public static void writeFile(String filePath, Workbook wb) {
		FileOutputStream fos = null;
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			wb.write(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
