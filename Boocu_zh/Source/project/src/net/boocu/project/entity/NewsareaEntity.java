package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 新闻区域管理
 * author deng
 * 
 * 20150811
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_newsarea")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_newsarea_sequence")
public class NewsareaEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -9217799779684710646L;
	
	/**名称*/
	private String  name;
	/**排序*/
	private int sort;
	/**状态*/
	private int status;
	/**创建人*/
	private String creatuser;
	/**修改人*/
	private String updateuser;
	
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
		
	
}
