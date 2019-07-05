package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 商品求购
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_buy")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_buy_sequence")
public class ProductBuyEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -454223291874121928L;
	
	/**预算价格币别*/
	private CurrencyEnum proMarketPriceType;
	
	/**预算价格单位*/
	private PriceUnitEnum proMarketPriceLimit;
	
	/**求购价格*/
	private BigDecimal proMarketPrice;
	
	/**商品基础信息*/
	private ProductEntity productEntity;
	
	/**删除与否  删除标识在基表,这个字段预留,若使用请标识 */
	private int isDel = 0;

	public CurrencyEnum getProMarketPriceType() {
		return proMarketPriceType;
	}

	public void setProMarketPriceType(CurrencyEnum proMarketPriceType) {
		this.proMarketPriceType = proMarketPriceType;
	}

	public PriceUnitEnum getProMarketPriceLimit() {
		return proMarketPriceLimit;
	}

	public void setProMarketPriceLimit(PriceUnitEnum proMarketPriceLimit) {
		this.proMarketPriceLimit = proMarketPriceLimit;
	}

	public BigDecimal getProMarketPrice() {
		return proMarketPrice;
	}

	public void setProMarketPrice(BigDecimal proMarketPrice) {
		this.proMarketPrice = proMarketPrice;
	}

	@OneToOne
	@JoinColumn(name="product_id")
	@NotFound(action=NotFoundAction.IGNORE)
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	
	
	
	
	
}
