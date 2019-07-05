package net.boocu.web.controller.admin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.CompanymessageEntity;
import net.boocu.project.service.CompanymessageService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 公司基本信息维护
 * @author deng
 *
 * 2015年8月13日
 */
@Controller("companymessageController")
@RequestMapping("/admin/basedata/companymessage")
public class CompanymessageController {
	
	private static final String TEM_PATH ="/template/admin/basedata/companymessage";
	
	@Resource
	private CompanymessageService companymessageService;
	
	@RequestMapping(value="toCompanymessageList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/companymessagelist";
	}
	
	@RequestMapping(value="data.json",method={RequestMethod.POST,RequestMethod.GET})
	public void dataJson(HttpServletRequest request,HttpServletResponse response,Model model){
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		String sortValue = ReqUtil.getString(request, "sort", "");
		String sortOrder = ReqUtil.getString(request, "order", "desc");
		
		Pageable pageable = new Pageable(pagenumber,rows);
		if(!sortValue.isEmpty()){
			List<Sequencer> ilSequencers = new ArrayList<Sequencer>();
			Sequencer sequencer = "desc".equals(sortOrder) ? Sequencer.desc(sortValue) :Sequencer.asc(sortValue) ;
			ilSequencers.add(sequencer);
			pageable.setSequencers(ilSequencers);
		}
		
		if(!keyword.isEmpty()){
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.like("address", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<CompanymessageEntity> page = companymessageService.findPage(pageable);
		List<CompanymessageEntity> companymessageEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(CompanymessageEntity item : companymessageEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("qqnumber", item.getQqnumber());
			map.put("hotline", item.getHotline());
			map.put("mobile", item.getMobile());
			map.put("fax", item.getFax());
			map.put("email", item.getEmail());
			map.put("logo", item.getLogo());
			map.put("argument", item.getArgument());
			map.put("address", item.getAddress());
			map.put("compost", item.getCompost());
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_companymessage.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Companymessage(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/companymessage_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_companymessage.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String qqnumber = ReqUtil.getString(request, "qqnumber", "");
    	String hotline = ReqUtil.getString(request, "hotline", "");
    	String mobile = ReqUtil.getString(request, "mobile", "");
    	String fax = ReqUtil.getString(request, "fax", "");
    	String email = ReqUtil.getString(request, "email", "");
    	String logo = ReqUtil.getString(request, "logo", "");
    	String argument = ReqUtil.getString(request, "argument", "");
    	String address = ReqUtil.getString(request, "address", "");
    	String compost = ReqUtil.getString(request, "compost", "");
    	
    	CompanymessageEntity companymessageEntity = new CompanymessageEntity();
    	
    	companymessageEntity.setQqnumber(qqnumber);
    	companymessageEntity.setHotline(hotline);
    	companymessageEntity.setMobile(mobile);
    	companymessageEntity.setFax(fax);
    	companymessageEntity.setEmail(email);
    	companymessageEntity.setLogo(logo);
    	companymessageEntity.setArgument(argument);
    	companymessageEntity.setAddress(address);
    	companymessageEntity.setCompost(compost);
    	companymessageService.save(companymessageEntity);
    	
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中ProductSaleEntity（广告本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_companymessage.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.companymessageService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_companymessage.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	CompanymessageEntity companymessageEntity = new CompanymessageEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		companymessageEntity = this.companymessageService.find(lid);
    		model.addAttribute("item", companymessageEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/companymessage_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_companymessage.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditCompanymessage(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String qqnumber = ReqUtil.getString(request, "qqnumber", "");
    	String hotline = ReqUtil.getString(request, "hotline", "");
    	String mobile = ReqUtil.getString(request, "mobile", "");
    	String fax = ReqUtil.getString(request, "fax", "");
    	String email = ReqUtil.getString(request, "email", "");
    	String logo = ReqUtil.getString(request, "logo", "");
    	String argument = ReqUtil.getString(request, "argument", "");
    	String address = ReqUtil.getString(request, "address", "");
    	String compost = ReqUtil.getString(request, "compost", "");
    	
    	CompanymessageEntity companymessageEntityOle = companymessageService.find(id);
    	if(companymessageEntityOle != null)
    	companymessageEntityOle.setQqnumber(qqnumber);
    	companymessageEntityOle.setHotline(hotline);
    	companymessageEntityOle.setMobile(mobile);
    	companymessageEntityOle.setFax(fax);
    	companymessageEntityOle.setEmail(email);
    	companymessageEntityOle.setLogo(logo);
    	companymessageEntityOle.setArgument(argument);
    	companymessageEntityOle.setAddress(address);
    	companymessageEntityOle.setCompost(compost);
    	companymessageService.update(companymessageEntityOle);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
