/**
 * 
 */
package net.boocu.project.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;

/**
 * @author Administrator
 *
 */
@Table(name="t_invoice")
@Entity
public class InvoiceEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5376639578224459984L;
	
	private Long userId;
	
	private String orderNumber;
	
	private String brandName;
	
	private String proNo;
	
	private String proName;
	
	private String item;
	
	private String invoiceAmount;
	
	private String applyTime;
	
	private String payCompleteTime;
	
	private int status;
	
	private int type;
	
	private String proIds;
	
	private List<ProductEntity> productList;
	
	@Transient
	public List<ProductEntity> getProductList() {
		return productList;
	}

	public void setProductList(List<ProductEntity> productList) {
		this.productList = productList;
	}

	public String getProIds() {
		return proIds;
	}

	public void setProIds(String proIds) {
		this.proIds = proIds;
	}

	private InvoiceInfoEntity invoiceInfoEntity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "invoice_info_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public InvoiceInfoEntity getInvoiceInfoEntity() {
		return invoiceInfoEntity;
	}

	public void setInvoiceInfoEntity(InvoiceInfoEntity invoiceInfoEntity) {
		this.invoiceInfoEntity = invoiceInfoEntity;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getProNo() {
		return proNo;
	}

	public void setProNo(String proNo) {
		this.proNo = proNo;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getPayCompleteTime() {
		return payCompleteTime;
	}

	public void setPayCompleteTime(String payCompleteTime) {
		this.payCompleteTime = payCompleteTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
