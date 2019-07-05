package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
 * 商品维修
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_repair")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_repair_sequence")
public class ProductRepairEntity extends BaseEntity {


	/**serialVersionUID*/
	private static final long serialVersionUID = 7292549699516605752L;
	
	/**商品基表*/
	private ProductEntity productEntity;
	
	/**故障现象*/
	private String bugloko;
	
	/**故障现象英文*/
	private String buglokoEn;
	
	/**常见故障*/
	private String fault;
	
	/**常见故障英文*/
	private String faultEn;
	
	/**维修案例*/
	private String maintaindemo;
	
	/**维修案例英文*/
	private String maintaindemoEn;
	
	/**维修周期*/
	private BigDecimal maintainPeriod;
	
	/**维修周期单位*/
	private DateTypeEnum maintainPeriodunit;
	
	/**工程师所在地*/
	private String maintenanceEngineerAddress;
	
	/**维修价格*/
	private BigDecimal repairPrice;
	
	/**维修价格单位*/
	private PriceUnitEnum repairPriceUnit;
	
	/**维修价格币别*/
	private CurrencyEnum repairPriceType;

	/**标识是否删除 0:存在   1:已删除   删除标识在基表,这个字段预留,若使用请标识*/
	private int isDel ;
	
	@Lob
	public String getBugloko() {
		return bugloko;
	}

	public void setBugloko(String bugloko) {
		this.bugloko = bugloko;
	}

	@Lob
	public String getBuglokoEn() {
		return buglokoEn;
	}

	public void setBuglokoEn(String buglokoEn) {
		this.buglokoEn = buglokoEn;
	}

	@Lob
	public String getFault() {
		return fault;
	}

	public void setFault(String fault) {
		this.fault = fault;
	}

	@Lob
	public String getFaultEn() {
		return faultEn;
	}

	public void setFaultEn(String faultEn) {
		this.faultEn = faultEn;
	}

	@Lob
	public String getMaintaindemo() {
		return maintaindemo;
	}

	public void setMaintaindemo(String maintaindemo) {
		this.maintaindemo = maintaindemo;
	}

	@Lob
	public String getMaintaindemoEn() {
		return maintaindemoEn;
	}

	public void setMaintaindemoEn(String maintaindemoEn) {
		this.maintaindemoEn = maintaindemoEn;
	}
	
	public BigDecimal getMaintainPeriod() {
		return maintainPeriod;
	}

	public void setMaintainPeriod(BigDecimal maintainPeriod) {
		this.maintainPeriod = maintainPeriod;
	}

	public DateTypeEnum getMaintainPeriodunit() {
		return maintainPeriodunit;
	}

	public void setMaintainPeriodunit(DateTypeEnum maintainPeriodunit) {
		this.maintainPeriodunit = maintainPeriodunit;
	}

	public String getMaintenanceEngineerAddress() {
		return maintenanceEngineerAddress;
	}

	public void setMaintenanceEngineerAddress(String maintenanceEngineerAddress) {
		this.maintenanceEngineerAddress = maintenanceEngineerAddress;
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
	
	public BigDecimal getRepairPrice() {
		return repairPrice;
	}

	public void setRepairPrice(BigDecimal repairPrice) {
		this.repairPrice = repairPrice;
	}

	public PriceUnitEnum getRepairPriceUnit() {
		return repairPriceUnit;
	}

	public void setRepairPriceUnit(PriceUnitEnum repairPriceUnit) {
		this.repairPriceUnit = repairPriceUnit;
	}

	public CurrencyEnum getRepairPriceType() {
		return repairPriceType;
	}

	public void setRepairPriceType(CurrencyEnum repairPriceType) {
		this.repairPriceType = repairPriceType;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	
}
