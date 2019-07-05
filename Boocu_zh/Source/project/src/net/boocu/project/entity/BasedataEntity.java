package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 资料库管理
 * author deng
 * 
 * 20150815
 * version 1.0
 * 
 * */

@Entity
@Table(name="jhj_basedata")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_basedata_sequence")
public class BasedataEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -5117937524804785770L;
	
	/**品牌*/
	private ProductBrandEntity productBrandEntity;
	/**型号*/
	private String proNo;
	/**商品名称*/
	private String proName;
	/**商品英文名称*/
	private String proNameEn;
	/**产品分类多选*/
	private String industryClass;
	/**设备类目实体*/
	private ProductclassEntity productclass;
	/**商品图片*/
	private String image;
	/**产品描述*/
	private String proSynopsis;
	/**产品英文描述*/
	private String proSynopsisEn;
	/**详细信息*/
	private String proContent;
	/**详细英文信息*/
	private String proContentEn;
	/**0存在,1删除 */
	private Integer isDel =0;
	/**状态 审核与否 0:未审核   1:已审核  2:驳回*/
	private Integer apprStatus =0 ;
	
	@ManyToOne
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
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getProNameEn() {
		return proNameEn;
	}
	public void setProNameEn(String proNameEn) {
		this.proNameEn = proNameEn;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@ManyToOne
	@JoinColumn(name="pro_class_id")
	public ProductclassEntity getProductclass(){
		return productclass;
	}
	public void setProductclass(ProductclassEntity productclass){
		this.productclass = productclass;
	}
	@Lob
	public String getProSynopsis() {
		return proSynopsis;
	}
	public void setProSynopsis(String proSynopsis) {
		this.proSynopsis = proSynopsis;
	}
	@Lob
	public String getProSynopsisEn() {
		return proSynopsisEn;
	}
	public void setProSynopsisEn(String proSynopsisEn) {
		this.proSynopsisEn = proSynopsisEn;
	}
	@Lob
	public String getProContent() {
		return proContent;
	}
	public void setProContent(String proContent) {
		this.proContent = proContent;
	}
	@Lob
	public String getProContentEn() {
		return proContentEn;
	}
	public void setProContentEn(String proContentEn) {
		this.proContentEn = proContentEn;
	}
	public String getIndustryClass() {
		return industryClass;
	}
	public void setIndustryClass(String industryClass) {
		this.industryClass = industryClass;
	}
	public Integer getIsDel() {
		return isDel;
	}
	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}
	public Integer getApprStatus() {
		return apprStatus;
	}
	public void setApprStatus(Integer apprStatus) {
		this.apprStatus = apprStatus;
	}
}
