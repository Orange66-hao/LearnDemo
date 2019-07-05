package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.enums.CooperateType;

/**
 * 友情链接管理
 * author deng
 * 
 * 20150811
 * version 1.0
 * 
 * */

@Entity
@Table(name="jhj_friends")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_friends_sequence")
public class FriendsEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -7846720900920519460L;
		
	/**友情链接名称*/
	private String  name;
	/**友情链接英文名称*/
	private String  nameEn;
	/**友情链接分类*/
	private String parentid;
	/**是否叶子结果，1是，0不是*/
	private String leaf ="1";
	/**排序*/
	private int sort;
	/**友情链接*/
	private String link;
	/**创建人*/
	private String creatuser;
	/**修改人*/
	private String updateuser;
	/**友情链接分类 edit by fang 20150906*/
	private CooperateType cooperateType = CooperateType.Customer;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
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
	@Enumerated
	public CooperateType getCooperateType() {
		return cooperateType;
	}
	public void setCooperateType(CooperateType cooperateType) {
		this.cooperateType = cooperateType;
	}
	public String getLeaf() {
		return leaf;
	}
	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}
	public String getNameEn() {
		return nameEn;
	}
	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	
}
