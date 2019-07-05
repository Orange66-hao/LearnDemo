
package net.boocu.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.boocu.framework.entity.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


/**
 * 行业分类
 * author fang
 * 
 * 20150827
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_brandAndModel")
@JsonIgnoreProperties("industryClassEntity")
public class BrandAndModelEntity extends BaseEntity {

	/**
	 * serialVersion
	 */

    @JsonProperty
	/**品牌*/
	private ProductBrandEntity productBrandEntity;
    @JsonProperty
	/**型号*/
	private String model;

	/**备注*/
	private String remark;

	private String sort="1";


	private ProductclassEntity productclassEntity;

	private  IndustryClassEntity industryClassEntity;



	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE}, optional = true)
	// JoinColumn表示外键的列
	@JoinColumn(name="pro_id")
	public ProductclassEntity getProductclassEntity() {
		return productclassEntity;
	}

	public void setProductclassEntity(ProductclassEntity productclassEntity) {
		this.productclassEntity = productclassEntity;
	}
	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE},  optional = true)
	// JoinColumn表示外键的列
	@JoinColumn(name="industry_id")
	public IndustryClassEntity getIndustryClassEntity() {
		return industryClassEntity;
	}

	public void setIndustryClassEntity(IndustryClassEntity industryClassEntity) {
		this.industryClassEntity = industryClassEntity;
	}
    @OneToOne
    @JoinColumn(name="brand_id")
    @NotFound(action=NotFoundAction.IGNORE)
    public ProductBrandEntity getProductBrandEntity() {
        return productBrandEntity;
    }

    public void setProductBrandEntity(ProductBrandEntity productBrandEntity) {
        this.productBrandEntity = productBrandEntity;
    }

}
