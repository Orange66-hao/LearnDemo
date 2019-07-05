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
import net.boocu.web.entity.MemberEntity;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "t_delivery_address")
public class AddressEntity  extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5936918757087956498L;
	
	private MemberEntity memberEntity;
	
	private String continent;
	
	private String country;
	
	private String province;
	
	private String city;
	
	private String area;
	
	private String detail;
	
	private String postCode;
	
	private String recvName;

	//移动电话的前缀
	private String codeM;
	
	private String mobilePhone;
	
	//固定电话的前缀
	private String codeTel;

	//固定电话的区号
	private String telQH;
	//固定电话的号码
	private String telNumber;
	//固定电话的分机号
	private String telFj;
	
	private int isDefault;
	
	public String getCodeM() {
		return codeM;
	}

	public void setCodeM(String codeM) {
		this.codeM = codeM;
	}

	public String getCodeTel() {
		return codeTel;
	}

	public void setCodeTel(String codeTel) {
		this.codeTel = codeTel;
	}

	public String getTelQH() {
		return telQH;
	}

	public void setTelQH(String telQH) {
		this.telQH = telQH;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	public String getTelFj() {
		return telFj;
	}

	public void setTelFj(String telFj) {
		this.telFj = telFj;
	}

	public String getRecvName() {
		return recvName;
	}

	public void setRecvName(String recvName) {
		this.recvName = recvName;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
}
