package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
@Entity
@Table(name = "sys_email_log")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_email_log_sequence")
public class EmailRecordEntity extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8453715517766499897L;
	public enum OpenType{
		PC,MOBILE,OTHER
	}
	/** 发送用户*/
	private String userName;
	/** 结果*/
	private boolean result;

	private Long member_id;


    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

	/**详情*/
	
	private String details;
	
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
