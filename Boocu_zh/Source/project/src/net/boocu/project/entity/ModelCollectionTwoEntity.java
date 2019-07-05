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
@Table(name="mc_company_two")

@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_buy_sequence")
public class ModelCollectionTwoEntity extends BaseEntity {
	

	/**serialVersionUID*/
	private static final long serialVersionUID = 4966266941624071445L;
	 /*对应模板
	private String template;*/
	
	/**等级名称*/
	private String name;
	
	
	private String ids;
	
	/**行业分类*/
	private String mc_industry_class; 
	
	/**主营产品*/
	private String major_product; 
	
	/**常用仪器*/
	private String mc_productclass; 
	
	/**品牌*/
	private String mc_brand; 
	
	/**型号*/
	private String mc_model; 
	
	/**联系人*/
	private String contact; 
	
	/**负责人*/
	private String blame; 
	
	/**创建人*/
	private String createuser; 
	
	/**修改人*/
	private String modifyuser; 

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

	public String getMc_industry_class() {
		return mc_industry_class;
	}

	public void setMc_industry_class(String mc_industry_class) {
		this.mc_industry_class = mc_industry_class;
	}

	public String getMajor_product() {
		return major_product;
	}

	public void setMajor_product(String major_product) {
		this.major_product = major_product;
	}

	public String getMc_productclass() {
		return mc_productclass;
	}

	public void setMc_productclass(String mc_productclass) {
		this.mc_productclass = mc_productclass;
	}

	public String getMc_model() {
		return mc_model;
	}

	public void setMc_model(String mc_model) {
		this.mc_model = mc_model;
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

	public String getMc_brand() {
		return mc_brand;
	}

	public void setMc_brand(String mc_brand) {
		this.mc_brand = mc_brand;
	}



	/*public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}*/
	
	
	
}
