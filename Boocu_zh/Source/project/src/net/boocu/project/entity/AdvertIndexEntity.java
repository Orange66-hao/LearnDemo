package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 首页广告管理
 * author deng
 * 
 * 20150811
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_advert")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_advert_sequence")
public class AdvertIndexEntity extends BaseEntity {
		
	/**serialVersionUID*/
	private static final long serialVersionUID = -8294320948120627944L;
	
	/**广告名称*/
	private String  name;
	/**广告图片*/
	private String image;
	/**排序*/
	private int sort;
	/**广告链接*/
	private String link;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
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
