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

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.DateTypeEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.ProuseAddressEnum;


/**
 * 产品测试
 * v1
 * deng in 20151027
 * 
 * */

@Entity
@Table(name="jhj_product_test")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_test_sequence")
public class ProductTestEntity extends BaseEntity {

	public enum ReportType{self,thirdSide,all}//公司检测,第三方检测,两种都有
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 3170588356426004030L;
	public enum NumberType {
		individual, stage, box, bar, block;
	}
	
	/**商品基表*/
	private ProductEntity productEntity;
	
	/**方案名称*/
	private String projectName;
	
	/**方案英文名称*/
	private String projectNameEn;
	
	/**测试周期*/
	private BigDecimal testPeriod;
	/**测试周期单位*/
	private DateTypeEnum testPeriodUnit = DateTypeEnum.day;
	/**测试地点*/
	private ProuseAddressEnum testAddress = ProuseAddressEnum.inChina;
	/**单位名称*/
	private String compName;
	/**测试详情*/
	private String testDetail;
	/**检测报告类型*/
	private ReportType reportType = ReportType.self;
	/**投标价格*/
	private BigDecimal price;
	/**投标价格单位*/
	private PriceUnitEnum priceUnit = PriceUnitEnum.yuan ;
	/**投标价格币种*/
	private CurrencyEnum priceLimit = CurrencyEnum.rmb;
	/**样品数量*/
	private BigDecimal number;
	/**样品数量单位*/
	private NumberType numberType;
	
	/**仪器列表*/
	private List<InstrumentEntity> productItems= new ArrayList<InstrumentEntity>();
	
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

	public ProuseAddressEnum getTestAddress() {
		return testAddress;
	}

	public void setTestAddress(ProuseAddressEnum testAddress) {
		this.testAddress = testAddress;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}
	@Lob
	public String getTestDetail() {
		return testDetail;
	}

	public void setTestDetail(String testDetail) {
		this.testDetail = testDetail;
	}

	public ReportType getReportType() {
		return reportType;
	}

	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public PriceUnitEnum getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(PriceUnitEnum priceUnit) {
		this.priceUnit = priceUnit;
	}

	public CurrencyEnum getPriceLimit() {
		return priceLimit;
	}

	public void setPriceLimit(CurrencyEnum priceLimit) {
		this.priceLimit = priceLimit;
	}

	@OneToMany(mappedBy="productTest")
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

	@OneToOne
	@JoinColumn(name="product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	
	public String getProjectNameEn() {
		return projectNameEn;
	}

	public void setProjectNameEn(String projectNameEn) {
		this.projectNameEn = projectNameEn;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public NumberType getNumberType() {
		return numberType;
	}

	public void setNumberType(NumberType numberType) {
		this.numberType = numberType;
	}
	
}
