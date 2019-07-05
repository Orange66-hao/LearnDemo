package net.boocu.project.entity;

import java.text.DateFormat;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.framework.util.DateTimeUtil;
import net.boocu.web.entity.MemberEntity;

/**
 * 订阅 发送记录
 * 
 */
@Entity
@Table(name = "jhj_product_subscribe_info")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_product_subscribe_info_sequence")
public class SubscribeInfoEntity extends BaseEntity {
	/** 订阅者 */
	private MemberEntity memberEntity;
	/** 发送订阅的产品记录 */
	private ProductEntity productEntity;
	/** 接收方式 */
	private int model;
	@ManyToOne
	@JoinColumn(name = "member_id")
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	@ManyToOne
	@JoinColumn(name = "product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}


}
