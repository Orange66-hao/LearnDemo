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
@Table(name="t_shop_cart")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_shop_cart_sequence")
public class ShopCart extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4669163031692283385L;

	private MemberEntity memberEntity;
	
	private ProductEntity productEntity;
	
	private int count ;
	
	private String url;

	private float price;
	
	private int status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pro_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
