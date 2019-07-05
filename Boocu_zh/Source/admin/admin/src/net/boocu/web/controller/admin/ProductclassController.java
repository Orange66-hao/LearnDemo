package net.boocu.web.controller.admin;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.persistence.Cache;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.*;
import net.boocu.project.service.BrandAndModelService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
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

/**
 * 仪器分类管理
 * 
 * @author dengwei
 * 2015年8月12日
 */
@Controller("productclassController")
@RequestMapping("/admin/basedata/productclass")
public class ProductclassController {

	private static final String TEM_PATH = "/template/admin/basedata/productclass";
	@Autowired
	private AdminService adminService;
	@Resource
	private ProductclassService productclassService;
	@Resource
	private IndustryClassService industryClassService;
    @Autowired
    private BrandAndModelService brandAndModelService;
	@Resource
	private MemberService memberService;
	@Autowired
	private RedisTemplate redisTemplate;
	@Resource
	private ProductService productService;

	@RequestMapping(value = "toProductclassList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/productclasslist";
	}
    @RequestMapping(value = "toProductclassAndYiqiList.jspx", method = { RequestMethod.POST, RequestMethod.GET })
    public String toProductclassAndYiqiList(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("currentUser",adminService.getCurrent());
		return TEM_PATH + "/modelcollectiontwolist";
    }
    @RequestMapping("guanlian.jspx")
    public String guanlian(HttpServletRequest request, HttpServletResponse response) {
		Long chanpinId = ReqUtil.getLong(request, "chanpinId", 0L);
		request.setAttribute("chanpinId",chanpinId);
		return TEM_PATH+"/guanlian";
    }
    //根据仪器Id获取对应的产品集合
    @ResponseBody
    @RequestMapping("expandByChanpinId.jspx")
    public Set expandByYiqiId(HttpServletRequest request, HttpServletResponse response) {
        Long chanpinId = ReqUtil.getLong(request, "chanpinId", 0L);
        /**
         * 此处需要过滤掉多对多的属性，否则报错
         */
		Set<ProductclassEntity> productclassEntities=new HashSet<>();
        IndustryClassEntity industryClassEntity = industryClassService.find(chanpinId);
		return  industryClassEntity==null?productclassEntities:industryClassEntity.getProductclassEntities();
    }

