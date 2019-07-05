package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 模板
 * author fang
 * 
 * 20150730
 * version 1.0
 * 
 * */


/**
 * 使用方法:如UserEntity 
 * 1.去掉序列号,重新生成
 * 2.将 module replaceAll 成 user  (注意大小写敏感,将Case sensitive勾选)
 * 3.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Entity
@Table(name="yt_module")
@SequenceGenerator(name="sequenceGenerator",sequenceName="yt_module_sequence")
public class ModuleEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -9079761429683168014L;
	
	/**模板名字*/
	private String  moduleName;
	/**文字*/
	private String moduleContent;
	/**整型*/
	private int moduleInt;
	/**时间*/
	private Date moduleDate;
	/**长数字*/
	private BigDecimal moduleBD;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleContent() {
		return moduleContent;
	}
	public void setModuleContent(String moduleContent) {
		this.moduleContent = moduleContent;
	}
	public int getModuleInt() {
		return moduleInt;
	}
	public void setModuleInt(int moduleInt) {
		this.moduleInt = moduleInt;
	}
	public Date getModuleDate() {
		return moduleDate;
	}
	public void setModuleDate(Date moduleDate) {
		this.moduleDate = moduleDate;
	}
	public BigDecimal getModuleBD() {
		return moduleBD;
	}
	public void setModuleBD(BigDecimal moduleBD) {
		this.moduleBD = moduleBD;
	}
	
	
	
}
