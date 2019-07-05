/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.tools.ant.types.resources.selectors.Compare;

import net.boocu.framework.entity.BaseOrderEntity;

/**
 * Entity - 词典
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Entity
@Table(name = "sys_menu")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "sys_menu_sequence")
public class MenuEntity extends BaseOrderEntity implements Cloneable{

    /** serialVersionUID */
    private static final long serialVersionUID = 6390958151840664098L;

    /** 名称 */
    private String title;

    /** 标识 */
    private String url;
 
    /** target */
    private String target;
    
    /** 标识 */
    private int menuType;
    
    /** 标识 */
    private int selected;
    
    /** 标识 */
    private int canexp;
    
    /** 图标 */
    private String icon;
      
    /** 是否有子菜单 */
    private boolean hasChildren = false;
    
    /** 父菜单 */
    private MenuEntity parent;
    
    /** 子菜单 */
    private List<MenuEntity> children = new ArrayList<MenuEntity>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getMenuType() {
		return menuType;
	}

	public void setMenuType(int menuType) {
		this.menuType = menuType;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public int getCanexp() {
		return canexp;
	}

	public void setCanexp(int canexp) {
		this.canexp = canexp;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Transient
	public boolean isHasChildren() {
		if(children.size()>0){
			hasChildren = true;
		}else{
			hasChildren = false;
		}
		return hasChildren;
	}

	@Transient
	public void setHasChildren(boolean hasChildren) {
//		this.hasChildren = hasChildren;
	}

	@ManyToOne(cascade ={CascadeType.REFRESH, CascadeType.PERSIST})
	@JoinColumn(name="pid")
	public MenuEntity getParent() {
		return parent;
	}

	public void setParent(MenuEntity parent) {
		this.parent = parent;
	}


	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.PERSIST,CascadeType.REMOVE})
	public List<MenuEntity> getChildren() {
		children.sort(new Comparator<MenuEntity>(){
			@Override
			public int compare(MenuEntity o1, MenuEntity o2) {
				// TODO Auto-generated method stub
				if(o1.getOrder()!=null && o2.getOrder()!=null){
					return o1.getOrder() - o2.getOrder();
				}
				return 0;
			}
			
		});
		return children;
	}

	public void setChildren(List<MenuEntity> children) {
		this.children = children;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}