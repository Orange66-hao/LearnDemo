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

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 商品租赁
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_rent")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_rent_sequence")
public class ProductRentEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = -7479972106169707008L;
	
	/**租赁周期*/
	private BigDecimal rentperiod;
	
	/**租赁周期单位*/
	private DateTypeEnum rentperiodunit;
	
	/**租赁价格*/
	private BigDecimal rentPrice ;
	
	/**出租价格单位*/
	private PriceUnitEnum rentPriceUnit;
	
	/**出租价格币别*/
	private CurrencyEnum rentPriceType;
	
	/**删除与否 删除标识在基表,这个字段预留,若使用请标识*/
	private int isDel =0 ;
	
	/**商品基表*/
	private ProductEntity productEntity;

	
	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
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

	public BigDecimal getRentperiod() {
		return rentperiod;
	}

	public void setRentperiod(BigDecimal rentperiod) {
		this.rentperiod = rentperiod;
	}

	public DateTypeEnum getRentperiodunit() {
		return rentperiodunit;
	}

	public void setRentperiodunit(DateTypeEnum rentperiodunit) {
		this.rentperiodunit = rentperiodunit;
	}

	public BigDecimal getRentPrice() {
		return rentPrice;
	}

	public void setRentPrice(BigDecimal rentPrice) {
		this.rentPrice = rentPrice;
	}

	public PriceUnitEnum getRentPriceUnit() {
		return rentPriceUnit;
	}

	public void setRentPriceUnit(PriceUnitEnum rentPriceUnit) {
		this.rentPriceUnit = rentPriceUnit;
	}

	public CurrencyEnum getRentPriceType() {
		return rentPriceType;
	}

	public void setRentPriceType(CurrencyEnum rentPriceType) {
		this.rentPriceType = rentPriceType;
	}
	
	
	
	
	
	
	
}