	@RequestMapping("getGroupData.jspx")
	public void getGroupData(HttpServletRequest request, HttpServletResponse response,Model model) {
		List<Map<String, Object>> topNoderows=productclassService.queryrows(request, response, model);//查询条数
		List<Map> page=productclassService.query(request, response, model);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", topNoderows.size());//获取行总数
		result.put("rows", page);
		RespUtil.renderJson( response, result);
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
            map.put("id", item.getId());
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
	// 跳转到实体添加页面
	@RequestMapping(value = "/add_productclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String add_IndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		return TEM_PATH + "/productclass_add";

	}
	//关联产品和仪器
	@ResponseBody
	@RequestMapping("addGuanlian.jspx")
	public Message addGuanlian(HttpServletRequest request, HttpServletResponse response) {

		Long chanpinid = ReqUtil.getLong(request, "chanpinid", 0L);
		Message message=new Message();
		try {
			String[] yiqiIdList = request.getParameterValues("yiqiIdList[]");
			IndustryClassEntity industryClassEntity1 = industryClassService.find(chanpinid);


            /**  以前是  135   现在提交是 235 则要删除 id为1的仪器，添加id为2的仪器
             * 拿到提交上来的仪器集合判断 是否包含以前的仪器集合里面的每一个元素，没有则删除，有则跳过   分开做这个
             */
            //先做删除
            List<ProductclassEntity> newProclassEntityList=new ArrayList<>();
			if(yiqiIdList!=null){//有关联
				for (String id : yiqiIdList) {
                    ProductclassEntity productclassEntity = productclassService.find(Long.parseLong(id));
                    if(productclassEntity!=null){
                        newProclassEntityList.add(productclassEntity);
                    }
                }
                Iterator<ProductclassEntity> iterator = industryClassEntity1.getProductclassEntities().iterator();
				while(iterator.hasNext()){
                    ProductclassEntity productclassEntity = iterator.next();
                    if(!newProclassEntityList.contains(productclassEntity)){
                        //删掉
                        List<BrandAndModelEntity> list = brandAndModelService.findList(Filter.eq("productclassEntity", productclassEntity),Filter.eq("industryClassEntity",industryClassEntity1));
                        if(list!=null&&list.size()>0){
                            brandAndModelService.deleteList(list);
                        }
                        iterator.remove();
                    }
                }

                for (ProductclassEntity productclassEntity : newProclassEntityList) {
                    if(!industryClassEntity1.getProductclassEntities().contains(productclassEntity)){
                        industryClassEntity1.getProductclassEntities().add(productclassEntity);
                    }
                }

            }else{//一个关联都没了  品牌型号全部删除  仪器 删除
                for (ProductclassEntity productclassEntity : industryClassEntity1.getProductclassEntities()) {
                    List<BrandAndModelEntity> list = brandAndModelService.findList(Filter.eq("productclassEntity", productclassEntity),Filter.eq("industryClassEntity",industryClassEntity1));
                    if(list!=null&&list.size()>0){
                        brandAndModelService.deleteList(list);
                    }
                }
				industryClassEntity1.getProductclassEntities().clear();
			}
			
			industryClassEntity1.setModifyDate(new Date());
			industryClassService.update(industryClassEntity1);
			message.setType(MessageTypeEnum.success);
			message.setCont("关联成功");
		} catch (Exception e) {
			System.out.println("关联失败"+e.getMessage());
			message.setType(MessageTypeEnum.error);
			message.setCont("关联失败");
		}
		return message;
	}
		// 获取树形行业网络图
		@ResponseBody
		@RequestMapping(value = "getData.json", method = { RequestMethod.POST, RequestMethod.GET })
		public List<Map> getDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
			String menuid = ReqUtil.getString(request, "id", "01");
			String keyword = ReqUtil.getString(request, "keyword", "");
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", menuid));
			// flist.add(Filter.eq("status", 1));
			List<Sequencer> slist = new ArrayList<Sequencer>();
		slist.add(Sequencer.asc("sort"));

		if (!keyword.isEmpty()) {
			// 将ID条件去除
			flist.remove(0);
			flist.add(Filter.like("name", "%" + keyword + "%"));
			flist.add(Filter.eq("leaf", "1"));
		}
		List<ProductclassEntity> resultList = productclassService.findList(flist, slist);
		List<Map> resultData = new ArrayList<Map>();
		for (ProductclassEntity item : resultList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("id", item.getMenuid());
			data.put("rowid", item.getId());
			data.put("name", item.getName()+"/"+item.getNameEn());
			data.put("sort", item.getSort());
			// 取得上传分类名称
			ProductclassEntity productclassEntity = productclassService.find(Filter.eq("menuid", item.getParentid()));
			if (productclassEntity != null) {
				data.put("parentName", productclassEntity.getName()+"/"+productclassEntity.getNameEn());
			}
			// 表示节点是否可以展开
			data.put("state", "0".equals(item.getLeaf()) ? "closed" : "open");
			resultData.add(data);
		}
		return resultData;
	}

    // 获取下拉树形结构信息
    @ResponseBody
    @RequestMapping(value = "/combotreeData.json", method = { RequestMethod.POST, RequestMethod.GET })
    public List<Map> getComboTreeDataJson(HttpServletRequest request, HttpServletResponse response, Model model) {
        boolean getSanji = ReqUtil.getBoolean(request, "getSanji", false);
        if(getSanji){
            List<Map> resultList = new ArrayList<Map>();
            ProductclassEntity topNode = productclassService.find(1L);
            return getNodeData2(topNode,resultList);
        }else{
            ProductclassEntity topNode = productclassService.find(1L);
            return getNodeData(topNode);
        }
    }
	// 递归查找树形结构信息
	public List<Map> getNodeData(ProductclassEntity topNode) {
		List<Map> resultList = new ArrayList<Map>();
		List<Filter> flist = new ArrayList<Filter>();
		flist.add(Filter.eq("parentid", topNode.getMenuid()));
		List<ProductclassEntity> items = productclassService.findList(flist, Sequencer.asc("sort"));
		for (ProductclassEntity item : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text", item.getName());
			List<ProductclassEntity> children = productclassService.findList(Filter.eq("parentid", item.getMenuid()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}
			resultList.add(map);
		}
		return resultList;
	}

	// 异步获取下拉树形结构信息
	@ResponseBody
	@RequestMapping(value = "combotree.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getComboTree(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "id", 1l);
		ProductclassEntity productclassEntity = productclassService.find(id);
		List<Map> resultList = new ArrayList<Map>();
		if (productclassEntity != null) {
			List<Filter> flist = new ArrayList<Filter>();
			flist.add(Filter.eq("parentid", productclassEntity.getMenuid()));
			List<ProductclassEntity> items = productclassService.findList(flist, Sequencer.asc("sort"));
			for (ProductclassEntity item : items) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("text", item.getName()+"/"+item.getNameEn());
				map.put("state", "0".equals(item.getLeaf()) ? "closed" : "open");
				resultList.add(map);
			}
		}

		return resultList;
	}

