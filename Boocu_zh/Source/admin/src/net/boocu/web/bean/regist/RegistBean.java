/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.regist;

import javax.validation.constraints.Pattern;

import net.boocu.web.entity.MemberEntity.MemberShipEnum;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 注册 20151012
 * 
 * @author fang
 * @version 1.0
 */
public class RegistBean {

    /** 邮箱地址 */
    private String email;

    /** 令牌代码 */
    private String code;

    /** 用户名 */
    private String username;
    
    /** 绑定第三方账号时用户名 */
    private String usernumber;
    
    /** 验证码ID */
    private String captchaId;

    /** 验证码 */
    private String captcha;

    /** 密码 */
    private String password;

    /** 手机号码 */
    private String mobile;
    
    /** 身份证号码 */
    private String identityNo;
    
    //常用仪器
    /** 品牌 */
    private String brandName; 
    /** 型号 */
    private String productClassName;
    /** 名称 */
    private String proName;
    
    /**主营产品*/
    private String mainProduct;
    
    /** 工作单位*/
    private String workPlace;
    
    /**职位*/
    private String wordPost;

    /**身份证正面*/
    private String identifyImgFront;
    /**身份证反面*/
    private String identifyImgBack;
    /**区号   */
    private String txtAre;
    /**电话   */
    private String txtTel;
	/**分机   */
    private String txtExt;
    /**qq*/
    private String qq;
    /**qq openid*/
    private String openid;
    /**微信号*/
    private String weixin;
    /**微博号*/
    private String weibo;
    
    /** 公司名称*/
    private String coName;
    
    /** 职务*/
    private String job;
    
    /** 联系人*/
    private String contacts;
    
    /** 联系人*/
    private String realName;
    
    /** 公司地址*/
    private String address;
    
    /** 详细地址*/
    private String deAddress;
    
    /** 营业执照号码*/
    private String licenseNo;
    
    /** 营业执照*/
    private String licenseImg;
    
    /** 公司传真*/
    private String fax;
    
    /** 公司网址*/
    private String url;
    
    /** 会员身份类型*/
    private MemberShipEnum memberShip;
    
    /** 备注*/
    private String remark;
    
    @NotBlank
    @Email
    @Length(max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = StringUtils.lowerCase(email);
    }

    @NotBlank
    @Length(max = 200)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NotBlank
    @Pattern(regexp = "^[0-9a-z_A-Z\\u4e00-\\u9fa5]+$")
    @Length(min = 5, max = 32)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.lowerCase(username);
    }

    @NotBlank
    @Pattern(regexp = "^[^\\s&\"<>]+$")
    @Length(min = 5, max = 20)
    public String getPassword() {
        return password;
    }

    public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public void setPassword(String password) {
        this.password = password;
    }
    
    @NotBlank
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getMobile() {
		return mobile;
	}
	
	@Pattern(regexp = "^1\\d{10,11}$")
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
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

	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
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

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUsernumber() {
		return usernumber;
	}

	public void setUsernumber(String usernumber) {
		this.usernumber = usernumber;
	}

	public String getCaptchaId() {
		return captchaId;
	}

	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}