package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 公司基本信息维护
 * author deng
 * 
 * 20150813
 * version 1.0
 * 
 * */


@Entity
@Table(name="jhj_company_message")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_company_message_sequence")
public class CompanymessageEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -5627921676721182790L;
	
	/**QQ号码*/
	private String  qqnumber;
	/**服务热线*/
	private String hotline;
	/**手机*/
	private String mobile;
	/**传真*/
	private String fax;
	/**E-mail*/
	private String email;
	/**公司logo*/
	private String logo;
	/**注册协议*/
	private String argument;
	/**公司地址*/
	private String address;
	/**邮编*/
	private String compost;
	
	public String getQqnumber() {
		return qqnumber;
	}
	public void setQqnumber(String qqnumber) {
		this.qqnumber = qqnumber;
	}
	public String getHotline() {
		return hotline;
	}
	public void setHotline(String hotline) {
		this.hotline = hotline;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompost() {
		return compost;
	}
	public void setCompost(String compost) {
		this.compost = compost;
	}
	
}
