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
import net.boocu.project.entity.DataCenterEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.service.DataCenterService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
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
 * 资料中心管理
 * @author deng
 *
 * 2015年10月23日
 */
@Controller("dataCenterController")
@RequestMapping("/admin/index/dataCenter")
public class DataCenterController {
	
	private static final String TEM_PATH ="/template/admin/index/dataCenter";
	
	@Resource
	private DataCenterService dataCenterService;
	
	@Resource
	private ProductService productService;
	
	@Resource 
	private ProductBrandService productBrandService;
	
	@RequestMapping(value="toDataCenterList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/dataCenterlist";
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
			flist.add(Filter.like("title", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<DataCenterEntity> page = dataCenterService.findPage(pageable);
		List<DataCenterEntity> dataCenterEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(DataCenterEntity item : dataCenterEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("brandId", item.getProductEntity().getProductBrandEntity()==null?"":item.getProductEntity().getProductBrandEntity().getName());
			map.put("fileAddress", item.getFileAddress());
			map.put("filename", item.getFilename());
			map.put("downTime", item.getDownTime());
			map.put("status", item.getStatus() == 1 ? "启用":"不启用");
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_dataCenter.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/dataCenter_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_dataCenter.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveDataCenter(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	long brandId = ReqUtil.getLong(request, "brandId", 0l);
    	String fileAddress= ReqUtil.getString(request, "fileAddress", "");
    	String filename= ReqUtil.getString(request, "filename", "");
    	int status = ReqUtil.getInt(request, "status", 0);
    	
    	DataCenterEntity dataCenterEntity = new DataCenterEntity();
    	ProductEntity productEntity = new ProductEntity();
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(productBrandEntity != null){
    		productEntity.setProductBrandEntity(productBrandEntity);
    	}	
    	dataCenterEntity.setFileAddress(fileAddress);
    	dataCenterEntity.setFilename(filename);
    	dataCenterEntity.setStatus(status);
    	dataCenterService.save(dataCenterEntity,productEntity);
    	
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
    @RequestMapping(value = "/delete_dataCenter.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteDataCenter(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.dataCenterService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_dataCenter.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editDataCenter(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	
    	DataCenterEntity dataCenterEntity = new DataCenterEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		dataCenterEntity = this.dataCenterService.find(lid);
    		model.addAttribute("item", dataCenterEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/dataCenter_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_dataCenter.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditDataCenter(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	long brandId = ReqUtil.getLong(request, "brandId", 0l);
    	String fileAddress= ReqUtil.getString(request, "fileAddress", "");
    	String filename= ReqUtil.getString(request, "filename", "");
    	int status = ReqUtil.getInt(request, "status", 0);

    	DataCenterEntity dataCenterEntity = dataCenterService.find(id);
    	ProductEntity productEntity = new ProductEntity();
    	ProductBrandEntity productBrandEntity = productBrandService.find(brandId);
    	if(dataCenterEntity != null)
    	{	
    		if(productBrandEntity != null){
    			dataCenterEntity.getProductEntity().setProductBrandEntity(productBrandEntity);
    		}
    		dataCenterEntity.setFileAddress(fileAddress);
    		dataCenterEntity.setFilename(filename);
    		dataCenterEntity.setStatus(status);
        	dataCenterService.update(dataCenterEntity);
    	}
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
