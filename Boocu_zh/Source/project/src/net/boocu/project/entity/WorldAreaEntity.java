package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 区域
 * author deng
 * 
 * 20151126
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_worldarea")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_worldarea_sequence")
public class WorldAreaEntity extends BaseEntity {
		
	/**serialVersionUID*/
	private static final long serialVersionUID = -8294320948120627944L;
	
	/**区域名称*/
	private String AREANAME;
	/**是否国外，0是国内，1是国外*/
	private String ifabroad;
	/**英文*/
	private String AREANAMEEN;

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
