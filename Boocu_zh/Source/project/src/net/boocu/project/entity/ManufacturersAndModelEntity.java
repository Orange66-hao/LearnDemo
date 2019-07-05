
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
@Table(name="jhj_manufacturersAndModel")
@JsonIgnoreProperties("industryClassEntity")
public class ManufacturersAndModelEntity extends BaseEntity {

	/**
	 * serialVersion
	 */

    @JsonProperty
	/**型号*/
	private String model;
	//厂家_名称
    @JsonProperty
	private  String manufacturers;

	private  IndustryClassEntity industryClassEntity;

	private  Long instrument_model_id;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(String manufacturers) {
		this.manufacturers = manufacturers;
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

	public Long getInstrument_model_id() {
		return instrument_model_id;
	}

	public void setInstrument_model_id(Long instrument_model_id) {
		this.instrument_model_id = instrument_model_id;
	}
}
