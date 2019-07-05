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
 * 权限实体类，继承抽象安全实体类
 * @author yuqs
 * @since 0.1
 */
@Entity
@Table(name = "sys_sec_authority")
public class AuthorityEntity extends BaseEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8349705525996917628L;
	//权限名称
	private String name;
	//权限描述
	private String description;
	
	//是否选择，该字段不需要持久化，仅仅是方便页面控制选择状态
	private Integer selected;
	
	//权限管辖的资源列表（多对多关联）
	private List<ResourceEntity> resources = new ArrayList<ResourceEntity>();
	
	//权限所属的角色列表（多对多关联）
	private List<RoleEntity> roles = new ArrayList<RoleEntity>();
    //权限包含的用户列表（多对多关联）这里表示：用户既可以指定角色，也可指定单独的权限
    private List<AdminEntity> users = new ArrayList<AdminEntity>();
    
    public AuthorityEntity() {}
 
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
	@JoinTable(name="sys_sec_authority_resource",joinColumns={ @JoinColumn(name = "authority_id") }, inverseJoinColumns = { @JoinColumn(name = "resource_id") })
	public List<ResourceEntity> getResources()
	{
		return resources;
	}

	public void setResources(List<ResourceEntity> resources) {
		this.resources = resources;
	}

	@ManyToMany(mappedBy="authorities")
	public List<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleEntity> roles) {
		this.roles = roles;
	}

    @ManyToMany(mappedBy="authorities")
	public List<AdminEntity> getUsers() {
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
}
