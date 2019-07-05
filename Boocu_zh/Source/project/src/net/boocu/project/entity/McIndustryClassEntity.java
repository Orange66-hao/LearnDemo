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
@Table(name="mc_industry_class")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_industry_class_sequence")
public class McIndustryClassEntity extends BaseEntity {

	/**
	 * serialVersion
	 */
	private static final long serialVersionUID = -4076239474595799651L;
	
	/**包含层次的ID*/
	private String menuid;
	/**父分类,如果是01，表示是顶层分类*/
	private String parentid;
	/**是否叶子结果，1是，0不是*/
	private String leaf ="1";
	/**名称*/
	private String name;
	/**英文名称*/
	private String nameEn;
	/**排序*/
	private int sort;
	/**描述*/
	private String remark;
	/**状态*/
	private int status;
	/**创建人*/
	private String createuser;
	/**修改人*/
	private String updateuser;
	
	public String getMenuid() {
		return menuid;
	}
	public void setMenuid(String menuid) {
		this.menuid = menuid;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getNameEn() {
		return nameEn;
	}
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}
	
	
}