	/**
	 * 
	 * 方法:将新添加的ProductclassEntity（本身实体 ）保存到数据库中 传入参数:ProductclassEntity的字段
	 * 传出参数:result（方法结果信息） 逻辑：接收前台ProductclassEntity的字段，对非空字段校验，新建实体赋值保存
	 */
	@ResponseBody
	@RequestMapping(value = "/save_productclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model,
			ProductclassEntity productclassEntity) {
		MemberEntity memberEntity = memberService.getCurrent();
		if (memberEntity != null) {
			productclassEntity.setCreateuser(memberEntity.getId().toString());
		}
		if (productclassEntity.getParentid().isEmpty()) {
			productclassEntity.setParentid("01");
		}
		productclassService.save(productclassEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}

	/**
	 * 
	 * 方法:删除选中ProductclassEntity（本身实体 ）并更新数据库 传入参数:id（选中实体的id数组）
	 * 传出参数:result（方法结果信息）
	 */
	@RequestMapping(value = "/delete_productclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public void deleteIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model, Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (id != null) {
			ProductclassEntity productclassEntity = productclassService.find(id);
			// 删除选项的menuid字段的长度
			int length = productclassEntity.getMenuid().length();
			// 如果表不为空且Menuid的长度为8
			if (productclassEntity != null && length == 8) {
				List<ProductEntity> productEntities = productService
						.findList(Filter.eq("productclass", productclassEntity));
				if (productEntities != null && productEntities.size() != 0) {
					result.put("result", 0);
					result.put("message", "该分类有数据存在");
				} else {
					productclassService.delete(productclassEntity);
					result.put("result", 1);
					result.put("message", "操作成功");
				}
			}
			// 如果表不为空且Menuid的长度为6
			if (productclassEntity != null && length == 6) {
				List<ProductEntity> productEntities = productService
						.findList(Filter.eq("productclass", productclassEntity));
				if (productEntities != null && productEntities.size() != 0) {
					result.put("result", 0);
					result.put("message", "该分类有数据存在");
				} else {
					List<ProductclassEntity> productclassEntities = productclassService
							.findList(Filter.eq("parentid", productclassEntity.getMenuid()));
					if (productclassEntities != null && productclassEntities.size() != 0) {
						for (ProductclassEntity item : productclassEntities) {
							List<ProductEntity> productEntities2 = productService
									.findList(Filter.eq("productclass", item));
							if (productEntities2 != null && productEntities2.size() != 0) {
								result.put("result", 0);
								result.put("message", "该分类有数据存在");
							} else {
								productclassService.delete(productclassEntity);
								productclassService.deleteList(productclassEntities);
								result.put("result", 1);
								result.put("message", "操作成功");
							}
						}
					} else {
						productclassService.delete(productclassEntity);
						result.put("result", 1);
						result.put("message", "操作成功");
					}
				}
			}
			// 如果表不为空且Menuid的长度为4
			if (productclassEntity != null && length == 4) {
				List<ProductEntity> productEntities = productService
						.findList(Filter.eq("productclass", productclassEntity));
				if (productEntities != null && productEntities.size() != 0) {
					result.put("result", 0);
					result.put("message", "该分类有数据存在");
				} else {
					List<ProductclassEntity> productclassEntities = productclassService
							.findList(Filter.eq("parentid", productclassEntity.getMenuid()));
					if (productclassEntities != null && productclassEntities.size() != 0) {
						for (ProductclassEntity item : productclassEntities) {
							List<ProductEntity> productEntities2 = productService
									.findList(Filter.eq("productclass", item));
							if (productEntities2 != null && productEntities2.size() != 0) {
								result.put("result", 0);
								result.put("message", "该分类有数据存在");
							} else {
								List<ProductclassEntity> productclassEntities2 = productclassService
										.findList(Filter.eq("parentid", item.getMenuid()));
								if (productclassEntities2 != null && productclassEntities2.size() != 0) {
									for (ProductclassEntity item1 : productclassEntities2) {
										List<ProductEntity> productEntities3 = productService
												.findList(Filter.eq("productclass", item1));
										if (productEntities3 != null && productEntities3.size() != 0) {
											result.put("result", 0);
											result.put("message", "该分类有数据存在");
										} else {
											productclassService.delete(productclassEntity);
											productclassService.deleteList(productclassEntities);
											productclassService.deleteList(productclassEntities2);
											result.put("result", 1);
											result.put("message", "操作成功");
										}
									}
								} else {
									productclassService.delete(productclassEntity);
									productclassService.deleteList(productclassEntities);
									result.put("result", 1);
									result.put("message", "操作成功");
								}
							}
						}
					} else {
						productclassService.delete(productclassEntity);
						result.put("result", 1);
						result.put("message", "操作成功");
					}
				}
			}

		}
		RespUtil.renderJson(response, result);
	}

