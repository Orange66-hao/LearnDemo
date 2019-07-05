package net.boocu.web.controller.admin;


import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.PbrandAndModelService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.enums.MessageTypeEnum;
import net.boocu.web.service.AdminService;
import net.boocu.web.service.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;


/**
 * 


 * @author fang
 *
 * 2015年8月27日
 */
@Controller("industryClassController")
    @RequestMapping("/admin/industyClass")
public class IndustryClassController {
	
	private static final String TEM_PATH ="/template/admin/basedata/industryClass";
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
	@Resource
	private IndustryClassService industryClassService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private ProductclassService productclassService;
	@Resource
	private MemberService memberService;
	@Autowired
	private PbrandAndModelService pbrandAndModelService;
	@Resource
	private ProductService productService;
	//跳转仪器关联产品列表
	@RequestMapping(value="toProductclassAndYiqiList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toProductclassAndYiqiList(HttpServletRequest request,HttpServletResponse response,Model model){
		model.addAttribute("currentUser",adminService.getCurrent());
		return TEM_PATH + "/modelcollectiontwolist";
	}

	//跳转仪器关联产品界面
	@RequestMapping("guanlian.jspx")
	public String guanlian(HttpServletRequest request, HttpServletResponse response) {
        String yiqiId = ReqUtil.getString(request,"yiqiId","");
        request.setAttribute("yiqiId",yiqiId);
		return TEM_PATH+"/guanlian";
	}
	@RequestMapping(value="toIndustryClassList.jspx",method={RequestMethod.POST,RequestMethod.GET})
	public String toList(HttpServletRequest request,HttpServletResponse response,Model model){

        return TEM_PATH+"/industryClasslist";
	}

