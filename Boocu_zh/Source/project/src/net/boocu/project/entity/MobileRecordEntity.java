package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
@Entity
@Table(name = "sys_mobile_log")
//@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_mobile_log_sequence")
public class MobileRecordEntity extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8453715517766499897L;

	/** 发送用户*/
	private String userName;
	/** 结果*/
	private boolean result;

	/**详情*/
	
	private String details;
	/*手机号码*/
	private String mobile;
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
}
