package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;

/**
 * 商品类型管理 author deng
 * 
 * 20150812 version 1.0
 * 
 */

@Entity
@Table(name = "jhj_producttype")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_producttype_sequence")
public class ProducttypeEntity extends BaseEntity {

	/** serialVersionUID */
	private static final long serialVersionUID = 386842892598634380L;

	/** 类型名称 */
	private String typeName;
	/** 类型英文名称 */
	private String typeNameEn;
	/** 状态 */
	private int status;
	/** 创建人 */
	private String creatuser;
	/** 修改人 */
	private String updateuser;
	/** 对应数据列表url */
	private String listUrl;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreatuser() {
		return creatuser;
	}

	public void setCreatuser(String creatuser) {
		this.creatuser = creatuser;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	public String getListUrl() {
		return listUrl;
	}

	public void setListUrl(String listUrl) {
		this.listUrl = listUrl;
	}

	public String getTypeNameEn() {
		return typeNameEn;
	}

	public void setTypeNameEn(String typeNameEn) {
		this.typeNameEn = typeNameEn;
	}
}
