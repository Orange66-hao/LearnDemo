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
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.service.MemberGradeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.support.json.JSONUtils;


/**
 * 会员等级
 * @author dengwei
 *
 * 2015年10月31日
 */
@Controller("memberGradeController")
@RequestMapping("/admin/member/memberGrade")
public class MemberGradeController {
	
	private static final String TEM_PATH ="/template/admin/member/memberGrade";
	
	@Resource
	private MemberGradeService memberGradeService;
	
	@RequestMapping(value="toMemberGradeList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/memberGradelist";
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
			flist.add(Filter.like("name", "%"+keyword+"%"));
			pageable.getFilters().add(Filter.or(flist));
		}
		Page<MemberGradeEntity> page = memberGradeService.findPage(pageable);
		List<MemberGradeEntity> memberGradeEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(MemberGradeEntity item : memberGradeEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("priceType", item.getPriceType());
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}

	//跳转到村实体添加页面
    @RequestMapping(value = "/add_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_MemberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/memberGrade_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveMemberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	String priceType = ReqUtil.getString(request, "priceType", "");
    	
    	MemberGradeEntity memberGradeEntity = new MemberGradeEntity();
    	
    	memberGradeEntity.setName(name);
    	memberGradeEntity.setPriceType(priceType);
    	memberGradeService.save(memberGradeEntity);
    	
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
    @RequestMapping(value = "/delete_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteMemberGrade(HttpServletRequest request, HttpServletResponse response, Model model,Long[] id) {
    	if(id != null && id.length>0){
    		this.memberGradeService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editMemberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	MemberGradeEntity memberGradeEntity = new MemberGradeEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		memberGradeEntity = this.memberGradeService.find(lid);
    		model.addAttribute("item", memberGradeEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/memberGrade_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_memberGrade.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditMemberGrade(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String name= ReqUtil.getString(request, "name", "");
    	String priceType = ReqUtil.getString(request, "priceType", "");
    	
    	MemberGradeEntity memberGradeEntity = memberGradeService.find(id);
    	if(memberGradeEntity != null)
    	memberGradeEntity.setName(name);
    	memberGradeEntity.setPriceType(priceType);
    	memberGradeService.update(memberGradeEntity);
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /** 取得用户等级的集合*/
    @ResponseBody
    @RequestMapping(value="/get_memberGrade_names.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void getMemberGradeNames(HttpServletRequest reqeust, HttpServletResponse response, Model model, MemberGradeEntity memberGradeEntity) {
    	List<MemberGradeEntity> memberGradeEntities = memberGradeService.findAll();
    	List<Map> resultList = new ArrayList<Map>();
    	for(MemberGradeEntity item : memberGradeEntities){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("id", item.getId());
    		map.put("text", item.getName());
    		resultList.add(map);
    	}
    	RespUtil.renderJson(response,JSONUtils.toJSONString(resultList));
    }
 
}
