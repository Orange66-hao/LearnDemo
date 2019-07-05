package net.boocu.project.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.print.DocFlavor;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.tencent.a.a.a.a.e;

import net.boocu.framework.util.DateTimeUtil;
import net.boocu.project.entity.EmailRecordEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.MailSignatureEntity;
import net.boocu.project.entity.McContactsEntity;
import net.boocu.project.entity.MobileRecordEntity;
import net.boocu.project.entity.ModelCollectionEntity;
import net.boocu.project.entity.ProductBrandEntity;
import net.boocu.project.entity.ProductEntity;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductSubscribeEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.entity.ProducttypeEntity;
import net.boocu.project.entity.SuContactsEntity;
import net.boocu.project.entity.SuModelCollectionEntity;
import net.boocu.project.entity.SubscribeInfoEntity;
import net.boocu.project.service.EmailRecordService;
import net.boocu.project.service.IndustryClassService;
import net.boocu.project.service.MailSignatureService;
import net.boocu.project.service.McContactsService;
import net.boocu.project.service.MobileRecordService;
import net.boocu.project.service.ModelCollectionService;
import net.boocu.project.service.ProductBrandService;
import net.boocu.project.service.ProductService;
import net.boocu.project.service.ProductSubscribeService;
import net.boocu.project.service.ProductclassService;
import net.boocu.project.service.ProducttypeService;
import net.boocu.project.service.SuContactsService;
import net.boocu.project.service.SuModelCollectionService;
import net.boocu.project.service.SubscribeInfoService;
import net.boocu.web.Filter;
import net.boocu.web.Pageable;
import net.boocu.web.Sequencer;
import net.boocu.web.entity.MemberEntity;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.random.random;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.service.EMailService;
import net.boocu.web.service.MemberGradeService;
import net.boocu.web.service.MemberService;
import net.boocu.web.service.TemplateService;

