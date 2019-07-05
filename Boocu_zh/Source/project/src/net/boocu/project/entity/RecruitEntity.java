package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;


/**
 * 模板
 * author fang
 * 
 * 20150730
 * version 1.0
 * 
 * */


/**
 * 使用方法:如UserEntity 
 * 1.去掉序列号,重新生成
 * 2.将 module replaceAll 成 user  (注意大小写敏感,将Case sensitive勾选)
 * 3.将 Module replaceAll 成 User (注意大小写敏感,将Case sensitive勾选)
 * */
@Entity
@Table(name="jhj_recruit")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_recruit_sequence")
public class RecruitEntity extends BaseEntity {
	
	/**serialVersionUID*/
	private static final long serialVersionUID = -6272932429125136809L;
	
	/**标题*/
	private String  title;
	/**内容*/
	private String content;
	/**排序*/
	private int sort;
	/**阅读次数*/
	private int readtime;
	/**创建人*/
	private String creatuser;
	/**修改人*/
	private String updateuser;
	/**是否启用*/
	private int status;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Lob
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getReadtime() {
		return readtime;
	}
	public void setReadtime(int readtime) {
		this.readtime = readtime;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}	
}