	// 跳转到实体更新页面
	@RequestMapping(value = "/edit_productclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String editIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		String id = ReqUtil.getString(request, "id", "");

		ProductclassEntity productclassEntity = new ProductclassEntity();

		if (!id.equals("")) {
			Long lid = Long.parseLong(id);
			productclassEntity = this.productclassService.find(lid);
			model.addAttribute("item", productclassEntity);
			// 上层
			ProductclassEntity productclassEntity2 = productclassService
					.find(Filter.eq("menuid", productclassEntity.getParentid()));
			model.addAttribute("parentid", productclassEntity2.getId());
		}
		return TEM_PATH + "/productclass_edit";

	}
	/** 增加跳转 */
	@RequestMapping(value = "/edit_class.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_class(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long id = ReqUtil.getLong(request, "chanpinId", 0l);
		model.addAttribute("chanpinId", id);
		return TEM_PATH + "/modelcollectiontwo_edit";
	}
	/**
	 * 
	 * 方法:保存更新之后的ProductclassEntity（本身实体 ） 传入参数:ProductclassEntity的字段
	 * id（更新实体的id） 传出参数:result（方法结果信息）
	 * 逻辑：查询该id的实体，存在则读取前台ProductclassEntity（村本身实体 ）的字段并执行更新操作；否则提示更新错误
	 */
	@ResponseBody
	@RequestMapping(value = "/save_edit_productclass.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public Map<String, Object> saveEditIndustryClass(HttpServletRequest request, HttpServletResponse response,
			Model model, @RequestParam long id,
			// @RequestParam String menuid,
			@RequestParam Long parentid,
			// @RequestParam String leaf,
			@RequestParam String name, @RequestParam String nameEn, @RequestParam String createuser,
			@RequestParam String updateuser, @RequestParam String remark, @RequestParam int sort) {
		// 上层
		ProductclassEntity productclassEntity = productclassService.find(parentid);

		ProductclassEntity OldProductclassEntity = productclassService.find(id);

		// OldProductclassEntity.setMenuid(menuid);
		// OldProductclassEntity.setLeaf(leaf);
		OldProductclassEntity.setParentid(productclassEntity.getMenuid());
		OldProductclassEntity.setName(name);
		OldProductclassEntity.setNameEn(nameEn);
		OldProductclassEntity.setCreateuser(createuser);
		OldProductclassEntity.setUpdateuser(updateuser);
		OldProductclassEntity.setRemark(remark);
		// OldProductclassEntity.setStatus(status);
		OldProductclassEntity.setSort(sort);
		productclassService.update(OldProductclassEntity);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", 1);
		result.put("message", "保存成功");
		return result;
	}
	// 点击仪器名称跳转
	@RequestMapping(value = "/edit_productclass2.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public String edit_productclass(HttpServletRequest request, HttpServletResponse response, Model model) {
		String yiqiId = ReqUtil.getString(request, "yiqiId", "");
        String productby = ReqUtil.getString(request, "productby", "");
        String productclass_type=request.getParameter("productclass_type");
		ProductclassEntity productclassEntity=new ProductclassEntity();

        String induId = ReqUtil.getString(request, "induId", "");
		if (!yiqiId.equals("")&&!induId.equals("")) {
            productclassEntity = this.productclassService.find(Long.parseLong(yiqiId));
            IndustryClassEntity industryClassEntity = industryClassService.find(Long.parseLong(induId));
            List<Filter> list=new ArrayList<>();
            list.add(Filter.eq("industryClassEntity",industryClassEntity));
            list.add(Filter.eq("productclassEntity",productclassEntity));
            List<BrandAndModelEntity> BrandAndModelEntityList = brandAndModelService.findList(list);

			model.addAttribute("item", productclassEntity);
            model.addAttribute("productby", productby);
            model.addAttribute("BrandAndModelEntityList", BrandAndModelEntityList);
		}

		if (productclass_type != null && productclass_type.equals("productclassshow")) {
			return TEM_PATH+"/mcproductclass_enterprise";
		}else{
			return TEM_PATH+"/mcproductclass_edit";
		}

	}
}
