package net.boocu.web.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import freemarker.template.Template;
import net.boocu.framework.entity.BaseEntity;


/**
 * 会员等级
 * author deng
 * 
 * 20151029
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_memberGrade")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_memberGrade_sequence")
public class MemberGradeEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 4966266941624071445L;
	/** 对应模板*/
	private String template;
	/**等级名称*/
	private String name;
	
	
	/**价格类型*/
	private String priceType; 


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}


}
