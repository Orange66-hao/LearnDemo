/**
 * 
 */
package net.boocu.web.controller.admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.project.entity.FlowEntity;
import net.boocu.project.service.*;
import net.boocu.web.Page;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.LookValEntity;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.controller.common.ExportLookExcelUtils;
import net.boocu.web.service.MemberService;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/stat")
public class StatController {
	
	@Autowired
	private LookValService lookValService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductSaleService productSaleService;
	
	@Autowired
	private ProductBuyService productBuyService;
	
	@Autowired
	private ProductRentService productRentService;
	
	@Autowired
	private ProductWantRentService productWantRentService;
	
	@Autowired
	private ProductRepairService productRepairService;
	
	@Autowired
	private ProducWantRepairService productWantRepairService;
	
	@Autowired
	private AutoTestService autoTestService;
	
	@Autowired
	private ProjectNeedService projectNeedService;
	
	@Autowired
	private ProductTestService productTestService;
	
	@Autowired
	private RequireTestService requireTestService;
	
	@Autowired
	private  CalibrationService calibrationService;
	@Autowired
	private EmailRecordService emailRecordService;
	@Autowired
	private FlowService flowService;
	@Autowired
	private ProductSubscribeService productSubscribeService;
	private static final String TEM_PATH = "/template/admin/stat";
	
	@RequestMapping("/toLookType.jspx")
	public String toLookType(HttpServletRequest request,
			HttpServletResponse response){
		
		return TEM_PATH+"/look_type";
	}
	
	@RequestMapping("/toLookTime.jspx")
	public String toLookTime(HttpServletRequest request,
			HttpServletResponse response){
		
		return TEM_PATH+"/look_time";
	}
	
	@RequestMapping("/toMostAdd.jspx")
	public String toAddMostPro(HttpServletRequest request,
			HttpServletResponse response){
		
		return TEM_PATH + "/most_add";
	}
	
	/*
	 * 订阅报表
	 */
	//产品类别
	@RequestMapping("/toProClass.jspx")
	public String toProClass(HttpServletRequest request,
		HttpServletResponse response){
		
		return TEM_PATH + "/subscript_class";
	}
	
	//行业类别
	@RequestMapping("/toIndustry.jspx")
	public String toIndustry(HttpServletRequest request,
		HttpServletResponse response){
		
		return TEM_PATH + "/subscript_industry";
	}
	
	//产品
	@RequestMapping("/toPro.jspx")
	public String toPro(HttpServletRequest request,
		HttpServletResponse response){
		
		return TEM_PATH + "/subscript_pro";
	}
	
