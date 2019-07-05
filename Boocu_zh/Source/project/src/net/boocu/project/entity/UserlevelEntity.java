package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 用户等级管理
 * author deng
 * 
 * 20150813
 * version 1.0
 * 
 * */


@Entity
@Table(name="jhj_userlevel")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_userlevel_sequence")
public class UserlevelEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 7563272198758070375L;
	
	/**用户等级*/
	private String  userlevel;
	/**价格类型*/
	private String pricetype;
	/**状态*/
	private int status;
	/**创建人*/
	private String createuser;
	/**修改人*/
	private String updateuser;
	
	public String getUserlevel() {
		return userlevel;
	}
	public void setUserlevel(String userlevel) {
		this.userlevel = userlevel;
	}
	public String getPricetype() {
		return pricetype;
	}
	public void setPricetype(String pricetype) {
		this.pricetype = pricetype;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateuser() {
		return createuser;
	}
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	public String getUpdateuser() {
		return updateuser;
	}
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}	
	
}
