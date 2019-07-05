/**
 * 
 */
package net.boocu.web.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name = "t_sys_config")
public class SysConfigEntity extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4462381639371136161L;

	private String key;
	
	private String value;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
