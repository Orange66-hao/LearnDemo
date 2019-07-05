/**
 * 
 */
package net.boocu.project.controller.front;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.support.json.JSONUtils;

import net.boocu.framework.util.ReqUtil;
import net.boocu.framework.util.RespUtil;
import net.boocu.project.entity.AddressEntity;
import net.boocu.project.entity.Order;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ShopCart;
import net.boocu.project.service.AddressService;
import net.boocu.project.service.HelpService;
import net.boocu.project.service.MessageService;
import net.boocu.project.service.OrderService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ShopCartService;
import net.boocu.project.util.OrderUtils;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.SysConfigService;
import net.boocu.web.util.Constants;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/shopCart")
public class ShopCartController {
	
	private static final Logger logger = Logger.getLogger(ShopCartController.class);
	
	@Autowired
	private ShopCartService shopCartService;
	
    @Resource
    HelpService helpService;
    
    @Resource
    private SysConfigService sysConfigService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private SysConfigService configService;
    
    @Autowired
    private AddressService addrService;
    
    @Autowired
    private MemberService memberService;
    
    @Resource
    private ProductService productService;
    
    @Resource
    private ProductBrandService pbService;
    
	@Resource
	private MessageService messageService;
    
	private static final String TEMPATH = "/template/front/userCenter";
	
	@RequestMapping("/toShopCart.jspx")
	public String toShopCart(HttpServletRequest request, HttpServletResponse response,Model model)
	{
		int pagenumber = ReqUtil.getInt(request, "page", 0);
		int rows = ReqUtil.getInt(request, "rows", 10);
		
		String proName = ReqUtil.getString(request, "txtProName", "");
		long selOType = ReqUtil.getLong(request, "selOType", (long) 0);
		String selProNo = ReqUtil.getString(request, "selProNo", "0");
		
		int isFirst = ReqUtil.getInt(request, "isFirst", 0);
		
		Pageable pageable = new Pageable(pagenumber,rows);
		
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		
		Map<String,Object> parmas = new HashMap<String,Object>();
		if(isFirst != 1){
			parmas.put("proName", proName);
			parmas.put("selOType", selOType);
			parmas.put("selProNo", selProNo);
		}else{
			parmas.put("proName", "");
			parmas.put("selOType", 0);
			parmas.put("selProNo", 0);
		}
		parmas.put("userId", member==null?"0":member.getId());
		model.addAttribute("currentMember", member);
		model.addAttribute("proName", proName);
		model.addAttribute("selOType", selOType);
		model.addAttribute("selProNo", selProNo);
		model.addAttribute("isFirst", isFirst);
		model.addAttribute("helps",helpService.findAll());
		model.addAttribute("shopCart", shopCartService.findShopCartByUserId(pageable,parmas));
		Page<ShopCart> a=shopCartService.findShopCartByUserId(pageable,parmas);
		return TEMPATH+"/shopCart/shopCart";
	}
	
