package net.boocu.project.entity;

import java.math.BigDecimal;
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
import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.web.entity.MemberGradeEntity;
import net.boocu.web.entity.MemberEntity.MemberShipEnum;
import net.boocu.web.entity.MemberEntity.UserTypeEnum;
import net.boocu.web.interceptor.MemberInterceptor;


/**
 * 商品求购
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="su_company")

@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_buy_sequence")
public class SuModelCollectionEntity extends BaseEntity {
	

	/**serialVersionUID*/
	private static final long serialVersionUID = 4966266941624071445L;
	 /*对应模板
	private String template;*/
	
	/**等级名称*/
	private String name;

	private String ids;

	/**行业分类*/
	private String su_industry_class;

	/**供应商性质*/
	/**
	 *1为进口生产厂商  2 为国产生产厂商 3 进口为代理商 4 为国产代理商 5 为进口经销商 6 为国产经销商 7 为二手同行 8 为系统集成商
	 */
	private String type;

	/**主营产品名称*/
	private String major_product;

	/**主营产品型号*/
	private String major_product_type;

	/**常用仪器名称*/
	private String su_productclass;

	/**常用仪器型号*/
	private String su_productclass_type;

	/**订阅内容*/
	private String subscribe_content;
	
	/**品牌*/
	private String su_brand; 
	
	/**型号*/
	private String su_model;

	/**常用仪器的品牌和型号*/
	private String su_brand_and_model;
	
	/**联系人*/
	private String contact;

	/**采购负责人*/
	private String blame_buy;

	/**跟单负责人*/
	private String blame;

	/**等级*/
	private String grade;

	/**计量时间*/
	private String measurement_time;

	/**创建人*/
	private String createuser; 
	
	/**修改人*/
	private String modifyuser; 
	
	/**邮件接收频率*/
	/**
	 *1为每天   2 为每周  3 为每月
	 */
	private String rate;

	/**
	 * 公司网站
	 */
	private String website;
	/**是否接邮件
	 *  0不接收      1 接受
	 * 
	 */
	private String isAcceptEmail; 
	
	
	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	
	public String getIsAcceptEmail() {
		return isAcceptEmail;
	}

	public void setIsAcceptEmail(String isAcceptEmail) {
		this.isAcceptEmail = isAcceptEmail;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSu_industry_class() {
		return su_industry_class;
	}

	public void setSu_industry_class(String su_industry_class) {
		this.su_industry_class = su_industry_class;
	}

	public String getMajor_product() {
		return major_product;
	}

	public void setMajor_product(String major_product) {
		this.major_product = major_product;
	}

	public String getSu_productclass() {
		return su_productclass;
	}

	public void setSu_productclass(String su_productclass) {
		this.su_productclass = su_productclass;
	}

	public String getSu_model() {
		return su_model;
	}

	public void setSu_model(String su_model) {
		this.su_model = su_model;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getBlame() {
		return blame;
	}

	public void setBlame(String blame) {
		this.blame = blame;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getModifyuser() {
		return modifyuser;
	}

	public void setModifyuser(String modifyuser) {
		this.modifyuser = modifyuser;
	}

	public String getSu_brand() {
		return su_brand;
	}

	public void setSu_brand(String su_brand) {
		this.su_brand = su_brand;
	}



	/*public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMajor_product_type() {
		return major_product_type;
	}

	public void setMajor_product_type(String major_product_type) {
		this.major_product_type = major_product_type;
	}

	public String getSu_productclass_type() {
		return su_productclass_type;
	}

	public void setSu_productclass_type(String su_productclass_type) {
		this.su_productclass_type = su_productclass_type;
	}

	public String getSubscribe_content() {
		return subscribe_content;
	}

	public void setSubscribe_content(String subscribe_content) {
		this.subscribe_content = subscribe_content;
	}

	public String getBlame_buy() {
		return blame_buy;
	}

	public void setBlame_buy(String blame_buy) {
		this.blame_buy = blame_buy;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getMeasurement_time() {
		return measurement_time;
	}

	public void setMeasurement_time(String measurement_time) {
		this.measurement_time = measurement_time;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getSu_brand_and_model() {
		return su_brand_and_model;
	}

	public void setSu_brand_and_model(String su_brand_and_model) {
		this.su_brand_and_model = su_brand_and_model;
	}
}
