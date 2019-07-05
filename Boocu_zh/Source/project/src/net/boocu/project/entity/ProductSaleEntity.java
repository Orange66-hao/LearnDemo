package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_sale")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_sale_sequence")
public class ProductSaleEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -454223291874121928L;
	
	/**商品基础信息*/
	private ProductEntity productEntity;
	/**销售价格*/
	private BigDecimal proShopPrice;
	/**价格币种*/
	private CurrencyEnum priceType; 
	/**价格单位*/
	private PriceUnitEnum priceUnit;
	/**标识是否删除 0:存在   1:已删除   删除标识在基表,这个字段预留,若使用请标识*/
	private int isDel ;
	
	@OneToOne
	@JoinColumn(name="product_id")
	@NotFound(action=NotFoundAction.IGNORE)
	public ProductEntity getProductEntity() {
		return productEntity;
	}
	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	public BigDecimal getProShopPrice() {
		return proShopPrice;
	}
	public void setProShopPrice(BigDecimal proShopPrice) {
		this.proShopPrice = proShopPrice;
	}
	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	public PriceUnitEnum getPriceUnit() {
		return priceUnit;
	}
	public void setPriceUnit(PriceUnitEnum priceUnit) {
		this.priceUnit = priceUnit;
	}
	public CurrencyEnum getPriceType() {
		return priceType;
	}
	public void setPriceType(CurrencyEnum priceType) {
		this.priceType = priceType;
	}
	
	
}
