package net.boocu.web.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.boocu.framework.entity.BaseEntity;

/**
 * 角色实体类，继承抽象安全实体类
 * @author yuqs
 * @since 0.1
 */
@Entity
@Table(name = "sys_sec_role")
public class RoleEntity extends BaseEntity{
	private static final long serialVersionUID = 2041148498753846675L;
	//角色名称
    private String name;
    //角色描述
    private String description;
    //是否选择，该字段不需要持久化，仅仅是方便页面控制选择状态
    private Integer selected;
 
    /** 是否内置 */
    private Boolean builtin;
    
    //角色拥有的权限列表（多对多关联）
    private List<AuthorityEntity> authorities = new ArrayList<AuthorityEntity>();
    
    //角色所包含的用户列表（多对多关联）
    private List<AdminEntity> users = new ArrayList<AdminEntity>();

    @Column(name = "name", unique = true, nullable = false, length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", length = 500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="sys_sec_role_authority",joinColumns={ @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "authority_id") })
	public List<AuthorityEntity> getAuthorities()
	{
		return authorities;
	}

	public void setAuthorities(List<AuthorityEntity> authorities) {
		this.authorities = authorities;
	}

    @ManyToMany(mappedBy="roles")
	public List<AdminEntity> getUsers() 
	{
		return users;
	}

	public void setUsers(List<AdminEntity> users) {
		this.users = users;
	}

	@Transient
	public Integer getSelected() {
		return selected;
	}

	public void setSelected(Integer selected) {
		this.selected = selected;
	}

	public Boolean getBuiltin() {
		return builtin;
	}

	public void setBuiltin(Boolean builtin) {
		this.builtin = builtin;
	}
	
	
}
