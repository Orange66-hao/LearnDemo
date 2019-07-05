package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.boocu.framework.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 商品分类管理
 * author deng
 * 
 * 20150812
 * version 1.0
 * 
 * */


@Entity
@Table(name="jhj_productclass")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_productclass_sequence")
@JsonIgnoreProperties({"industryClassEntities"})
public class ProductclassEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = 2935939920543348071L;
	
	/**包含层次关系的id*/
	private String  menuid;
	/**父分类*/
	private String parentid;
	/**是否叶子结果，1是，0不是*/

	private String leaf = "1";
	/**名称*/
	@JsonProperty
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


	private List<BrandAndModelEntity> brandAndModelEntityList=new ArrayList<>();
	//多个产品集合

	private Set<IndustryClassEntity> industryClassEntities = new HashSet<IndustryClassEntity>();

	@ManyToMany(targetEntity = IndustryClassEntity.class,cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE})
	@JoinTable(name = "productClass_industryClass", joinColumns = { @JoinColumn(name = "pro_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "indu_id", referencedColumnName = "id") })
	public Set<IndustryClassEntity> getIndustryClassEntities() {
		return industryClassEntities;
	}

	public void setIndustryClassEntities(Set<IndustryClassEntity> industryClassEntities) {
		this.industryClassEntities = industryClassEntities;
	}
	@JsonProperty
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
	//分类没了级联删除所有的品牌型号
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "productclassEntity")
	public List<BrandAndModelEntity> getBrandAndModelEntityList() {
		return brandAndModelEntityList;
	}

	public void setBrandAndModelEntityList(List<BrandAndModelEntity> brandAndModelEntityList) {
		this.brandAndModelEntityList = brandAndModelEntityList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((menuid == null) ? 0 : menuid.hashCode());
		return result;
	}
}
