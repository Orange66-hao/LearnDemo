/**
 * 
 */
package net.boocu.project.entity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.MemberEntity;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name="jhj_user_inquiry")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_user_inquiry_sequence")
public class UserInquiryEntity extends BaseEntity {

	private static final long serialVersionUID = -1732665179801439011L;
	
	//产品名称
	private String proName;
	
	//优惠价
	private float privilegePrice;
	
	//单价
	private float onePrice;
	
	//访问URL
	private String url;
	
	// 状态 0未处理 1已处理
	private int status;
	
	private MemberEntity memberEntity;
	
	private ProductEntity productEntity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_id")
	@NotFound(action=NotFoundAction.IGNORE)
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@NotFound(action=NotFoundAction.IGNORE)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public float getPrivilegePrice() {
		return privilegePrice;
	}

	public void setPrivilegePrice(float privilegePrice) {
		this.privilegePrice = privilegePrice;
	}

	public float getOnePrice() {
		return onePrice;
	}

	public void setOnePrice(float onePrice) {
		this.onePrice = onePrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