    //跳转到实体添加页面
    @RequestMapping(value = "/add_industryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String add_IndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
    	return TEM_PATH+"/industryClass_add";
    	
    }
	@RequestMapping(value = "/get.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
	public void get(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 0L);
		IndustryClassEntity industryClassEntity = industryClassService.find(id);
		System.out.println(industryClassEntity);
	}
	//关联产品和仪器
	@ResponseBody
	@RequestMapping("addGuanlian.jspx")
	public Message addGuanlian(HttpServletRequest request, HttpServletResponse response) {

		Long yiqiid = ReqUtil.getLong(request, "yiqiid", 0L);
		Message message=new Message();
		try {
			String[] chanpinIdList = request.getParameterValues("chanpinIdList[]");
			ProductclassEntity productclassEntity = productclassService.find(yiqiid);


			/**  以前是  135   现在提交是 235 则要删除 id为1的仪器，添加id为2的仪器
			 * 拿到提交上来的仪器集合判断 是否包含以前的仪器集合里面的每一个元素，没有则删除，有则跳过   分开做这个
			 */
			//先做删除
			List<IndustryClassEntity> newProclassEntityList=new ArrayList<>();
			if(chanpinIdList!=null){//有关联
				for (String id : chanpinIdList) {
					IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(id));
					if(industryClassEntity!=null){
						newProclassEntityList.add(industryClassEntity);
					}
				}
				Iterator<IndustryClassEntity> iterator = productclassEntity.getIndustryClassEntities().iterator();
				while(iterator.hasNext()){
					IndustryClassEntity industryClassEntity= iterator.next();
					if(!newProclassEntityList.contains(industryClassEntity)){
						//删掉
						List<PbrandAndModelEntity> list = pbrandAndModelService.findList(Filter.eq("productclassEntity", productclassEntity),Filter.eq("industryClassEntity",industryClassEntity));
						if(list!=null&&list.size()>0){
							pbrandAndModelService.deleteList(list);
						}
						iterator.remove();
					}
				}

				for (IndustryClassEntity industryClassEntity : newProclassEntityList) {
					if(!productclassEntity.getIndustryClassEntities().contains(industryClassEntity)){
						productclassEntity.getIndustryClassEntities().add(industryClassEntity);
					}
				}

			}else{//一个关联都没了  品牌型号全部删除   删除关联的仪器
				for (IndustryClassEntity industryClassEntity : productclassEntity.getIndustryClassEntities()) {
					List<PbrandAndModelEntity> list = pbrandAndModelService.findList(Filter.eq("productclassEntity", productclassEntity),Filter.eq("industryClassEntity",industryClassEntity));
					if(list!=null&&list.size()>0){
						pbrandAndModelService.deleteList(list);
					}
				}
				productclassEntity.getIndustryClassEntities().clear();
			}

			productclassEntity.setModifyDate(new Date());
			productclassService.update(productclassEntity);
			message.setType(MessageTypeEnum.success);
			message.setCont("关联成功");
		} catch (Exception e) {
			System.out.println("关联失败"+e.getMessage());
			message.setType(MessageTypeEnum.error);
			message.setCont("关联失败");
		}
		return message;
	}
	//关联列表分页数据
	@RequestMapping("getGroupData.jspx")
	public void getGroupData(HttpServletRequest request, HttpServletResponse response,Model model) {
	    //查询的是procuctclass表
		List<Map<String, Object>> topNoderows=industryClassService.queryrows(request, response, model);//查询条数
		List<Map> page=industryClassService.query(request, response, model);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());//获取行总数
		result.put("rows", page);
		RespUtil.renderJson( response, result);
	}
    //获取树形行业网络图 
    @ResponseBody
    @RequestMapping(value="getData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
    	long id = ReqUtil.getLong(request, "id", 1l);
    	String keyword = ReqUtil.getString(request, "keyword", "");
    	List<Filter> flist = new ArrayList<Filter>(); 
    	flist.add(Filter.eq("parentid", id));
    	//flist.add(Filter.eq("status", 1));
    	List<Sequencer> slist = new ArrayList<Sequencer>(); 
    	slist.add(Sequencer.asc("sort"));
    	
    	if(!keyword.isEmpty()){
    		//将ID条件去除
    		flist.remove(0);
    		flist.add(Filter.like("name", "%"+keyword+"%"));
    		flist.add(Filter.eq("leaf", "1"));
    	}
    	List<IndustryClassEntity> resultList =  industryClassService.findList(flist, slist);
    	List<Map> resultData = new ArrayList<Map>();
    	for(IndustryClassEntity item : resultList){
    		Map<String, Object> data = new HashMap<String, Object>();
    		data.put("id", item.getId());
    		data.put("name", item.getName()+"/"+item.getNameEn());
    		data.put("sort", item.getSort());
    		data.put("itemStatus", item.getStatus());
    		//取得上传分类名称
			IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(item.getParentid()));
			if(industryClassEntity != null){
				data.put("parentName", industryClassEntity.getName()+"/"+industryClassEntity.getNameEn());
			}
    		//表示节点是否可以展开
    		data.put("state", "0".equals(item.getLeaf())?"closed":"open");
    		resultData.add(data);
    	}
    	return resultData;
    }
    //获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotreeData.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model){
		boolean getSanji = ReqUtil.getBoolean(request, "getSanji", false);
		if(getSanji){
			List<Map> resultList = new ArrayList<Map>();
			IndustryClassEntity topNode = industryClassService.find(1L);
			return getNodeData2(topNode,resultList);
		}else{
			IndustryClassEntity topNode = industryClassService.find(1L);
			return getNodeData(topNode);
		}
    }
    
    //递归查找树形结构信息
    public List<Map> getNodeData(IndustryClassEntity topNode){
    	List<Map> resultList = new ArrayList<Map>();
    	List<Filter> flist = new ArrayList<Filter>();
    	flist.add(Filter.eq("parentid", topNode.getId()));
    	List<IndustryClassEntity> items = industryClassService.findList(flist, Sequencer.asc("sort"));
		for(IndustryClassEntity item : items){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<IndustryClassEntity> children = industryClassService.findList(Filter.eq("parentid", item.getId()));
			if(children.size() != 0){
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
    	return resultList;
    }
	//根据仪器Id获取对应的产品集合
	@ResponseBody
	@RequestMapping("expandByYiqiId.jspx")
	public List<Map> expandByYiqiId(HttpServletRequest request, HttpServletResponse response) {
		Long yiqiid = ReqUtil.getLong(request, "yiqiId", 0L);
		/**
		 * 此处需要过滤掉多对多的属性，否则报错
		 */
		List<Map> resultList=new ArrayList<>();

		ProductclassEntity productclassEntity = productclassService.find(yiqiid);
		if(productclassEntity!=null){
			Iterator<IndustryClassEntity> iterator = productclassEntity.getIndustryClassEntities().iterator();
			while (iterator.hasNext()){
				IndustryClassEntity next = iterator.next();
				Map map=new HashMap();
				map.put("id",next.getId());
				map.put("text",next.getName());
				resultList.add(map);
			}
		}

		return resultList;
	}
	//递归查找三级目录
	public List<Map> getNodeData2(ProductclassEntity topNode, List<Map> resultList) {
		Filter filter = Filter.eq("parentid", topNode.getMenuid());
		List filters=new ArrayList();
		filters.add(filter);
		Sequencer sequencer=Sequencer.asc("sort");
		List<ProductclassEntity> items = productclassService.findList(filters,sequencer);
		for (ProductclassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getMenuid());
			map.put("text", item.getName());
			ArrayList<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.eq("parentid",item.getMenuid()));
			List<ProductclassEntity> children = productclassService.findList(filters1,sequencer);
			if (children.size() != 0) {
				getNodeData2(item,resultList);
			}else{
				resultList.add(map);
			}
		}
		return resultList;
	}
	//递归查找三级目录
	public List<Map> getNodeData2(IndustryClassEntity topNode, List<Map> resultList) {
		Filter filter = Filter.eq("parentid", topNode.getId());
		List filters=new ArrayList();
		filters.add(filter);
		Sequencer sequencer=Sequencer.asc("sort");
		List<IndustryClassEntity> items = industryClassService.findList(filters,sequencer);
		for (IndustryClassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			ArrayList<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.eq("parentid",item.getId()));
			List<IndustryClassEntity> children = industryClassService.findList(filters1,sequencer);
			if (children.size() != 0) {
				getNodeData2(item,resultList);
			}else{
				resultList.add(map);
			}
		}
		return resultList;
	}
    
    //获取异步下拉树形结构信息
    @ResponseBody
    @RequestMapping(value="combotree.json",method={RequestMethod.POST,RequestMethod.GET})
    public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model){
    	Long id = ReqUtil.getLong(request, "id", 1l);
    	IndustryClassEntity industryClassEntity = industryClassService.find(id);
    	List<Map> resultList = new ArrayList<Map>();
    	if(industryClassEntity !=null){
        	List<Filter> flist = new ArrayList<Filter>();
        	flist.add(Filter.eq("parentid", industryClassEntity.getId()));
        	List<IndustryClassEntity> items = industryClassService.findList(flist, Sequencer.asc("sort"));
    		for(IndustryClassEntity item : items){
    			Map<String,Object> map = new HashMap<String, Object>();
    			map.put("id", item.getId());
    			map.put("text", item.getName()+"/"+item.getNameEn());
    			map.put("state", "0".equals(item.getLeaf())?"closed":"open");
    			resultList.add(map);
    		}
    	}
    	return resultList;
    }
    
    /**
     * 
     * 方法:将新添加的IndustryClassEntity（本身实体 ）保存到数据库中
     * 传入参数:IndustryClassEntity的字段
     * 传出参数:result（方法结果信息）
     * 逻辑：接收前台IndustryClassEntity的字段，对非空字段校验，新建实体赋值保存
     */
    @ResponseBody
    @RequestMapping(value = "/save_industryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,IndustryClassEntity industryClassEntity) {
    	MemberEntity memberEntity = memberService.getCurrent();
    	if(memberEntity != null){
    		industryClassEntity.setCreateuser(memberEntity.getId().toString());
    	}
    	if(industryClassEntity.getParentid().isEmpty()){
    		industryClassEntity.setParentid("1");
    	}
    	industryClassService.save(industryClassEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    } 
    
    /**
     * 
     * 方法:删除选中IndustryClassEntity（本身实体 ）并更新数据库
     * 传入参数:id（选中实体的id数组）
     * 传出参数:result（方法结果信息）
     */
    @RequestMapping(value = "/delete_industryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public void deleteIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,Long id) {
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			if(id != null ){
                IndustryClassEntity industryClassEntity = industryClassService.find(id);
                deleteClassEntity(industryClassEntity);
				result.put("result", 1);
				result.put("message", "操作成功");
            }
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", 0);
			result.put("message", "操作失败，服务器出错了");
		}
    	RespUtil.renderJson(response, result);
    }
    private void  deleteClassEntity(IndustryClassEntity industryClassEntity){
		List<IndustryClassEntity> list = industryClassService.findList(Filter.eq("parentid", industryClassEntity.getId()));
		if(list!=null&&list.size()>0){
			for (IndustryClassEntity classEntity : list) {
				//递归删除
				deleteClassEntity(classEntity);
			}
		}
		industryClassService.delete(industryClassEntity);
	}
    //跳转到实体更新页面
    @RequestMapping(value = "/edit_industryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public String editIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String id = ReqUtil.getString(request, "id", "");
    	
    	IndustryClassEntity industryClassEntity = new IndustryClassEntity();
    	
    	if(!id.equals("")){
    		Long lid = Long.parseLong(id);
    		industryClassEntity = this.industryClassService.find(lid);
    		model.addAttribute("item", industryClassEntity);
    	}
    	return TEM_PATH+"/industryClass_edit";
    	
    }
	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_industryClass2.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_industryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");
		String industryby = ReqUtil.getString(request, "industryby", "");
		String productclass_type=request.getParameter("productclass_type");
		ProductclassEntity productclassEntity=new ProductclassEntity();

		String induId = ReqUtil.getString(request, "induId", "");
		if (!id.equals("")&&!induId.equals("")) {
			Long lid = Long.parseLong(id);
			productclassEntity = this.productclassService.find(lid);
			IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(induId));
			List<Filter> list=new ArrayList<>();
			list.add(Filter.eq("industryClassEntity",industryClassEntity));
			list.add(Filter.eq("productclassEntity",productclassEntity));
			List<PbrandAndModelEntity> pbrandAndModelEntities = pbrandAndModelService.findList(list);

			model.addAttribute("item", industryClassEntity);
			model.addAttribute("industryby", industryby);
			model.addAttribute("pBrandAndModelEntityList", pbrandAndModelEntities);

		}

		if (productclass_type != null && productclass_type.equals("productclassshow")) {
			return TEM_PATH+"/mcproductclass_enterprise";
		}else{
			return TEM_PATH+"/mcproductclass_edit";
		}

	}
    /**
     * 
     * 方法:保存更新之后的IndustryClassEntity（本身实体 ）
     * 传入参数:IndustryClassEntity的字段    id（更新实体的id）
     * 传出参数:result（方法结果信息）
     * 逻辑：查询该id的实体，存在则读取前台IndustryClassEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
     */
    @ResponseBody
    @RequestMapping(value = "/save_edit_industryClass.jspx",method ={ RequestMethod.POST, RequestMethod.GET})
    public Map<String,Object> saveEditIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,
    		@RequestParam long id,
    		@RequestParam String name,@RequestParam String nameEn,
    		@RequestParam String parentid,
    		@RequestParam int status,
    		@RequestParam int sort) {
    	IndustryClassEntity OldIndustryClassEntity = industryClassService.find(id);
    	OldIndustryClassEntity.setParentid(parentid);
    	OldIndustryClassEntity.setStatus(status);
    	OldIndustryClassEntity.setSort(sort);
    	OldIndustryClassEntity.setName(name);
		OldIndustryClassEntity.setNameEn(nameEn);
    	industryClassService.update(OldIndustryClassEntity);
    	Map<String, Object> result = new HashMap<String, Object>(); 
    	result.put("result", 1);
    	result.put("message", "保存成功");
    	return result;
    }
	/** 增加跳转 */
	@RequestMapping(value = "/edit_class.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_class(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "yiqiId", 0l);
		model.addAttribute("yiqiId", id);
		return TEM_PATH + "/modelcollectiontwo_edit";
	}
}
