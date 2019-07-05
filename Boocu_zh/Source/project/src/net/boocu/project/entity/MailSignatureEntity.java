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
@Table(name="sys_signature_member")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_buy_sequence")
public class MailSignatureEntity extends BaseEntity {
	

	/**serialVersionUID*/
	private static final long serialVersionUID = 4966266941624071445L;
	 /*对应模板
	private String template;*/
	/**等级名称*/
	private String name;//数据库是公司名称:晧辰仪联网
	
	/**联系地址和联系方式*/
	private String content;
	/*
		数据库是以下信息:
		地址：深圳市南山区西丽街道牛成路221号8楼
		电话：0755－86016691
		邮箱：info@wl95.com
		传真：0755-86641139-816
		邮编：518055
	*/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/*public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}*/
	
	
	
}
