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
@Table(name="jhj_user_quote")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_user_quote_sequence")
public class UserQuoteEntity extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3787342608204317050L;

	//产品名称
	private String proName;
	
	//优惠价
	private float privilegePrice;
	
	//单价
	private float originalPrice;
	
	//URL类型用于跳转页面详情
	private String type;
	
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

	public float getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(float originalPrice) {
		this.originalPrice = originalPrice;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
