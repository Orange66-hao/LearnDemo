
/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.boocu.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.boocu.framework.entity.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


/**
 * 行业分类
 * author zhang
 * 
 * 20181126
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_pBrandAndModel")
public class PbrandAndModelEntity extends BaseEntity {

    @JsonProperty
	/**型号*/
	private String model;

	/**备注*/
	private String remark;

	private String sort="1";

	private String brand;

	private ProductclassEntity productclassEntity;

	private  IndustryClassEntity industryClassEntity;



	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE}, optional = true)
	// JoinColumn表示外键的列
	@JoinColumn(name="pro_id")
	public ProductclassEntity getProductclassEntity() {
		return productclassEntity;
	}

	public void setProductclassEntity(ProductclassEntity productclassEntity) {
		this.productclassEntity = productclassEntity;
	}
	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE}, optional = true)
	// JoinColumn表示外键的列
	@JoinColumn(name="industry_id")
	public IndustryClassEntity getIndustryClassEntity() {
		return industryClassEntity;
	}

	public void setIndustryClassEntity(IndustryClassEntity industryClassEntity) {
		this.industryClassEntity = industryClassEntity;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
}
