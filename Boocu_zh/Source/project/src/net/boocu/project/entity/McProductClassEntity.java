package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 商品分类管理
 * author deng
 * 
 * 20150812
 * version 1.0
 * 
 * */


@Entity
@Table(name="mc_productclass")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_productclass_sequence")
public class McProductClassEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 2935939920543348071L;
	
	/**包含层次关系的id*/
	private String  menuid;
	/**父分类*/
	private String parentid;
	/**是否叶子结果，1是，0不是*/
	private String leaf = "1";
	/**名称*/
	private String name;
	/**英文名称*/
	private String nameEn;
	/**排序*/
	private int sort;
	/**描述*/
	private String remark;
	/**创建人*/
	private String createuser;
	/**修改人*/
	private String updateuser;
	/**全名*/
	private String fullName;
	
	private String ids;
	
	
	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	/**全名*/
	private String mc_major;
	
	public String getMenuid() {
		return menuid;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getLeaf() {
		return leaf;
	}

	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateuser() {
		return createuser;
	}

	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getMc_major() {
		return mc_major;
	}

	public void setMc_major(String mc_major) {
		this.mc_major = mc_major;
	}
	
	
}