public class QuartzSendMail {
	private org.apache.log4j.Logger logger= org.apache.log4j.Logger.getLogger(QuartzSendMail.class);
	@Resource(name = "productSubscribeServiceImpl")
	private ProductSubscribeService subscribeService;
	@Resource(name = "subscribeInfoServiceImpl")
	private SubscribeInfoService subInfoService;

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "memberServiceImpl")
	private MemberService memberService;

	@Resource(name = "producttypeServiceImpl")
	private ProducttypeService producttypeService;

	@Resource
	ProductclassService productclassService;

	@Resource
	IndustryClassService industryClassService;

	@Resource
	EMailService emailService;

	@Resource
	TemplateService templateService;

	@Resource
	EmailRecordService emaiRecordService;

	@Resource
	MailSignatureService mailSignatureService;

	@Resource
	private JdbcTemplate JdbcTemplate;

	@Resource
	private McContactsService mcContactsService;

	@Resource
	ModelCollectionService modelCollectionService;

	@Resource
	ProductBrandService productBrandService;

	@Resource
	MemberGradeService memberGradeService;

	@Resource
	SuModelCollectionService suModelCollectionService;

	@Resource
	SuContactsService suContactsService;

	@Resource
	MobileRecordService mobileRecordService;
	/**
	 * 仪联网前台用户注册后，邮件订阅发送方法
	 */
	//@Scheduled(cron="0/9 * * * * ?")//测试每9秒执行一次发邮件的函数
	//@Scheduled(cron = "0 * * * * ?")
	public void sendMail() {
		System.out.println(" --  前台用户订阅      执行Email.........");
		long currentTimeMillis = System.currentTimeMillis();
		//先根据当前时间筛选出需要推送的服务类型和用户id
        Date dt = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");//  "EEEE"代表星期
        String currSun = dateFormat.format(dt);// 获取当前时间是星期几
        LocalDateTime datetime = LocalDateTime.now();// 创建获取时间的对象
        int dayOfMonth = datetime.getDayOfMonth();// 获取当前时间的是当月的几号
        List<Map<String,Object>> list=new ArrayList<>();
        int week=-1;
        switch (currSun){
            case "星期一":
                week=1;
                break;
            case "星期二":
                week=2;
                break;
            case "星期三":
                week=3;
                break;
            case "星期四":
                week=4;
                break;
            case "星期五":
                week=5;
                break;
            case "星期六":
                week=6;
                break;
            case "星期日":
                week=7;
                break;
        }

        //当前正在订阅的类型id以及memberid集合
        list=subscribeService.querySubscribeTypeAndMemberList(week,dayOfMonth);//周几？每个月第几天？


		if(list!=null&&list.size()>0){
            outContinue:for (Map<String, Object> map : list) {
			    //根据服务类型 和用户id查询条件集合
				List<ProductSubscribeEntity> currentMemebrSubscribeList= subscribeService.queryConditionList(map.get("pro_type").toString(),map.get("member_id").toString());
                if(currentMemebrSubscribeList!=null&&currentMemebrSubscribeList.size()>0){
                    Long typeId=Long.parseLong(currentMemebrSubscribeList.get(0).getProType());
                    ProducttypeEntity entity=new ProducttypeEntity();
                    entity.setId(typeId);
                    List<ProductEntity> productList=new ArrayList();
                    List<ProductSubscribeEntity> brandList=new ArrayList();
                    List<ProductSubscribeEntity> productClassList=new ArrayList();
                    List<ProductSubscribeEntity> industryClassList=new ArrayList();
                    for (ProductSubscribeEntity productSubscribeEntity : currentMemebrSubscribeList) {
                        //订的是品牌型号
                        if(productSubscribeEntity.getProductBrandEntity()!=null&&productSubscribeEntity.getModel()!=null){
                            brandList.add(productSubscribeEntity);
                        }
                        //订的是仪器分类
                        if(productSubscribeEntity.getProClassEntity()!=null){
                            productClassList.add(productSubscribeEntity);
                        }
                        //订的是产品分类
                        if(productSubscribeEntity.getIndClassEntity()!=null){
                            industryClassList.add(productSubscribeEntity);
                        }
                    }
                    if(brandList.size()>0){
                        for (ProductSubscribeEntity productSubscribeEntity : brandList) {
                            List filters=new ArrayList();
                            filters.add(Filter.eq("productType", producttypeService.find(Long.parseLong(productSubscribeEntity.getProType()))));
                            filters.add(Filter.eq("productBrandEntity",productSubscribeEntity.getProductBrandEntity()));
                            filters.add(Filter.eq("proNo",productSubscribeEntity.getModel(),true));

                            filters.add(Filter.eq("productType",entity));
                            //全新二手国产进口条件加进去
							if(productSubscribeEntity.getSubscribeTerm()!=null&&productSubscribeEntity.getSubscribeTerm().length()>0){
                                if(productSubscribeEntity.getSubscribeTerm().contains("12")){//全新二手都要

                                }else if(productSubscribeEntity.getSubscribeTerm().contains("1")){//只要全新的
                                    filters.add(Filter.eq("qualityStatus", ProductEntity.QualityStatusEnum.all));
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("2")){//只要二手的
                                    filters.add(Filter.ne("qualityStatus", ProductEntity.QualityStatusEnum.all));
                                }
                                if(productSubscribeEntity.getSubscribeTerm().contains("34")){//国产进口都要

                                }else if(productSubscribeEntity.getSubscribeTerm().contains("3")){//只要进口的
                                    filters.add(Filter.eq("isImport","0"));
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("4")){//只要国产的
                                    filters.add(Filter.ne("isImport","0"));
                                }
                            }
							//参数解释 first  count 查询第一条  一个
                            List<ProductEntity> p = productService.findList(0, 1,filters ,Sequencer.desc("modifyDate"));
                            if(p!=null&&p.size()>0){
                                productList.add(p.get(0));
                                if(productList.size()==10){
                                    boolean result = sendEmailAndSaveLog( productList, productSubscribeEntity.getMemberEntity());
                                    if(result){
                                        continue outContinue;
                                    }
                                }
                            }
                        }
                    }
                    //按照仪器分类订阅
                    if(productClassList.size()>0){
                        List filters=new ArrayList();
                        for (ProductSubscribeEntity productSubscribeEntity : productClassList) {
                            filters.clear();
                            //全新二手国产进口条件加进去
                            if(productSubscribeEntity.getSubscribeTerm()!=null&&productSubscribeEntity.getSubscribeTerm().length()>0){
                                if(productSubscribeEntity.getSubscribeTerm().contains("12")){//全新二手都要

                                }else if(productSubscribeEntity.getSubscribeTerm().contains("1")){//只要全新的
                                    filters.add(Filter.eq("qualityStatus", ProductEntity.QualityStatusEnum.all));
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("2")){//只要二手的
                                    filters.add(Filter.ne("qualityStatus", ProductEntity.QualityStatusEnum.all));
                                }
                                if(productSubscribeEntity.getSubscribeTerm().contains("34")){//国产进口都要

                                }else if(productSubscribeEntity.getSubscribeTerm().contains("3")){//只要进口的
                                    filters.add(Filter.eq("isImport","0"));
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("4")){//只要国产的
                                    filters.add(Filter.ne("isImport","0"));
                                }
                            }
                            filters.add(Filter.eq("productType",entity));
                            filters.add(Filter.eq("productclass", productSubscribeEntity.getProClassEntity()));
                            List<ProductEntity> p = productService.findList(0, 10 - productList.size(), filters,Sequencer.desc("modifyDate"));
                            if(p!=null&&p.size()>0){
                                for (ProductEntity productEntity : p) {
                                    productList.add(productEntity);
                                    if(productList.size()==10){
                                        boolean b = sendEmailAndSaveLog( productList, productSubscribeEntity.getMemberEntity());
                                        if(b){
                                            continue outContinue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //按照产品分类订阅  此处注意product实体表的产品分类为id拼接字符串 需要做切割
                    if(industryClassList.size()>0){
                        for (ProductSubscribeEntity productSubscribeEntity : industryClassList) {
                            IndustryClassEntity indClassEntity = productSubscribeEntity.getIndClassEntity();
                            //全新二手国产进口条件加进去
                            String c1="";
                            String c2="";
                            if(productSubscribeEntity.getSubscribeTerm()!=null&&productSubscribeEntity.getSubscribeTerm().length()>0){
                                if(productSubscribeEntity.getSubscribeTerm().contains("12")){//全新二手都要
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("1")){//只要全新的
                                    c1="1";
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("2")){//只要二手的
                                    c1="2";
                                }
                                if(productSubscribeEntity.getSubscribeTerm().contains("34")){//国产进口都要
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("3")){//只要进口的
                                    c2="3";
                                }else if(productSubscribeEntity.getSubscribeTerm().contains("4")){//只要国产的
                                    c2="4";
                                }
                            }
                            List<ProductEntity> p=productService.findByIndustryClass(10-productList.size(),indClassEntity.getId(),productSubscribeEntity.getProType(),c1,c2);
                            if(p!=null&&p.size()>0){
                                productList.addAll(p);
                            }
                        }
                    }
                    sendEmailAndSaveLog(productList,currentMemebrSubscribeList.get(0).getMemberEntity());
                }
            }
		}
		long endTime = System.currentTimeMillis();
		System.out.println("前台用户订阅邮件全部发送完毕,发送邮件耗时" + (endTime - currentTimeMillis) / 1000 + "秒");
	}


    private boolean sendEmailAndSaveLog(List<ProductEntity> productList, MemberEntity memberEntity) {
        try {
            if(productList.size()>0){
                sendEmail(memberEntity,productList);
                SaveSendMailLog(productList,memberEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return true;
        }
    }

    public void sendMobile() {
		System.out.println(" --  前台用户订阅      执行短信推送.........");
		long currentTimeMillis = System.currentTimeMillis();
		List<MemberEntity> memberList = new ArrayList<MemberEntity>();// 定义一个会员实体类MemberEntity的list数组
		try {
			memberList = subscribeService.findSubScribeAll1("mobile");// 查询出有手机订阅的且没有取消的用户集合
		} catch (Exception e) {
			System.out.println(e);
		}

		if (memberList.size() > 0) {// ?有订阅信息的客户
			for (MemberEntity memberEntity : memberList) {
				if (memberEntity.getMobile() != null || memberEntity.getMobileSubscribe() != null) {
					List<SubscribeInfoEntity> findList = subInfoService.findList(Filter.eq("memberEntity", memberEntity));
					List<ProductSubscribeEntity> findList2 = subscribeService.findList(Filter.eq("memberEntity", memberEntity));
					
					if(findList.size()>0&&findList2.size()>0) {
						if (findList2.get(0).getSubscribeMobile().equals("1")) {
							saveLog(memberEntity);
						}
						Date dt = new Date();
						SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");// "EEEE"代表星期
						String currSun = dateFormat.format(dt);// 获取当前时间是星期几
						if (findList2.get(0).getSubscribeMobile().substring(0,1).equals("2") && currSun.equals("星期一")) {// 是星期一就发送
							saveLog(memberEntity);
						}

						LocalDateTime datetime = LocalDateTime.now();// 创建获取时间的对象
						int dayOfMonth = datetime.getDayOfMonth();// 获取当前时间的是当月的几号
						
						if (findList2.get(0).getSubscribeMobile().substring(0,1).equals("3") && dayOfMonth == 15) {// 每月一次并且是15号的时候
							saveLog(memberEntity);
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("发送短信耗时" + (endTime - currentTimeMillis) + "毫秒");
	}
	//保存手机发送日志
	public void saveLog(MemberEntity memberEntity) {
		String mobile=memberEntity.getMobileSubscribe()==null?memberEntity.getMobile():memberEntity.getMobileSubscribe();
		MobileRecordEntity log=new MobileRecordEntity();
		log.setUserName(memberEntity.getUsername());
		log.setMobile(mobile);
		log.setResult(true);
		//保存记录//发送短信
		try {
			Map rs = MyUtil.generateShortUrl("http://www.wl95.com/subscribeInfo/toMobileList.jspx?username="+memberEntity.getUsername()+"&password="+memberEntity.getPassword()+"");
			int object = (int) rs.get("Code");
			String url="";
			if(rs!=null&&object==0) {
				url=(String) rs.get("ShortUrl");
			}
			sendSMS(mobile,ConfigUtil.getConfig("text.subscribecontent").replace("urlStr", url).replace("username", memberEntity.getUsername()),"");
			//System.out.println("返回信息:"+ sendSMS(mobile,ConfigUtil.getConfig("text.subscribecontent").replace("urlStr", url).replace("username", memberEntity.getUsername()),""));
		} catch (Exception e) {
			e.printStackTrace();
			log.setResult(false);
			log.setDetails(e.getMessage());
			System.err.println("发送短信异常,请联系短信接口人员!");
		}
		mobileRecordService.save(log);
	}
	public static int sendSMS(String Mobile,String Content,String send_time) throws MalformedURLException, UnsupportedEncodingException {
		URL url = null;
		String CorpID=ConfigUtil.getConfig("text.account_id");//账户名
		
		String Pwd=ConfigUtil.getConfig("text.pwd");//密码
		String send_content=URLEncoder.encode(Content.replaceAll("<br/>", " "), "GBK");//发送内容
		url = new URL("https://mb345.com/ws/BatchSend2.aspx?CorpID="
				+ CorpID + "&Pwd=" + Pwd + "&Mobile=" + Mobile + "&Content="
				+ send_content + "&Cell=&SendTime=" + send_time);
		BufferedReader in = null;
		int inputLine = 0;
		try {
			//System.out.println("开始发送短信手机号码为 ："+Mobile);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			inputLine = new Integer(in.readLine()).intValue();
		} catch (Exception e) {
			System.out.println("网络异常,发送短信失败！");
			inputLine=-2;
		}
		//System.out.println("结束发送短信返回值：  "+inputLine);
		return inputLine;
	}
	public ProductEntity queryentity(Map<String, Object> map) {
		List<Filter> filtersbra = new ArrayList<Filter>();// 条件集合
		List<Sequencer> ilSequencersbra = new ArrayList<Sequencer>();
		Sequencer sequencerbra = Sequencer.desc("createDate");
		ilSequencersbra.add(sequencerbra);
		filtersbra.add(Filter.eq("id", map.get("brand_id")));
		List<ProductBrandEntity> infoListbra = productBrandService.findList(1, filtersbra, ilSequencersbra);

		ProductEntity pro = new ProductEntity();
		pro.setId((long) map.get("id"));
		pro.setProductBrandEntity(infoListbra.get(0));
		pro.setProName((String) map.get("pro_name"));
		pro.setProNo((String) map.get("pro_no"));
		pro.setPoption((String) map.get("poption"));
		pro.setModifyDate((Date) map.get("modify_date"));
		pro.setProStock((int) map.get("pro_stock"));
		pro.setCycle((String) map.get("cycle"));
		pro.setCycleUnit((DateTypeEnum) map.get("cycleUnit"));
		// pro.setCreateDate((Date)map.get("create_date"));
		// pro.setQualityStatus((String)quality_status);

		return pro;
	}


    //保存邮件发送日志

	public void SaveSendMailLog(List<ProductEntity> list, MemberEntity memberEntity) {
		for (ProductEntity productEntity : list) {
			SubscribeInfoEntity entity = new SubscribeInfoEntity();
			entity.setMemberEntity(memberEntity);
			entity.setProductEntity(productEntity);
			entity.setCreateDate(DateTimeUtil.getSystemDate(DateTimeUtil.FMT_yyyyMMddHHmm));
			subInfoService.save(entity);
		}
	}
	public void sendEmail(MemberEntity memberEntity, List<ProductEntity> list) {
		// 根据MailSignatureId查询邮件签名人信息
		MailSignatureEntity Signatur = mailSignatureService.find(memberEntity.getMailSignatureId());
		if (Signatur == null) {
			Signatur = mailSignatureService.find(1l);
		}
		EmailRecordEntity log = new EmailRecordEntity();// 存储记录 Map<String, Object>
		Map<String, Object> map = new HashMap<>();// 新建一个map集合

		map.put("Content", Signatur.getContent());// 联系地址和联系方式
		map.put("entity", memberEntity);
		map.put("priceType", memberEntity.getMemberGradeEntity().getName());//根据member的等级显示不同的价格
		map.put("proEntity", list);

		map.put("username", memberEntity.getUsername());
		map.put("password", memberEntity.getPassword());
		if(memberEntity.getMemberGradeEntity().getName().equals("国外客户")) {
			map.put("projectPath", "en.wl95.com"); // 在会员等级表获取对应的模板字段template,值有:
			map.put("siteName", "Test Equipment Subscription");
			map.put("title", "Haochen Test Equipment");
		}else {
			map.put("projectPath", "www.wl95.com"); // 在会员等级表获取对应的模板字段template,值有:
			map.put("siteName", "晧辰仪联网订阅中心");
			map.put("title", "订阅邮件发送");
		}
		net.boocu.web.Template template = null;
		MemberGradeEntity memberGradeEntity = memberEntity.getMemberGradeEntity();

		if (memberGradeEntity == null) {
			memberGradeEntity = new MemberGradeEntity();
			memberGradeEntity.setTemplate("subMail1");
			template = templateService.get(memberGradeEntity.getTemplate());
		} else {
			template = templateService.get(memberEntity.getMemberGradeEntity().getTemplate());
		}
		try {
			if (StringUtils.isNotBlank(memberEntity.getEmailSubscribe())) {// 订阅邮箱不为空时
				emailService.send(memberEntity.getEmailSubscribe(), map.get("title").toString(), template.getTemplatePath(), map);
			} else if (StringUtils.isNotBlank(memberEntity.getEmail())) {// 订阅邮箱为空时
				emailService.send(memberEntity.getEmail(),  map.get("title").toString(), template.getTemplatePath(), map);
			} else {
                System.out.println("此用户两个邮箱都是空的");
            }
			log.setResult(true);
			log.setMember_id(memberEntity.getId());

        } catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
            log.setDetails(e.getMessage());
            log.setResult(false);
		}
		log.setCreateDate(new Date());// 设置创建的时间
		log.setUserName(memberEntity.getUsername());// 设置用户名
		emaiRecordService.save(log);// 保存到数据库 System.out.println("保存成功......"); }
	}

	public static void main(String[] args) throws MalformedURLException, UnsupportedEncodingException {
		Map<String,String> map=new HashMap<>();
		map.put("王海涛","13715327187");
		map.put("郭彦麟","13652394660");
		map.put("周艳波","15899870216");
		map.put("林婉珠","13631532292");
		map.put("吴小梅","13631536101");
		map.put("刘义红","13631536216");
		map.put("吴茂睿","18123986106");
		map.put("尹红丽","13652324600");
		map.put("古石林","18126447241");
		map.put("韦尚远","15914152356");
		map.put("张鹏","13631536386");
		map.put("李健","13602653900");
		map.put("涂美聪","18126449643");
		map.put("许天浩","13923814905");
		map.put("凌芦群","18123986107");
		map.put("王凌峰","18126443402");
		map.put("吴兰","13928471000");
		map.put("陈晓婷","15013646179");
		map.put("欧阳静","18123986105");
		map.put("谢婷","13631531063");
		map.put("邹鑫婷","13246680727");
		map.put("高良友", "13699887208");
		Map rs = MyUtil.generateShortUrl("http://m.wl95.com/subscribeInfo/toMobileList.jspx?username=13699887208&password=547ad5bd8766e40d2908b9072dcaf92b");
		int object = (int) rs.get("Code");
		String url="http://www.wl95.com";
		if(rs!=null&&object==0) {
			url=(String) rs.get("ShortUrl");
		}

		for (Map.Entry<String, String> entry : map.entrySet()) {
			  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            System.out.println("返回信息:"+ sendSMS(entry.getValue(),ConfigUtil.getConfig("text.startWork"),""));
		}
	}
}
