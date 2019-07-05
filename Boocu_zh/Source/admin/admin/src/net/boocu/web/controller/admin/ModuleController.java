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
import net.boocu.project.entity.ModuleEntity;
import net.boocu.project.service.ModuleService;
import net.boocu.project.util.ParamsUtil;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 使用方法:如UserEntity
 * 1.将 module replaceAll 成 user (注意大小写敏感,将Case sensitive勾选)
 * 2.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */ 
@Controller("moduleController")
@RequestMapping("/admin/module")
public class ModuleController {

    /** 模板路径 */
    private static final String TEMPLATE_PATH = "/admin/module";
    
    @Resource(name = "moduleServiceImpl")
    private ModuleService moduleService;
    
    @ModelAttribute
    public void getModule(@RequestParam(required=false,defaultValue="0") Long id ,Model model){
    	ModuleEntity module = moduleService.find(id);
    	if(module != null){
    		model.addAttribute("editModule", module);
    	}
    	
    }
    
    @RequestMapping(value = "/list.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String list(HttpServletRequest request, HttpServletResponse response, Model model) {

    	return TEMPLATE_PATH+"/list";
    }

    @RequestMapping(value="/list/data.json",method = {RequestMethod.GET,RequestMethod.POST})
    public void data(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<Map> resultDataList = new ArrayList<Map>();
		
		
		String keyword = ReqUtil.getString(request, "keyword", "");
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		Pageable pageable = new Pageable(pagenumber, rows);
		if(!keyword.isEmpty()){
    		List<Filter> fList = new ArrayList<Filter>();
    		//搜索条件更改 TODO
    		fList.add(Filter.like("moduleName", "%"+keyword+"%"));
    		pageable.getFilters().add(Filter.or(fList));
    	}
		
		Page<ModuleEntity> page =moduleService.findPage(pageable);
		
		List<ModuleEntity> resultList = page.getCont();
		for(ModuleEntity  module: resultList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("item",module);
			resultDataList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultDataList); 
		
		RespUtil.renderJson(response, result);
    }
    
    //跳转到广告实体添加页面
    @RequestMapping(value = "/add_module.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Module(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEMPLATE_PATH+"/module_add";
    } 
    
    @ResponseBody
    @RequestMapping(value = "/save_module.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveModule(HttpServletRequest request, HttpServletResponse response, ModuleEntity module) {
    	
    	//获取到页面的基本信息
    	/*ModuleEntity moduleEntity =ParamsUtil.reflectEntity(request,ModuleEntity.class,moduleService);*/
    	Map<String,Object> result = new HashMap();
    	moduleService.save(module);
    	result.put("result", 1);
    	result.put("message", "保存成功!");
    	return result;
    } 
    
	@RequestMapping(value = "/delete_module.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteModule(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.moduleService.deleteList(id);
    	}
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_module.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editModule(HttpServletRequest request, HttpServletResponse response, Model model ) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	ModuleEntity ab = new ModuleEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		ab = this.moduleService.find(lid);
    	}
    	
    	model.addAttribute("item", ab);
    	return TEMPLATE_PATH+"/module_edit";
    	
    } 
     
    @ResponseBody
    @RequestMapping(value = "/save_edit_module.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditModule(HttpServletRequest request, HttpServletResponse response, Model model , @ModelAttribute(value="editModule") ModuleEntity moduleEntity) {
    	Map<String,Object> result = new HashMap<String,Object>();		
    	moduleService.update(moduleEntity);
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}