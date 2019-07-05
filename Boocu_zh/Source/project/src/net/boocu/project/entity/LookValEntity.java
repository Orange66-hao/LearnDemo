/**
 * 
 */
package net.boocu.project.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class LookValEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2520267852135973807L;
	
	private Long id;
	
	private Long brandId;
	
	private String proNo;
	
	private String brandName;
	
	private int lookVal;
	
	private Long proId;
	
	private String createTime;
	
	private String userName;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Long getProId() {
		return proId;
	}

	public void setProId(Long proId) {
		this.proId = proId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getProNo() {
		return proNo;
	}

	public void setProNo(String proNo) {
		this.proNo = proNo;
	}

	public int getLookVal() {
		return lookVal;
	}

	public void setLookVal(int lookVal) {
		this.lookVal = lookVal;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
