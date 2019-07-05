package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 行政区域管理
 * author deng
 * 
 * 20150812
 * version 1.0
 * 
 * */

@Entity
@Table(name="jhj_adminarea")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_adminarea_sequence")
public class AdminareaEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -4871749027888530626L;
			
	/**代码*/
	private String  AREACODE;
	/**名称*/
	private String AREANAME;
	/**英文名称*/
	private String  ifabroad;
	/**区域*/
	private String AREANAMEEN;
	
	public String getAREACODE() {
		return AREACODE;
	}
	public void setAREACODE(String aREACODE) {
		AREACODE = aREACODE;
	}
	public String getAREANAME() {
		return AREANAME;
	}
	public void setAREANAME(String aREANAME) {
		AREANAME = aREANAME;
	}
	public String getIfabroad() {
		return ifabroad;
	}
	public void setIfabroad(String ifabroad) {
		this.ifabroad = ifabroad;
	}
	public String getAREANAMEEN() {
		return AREANAMEEN;
	}
	public void setAREANAMEEN(String aREANAMEEN) {
		AREANAMEEN = aREANAMEEN;
	}
}
