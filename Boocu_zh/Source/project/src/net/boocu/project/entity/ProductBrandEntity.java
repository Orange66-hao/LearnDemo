package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 
 * author fang
 * 
 * 20150807
 * version 1.0
 * 
 * */


@Entity
@Table(name="jhj_product_brand")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_product_brand_sequence")
public class ProductBrandEntity extends BaseEntity {

	/**serialVersionUID*/
	private static final long serialVersionUID = -4310991095967553794L;
	
	/**品牌名称*/
	private String name;
	/**品牌英文名称*/
	private String nameEn;
	/**品牌图片路径*/
	private String image;
	/**国家*/
	private String country;
	/**描述*/
	private String description;
	/**排序*/
	private int sort;
	/**是否推荐 0:否   1:是*/
	private int isRecommend;
	/**状态 0停用    1启用*/
	private int status;
	/**创建者 todo*/
	private String createUser;
	/**更新者 todo*/
	private String updateUser;
	/**区域 todo*/
	private String area;
	/**状态 审核与否 0:未审核   1:已审核 */
	private Integer apprStatus =0 ;
	/**0存在,1删除 */
	private Integer isDel =0;
	
	//private Set<ProductEntity> productEntities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}


	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public int getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(int isRecommend) {
		this.isRecommend = isRecommend;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getApprStatus() {
		return apprStatus;
	}

	public void setApprStatus(Integer apprStatus) {
		this.apprStatus = apprStatus;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	/*@OneToMany(mappedBy="productBrandEntity",cascade=CascadeType.REFRESH)
	public Set<ProductEntity> getProductEntities() {
		return productEntities;
	}

	public void setProductEntities(Set<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}*/

	@Override
	public String toString() {
		return "ProductBrandEntity{" +
				"name='" + name + '\'' +
				", nameEn='" + nameEn + '\'' +
				'}';
	}
}
