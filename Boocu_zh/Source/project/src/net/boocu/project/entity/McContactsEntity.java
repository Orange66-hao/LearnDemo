package net.boocu.project.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 行业分类
 * author fang
 * 
 * 20150827
 * version 1.0
 * 
 * */



@Entity
@Table(name="mc_contacts")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_industry_class_sequence")
public class McContactsEntity extends BaseEntity {

	/**
	 * serialVersion
	 */
	private static final long serialVersionUID = -4076239474595799651L;
	/**父分类,如果是01，表示是顶层分类*/
	private String parentid;
	/**联系人姓名*/
	private String name;
	/**职位*/
	private String job;
	/**老师名称*/
	private String teacher;
	/**专业*/
	private String major;
	/**简历*/
	private String resume;
	/**备注*/
	private String remark;
	/**排序*/
	private int sort;
	/**电话*/
	private String phone;
	/**邮箱*/
	private String mail;
	/**关联所属公司名称表*/
	private String mc_company;
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMc_company() {
		return mc_company;
	}
	public void setMc_company(String mc_company) {
		this.mc_company = mc_company;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
}
