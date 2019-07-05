/**
 * 
 */
package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;

/**
 * @author Administrator
 *
 */

@Entity
@Table(name = "t_order_product")
public class OrderProduct extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5828620761851641158L;

	private Order order;
	
	private ProductEntity product;
	
	private int count;
	
	/** 单价 */
	private float price;
	
	@OneToOne
	@JoinColumn(name = "order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	@OneToOne
	@JoinColumn(name = "product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public ProductEntity getProduct() {
		return product;
	}

	public void setProduct(ProductEntity product) {
		this.product = product;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
