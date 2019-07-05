/**
 * 
 */
package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import net.boocu.framework.entity.BaseEntity;

/**
 * @author Administrator
 *
 */
@Table(name="t_invoice_info")
@Entity
public class InvoiceInfoEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7254226628043106195L;
	
	private Long userId;
	
	private int type;
	
	//如果是普通发票则是 公司或个人姓名，增值发票是单位名称
	private String unitName;
	
	private String recordCode;
	
	private String regAddress;
	
	private String regPhone;
	
	private String bank;
	
	private String bankCode;
	
	private String details;
	
	private AddressEntity addressEntity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recv_addr_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AddressEntity getAddressEntity() {
		return addressEntity;
	}

	public void setAddressEntity(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getRecordCode() {
		return recordCode;
	}

	public void setRecordCode(String recordCode) {
		this.recordCode = recordCode;
	}

	public String getRegAddress() {
		return regAddress;
	}

	public void setRegAddress(String regAddress) {
		this.regAddress = regAddress;
	}

	public String getRegPhone() {
		return regPhone;
	}

	public void setRegPhone(String regPhone) {
		this.regPhone = regPhone;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
