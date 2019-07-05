package net.boocu.project.entity;

import net.boocu.framework.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * 行业分类
 * author fang
 * 
 * 20190214
 * version 1.0
 * 
 * */



@Entity
@Table(name="mc_brand_and_model")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_industry_class_sequence")
public class McBrandAndModelEntity extends BaseEntity {

	/**
	 * serialVersion
	 */
	private static final long serialVersionUID = -4076239474595799651L;
	/**品牌名称*/
	private String mc_model;
	/**名称*/
	private String name_en;
	/**品牌表的品牌id*/
	private String mc_brand;
	/**仪器数量*/
	private String mc_productclass_number;
	/**仪器名称*/
	private String mc_productclass_name;
	/**仪器的所属公司*/
	private  String mc_company;
	/**备注*/
	private String remark;
	/**公司类型，1代表客户公司或高校名称，2代表供应商公司名*/
	private Integer company_type;

	public String getMc_model() {
		return mc_model;
	}

	public void setMc_model(String mc_model) {
		this.mc_model = mc_model;
	}

	public String getName_en() {
		return name_en;
	}
	public void setName_en(String name_en) {
		this.name_en = name_en;
	}
	
	public String getMc_brand() {
		return mc_brand;
	}
	public void setMc_brand(String mc_brand) {
		this.mc_brand = mc_brand;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMc_productclass_number() {
		return mc_productclass_number;
	}

	public void setMc_productclass_number(String mc_productclass_number) {
		this.mc_productclass_number = mc_productclass_number;
	}

	public String getMc_productclass_name() {
		return mc_productclass_name;
	}

	public void setMc_productclass_name(String mc_productclass_name) {
		this.mc_productclass_name = mc_productclass_name;
	}

	public String getMc_company() {
		return mc_company;
	}

	public void setMc_company(String mc_company) {
		this.mc_company = mc_company;
	}

	public Integer getCompany_type() {
		return company_type;
	}

	public void setCompany_type(Integer company_type) {
		this.company_type = company_type;
	}
}
