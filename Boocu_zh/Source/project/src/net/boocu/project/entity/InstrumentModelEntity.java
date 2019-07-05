
package net.boocu.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.boocu.framework.entity.BaseEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
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
@Table(name="jhj_instrumentModel")
public class InstrumentModelEntity extends BaseEntity {

    @JsonProperty
	/**品牌*/
	private ProductBrandEntity productBrandEntity;
    @JsonProperty
	/**型号*/
	private String model;
    //选件
    private  String poption;
    //备注
    private  String remark;
    @NotFound(action=NotFoundAction.IGNORE)
	private ProductclassEntity productclassEntity;

	private Set<IndustryClassModelEntity> industryClassModelEntities=new HashSet<>();
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.PERSIST,CascadeType.MERGE}, optional = true)
	//JoinColumn表示外键的列
	@JoinColumn(name="pro_id")
	public ProductclassEntity getProductclassEntity() {
		return productclassEntity;
	}

	public void setProductclassEntity(ProductclassEntity productclassEntity) {
		this.productclassEntity = productclassEntity;
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


    public String getPoption() {
        return poption;
    }

    public void setPoption(String poption) {
        this.poption = poption;
    }
    //分类没了级联删除所有的品牌型号
    @OneToMany(cascade = {CascadeType.ALL},  mappedBy = "instrumentModelEntity")
    @Fetch(FetchMode.SUBSELECT)
    public Set<IndustryClassModelEntity> getIndustryClassModelEntities() {
        return industryClassModelEntities;
    }

    public void setIndustryClassModelEntities(Set<IndustryClassModelEntity> industryClassModelEntities) {
        this.industryClassModelEntities = industryClassModelEntities;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
