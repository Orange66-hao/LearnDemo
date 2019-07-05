package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
 * 商品求修
 * v1
 * fang in 20150807
 * 
 * */

@Entity
@Table(name="jhj_product_want_repair")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_want_repair_sequence")
public class ProducWantRepairEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = 5138059453108379281L;

	/**商品基表*/
	private ProductEntity productEntity;
	
	/**故障现象*/
	private String bugloko;
	
	/**故障现象英文*/
	private String buglokoEn;
	
	/**维修周期*/
	private BigDecimal maintainPeriod;
	
	/**维修周期单位*/
	private DateTypeEnum maintainPeriodunit;
	
	/**工程师所在地*/
	private String maintenanceEngineerAddress;
	
	/**故障原因*/
	private String faultCause;
	
	/**故障原因英文*/
	private String faultCauseEn;

	/**预算价格*/
	private BigDecimal repairPrice ;
	
	/**预算价格单位*/
	private PriceUnitEnum repairPriceUnit;
	
	/**预算价格币别*/
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

	public String getBuglokoEn() {
		return buglokoEn;
	}

	@Lob
	public void setBuglokoEn(String buglokoEn) {
		this.buglokoEn = buglokoEn;
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


	@Lob
	public String getFaultCause() {
		return faultCause;
	}

	public void setFaultCause(String faultCause) {
		this.faultCause = faultCause;
	}

	@Lob
	public String getFaultCauseEn() {
		return faultCauseEn;
	}

	public void setFaultCauseEn(String faultCauseEn) {
		this.faultCauseEn = faultCauseEn;
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
