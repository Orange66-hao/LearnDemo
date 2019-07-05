package net.boocu.web.controller.admin;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.FriendsEntity;
import net.boocu.project.entity.ProductSaleEntity;
import net.boocu.project.service.FriendsService;
import net.boocu.web.Filter;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import freemarker.core._RegexBuiltins.matchesBI;


/**
 * 首页广告
 * @author dengwei
 *
 * 2015年8月10日
 */
@Controller("friendsController")
@RequestMapping("/admin/index/friends")
public class FriendsController {
	
	private static final String TEM_PATH ="/template/admin/index/friends";
	
	@Resource
	private FriendsService friendsService;
	
	@RequestMapping(value="toFriendsList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){
		return TEM_PATH+"/friendslist";
	}
	
	@ResponseBody
	@RequestMapping(value="getData.json", method={RequestMethod.GET, RequestMethod.POST})
	public List<Map> datajsn(HttpServletResponse response, HttpServletRequest request, Model model){
		long id = ReqUtil.getLong(request, "id", 1000341349l);
		String keyword = ReqUtil.getString(request, "keyword", "");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("parentid", id));
		List<Sequencer> slist = new ArrayList<Sequencer>(); 
		slist.add(Sequencer.asc("sort"));
		
		if(!keyword.isEmpty()){
			//将ID条件去除
			filters.remove(0);
			filters.add(Filter.like("name", "%"+keyword+"%"));
			filters.add(Filter.eq("leaf", "1"));
		}
		List<FriendsEntity> friendsEntities = friendsService.findList(filters, slist);
		List<Map> resultList = new ArrayList<Map>();
		for(FriendsEntity item : friendsEntities){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("name", item.getName()+"/"+item.getNameEn());
			map.put("sort", item.getSort());
			map.put("link", item.getLink());
			//取得上层分类名称
			FriendsEntity friendsEntity = friendsService.find(Long.parseLong(item.getParentid()));
			if(friendsEntity !=null){
				map.put("parentName", friendsEntity.getName());
			}
			map.put("state", "0".equals(item.getLeaf())?"closed":"open");
			resultList.add(map);
		}
		return resultList;	
	}
	
	//获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value="/friendsData.json", method={RequestMethod.GET, RequestMethod.POST})
	public List<Map> friendsData(HttpServletRequest request, HttpServletResponse response, Model model){
		FriendsEntity friendsEntity = friendsService.find(1000341349l);
		
		return getNodeData(friendsEntity);
	}
	
	//查找递归的树形结构
	public List<Map> getNodeData(FriendsEntity topNode){
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("parentid", topNode.getId()));
		List<FriendsEntity> friendsEntities = friendsService.findList(filters, Sequencer.desc("sort"));
		List<Map> resultList = new ArrayList<Map>();
		for(FriendsEntity item : friendsEntities){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName()+"/"+item.getNameEn());
			List<FriendsEntity> friendsEntities2 = friendsService.findList(Filter.eq("parentid", item.getId()));
			if(friendsEntities2.size() !=0){
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		
		return resultList;
	}
	
/*	@RequestMapping(value="data.json",method={RequestMethod.POST,RequestMethod.GET})
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
		Page<FriendsEntity> page = friendsService.findPage(pageable);
		List<FriendsEntity> friendsEntities = page.getCont();
		List<Map> resultList = new ArrayList<Map>();
		
		for(FriendsEntity item : friendsEntities){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", item.getId());
			map.put("name", item.getName());
			map.put("parentid", item.getParentid());
			map.put("sort", item.getSort());
			map.put("link", item.getLink());
			map.put("creatuser", item.getCreatuser());
			map.put("updateuser", item.getUpdateuser());
			resultList.add(map);
		}
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total",page.getTotal() );
		result.put("rows",resultList); 
		RespUtil.renderJson(response, result);
	}*/
	
	//跳转到村实体添加页面
    @RequestMapping(value = "/add_friends.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_Friends(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/friends_add";
    	
    } 
    
    /**
     * 
     * 方法:将新添加的ProductSaleEntity（本身实体 ）保存到数据库中
     * 传入参数:ProductSaleEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台ProductSaleEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_friends.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	
    	//获取到页面的基本信息
    	String name= ReqUtil.getString(request, "name", "");
    	String nameEn= ReqUtil.getString(request, "nameEn", "");
    	String parentid= ReqUtil.getString(request, "parentid", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String link = ReqUtil.getString(request, "link", "");
    	
    	FriendsEntity friendsEntity = new FriendsEntity();
    	
    	friendsEntity.setName(name);
    	friendsEntity.setNameEn(nameEn);
    	friendsEntity.setParentid(parentid);
    	friendsEntity.setSort(sort);
    	friendsEntity.setLink(link);
    	friendsEntity.setCreatuser("10000001");
    	friendsEntity.setUpdateuser("10000001");
    	friendsService.save(friendsEntity);
    	
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
    @RequestMapping(value = "/delete_friends.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	if(id != null && id != 0l){
    		this.friendsService.deleteList(id);
    	}
    	
    	Map<String,Object> result = new HashMap<String,Object>();
    	result.put("result", 1);
    	result.put("message", "操作成功");
    	
    	RespUtil.renderJson(response, result);
    }
    
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_friends.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editProductSale(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	FriendsEntity friendsEntity = new FriendsEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		friendsEntity = this.friendsService.find(lid);
    		model.addAttribute("item", friendsEntity);
    	}
    	
    	
    	
    	return TEM_PATH+"/friends_edit";
    	
    } 
     
    /**
     * 
     * 方法:保存更新之后的ProductSaleEntity（村本身实体 ）
     * 传入参数:ProductSaleEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台ProductSaleEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_friends.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditFriends(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Long id = ReqUtil.getLong(request, "id", 0l);
    	String name= ReqUtil.getString(request, "name", "");
    	String nameEn= ReqUtil.getString(request, "nameEn", "");
    	String parentid= ReqUtil.getString(request, "parentid", "");
    	int sort = ReqUtil.getInt(request, "sort", 0);
    	String link = ReqUtil.getString(request, "link", "");
    	
    	FriendsEntity friendsEntityOle = friendsService.find(id);
    	if(friendsEntityOle !=null){
    		friendsEntityOle.setName(name);
    		friendsEntityOle.setNameEn(nameEn);
        	friendsEntityOle.setParentid(parentid);
        	friendsEntityOle.setSort(sort);
        	friendsEntityOle.setLink(link);
        	friendsService.update(friendsEntityOle);	
    	}
    			
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
}
