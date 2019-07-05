/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.AddressEntity;
import net.boocu.web.interceptor.MemberInterceptor;

/**
 * Entity - 会员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Entity
@Table(name = "sys_member")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_member_sequence")
public class MemberEntity extends BaseEntity {
	/** 出版商用户 */
	public static final int TYPE_VENDER = 1;

	public static final int TYPE_BUYER = 0;

	/** serialVersionUID */
	private static final long serialVersionUID = 3398058230264615523L;

	/** “身份信息”参数名称 */
	public static final String PRINCIPAL_ATTR_NAME = MemberInterceptor.class.getName() + ".PRINCIPAL";

	/** “密钥”参数 */
	public static final String KEY_ATTR = "P2PKey";

	/** “用户名”Cookie名称 */
	public static final String USERNAME_COOKIE_NAME = "BoocuUsername";

	/** “自动登录”Cookie名称 */
	public static final String AUTOLOGIN_COOKIE_NAME = "AutoToken";

	/** “自动登录”Cookies有效时间15天 */
	public static final Integer AUTOLOGIN_COOKIE_TIME = 1296000;

	public enum MemberShipEnum {
		personal, enterprise;
	}

	public enum UserTypeEnum {
		buyer, seller, business;
	}

	/** 用户等级 */
	private MemberGradeEntity memberGradeEntity;

	private AddressEntity addressEntity;

	/** 邮箱地址 */
	private String email;
	
	/** 接收邮件的邮箱地址 */
	private String emailSubscribe;

	/** 用户名 */
	private String username;

	/** 密码 */
	private String password;

	/** 会员身份 0代表个人,1代表企业, */
	private MemberShipEnum memberShip;

	/** 用户类型 */
	private UserTypeEnum userType;

	/** 真实姓名 */
	private String realName;

	/** 手机号码 */
	private String mobile;
	
	/** 订阅的手机号码 */
	private String mobileSubscribe;

	/** 身份证号码 */
	private String identityNo;

	// 常用仪器
	/** 名称 */
	private String proName;
	/** 品牌 */
	private String brandName;
	/** 型号 */
	private String productClassName;

	/** 主营产品 */
	private String mainProduct;

	/** 职位 */
	private String wordPost;

	/** 身份证正面 */
	private String identifyImgFront;
	/** 身份证反面 */
	private String identifyImgBack;

	/** 区号 */
	private String txtAre;
	/** 电话 */
	private String txtTel;
	/** 分机 */
	private String txtExt;

	/** qq */
	private String qq;
	/** qq openid*/
	private String openid;
	/** 微信 openid*/
	private String wxopenid;
	/** 微信号 */
	private String weixin;
	/** 微博号 */
	private String weibo;

	/** 账号 激活与否 0:被禁用 1:未激活 2:未审核 3:审核 */
	private int enabled;
	
	/** 账号 是否为会员邀请 否为0  ； 是为1 */
	private int invite;
	
	/** 账号 是否为会员邀请 是否为第一次登陆 是为1，不是为0 */
	private int first;

	/** 是否锁定 */
	private Boolean locked = false;

	/** 连续登录失败次数 */
	private Integer loginFailureCount = 0;

	/** 锁定日期 */
	private Date lockedDate;

	/** 注册IP */
	private String registIp;

	/** 最后登录IP */
	private String loginIp;

	/** 最后登录日期 */
	private Date loginDate;

	/** 公司名称 */
	private String coName;

	/** 联系人 */
	private String contacts;

	/** 公司地址 */
	private String address;

	/** 详细地址 */
	private String deAddress;

	/** 营业执照号码 */
	private String licenseNo;

	/** 营业执照 */
	private String licenseImg;

	/** 公司传真 */
	private String fax;

	/** 公司网址 */
	private String url;

	/** 公司编号 */
	private String number;

	/** 邮编 */
	private String code;
	
	//邮件签名
	private long mailSignatureId=1;
	
	//邀请人
	private long blame;

	/** 备注 */
	private String remark;

	/** 公司省 */
	private String comProvince;
	/** 公司市 */
	private String comCity;
	/** 公司区 */
	private String comArea;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_del_addr")
	@NotFound(action = NotFoundAction.IGNORE)
	public AddressEntity getAddressEntity() {
		return addressEntity;
	}

	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}


	public long getBlame() {
		return blame;
	}

	public void setBlame(long blame) {
		this.blame = blame;
	}

	public long getMailSignatureId() {
		return mailSignatureId;
	}

	public void setMailSignatureId(long mailSignatureId) {
		this.mailSignatureId = mailSignatureId;
	}

	public String getComProvince() {
		return comProvince;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public void setComProvince(String comProvince) {
		this.comProvince = comProvince;
	}

	public String getComCity() {
		return comCity;
	}

	public void setComCity(String comCity) {
		this.comCity = comCity;
	}

	public String getComArea() {
		return comArea;
	}

	public void setComArea(String comArea) {
		this.comArea = comArea;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getInvite() {
		return invite;
	}

	public void setInvite(int invite) {
		this.invite = invite;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getEmailSubscribe() {
		return emailSubscribe;
	}

	public void setEmailSubscribe(String emailSubscribe) {
		this.emailSubscribe = emailSubscribe;
	}

	public String getMobileSubscribe() {
		return mobileSubscribe;
	}

	public void setMobileSubscribe(String mobileSubscribe) {
		this.mobileSubscribe = mobileSubscribe;
	}

	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getMainProduct() {
		return mainProduct;
	}

	public void setMainProduct(String mainProduct) {
		this.mainProduct = mainProduct;
	}

	public String getWordPost() {
		return wordPost;
	}

	public void setWordPost(String wordPost) {
		this.wordPost = wordPost;
	}

	public String getIdentifyImgFront() {
		return identifyImgFront;
	}

	public void setIdentifyImgFront(String identifyImgFront) {
		this.identifyImgFront = identifyImgFront;
	}

	public String getIdentifyImgBack() {
		return identifyImgBack;
	}

	public void setIdentifyImgBack(String identifyImgBack) {
		this.identifyImgBack = identifyImgBack;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Integer getLoginFailureCount() {
		return loginFailureCount;
	}

	public void setLoginFailureCount(Integer loginFailureCount) {
		this.loginFailureCount = loginFailureCount;
	}

	public Date getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(Date lockedDate) {
		this.lockedDate = lockedDate;
	}

	public String getRegistIp() {
		return registIp;
	}

	public void setRegistIp(String registIp) {
		this.registIp = registIp;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDeAddress() {
		return deAddress;
	}

	public void setDeAddress(String deAddress) {
		this.deAddress = deAddress;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getLicenseImg() {
		return licenseImg;
	}

	public void setLicenseImg(String licenseImg) {
		this.licenseImg = licenseImg;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MemberShipEnum getMemberShip() {
		return memberShip;
	}

	public void setMemberShip(MemberShipEnum memberShip) {
		this.memberShip = memberShip;
	}

	/**
	 * 验证登录密码
	 * 
	 * @param password
	 *            密码
	 * @return 验证是否通过
	 */
	@Transient
	public boolean verifyLoginPassword(String password) {
		return StringUtils.equals(DigestUtils.md5Hex(password), getPassword());
	}

	public UserTypeEnum getUserType() {
		return userType;
	}

	public void setUserType(UserTypeEnum userType) {
		this.userType = userType;
	}

	@OneToOne
	@JoinColumn(name = "memberGrade_id")
	public MemberGradeEntity getMemberGradeEntity() {
		return memberGradeEntity;
	}

	public void setMemberGradeEntity(MemberGradeEntity memberGradeEntity) {
		this.memberGradeEntity = memberGradeEntity;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public String getProductClassName() {
		return productClassName;
	}

	public void setProductClassName(String productClassName) {
		this.productClassName = productClassName;
	}

	public String getTxtAre() {
		return txtAre;
	}

	public void setTxtAre(String txtAre) {
		this.txtAre = txtAre;
	}

	public String getTxtTel() {
		return txtTel;
	}

	public void setTxtTel(String txtTel) {
		this.txtTel = txtTel;
	}

	public String getTxtExt() {
		return txtExt;
	}

	public void setTxtExt(String txtExt) {
		this.txtExt = txtExt;
	}

	public String getWxopenid() {
		return wxopenid;
	}

	public void setWxopenid(String wxopenid) {
		this.wxopenid = wxopenid;
	}

}