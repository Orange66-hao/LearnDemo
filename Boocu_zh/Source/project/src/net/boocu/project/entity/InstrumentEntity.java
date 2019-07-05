package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;
import net.boocu.project.entity.ProductEntity.QualityStatusEnum;
import net.boocu.project.entity.ProductEntity.RateEnum;
import net.boocu.project.entity.ProductEntity.StatusEnum;
import net.boocu.web.controller.admin.productmng.ProductWantRepairFastController;
import net.boocu.web.entity.AreaEntity;
import net.boocu.web.entity.MemberEntity;


/**
 * 仪器表   属于信息发布中的字表
 * fang in 20160106
 * 
 * */

@Entity
@Table(name="jhj_instrument")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_instrument_sequence")
public class InstrumentEntity extends BaseEntity {
	
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 8978592455062591471L;
	/**商品名称*/
	private String proName;
	/**品牌*/
	private ProductBrandEntity productBrandEntity;
	/**商品类别*/
	private ProductclassEntity productclass;
	/**商品型号*/
	private String proNo;
	/**产品状态*/
	private StatusEnum status;
	/**选件*/
	private String poption;
	/**数量*/
	private Integer proStock=0;
	/**成色*/
	private QualityStatusEnum qualityStatus;
	/**备注*/
	private String remain;
	/**0存在,1删除 */
	private Integer isDel =0;
	/**自动化测试方案*/
	private AutoTestEntity autoTest;
	/**需求方案*/
	private ProjectNeedEntity projectNeed;
	/**产品测试*/
	private ProductTestEntity productTest;
	/**需求测试*/
	private RequireTestEntity requireTest;
	/**计量标准*/
	private CalibrationEntity calibration;
	/**品牌名*/
	private String brandName;
	
	@ManyToOne
	@JoinColumn(name="require_test_id")
	public RequireTestEntity getRequireTest() {
		return requireTest;
	}
	public void setRequireTest(RequireTestEntity requireTest) {
		this.requireTest = requireTest;
	}
	@ManyToOne
	@JoinColumn(name="project_test_id")
	public ProductTestEntity getProductTest() {
		return productTest;
	}
	public void setProductTest(ProductTestEntity productTest) {
		this.productTest = productTest;
	}
	@ManyToOne
	@JoinColumn(name="project_need_id")
	public ProjectNeedEntity getProjectNeed() {
		return projectNeed;
	}
	public void setProjectNeed(ProjectNeedEntity projectNeed) {
		this.projectNeed = projectNeed;
	}
	@ManyToOne
	@JoinColumn(name="auto_test_id")
	public AutoTestEntity getAutoTest() {
		return autoTest;
	}
	public void setAutoTest(AutoTestEntity autoTest) {
		this.autoTest = autoTest;
	}
	@ManyToOne
	@JoinColumn(name="calibration_id")
	public CalibrationEntity getCalibration() {
		return calibration;
	}
	public void setCalibration(CalibrationEntity calibration) {
		this.calibration = calibration;
	}
	public String getPoption() {
		return poption;
	}
	public void setPoption(String poption) {
		this.poption = poption;
	}
	public Integer getProStock() {
		return proStock;
	}
	public void setProStock(Integer proStock) {
		this.proStock = proStock;
	}
	
	public String getRemain() {
		return remain;
	}
	public void setRemain(String remain) {
		this.remain = remain;
	}

	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="brand_id")
	public ProductBrandEntity getProductBrandEntity() {
		return productBrandEntity;
	}
	public void setProductBrandEntity(ProductBrandEntity productBrandEntity) {
		this.productBrandEntity = productBrandEntity;
	}
	public String getProNo() {
		return proNo;
	}
	public void setProNo(String proNo) {
		this.proNo = proNo;
	}

	public StatusEnum getStatus() {
		return status;
	}
	public void setStatus(StatusEnum status) {
		this.status = status;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public QualityStatusEnum getQualityStatus() {
		return qualityStatus;
	}
	public void setQualityStatus(QualityStatusEnum qualityStatus) {
		this.qualityStatus = qualityStatus;
	}
	@ManyToOne
	@JoinColumn(name="pro_class_id")
	public ProductclassEntity getProductclass() {
		return productclass;
	}
	public void setProductclass(ProductclassEntity productclass) {
		this.productclass = productclass;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	
}
