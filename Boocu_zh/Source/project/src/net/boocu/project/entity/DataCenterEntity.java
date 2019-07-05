package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 资料中心
 * author deng
 * 
 * 20151023
 * version 1.0
 * 
 * */


@Entity
@Table(name="jhj_dataCenter")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_dataCenter_sequence")
public class DataCenterEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = 7236527443808412872L;
	
	/** 文件地址*/
	private String  fileAddress;
	/**  文件名*/
	private String filename;
	/** 下载次数*/
	private int downTime;
	/** 是否启用*/
	private int status;
	/** 商品信息*/
	private ProductEntity productEntity;
	
	public String getFileAddress() {
		return fileAddress;
	}
	public void setFileAddress(String fileAddress) {
		this.fileAddress = fileAddress;
	}
	public int getDownTime() {
		return downTime;
	}
	public void setDownTime(int downTime) {
		this.downTime = downTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@OneToOne
	@JoinColumn(name="product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}
	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	
	
	
}
