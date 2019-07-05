package net.boocu.project.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.MemberEntity;

/**
 * 
 * 
 * 20160616 version 1.0
 * 
 */
@Entity
@Table(name = "jhj_product_collection")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_product_collection_sequence")
public class ProductCollectionEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7326185577446569006L;

	/**
	 * 商品实体对象
	 */
	private ProductEntity productEntity;
	/**
	 * 用户实体对象
	 */
	private MemberEntity memberEntity;

	/** 是否删除 */
	private Integer isDel;

	@ManyToOne
	@JoinColumn(name = "product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	@ManyToOne
	@JoinColumn(name = "member_id")
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

}
