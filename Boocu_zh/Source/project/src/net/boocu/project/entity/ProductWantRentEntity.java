package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

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
 * 商品求租
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_want_rent")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_want_rent_sequence")
public class ProductWantRentEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = -7073746609348538942L;

	/**商品基表*/
	private ProductEntity productEntity;
	
	/**求租预算*/
	private BigDecimal magdebrugBudget;
	
	/**价格币种*/
	private CurrencyEnum magdebrugBudgetType;
	
	/**价格单位*/
	private PriceUnitEnum magdebrugBudgetLimit;
	
	/**起租日期*/
	private Date startRent;
	
	/**租赁周期*/
	private BigDecimal rentperiod;
	
	/**租赁周期单位*/
	private DateTypeEnum rentperiodunit;
	
	/**要求*/
	private String required;
	
	/**删除与否  删除标识在基表,这个字段预留,若使用请标识*/
	private int isDel = 0;

	@OneToOne
	@JoinColumn(name="product_id")
	@NotFound(action=NotFoundAction.IGNORE)
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	public Date getStartRent() {
		return startRent;
	}

	public void setStartRent(Date startRent) {
		this.startRent = startRent;
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

	public BigDecimal getMagdebrugBudget() {
		return magdebrugBudget;
	}

	public void setMagdebrugBudget(BigDecimal magdebrugBudget) {
		this.magdebrugBudget = magdebrugBudget;
	}

	public CurrencyEnum getMagdebrugBudgetType() {
		return magdebrugBudgetType;
	}

	public void setMagdebrugBudgetType(CurrencyEnum magdebrugBudgetType) {
		this.magdebrugBudgetType = magdebrugBudgetType;
	}

	public PriceUnitEnum getMagdebrugBudgetLimit() {
		return magdebrugBudgetLimit;
	}

	public void setMagdebrugBudgetLimit(PriceUnitEnum magdebrugBudgetLimit) {
		this.magdebrugBudgetLimit = magdebrugBudgetLimit;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}
	
	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

	
	
}
