package net.boocu.project.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.ProductEntity.CurrencyEnum;
import net.boocu.project.entity.ProductEntity.PriceUnitEnum;


/**
 * 促销信息管理
 * author deng
 * 
 * 20150730
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_sales")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_sales_sequence")
public class SalesEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 6244986100645245581L;
	/**促销图片*/
	private String image;
	/**排序*/
	private int sort;
	/**促销链接*/
	private String link;
	/**创建人*/
	private String creatuser;
	/**修改人*/
	private String updateuser;
	/**是否显示在促销中 0否    1是 add by fang 20150906*/
	private int isShow;
	/**商品基表*/
	private ProductEntity productEntity;
	/**促销价格*/
	private BigDecimal salesPrice;
	/**促销价格单位*/
	private PriceUnitEnum salesPriceUnit;
	/**促销价格币别*/
	private CurrencyEnum salesPriceType;
	
	/**实际价格--人民币*/
	private BigDecimal realPrice;
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCreatuser() {
		return creatuser;
	}
	public void setCreatuser(String creatuser) {
		this.creatuser = creatuser;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	public int getIsShow() {
		return isShow;
	}
	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}
	@OneToOne
	@JoinColumn(name="product_id")
	public ProductEntity getProductEntity() {
		return productEntity;
	}
	public void setProductEntity(ProductEntity productEntity){
		this.productEntity = productEntity;
	}
	public BigDecimal getSalesPrice() {
		return salesPrice;
	}
	public void setSalesPrice(BigDecimal salesPrice) {
		this.salesPrice = salesPrice;
	}
	public PriceUnitEnum getSalesPriceUnit() {
		return salesPriceUnit;
	}
	public void setSalesPriceUnit(PriceUnitEnum salesPriceUnit) {
		this.salesPriceUnit = salesPriceUnit;
	}
	public CurrencyEnum getSalesPriceType() {
		return salesPriceType;
	}
	public void setSalesPriceType(CurrencyEnum salesPriceType) {
		this.salesPriceType = salesPriceType;
	}
	public BigDecimal getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(BigDecimal realPrice) {
		this.realPrice = realPrice;
	}
}
