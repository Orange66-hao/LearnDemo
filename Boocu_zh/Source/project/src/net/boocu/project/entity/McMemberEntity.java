package net.boocu.project.entity;


import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 行业分类
 * author fang
 * 
 * 20150827
 * version 1.0
 * 
 * */



@Entity
@Table(name="sys_admin")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_industry_class_sequence")
public class McMemberEntity extends BaseEntity {

	/**
	 * serialVersion
	 */
	private static final long serialVersionUID = -4076239474595799651L;
	
	/**名称*/
	private String name;
	/**登录用户名*/
	private String username;
	/**  登录密码 */
	private String password;
	/**状态*/
	private String status;
	/**备注*/
	private String content;
	/**电话*/
	private String phone;
	/**电话*/
	private String usertype;
	/**排序*/
	private int sort;
	/**电话*/
	private String leaf;
	/**是否启用*/
	private Boolean enabled = true;
	/**是否锁定*/
	private Boolean locked = false;
	
	
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getLeaf() {
		return leaf;
	}
	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public Boolean getLocked() {
		return locked;
	}
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}


	
	
}
