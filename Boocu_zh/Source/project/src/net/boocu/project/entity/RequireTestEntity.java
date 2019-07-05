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
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;
import net.boocu.project.entity.ProductTestEntity.NumberType;
import net.boocu.project.entity.ProductTestEntity.ReportType;


/**
 * 需求测试
 * v1
 * deng in 20151027
 * 
 * */

@Entity
@Table(name="jhj_require_test")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_require_test_sequence")
public class RequireTestEntity extends BaseEntity {


	/**serialVersionUID*/
	private static final long serialVersionUID = -5667599429190543040L;

	/**商品基表*/
	private ProductEntity productEntity;
	/**方案名称*/
	private String projectName;
	/**方案英文名称*/
	private String projectNameEn;
	/**样品名称*/
	private String sampleName;
	/**名称*/
	private String name;
	/**检测报告类型*/
	private ReportType reportType = ReportType.self;
	/**第三方机构名称*/
	private String compName;
	/**检测地点*/
	private ProuseAddressEnum testAddress = ProuseAddressEnum.inChina;
	/**数量*/
	private BigDecimal number;
	/**样品数量单位*/
	private NumberType numberType;
	/**检测详情*/
	private String testDetail;
	/**检测周期*/
	private BigDecimal testPeriod;
	/**检测周期单位*/
	private DateTypeEnum testPeriodUnit = DateTypeEnum.day;
	/**方案介绍*/
	private String projectIntroduction;
	/**预算价格*/
	private BigDecimal budgetPrice ;
	/**预算价格单位*/
	private PriceUnitEnum budgetPriceUnit = PriceUnitEnum.yuan;
	/**预算价格币别*/
	private CurrencyEnum budgetPriceType = CurrencyEnum.rmb;
	
	/**方案介绍英文*/
	private String projectIntroductionEn;
	
	/**仪器列表*/
	private List<InstrumentEntity> productItems= new ArrayList<InstrumentEntity>();
	
	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public ProuseAddressEnum getTestAddress() {
		return testAddress;
	}

	public void setTestAddress(ProuseAddressEnum testAddress) {
		this.testAddress = testAddress;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public BigDecimal getTestPeriod() {
		return testPeriod;
	}

	public void setTestPeriod(BigDecimal testPeriod) {
		this.testPeriod = testPeriod;
	}

	public DateTypeEnum getTestPeriodUnit() {
		return testPeriodUnit;
	}

	public void setTestPeriodUnit(DateTypeEnum testPeriodUnit) {
		this.testPeriodUnit = testPeriodUnit;
	}

	@OneToMany(mappedBy="requireTest")
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

	public NumberType getNumberType() {
		return numberType;
	}

	public void setNumberType(NumberType numberType) {
		this.numberType = numberType;
	}
	
	@Lob
	public String getTestDetail() {
		return testDetail;
	}

	public void setTestDetail(String testDetail) {
		this.testDetail = testDetail;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}
	
}