	@RequestMapping("/toSettle.jspx")
	public void toSettlePage(HttpServletRequest request,
			HttpServletResponse response,Model model){
		try {
			String ids = ReqUtil.getString(request, "ids", "");
			
			HttpSession session = request.getSession();
			
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			
			//把ids转换成数组
			String [] ints = ids.split(",");
			Order order = null;
			float sumPrice = 0f;
			
			List<ShopCart> shopCarts = new ArrayList<ShopCart>();
			for(int i=0;i<ints.length;i++){
				ShopCart sc = shopCartService.findById(Long.parseLong(ints[i]));
				sumPrice += sc.getPrice()*sc.getCount();
				order = new Order();
				MemberEntity buyMember = memberService.find(sc.getMemberEntity().getId());
				order.setBuyMember(buyMember);
				
				MemberEntity sellMember = memberService.find(sc.getProductEntity().getMemberEntity().getId());
				order.setSellMember(sellMember);
				shopCarts.add(sc);
			}
			//订单编号
			OrderUtils ou = new OrderUtils();
			order.setOrderNumber(ou.genOrderNumber());
			
			if(member != null){
				//收货地址
				AddressEntity addr = addrService.findDefAddr(member.getId());
				if(addr.getId() != null){
					AddressEntity ae = addrService.findById((long) addr.getId());
					if(ae != null){
						order.setDeliveryAddress(ae.getCountry()+" "+ae.getProvince()+" "+ae.getCity()+" "+ae.getDetail());
						order.setAddressEntity(ae);
					}
				}
			}
			
			//订单总价低于300不包邮
			sumPrice = new BigDecimal(sumPrice).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if(sumPrice < 300){
				int freight = Integer.parseInt(configService.getValByKey(Constants.FREIGHTKEY));
				sumPrice += freight;
				order.setFreight(freight);
			}
			order.setSumPrice(sumPrice);
			Long orderId = orderService.addOrder(order, shopCarts);
			//同时删除购物车的内容
			shopCartService.batchDeleteSC(ids);
			
			response.getWriter().write(orderId.toString());
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 计算价格
	 * @param me
	 * @param pe
	 * @return
	 */
	private BigDecimal checkPrice(MemberEntity me,ProductEntity pe){
		String memberGrade = me.getMemberGradeEntity().getPriceType();
		
		BigDecimal price = null;
		
		BigDecimal million = new BigDecimal(10000);

		String val  = sysConfigService.getValByKey(Constants.DOLLERCHRMBKEY);
		//获取美元转换成人民币的汇率
		BigDecimal CHRATE = new BigDecimal((val != null && !"".equals(val))?val:"0");
		
		switch (memberGrade) {
		case "客户":
			if(pe.getProMemberPrice().compareTo(BigDecimal.ZERO) > 0){
				//计算客户价格
				if(pe.getProMemberPriceType().ordinal() == 1){
					//将美元转换成人民币
					price = pe.getProMemberPrice().multiply(CHRATE);
					if(pe.getProMemberPriceLimit().ordinal() == 1){
						//将万元换成元
						return price.multiply(million);
					}else{
						return price;
					}
				}else{
					if(pe.getProMemberPriceLimit().ordinal() == 1){
						//将万元换成元
						return pe.getProMemberPrice().multiply(million);
					}else{
						return pe.getProMemberPrice();
					}
				}
			}
			break;
			
		case "同行":
			if(pe.getProPeer().compareTo(BigDecimal.ZERO) > 0){
				//计算同行价格
				if(pe.getProPeerType().ordinal() == 1){
					//将美元转换成人民币
					price = pe.getProPeer().multiply(CHRATE);
					if(pe.getProPeerLimit().ordinal() == 1){
						//将万元换成元
						price = price.multiply(million);
					}else{
						return price;
					}
				}else{
					if(pe.getProPeerLimit().ordinal() == 1){
						//将万元换成元
						return pe.getProPeer().multiply(million);
					}else{
						return pe.getProPeer();
					}
				}
			}
			break;
			
		case "VIP":
			if(pe.getProVipPrice().compareTo(BigDecimal.ZERO) > 0){
				//计算VIP价格
				if(pe.getProVipPriceType().ordinal() == 1){
					//将美元转换成人民币
					price = pe.getProVipPrice().multiply(CHRATE);
					if(pe.getProVipPriceLimit().ordinal() == 1){
						//将万元换成元
						return price.multiply(million);
					}else{
						return price;
					}
				}else{
					if(pe.getProVipPriceLimit().ordinal() == 1){
						//将万元换成元
						return pe.getProVipPrice().multiply(million);
					}else{
						return pe.getProVipPrice();
					}
				}
			}
			break;
			
		case "来宾":
			if(pe.getProCustomPrice().compareTo(BigDecimal.ZERO) > 0){
				//计算同行价格
				if(pe.getProCustomPriceType().ordinal() == 1){
					//将美元转换成人民币
					price = pe.getProCustomPrice().multiply(CHRATE);
					if(pe.getProCustomPriceLimit().ordinal() == 1){
						//将万元换成元
						return price.multiply(million);
					}else{
						return price;
					}
				}else{
					if(pe.getProCustomPriceLimit().ordinal() == 1){
						//将万元换成元
						return pe.getProCustomPrice().multiply(million);
					}else{
						return pe.getProCustomPrice();
					}
				}
			}
			break;

		default:
			break;
		}
		return null;
	}
	
	@RequestMapping("/addShopCart.jspx")
	public void addShopCart(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		try {
			String msg = "err";
			MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
			//when user was logined,insert product to cart,else ,add product to cookie ,when user login execute this method
			if(member != null){
				Long proId = ReqUtil.getLong(request, "proId", (long)0);
				String proOriginal1=request.getParameter("proOriginal1");
				String price = ReqUtil.getString(request, "price", "");
				String url = ReqUtil.getString(request, "url", "");
				int from = ReqUtil.getInt(request, "from", 0);
				
				ProductEntity product = productService.find(proId);
				
				BigDecimal newPrice =  (BigDecimal) checkPrice(member,product);
				
				BigDecimal resultP = null;
				
				if(from != 1){
					//询价单
					price = price.split("元")[0].toString();
					
					if(price.indexOf("万") > 0){
						String str = price.split("万")[0].toString();
						resultP = new BigDecimal(str).multiply(new BigDecimal(10000));
					}else{
						resultP = new BigDecimal(price);
					}
					logger.info("resultP:"+resultP +"  newPrice:"+newPrice+"  result:"+(resultP.floatValue()==newPrice.floatValue()));
				}
				
				if(from != 1 && resultP.floatValue()!=newPrice.floatValue()){
					//页面的价格和后台计算的价格不一致,可能是价格被篡改,抛出异常
					//throw new RuntimeException("页面的价格和计算出来不一致，可能是价格被篡改。");
					msg = "priceEx";
				}else{
					//重复添加商品
					if(shopCartService.getProductByUserIdAndProductId(member.getId(), proId) > 0){
						//商品数量+1
						if(shopCartService.addCountOne(member.getId(), proId) > 0)
							msg = "ok";
					}else{
						ShopCart sc = new ShopCart();
						ProductEntity pe = new ProductEntity();
						pe.setId(proId);
						pe.setProOriginal1(proOriginal1);
						sc.setProductEntity(pe);
						sc.setUrl(url);
						sc.setMemberEntity(member);
						if(from == 1){
							//从询价单加入购物车，商品价格直接取优惠价
							sc.setPrice(Float.parseFloat(price));
						}else{
							sc.setPrice(newPrice.floatValue());
						}
						if(shopCartService.insertSC(sc) > 0)
							msg = "ok"; 
					}
				}
			}else{
				//user not login
				msg = "unlogin";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/delshopcart.jspx")
	public void delSC(HttpServletRequest request, HttpServletResponse response){
		Long id = ReqUtil.getLong(request, "id", (long)0);
		String msg = "err";
		try {
			if(shopCartService.deleteSC(id) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/batchDel.jspx")
	public void batchDel(HttpServletRequest request, HttpServletResponse response){
		String ids = ReqUtil.getString(request, "ids", "");
		String msg = "err";
		try {
			if(shopCartService.batchDeleteSC(ids) > 0){
				msg = "ok";
			}
			response.getWriter().write(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/barndLists.jspx")
	public void findAllBrand(HttpServletRequest request, HttpServletResponse response){
		List<ProductBrandEntity> brandLists = pbService.findAll();
		
		List<Map> resultList = new ArrayList<Map>();
		for (ProductBrandEntity item : brandLists) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", item.getId());
			map.put("text",item.getName());
			resultList.add(map);
		}
		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}
	
	@RequestMapping("/proNoLists.jspx")
	public void findAllProNo(HttpServletRequest request, HttpServletResponse response){
		
		HttpSession session = request.getSession();
		
		MemberEntity member = (MemberEntity) session.getAttribute("loginUser");
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map> resultList = null;
		if(member != null){
			params.put("userId", member.getId());
			List<ShopCart> psLists = shopCartService.shopCartList(params);
			
			resultList = new ArrayList<Map>();
			for (ShopCart item : psLists) {
				if(item.getProductEntity() != null){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", item.getProductEntity().getProNo());
					map.put("text",item.getProductEntity().getProNo());
					resultList.add(map);
				}
			}
		}

		RespUtil.renderJson(response, JSONUtils.toJSONString(resultList));
	}
	
	@RequestMapping("/getCookie.jspx")
	public void getCookie(HttpServletRequest request, HttpServletResponse response){
		Cookie [] cookie = request.getCookies();
		for(Cookie c : cookie){
			System.out.println(c.getName()+": "+c.getValue());
		}
	}
}
