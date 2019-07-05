package net.boocu.project.controller.front;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.apache.commons.beanutils.converters.IntegerArrayConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import jdk.nashorn.api.scripting.JSObject;
import net.boocu.framework.util.ReqUtil;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.FriendsService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.web.Filter;
import net.boocu.web.Message;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;

@Controller("subscribeController")
@RequestMapping("/subscribe")
public class ProductSubscribeController {
	/** 模板路径 */
	private static final String TEMPLATE_PATH = "front/subscribe/subscription";

	/** 成功信息 */
	private static final Message SUCCESS_MESSAGE = Message.success("操作成功!");
	/** 失败信息 */
	private static final Message ERROR_MESSAGE = Message.error("操作失败!");
	/** 提示信息 */
	private static final Message INFO_MESSAGE = Message.error("您已经收藏过此商品!");

	@Resource(name = "productSubscribeServiceImpl")
	private ProductSubscribeService subscribeService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "friendsServiceImpl")
	private FriendsService friendsService;

	@Resource(name = "helpServiceImpl")
	private HelpService helpService;

	@Resource(name = "productBrandServiceImpl")
	private ProductBrandService proBrandService;

	@Resource(name = "productclassServiceImpl")
	private ProductclassService proClassService;

	@Resource(name = "producttypeServiceImpl")
	private ProducttypeService producttypeService;

	@Resource(name = "industryClassServiceImpl")
	private IndustryClassService indclassService;

	@Resource
	ProductclassService productclassService;

	@Resource
	ProductBrandService productBrandService;

	@Resource
	IndustryClassService industryClassService;

	@Resource
	private MessageService messageService;

	/** 默认查找用户注册信息显示推荐仪器 */
	@RequestMapping("/toList.jspx")
	public String toList(HttpServletRequest request, HttpServletResponse response, Model model) {

		// 获取登录用户信息
		MemberEntity currentMember = this.memberService.getCurrent();
		model.addAttribute("currentMember", currentMember);
		if (currentMember != null) {
			Map ms = new HashMap();
			ms.put("member", this.memberService.getCurrent());
			ms.put("state", Integer.valueOf(0));
			ms.put("type", Integer.valueOf(1));
			model.addAttribute("Sysmessage", messageService.findPageOrSendList(null, ms));
			List productList = null;
			List subList = this.subscribeService.findList(new Filter[] { Filter.eq("memberEntity", currentMember) });
			List sequencers = new ArrayList();
			Map map = new HashMap();
			Pageable pageable = new Pageable(1, 6);
			if (subList.size() > 0) {
				map.put("proList", subList);
			}
			if ((currentMember.getBrandName() != null) && (!currentMember.getBrandName().isEmpty())) {
				String[] proBrand = currentMember.getBrandName().split("、");
				if ((proBrand.length > 0) && (!proBrand[0].isEmpty())) {
					List filters = new ArrayList();
					filters.add(Filter.like("name", "%" + proBrand[0] + "%"));
					filters.add(Filter.eq("status", 1));
					filters.add(Filter.eq("isDel", 0));
					filters.add(Filter.eq("apprStatus", 1));
					ProductBrandEntity brandEntity = (ProductBrandEntity) this.proBrandService.find(filters);
					map.put("productBrandEntity", brandEntity);
					map.put("isDel", 0);
					map.put("apprStatus", 1);
					map.put("webzh", 1);
				}
			}
			productList = this.productService.findSubscriptInfo(pageable, map).getCont();
			sequencers.add(Sequencer.desc("lookTime"));
			model.addAttribute("items", productList);

			model.addAttribute("friends", this.friendsService.findAll());

			model.addAttribute("helps", this.helpService.findAll());

			model.addAttribute("proType", this.producttypeService.findAll());

			String proLink = request.getSession().getServletContext().getRealPath("/");
			model.addAttribute("proLink", proLink);
			return TEMPLATE_PATH;
		} else {
			return "redirect:/login.jhtml";
		}
	}

	/** 跳转显示产品分类 */
	@RequestMapping("/toProductClass.jspx")
	public String toProduct(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "front/subscribe/productClass";
	}

	// 获取树形产品网络图 -->产品类别
	@ResponseBody
	@RequestMapping(value = "getProClassData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getProClassData(HttpServletRequest request, HttpServletResponse response, Model model) {
		ProductclassEntity topNode = productclassService.find(1L);
		return getNodeData(topNode);
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
            map.put("state", "closed");
			map.put("tree", "productClassTree");
			List<ProductclassEntity> children = productclassService.findList(Filter.eq("parentid", item.getMenuid()));
			if (children.size() != 0) {
				map.put("children", getNodeData(item));
			}else{
                map.put("state", "open");
            }
			resultList.add(map);
		}
		return resultList;
	}
	
	/*跳转树形网络图*/

	/**
	 *
	 * @param proType  12项服务里面某一个标识id
	 * @param model
	 * @return
	 */
	@RequestMapping("/toProClass.jspx")
	public String toProClass(String proType, Model model) {
		model.addAttribute("proType", proType);
		return "front/userCenter/dataManage/subscribe/user_subscription";
		
	}

	/** 跳转显示仪器分类 */
	@RequestMapping("/toIndustryClass.jspx")
	public String toIndustryClass(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "front/subscribe/industryClass";
	}

	// 获取树形行业网络图 --> 行业分类
	@ResponseBody
	@RequestMapping(value = "getIndClassData.json", method = { RequestMethod.POST, RequestMethod.GET })
	public List<Map> getIndClassData(HttpServletRequest request, HttpServletResponse response, Model model) {
        IndustryClassEntity topNode =  industryClassService.find(1l);
        return getNodeData2(topNode);
	}
    //递归查找树形结构信息
    public List<Map> getNodeData2(IndustryClassEntity topNode){
        List<Map> resultList = new ArrayList<Map>();
        List<Filter> flist = new ArrayList<Filter>();
        flist.add(Filter.eq("parentid", topNode.getId()));
        List<IndustryClassEntity> items = industryClassService.findList(flist, Sequencer.asc("sort"));
        for(IndustryClassEntity item : items){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id", item.getId());
            map.put("text", item.getName());
            map.put("state", "closed");
			map.put("tree", "industryClassTree");
            List<IndustryClassEntity> children = industryClassService.findList(Filter.eq("parentid", item.getId()));
            if(children.size() != 0){
                map.put("children", getNodeData2(item));
            }else{
                map.put("state", "open");
            }
            resultList.add(map);
        }
        return resultList;
    }


	/** 查找仪器信息 */
	@ResponseBody
	@RequestMapping("/seachProInfo.jspx")
	public List<Map> searchPro(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<ProductEntity> proList = null;
		String keyword = ReqUtil.getString(request, "keyword", "");
		/*List<ProductBrandEntity> brandList = productBrandService.findList(Filter.like("name", "%" + keyword + "%"));
		List<Filter> brandFilter = new ArrayList<>();
		brandFilter.add(Filter.in("productBrandEntity", brandList));*/
		//List<Filter> proNoFilter = new ArrayList<>();
		//proNoFilter.add(Filter.like("proNo", "%" + keyword + "%"));
		//List<Filter> filters = new ArrayList<Filter>();
		/*filters.add(Filter.like("proName", "%" + keyword + "%").or(brandFilter).or(proNoFilter));*/
		//filters.add(Filter.like("proName", "%" + keyword + "%"));
		proList = productService.searchList(keyword);
		
		List<Map> mapList = new ArrayList<Map>();
		for (ProductEntity proentity : proList) {
			String text="";
			Map map = new HashMap();
			map.put("id", proentity.getId());
			if(proentity.getProductBrandEntity()!=null) {
				text+=proentity.getProductBrandEntity().getName()+" ";
			}
			if(proentity.getProNo()!=null) {
				text+=proentity.getProNo()+" ";
			}
			map.put("text",text
					+ proentity.getProName());
			mapList.add(map);
		}
		return mapList;
	}

	/** 提交保存订阅 */
	@RequestMapping(value = "/addsubscribe.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Message addsubscribe(HttpServletRequest request, HttpServletResponse response) {
        try {
            //默认订阅销售类型
            String type = ReqUtil.getString(request, "type", "1");
            //要取消订阅的订阅表id数组
            String[] del = ReqUtil.getParams(request, "del");
            String[] addProductClassIds = ReqUtil.getParams(request, "addProductClassIds");
            String[] addIndustryClassIds = ReqUtil.getParams(request, "addIndustryClassIds");
            if(del!=null&&del.length>0){//退订的集合
                for (String id : del) {
                    subscribeService.deleteList(Long.parseLong(id));
                }
            }
            MemberEntity currentMember = memberService.getCurrent();
            //查询该用户以前是否订阅   获取以前的订阅频率
            List<ProductSubscribeEntity> currentMemberSubscribe = subscribeService.findList(Filter.eq("memberEntity", currentMember),Filter.eq("isDelete",0), Filter.eq("proType",type));
            if(currentMemberSubscribe==null){
            	currentMemberSubscribe=new ArrayList<>();
			}
            ProductclassEntity productclassEntity=new ProductclassEntity();
            if(addProductClassIds!=null&&addProductClassIds.length>0){//添加的仪器集合
				List<ProductSubscribeEntity> list = subscribeService.findList(Filter.eq("memberEntity", currentMember),Filter.eq("isDelete",1), Filter.eq("proType",type),Filter.isNotNull("proClassEntity"));
                continueOut:for (String productClassId : addProductClassIds) {
                    //先看以前是否订阅过这个分类   有的话把is_delete设置为0
                    productclassEntity.setId(Long.parseLong(productClassId));
					if(list!=null&&list.size()>0){
                        for (ProductSubscribeEntity productSubscribeEntity : list) {//表示以前订阅过这个
                            if(productSubscribeEntity.getProClassEntity().getId().equals(productclassEntity.getId())){
                                productSubscribeEntity.setIsDelete(0);
                                subscribeService.update(productSubscribeEntity);
                                continue continueOut;
                            }
                        }
                    }
                    ProductSubscribeEntity productSubscribeEntity=new ProductSubscribeEntity();
                    productSubscribeEntity.setSubscribeTerm(currentMemberSubscribe.size()==0?"1234":currentMemberSubscribe.get(0).getSubscribeTerm());
                    productSubscribeEntity.setProType(type);
                    productSubscribeEntity.setMemberEntity(currentMember);
					productSubscribeEntity.setSubscribeMobile(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeMobile());
					productSubscribeEntity.setSubscribeEmail(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeEmail());
                    productSubscribeEntity.setProClassEntity(productclassService.find(Long.parseLong(productClassId)));
                    subscribeService.save(productSubscribeEntity);
                }
            }
            IndustryClassEntity industryClassEntity=new IndustryClassEntity();
            if(addIndustryClassIds!=null&&addIndustryClassIds.length>0){//添加的仪器集合
				List<ProductSubscribeEntity> list = subscribeService.findList(Filter.eq("isDelete",1),Filter.eq("memberEntity", currentMember), Filter.isNotNull("indClassEntity"), Filter.eq("proType",type));
                continueOut:for (String indstryClassId : addIndustryClassIds) {
                    //先看以前是否订阅过这个分类   有的话把is_delete设置为0
                    industryClassEntity.setId(Long.parseLong(indstryClassId));
					if(list!=null&&list.size()>0){
						for (ProductSubscribeEntity productSubscribeEntity : list) {//表示以前订阅过这个
							if(productSubscribeEntity.getIndClassEntity().getId().equals(industryClassEntity.getId())){
								productSubscribeEntity.setIsDelete(0);
								subscribeService.update(productSubscribeEntity);
								continue continueOut;
							}
						}
					}
                    ProductSubscribeEntity productSubscribeEntity=new ProductSubscribeEntity();
                    productSubscribeEntity.setProType(type);
					productSubscribeEntity.setSubscribeTerm(currentMemberSubscribe.size()==0?"1234":currentMemberSubscribe.get(0).getSubscribeTerm());
                    productSubscribeEntity.setMemberEntity(currentMember);
					productSubscribeEntity.setSubscribeMobile(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeMobile());
					productSubscribeEntity.setSubscribeEmail(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeEmail());
                    productSubscribeEntity.setIndClassEntity(industryClassService.find(Long.parseLong(indstryClassId)));
                    subscribeService.save(productSubscribeEntity);
                }
            }
			String[] brandAndModels = ReqUtil.getParams(request, "brandAndModel");
            //相同的服务类型只能订阅一个 查询当前服务类型下已经订阅的条件
			List<ProductSubscribeEntity> list = subscribeService.findList(Filter.eq("memberEntity", currentMember), Filter.eq("proType",type), Filter.isNotNull("productBrandEntity"));
			for (String brandAndModel : brandAndModels) {
				List<Map> maps = JSONObject.parseArray(brandAndModel, Map.class);
				Iterator<Map> iterator = maps.iterator();
                continueOut:while (iterator.hasNext()){
					Map next = iterator.next();
					ProductBrandEntity productBrandEntity=new ProductBrandEntity();
					productBrandEntity.setId(Long.parseLong(next.get("brandId").toString()));
					for (ProductSubscribeEntity productSubscribeEntity : list) {//订阅过这个 品牌型号
						if(productSubscribeEntity.getProductBrandEntity().getId().equals(productBrandEntity.getId())
								&&productSubscribeEntity.getModel().equals(next.get("model"))){
							productSubscribeEntity.setIsDelete(0);
							subscribeService.update(productSubscribeEntity);
							continue   continueOut;
						}
					}
					ProductSubscribeEntity productSubscribeEntity=new ProductSubscribeEntity();
					productSubscribeEntity.setSubscribeTerm(currentMemberSubscribe.size()==0?"1234":currentMemberSubscribe.get(0).getSubscribeTerm());
					productSubscribeEntity.setProType(type);
					productSubscribeEntity.setMemberEntity(currentMember);
					productSubscribeEntity.setSubscribeMobile(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeMobile());
					productSubscribeEntity.setSubscribeEmail(currentMemberSubscribe.size()==0?"2-1":currentMemberSubscribe.get(0).getSubscribeEmail());
					productSubscribeEntity.setProductBrandEntity(productBrandEntity);
					productSubscribeEntity.setModel(next.get("model").toString());
					subscribeService.save(productSubscribeEntity);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_MESSAGE;
        }
        return SUCCESS_MESSAGE;
	}

	@RequestMapping(value = "/deleteSubscribe.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Message deleteSubscribe(HttpServletRequest request, HttpServletResponse response, Model model,
			Long[] delId) {
		/*String proType = request.getParameter("proType");
		List<Filter> filters = new ArrayList<>();
        filters.add(Filter.eq("memberEntity", memberService.getCurrent()));
        filters.add(Filter.eq("proType", proType));
        List<ProductSubscribeEntity> list = subscribeService.findList(filters);*/
		if(delId!=null&&delId.length>0)
		//修改is_delete字段为1   代表删除
		subscribeService.deleteList(delId);
		return Message.success("取消订阅成功...");
	}

	public static void main(String[] args) {
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		String[] str1 = {"hongliang@scs-g.com.cn","itzhangpd@163.com","1123@163.com","113fe$@11.com","han. @sohu.com.cn","han.c@sohu.com.cn.cm.cm"};
		for (String str:str1){
			System.out.println(str+" \\\\. "+str.matches(regex));
		}
	}
	private String REGEX_EMAIL = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
	private String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$";
	@RequestMapping(value = "/updateInfo.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody Message updateInfo(HttpServletRequest request, HttpServletResponse response, Model model,
			String subscribeEmail, String subscribeMobile,Integer subscribePlatform, String emailSubscribe,
            String mobileSubscribe,boolean isNew,boolean isSecondHand,boolean isImport,boolean isChinese,Long type) {
		Long fistEmailRateChar =Long.parseLong( subscribeEmail.substring(0, 1));
		if (fistEmailRateChar == null || fistEmailRateChar >3 || fistEmailRateChar <0) {
			return Message.success("请选择正确的邮箱订阅频率");
		}
		Long fistMobileChar =Long.parseLong( subscribeEmail.substring(0, 1));
		if (fistMobileChar == null || fistMobileChar >3 || fistMobileChar <0) {
			return Message.success("请选择正确的手机订阅频率");
		}

		if (StringUtils.isNotBlank(emailSubscribe) && !Pattern.matches(REGEX_EMAIL, emailSubscribe)) {
			return Message.success("请输入正确的邮箱");
		}
		if (StringUtils.isNotBlank(mobileSubscribe)&& !Pattern.matches(REGEX_MOBILE, mobileSubscribe)) {
			return Message.success("请输入正确的手机号码");
		}
		String subscribeTerm="";
        if(isNew){//全新
            subscribeTerm+=1;
        }
        if(isSecondHand){//二手
            subscribeTerm+=2;
        }
        if(isImport){//进口
            subscribeTerm+=3;
        }
        if(isChinese){//国产
            subscribeTerm+=4;
        }
		MemberEntity current = memberService.getCurrent();
		current.setEmailSubscribe(emailSubscribe);
		current.setMobileSubscribe(mobileSubscribe);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("memberEntity", memberService.getCurrent()));
        filters.add(Filter.eq("proType", type));
		List<ProductSubscribeEntity> list = subscribeService.findList(filters);
		if (list.size() > 0) {
			for (ProductSubscribeEntity entity : list) {
				entity.setSubscribeEmail(subscribeEmail);
				entity.setSubscribeMobile(subscribeMobile);
               entity.setSubscribeTerm(subscribeTerm);
				subscribeService.update(entity);
			}
		}
		return Message.success("修改订阅信息成功...");
	}

    /**
     * @param proType
     * @return
     */
    //获取用户的订阅信息 订阅条件回显在前台
	@RequestMapping(value = "/getUserPro.jspx", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List getUserPro(String proType) throws IOException {
        List<Filter> filters = new ArrayList<>();
        filters.add(Filter.eq("memberEntity", memberService.getCurrent()));
        filters.add(Filter.eq("proType", proType));
        filters.add(Filter.eq("isDelete", 0));
        List<ProductSubscribeEntity> list = subscribeService.findList(filters);
        ArrayList<Object> objects = new ArrayList<>();
        for (ProductSubscribeEntity productSubscribeEntity : list) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",productSubscribeEntity.getId());
            map.put("proType",productSubscribeEntity.getProType());
            if(productSubscribeEntity.getIndClassEntity()!= null){
                map.put("industryclassId",productSubscribeEntity.getIndClassEntity().getId());
                map.put("name", productSubscribeEntity.getIndClassEntity().getName());
            }
            if(productSubscribeEntity.getProClassEntity() != null){
                map.put("productclassId",productSubscribeEntity.getProClassEntity().getId());
                map.put("name", productSubscribeEntity.getProClassEntity().getName());
                map.put("productclassMenuid", productSubscribeEntity.getProClassEntity().getMenuid());
            }
            if(productSubscribeEntity.getProductBrandEntity()!=null){
				map.put("brandId",productSubscribeEntity.getProductBrandEntity().getId());
				map.put("brandName",productSubscribeEntity.getProductBrandEntity().getNameEn());
                map.put("textZh",productSubscribeEntity.getProductBrandEntity().getName());
				map.put("model",productSubscribeEntity.getModel());
                map.put("subscribeId",productSubscribeEntity.getId());
			}
            objects.add(map);
        }
        return objects;
    }                                       //[["01030103"]]
	public static ArrayList<String> StringToInt(String[] str) {//[["01030103","01030102","01030103","01030104","01030102","010301"]]
		List list=new ArrayList<>();
		String string=str[0].substring(3, str[0].length()-3);
		//String s=str.substring(3, str.length()-3);
		return null;
	}
	
	
	
}
