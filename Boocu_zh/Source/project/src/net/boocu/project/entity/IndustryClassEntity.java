package net.boocu.project.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.boocu.framework.entity.BaseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 行业分类
 * author fang
 * 
 * 20150827
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_industry_class")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_industry_class_sequence")
@JsonIgnoreProperties({"productclassEntities"})
public class IndustryClassEntity extends BaseEntity {

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

	//多个仪器集合
	private Set<ProductclassEntity> productclassEntities = new HashSet<ProductclassEntity>();
	//无用 使用品牌型号表来维护关系
	private List<PbrandAndModelEntity> pBrandAndModelEntityList=new ArrayList<>();
	//无用 使用品牌型号表来维护关系
	private List<ManufacturersAndModelEntity> manufacturersAndModelEntityList=new ArrayList<>();

	@ManyToMany(targetEntity = ProductclassEntity.class,cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "productClass_industryClass", joinColumns = { @JoinColumn(name = "indu_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "pro_id", referencedColumnName = "id") })
	public Set<ProductclassEntity> getProductclassEntities() {
		return productclassEntities;
	}

	public void setProductclassEntities(Set<ProductclassEntity> productclassEntities) {
		this.productclassEntities = productclassEntities;
	}

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
	//分类没了级联删除所有的品牌型号
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "industryClassEntity")
	public List<PbrandAndModelEntity> getpBrandAndModelEntityList() {
		return pBrandAndModelEntityList;
	}

	public void setpBrandAndModelEntityList(List<PbrandAndModelEntity> pBrandAndModelEntityList) {
		this.pBrandAndModelEntityList = pBrandAndModelEntityList;
	}
	//分类没了级联删除所有的厂家型号
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "industryClassEntity")
    public List<ManufacturersAndModelEntity> getManufacturersAndModelEntityList() {
        return manufacturersAndModelEntityList;
    }

    public void setManufacturersAndModelEntityList(List<ManufacturersAndModelEntity> manufacturersAndModelEntityList) {
        this.manufacturersAndModelEntityList = manufacturersAndModelEntityList;
    }
}
