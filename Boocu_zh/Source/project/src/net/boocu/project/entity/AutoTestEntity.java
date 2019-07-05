package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 自动化测试方案
 * v1
 * deng in 20151027
 * 
 * */

@Entity
@Table(name="jhj_auto_test")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_auto_test_sequence")
public class AutoTestEntity extends BaseEntity {


	/**serialVersionUID*/
	private static final long serialVersionUID = -5667599429190543040L;

	/**商品基表*/
	private ProductEntity productEntity;
	
	/**方案名称*/
	private String projectName;
	
	/**方案英文名称*/
	private String projectNameEn;
	
	/**开发周期*/
	private BigDecimal developPeriod;
	
	/**开发周期单位*/
	private DateTypeEnum developPeriodUnit;
	
	/**预算价格*/
	private BigDecimal budgetPrice ;
	
	/**预算价格单位*/
	private PriceUnitEnum budgetPriceUnit;
	
	/**预算价格币别*/
	private CurrencyEnum budgetPriceType;
	
	/**方案介绍*/
	private String projectIntroduction;
	
	/**方案介绍英文*/
	private String projectIntroductionEn;
	
	/**仪器列表*/
	private List<InstrumentEntity> productItems= new ArrayList<InstrumentEntity>();
	
	@OneToMany(mappedBy="autoTest")
	public List<InstrumentEntity> getProductItems() {
		return productItems;
	}

	public void setProductItems(List<InstrumentEntity> productItems) {
		this.productItems = productItems;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getDevelopPeriod() {
		return developPeriod;
	}

	public void setDevelopPeriod(BigDecimal developPeriod) {
		this.developPeriod = developPeriod;
	}

	public DateTypeEnum getDevelopPeriodUnit() {
		return developPeriodUnit;
	}

	public void setDevelopPeriodUnit(DateTypeEnum developPeriodUnit) {
		this.developPeriodUnit = developPeriodUnit;
	}

	public BigDecimal getBudgetPrice() {
		return budgetPrice;
	}

	public void setBudgetPrice(BigDecimal budgetPrice) {
		this.budgetPrice = budgetPrice;
	}

	public PriceUnitEnum getBudgetPriceUnit() {
		return budgetPriceUnit;
	}

	public void setBudgetPriceUnit(PriceUnitEnum budgetPriceUnit) {
		this.budgetPriceUnit = budgetPriceUnit;
	}

	public CurrencyEnum getBudgetPriceType() {
		return budgetPriceType;
	}

	public void setBudgetPriceType(CurrencyEnum budgetPriceType) {
		this.budgetPriceType = budgetPriceType;
	}

	@Lob
	public String getProjectIntroduction() {
		return projectIntroduction;
	}

	public void setProjectIntroduction(String projectIntroduction) {
		this.projectIntroduction = projectIntroduction;
	}

	@OneToOne
	@JoinColumn(name="product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	
	@Lob
	public String getProjectIntroductionEn() {
		return projectIntroductionEn;
	}

	public void setProjectIntroductionEn(String projectIntroductionEn) {
		this.projectIntroductionEn = projectIntroductionEn;
	}

	public String getProjectNameEn() {
		return projectNameEn;
	}

	public void setProjectNameEn(String projectNameEn) {
		this.projectNameEn = projectNameEn;
	}
	
}
