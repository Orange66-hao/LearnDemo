/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import net.boocu.framework.entity.BaseEntity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Entity - 管理员
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Entity
@Table(name = "sys_admin")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_admin_sequence")
public class AdminEntity extends BaseEntity {

    /** serialVersionUID */
    private static final long serialVersionUID = 6061703023088115650L;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 邮箱地址 */
    private String email;

    /** 部门 */
    private String department;

    /** 姓名 */
    private String name;
    
    /** 类型 */
    private String usertype;

    /** 是否启用 */
    private Boolean enabled = true;

    /** 是否锁定 */
    private Boolean locked = false;

    /** 连续登录失败次数 */
    private Integer loginFailureCount = 0;

    /** 锁定日期 */
    private Date lockedDate;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录日期 */
    private Date loginDate;

    //角色列表（多对多关联）
    private Set<RoleEntity> roles = new HashSet<RoleEntity>();
  
	//权限列表（多对多关联）
	private List<AuthorityEntity> authorities = new ArrayList<AuthorityEntity>();
	 
    @NotBlank(groups = Save.class)
    @Pattern(regexp = "^[0-9a-z_A-Z\\u4e00-\\u9fa5]+$")
    @Length(min = 2, max = 20)
    @Column(nullable = false, updatable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotBlank(groups = Save.class)
    @Pattern(regexp = "^[^\\s&\"<>]+$")
    @Length(min = 4, max = 20)
    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
 
//    @Email
    @Length(max = 200)
    @Column(nullable = true, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Length(max = 200)
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Length(max = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Column(nullable = false)
    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Column(nullable = false)
    public Integer getLoginFailureCount() {
        return loginFailureCount;
    }

    public void setLoginFailureCount(Integer loginFailureCount) {
        this.loginFailureCount = loginFailureCount;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    @Length(max = 200)
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }


    public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="sys_admin_roles",joinColumns={ @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    /**
     * 重写toString方法
     * 
     * @return 全称
     */
    @Override
    public String toString() {
        return getUsername();
    }

    @ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="sys_admin_authoritys",joinColumns={ @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "authority_id") })
	public List<AuthorityEntity> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthorityEntity> authorities) {
		this.authorities = authorities;
	}
    
    

}