package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 方案需求
 * v1
 * deng in 20151028
 * 
 * */

@Entity
@Table(name="jhj_projectNeed")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_projectNeed_sequence")
public class ProjectNeedEntity extends BaseEntity {


	/**serialVersionUID*/
	private static final long serialVersionUID = 6838831667353370463L;

	/**商品基表*/
	private ProductEntity productEntity;
	
	/**方案需求名称*/
	private String projectName;
	
	/**方案需求英文名称*/
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
	private List<InstrumentEntity> InstrumentEntities= new ArrayList<InstrumentEntity>();
	
	@OneToMany(mappedBy="projectNeed")
	public List<InstrumentEntity> getInstrumentEntities() {
		return InstrumentEntities;
	}

	public void setInstrumentEntities(List<InstrumentEntity> instrumentEntities) {
		InstrumentEntities = instrumentEntities;
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