	@RequestMapping("/exportSubExcel.jspx")
	public void exportSubExcel(HttpServletRequest request,
		HttpServletResponse response){
		try {
			int type = ReqUtil.getInt(request, "type", 0);
			int group = ReqUtil.getInt(request, "group", 0);
			String typeStr = null;
			switch (group) {
			case 1:
				typeStr = "销售";
				break;
			case 2:
				typeStr = "求购";
				break;
			case 3:
				typeStr = "出租";
				break;
			case 4:
				typeStr = "求租";
				break;
			case 5:
				typeStr = "维修";
				break;
			case 6:
				typeStr = "求修";
				break;
			case 7:
				typeStr = "自动化测试方案";
				break;
			case 8:
				typeStr = "自动化测试需求";
				break;
			case 11:
				typeStr = "产品测试";
				break;
			case 12:
				typeStr = "测试需求";
				break;
			case 13:
				typeStr = "计量校准";
				break;
			default:
				break;
			}
			
			List<LookValEntity> list = lookValService.lvSubscript(type, group);
			
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			
			List<LookValEntity> newlist = new ArrayList<LookValEntity>();
			if(list.size() > 0){
				for(LookValEntity lv : list){
					//剔除分类
					String [] pGroup = lv.getProNo().split("、");
					for(int i = 0;i < pGroup.length;i++){
						if(type == Integer.valueOf(pGroup[i])){
							//剔除不相等的，like匹配到的
							newlist.add(lv);
							break;
						}
					}
				}
				
				//此处list是含有正常数据的集合
				for(LookValEntity lve : newlist){
					Map<String,Object> temp = new HashMap<String,Object>();
					temp = lookValService.findSubScript(lve.getProId(), type);
					if(temp != null){
						temp.put("name", lve.getBrandName());
						temp.put("type", typeStr);
						resultList.add(temp);
					}
				}
			}
			
			String path = request.getSession().getServletContext().getRealPath("/");
			String rePaht = ExportLookExcelUtils.exportSubExport(resultList, path);
			//下载文件
			download(rePaht,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/lvSubScript.json")
	public void lvSubscript(HttpServletRequest request,
		HttpServletResponse response){
		int type = ReqUtil.getInt(request, "type", 0);
		int group = ReqUtil.getInt(request, "group", 0);
		
		StringBuilder proNo = new StringBuilder();
		StringBuilder proVal = new StringBuilder();
		
		List<LookValEntity> list = lookValService.lvSubscript(type, group);
		
		if(list.size() > 0){
			for(LookValEntity lv : list){
				//剔除分类
				String [] pGroup = lv.getProNo().split("、");
				for(int i = 0;i < pGroup.length;i++){
					if(group == Integer.valueOf(pGroup[i])){
						//存在和type相等的，而非是like匹配到的
						proVal.append(lv.getLookVal()).append(",");
						proNo.append(lv.getBrandName()).append(",");
						break;
					}
				}
			}
			//去除结尾的“，”
			if(!"".equals(proNo.toString()) && proNo != null && !"".equals(proVal.toString()) && proVal != null){
				proNo.deleteCharAt(proNo.lastIndexOf(","));
				proVal.deleteCharAt(proVal.lastIndexOf(","));
			}
		}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("subName", proNo);
		data.put("subVal", proVal);
		RespUtil.renderJson(response, data);
	}
	
	@RequestMapping("/loadSubscript.json")
	public void loadSubscript(HttpServletRequest request,
		HttpServletResponse response){
		int type = ReqUtil.getInt(request, "type", 0);
		int group = ReqUtil.getInt(request, "group", 0);
		
		List<LookValEntity> list = lookValService.lvSubscript(type, group);
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		List<LookValEntity> newlist = new ArrayList<LookValEntity>();
		if(list.size() > 0){
			for(LookValEntity lv : list){
				//剔除分类
				String [] pGroup = lv.getProNo().split("、");
				for(int i = 0;i < pGroup.length;i++){
					if(type == Integer.valueOf(pGroup[i])){
						//剔除不相等的，like匹配到的
						newlist.add(lv);
					}
				}
			}
			
			//此处list是含有正常数据的集合
			for(LookValEntity lve : newlist){
				Map<String,Object> temp = new HashMap<String,Object>();
				temp = lookValService.findSubScript(lve.getProId(), type);
				if(temp != null){
					temp.put("name", lve.getBrandName());
					resultList.add(temp);
				}
			}
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}
	
	@RequestMapping("/lvMostData.json")
	public void lvMostAdd(HttpServletRequest request,
			HttpServletResponse response){
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		
		StringBuilder proNo = new StringBuilder();
		StringBuilder proVal = new StringBuilder();
		
		List<LookValEntity> list = lookValService.lvMostAdd(params);
		if(list.size() > 0){
			for(LookValEntity lv : list){
				proNo.append(lv.getBrandName()).append(lv.getProNo()).append(",");
				proVal.append(lv.getLookVal()).append(",");
			}
			//去除结尾的“，”
			if(!"".equals(proNo.toString()) && proNo != null && !"".equals(proVal.toString()) && proVal != null){
				proNo.deleteCharAt(proNo.lastIndexOf(","));
				proVal.deleteCharAt(proVal.lastIndexOf(","));
			}
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("mostName", proNo);
		data.put("mostVal", proVal);
		RespUtil.renderJson(response, data);
	}
	
	@RequestMapping("/loadMostAddData.json")
	public void loadMostAddData(HttpServletRequest request,
			HttpServletResponse response){
		
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		List<LookValEntity> list = lookValService.lvMostAdd(params);
		
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(LookValEntity lve : list){
			int userCount = lookValService.findMostUserCount(lve.getProNo(), lve.getBrandId(), type);
			int proCount = lookValService.findMostProCount(lve.getProNo(), lve.getBrandId(), type);
			
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("proInfo", lve.getBrandName() + " " + lve.getProNo());
			map.put("proCount", proCount);
			map.put("userCount", userCount);
			resultList.add(map);
		}
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}
	
	@RequestMapping("/lvTypeData.json")
	public void lvTypeList(HttpServletRequest request,
			HttpServletResponse response){
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		
		StringBuilder proNo = new StringBuilder();
		StringBuilder proVal = new StringBuilder();
		
		List<LookValEntity> list = lookValService.lvTypeList(params);
		if(list.size() > 0){
			for(LookValEntity lv : list){
				proNo.append(lv.getProNo()).append(",");
				proVal.append(lv.getLookVal()).append(",");
			}
			//去除结尾的“，”
			if(!"".equals(proNo.toString()) && proNo != null && !"".equals(proVal.toString()) && proVal != null){
				proNo.deleteCharAt(proNo.lastIndexOf(","));
				proVal.deleteCharAt(proVal.lastIndexOf(","));
			}
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("typeName", proNo);
		data.put("typeVal", proVal);
		RespUtil.renderJson(response, data);
	}
	
	@RequestMapping("/loadTypeData.json")
	public void loadTypeData(HttpServletRequest request,
			HttpServletResponse response){
		
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		List<LookValEntity> list = lookValService.lvTypeList(params);
		
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(LookValEntity lve : list){
			LookValEntity le = lookValService.findTypeInfo(lve.getProId(), type);
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("typeName", lve.getProNo());
			map.put("userName", le.getUserName());
			map.put("proInfo", le.getBrandName()+" "+le.getProNo());
			map.put("lookDate", le.getCreateTime());
			resultList.add(map);
		}
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}
	
	private String int2TypeStr(int type){
		String typeStr = null;
		switch (type) {
		case 1:
			typeStr = "销售";
			break;
		case 2:
			typeStr = "求购";
			break;

		case 3:
			typeStr = "出租";
			break;

		case 4:
			typeStr = "求租";
			break;

		case 5:
			typeStr = "维修";
			break;

		case 6:
			typeStr = "求修";
			break;

		default:
			break;
		}
		return typeStr;
	}
	
	@RequestMapping("/exportTypeExcel.jspx")
	public void exportTypeExcel(HttpServletRequest request,
			HttpServletResponse response){
		try {
			String time = ReqUtil.getString(request, "time", "");
			int type = ReqUtil.getInt(request, "type", 0);
			
			String typeStr = int2TypeStr(type);

			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("type", type);
			//处理时间
			if(!"".equals(time)){
				String startTime = time.substring(0, 10);
				String endTime = time.substring(14, 24);
				params.put("startTime", startTime + " 00:00:00");
				params.put("endTime", endTime + " 23:59:59");
			}else{
				params.put("startTime", "");
				params.put("endTime", "");
			}
			List<LookValEntity> list = lookValService.lvTypeList(params);
			
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			for(LookValEntity lve : list){
				LookValEntity le = lookValService.findTypeInfo(lve.getProId(), type);
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("typeName", lve.getProNo());
				map.put("userName", le.getUserName());
				map.put("proInfo", le.getBrandName()+" "+le.getProNo());
				map.put("lookDate", le.getCreateTime());
				map.put("type", typeStr);
				resultList.add(map);
			}
			String path = request.getSession().getServletContext().getRealPath("/");
			String rePaht = ExportLookExcelUtils.exportTypeExport(resultList,path);
			
			//下载文件
			download(rePaht,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/loadLookData.json")
	public void loadLookData(HttpServletRequest request,
			HttpServletResponse response){
		
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		List<LookValEntity> list = lookValService.lvList(params);
		
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(LookValEntity lve : list){
			Map<String, Object> pMap = lookValService.findLookInfo(lve.getProNo(), lve.getBrandId(), type);
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("proInfo", lve.getBrandName() + " " + lve.getProNo());
			map.put("lookVal", lve.getLookVal());
			map.put("proCount", pMap.get("proCount"));
			map.put("maxLook", pMap.get("maxLook"));
			resultList.add(map);
		}
		result.put("rows", resultList);
		RespUtil.renderJson(response, result);
	}
	
	@RequestMapping("/exportAddMostExcel.jspx")
	public void exportAddMostExcel(HttpServletRequest request,
			HttpServletResponse response){
		try {
			String time = ReqUtil.getString(request, "time", "");
			int type = ReqUtil.getInt(request, "type", 0);
			
			String typeStr = int2TypeStr(type);
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("type", type);
			//处理时间
			if(!"".equals(time)){
				String startTime = time.substring(0, 10);
				String endTime = time.substring(14, 24);
				params.put("startTime", startTime + " 00:00:00");
				params.put("endTime", endTime + " 23:59:59");
			}else{
				params.put("startTime", "");
				params.put("endTime", "");
			}
			List<LookValEntity> list = lookValService.lvMostAdd(params);
			
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			for(LookValEntity lve : list){
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("proInfo", lve.getBrandName() + " " + lve.getProNo());
				map.put("userCount", lookValService.findMostUserCount(lve.getProNo(), lve.getBrandId(), type));
				map.put("proCount", lookValService.findMostProCount(lve.getProNo(), lve.getBrandId(), type));
				map.put("type", typeStr);
				resultList.add(map);
			}
			String path = request.getSession().getServletContext().getRealPath("/");
			String rePaht = ExportLookExcelUtils.exportAddMostExport(resultList,path);
			
			//下载文件
			download(rePaht,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/exportExcel.jspx")
	public void exportExcel(HttpServletRequest request,
		HttpServletResponse response){
		try {
			String time = ReqUtil.getString(request, "time", "");
			int type = ReqUtil.getInt(request, "type", 0);
			
			String typeStr = int2TypeStr(type);
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("type", type);
			//处理时间
			if(!"".equals(time)){
				String startTime = time.substring(0, 10);
				String endTime = time.substring(14, 24);
				params.put("startTime", startTime + " 00:00:00");
				params.put("endTime", endTime + " 23:59:59");
			}else{
				params.put("startTime", "");
				params.put("endTime", "");
			}
			List<LookValEntity> list = lookValService.lvList(params);
			
			List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
			for(LookValEntity lve : list){
				Map<String, Object> pMap = lookValService.findLookInfo(lve.getProNo(), lve.getBrandId(), type);
				Map<String, Object> map = new HashMap<String,Object>();
				map.put("proInfo", lve.getBrandName() + " " + lve.getProNo());
				map.put("lookVal", lve.getLookVal());
				map.put("proCount", pMap.get("proCount"));
				map.put("maxLook", pMap.get("maxLook"));
				map.put("type", typeStr);
				resultList.add(map);
			}
			String path = request.getSession().getServletContext().getRealPath("/");
			String rePaht = ExportLookExcelUtils.exportLookExport(resultList,path);
			
			//下载文件
			download(rePaht,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/lvLookData.json")
	public void lvLookData(HttpServletRequest request,
			HttpServletResponse response){
		String time = ReqUtil.getString(request, "time", "");
		int type = ReqUtil.getInt(request, "type", 0);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		
		StringBuilder proNo = new StringBuilder();
		StringBuilder proVal = new StringBuilder();
		
		List<LookValEntity> list = lookValService.lvList(params);
		if(list.size() > 0){
			for(LookValEntity lv : list){
				proNo.append(lv.getBrandName()).append(lv.getProNo()).append(",");
				proVal.append(lv.getLookVal()).append(",");
			}
			//去除结尾的“，”
			if(!"".equals(proNo.toString()) && proNo != null && !"".equals(proVal.toString()) && proVal != null){
				proNo.deleteCharAt(proNo.lastIndexOf(","));
				proVal.deleteCharAt(proVal.lastIndexOf(","));
			}
		}
		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("proNo", proNo);
		data.put("proVal", proVal);
		RespUtil.renderJson(response, data);
	}
	
	private void download(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	@RequestMapping("/sumMember.json")
	public void sumMember(HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		String time = ReqUtil.getString(request, "time", "");
		Map<String,Object> params = new HashMap<String,Object>();
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		Pageable pageable=new Pageable();
		pageable.setPageNumber(1);
		pageable.setPageSize(10);
		String stime=params.get("startTime").toString();
		String etime=params.get("endTime").toString();
		List<Filter> filters=new ArrayList<>();
		filters.add(Filter.ge("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):stime)));
		filters.add(Filter.le("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(etime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):etime)));
		//List<MemberEntity> findList = memberService.findList(Filter.ge("createDate", params.get("startTime")),Filter.le("createDate", params.get("endTime")));
      	long newMember = memberService.count(filters);
      	//推送点击数量
		long clickCount = flowService.getTotal(params);
		long count = memberService.count();
		long totalSubscribe=productSubscribeService.findSubScribeAllMember(stime.equals("")?null:stime,etime.equals("")?null:etime,pageable).getTotal();
		List<Filter> filters2=new ArrayList<>();
		filters2.add(Filter.ge("loginDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):stime)));
		filters2.add(Filter.le("loginDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(etime.equals("")?new DateFormatUtils().format(new Date(), "yyyy-MM-dd HH:mm:ss"):etime)));
		long totalLoginMember=memberService.count(filters2);
		//查询新增订阅用户数量
		long subCount=productSubscribeService.getCount(stime,etime);
		long closeSubscribe=productSubscribeService.getCloseSubscribe(stime,etime,pageable).getTotal();
		long mobileSubscribe=productSubscribeService.getMobileSubscribe(stime,etime);
		long closeMobileSubscribe=productSubscribeService.getCloseMobileSubscribe(stime,etime,pageable).getTotal();
		//查询信息
		Map<String, Object> query =  productSubscribeService.query(stime,etime);
		long sendEmailCount=emailRecordService.getCount(stime,etime);
		
	/*	
		long companyCount = memberService.count(Filter.eq("memberShip", 1));*/
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalMember", count);
		data.put("clickCount", clickCount);
		data.put("sendEmailCount", sendEmailCount);
		data.put("totalSubscribe", totalSubscribe);
		data.put("totalNewMember", newMember);
		data.put("totalLoginMember", totalLoginMember);
		data.put("totalNewSubscribe", subCount);
		data.put("totalCloseSubscribe", closeSubscribe);
		data.put("totalMobileSubscribe", mobileSubscribe);
		data.put("totalCloseMobileSubscribe", closeMobileSubscribe);
		/*data.put("totalQiYe", companyCount);*/
		data.put("userData", query);
		RespUtil.renderJson(response, data);
	}
	@RequestMapping("/toPage.jspx")
	public String page() {
		return TEM_PATH+"/our";
	}
	@RequestMapping("/sumProduct.json")
	public void sumProduct(HttpServletRequest request,
			HttpServletResponse response) {
		String time = ReqUtil.getString(request, "time", "");
		Map<String,Object> params = new HashMap<String,Object>();
		//处理时间
		if(!"".equals(time)){
			String startTime = time.substring(0, 10);
			String endTime = time.substring(14, 24);
			params.put("startTime", startTime + " 00:00:00");
			params.put("endTime", endTime + " 23:59:59");
		}else{
			params.put("startTime", "");
			params.put("endTime", "");
		}
		String stime=params.get("startTime").toString();
		String etime=params.get("endTime").toString();
		long totalProductCount = productService.count();
		long totalProductSaleCount = productSaleService.count();
		long productBuyCount = productBuyService.count();
		long productRentCount = productRentService.count();
		long productWantRentCount = productWantRentService.count();
		long productRepairCount = productRepairService.count();
		long productWantRepairCount = productWantRepairService.count();
		long productAutoTestCount = autoTestService.count();
		long projectNeedCount = projectNeedService.count();
		long productTestCount = productTestService.count();
		long productRequireTestCount = requireTestService.count();
		long calibrationCount = calibrationService.count();
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("totalProductCount", totalProductCount);
		data.put("totalMember", totalProductCount);
		RespUtil.renderJson(response, data);
	}

    @RequestMapping(value = { "/getClickDeatilList.json", }, method = { RequestMethod.GET ,RequestMethod.POST})
    public void getList(HttpServletRequest request,HttpServletResponse response, Model model) {
        int pageNumber=ReqUtil.getInt(request, "page", 1);
        int pageSize=ReqUtil.getInt(request, "rows", 5);
        String time=ReqUtil.getString(request, "time", "");
        String param = ReqUtil.getString(request, "params", "");

        Map<String,Object> params = new HashMap<String,Object>();
        if(!"".equals(time)){
            String startTime = time.substring(0, 10);
            String endTime = time.substring(14, 24);
            params.put("startTime", startTime + " 00:00:00");
            params.put("endTime", endTime + " 23:59:59");
        }else{
            params.put("startTime", "");
            params.put("endTime", "");
        }


        Map<String, Object> result = flowService.findPage(params, pageNumber, pageSize);
        RespUtil.renderJson(response, result);
    }
}
