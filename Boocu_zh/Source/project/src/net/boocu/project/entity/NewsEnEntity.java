package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.project.entity.NewsEntity.NewsTypeEnum;

/**
 * 新闻管理
 * author deng
 * 
 * 20150811
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_news_en")
@SequenceGenerator(name="sequenceGenerator",sequenceName="jhj_news_en_sequence")
public class NewsEnEntity extends BaseEntity {
		
	/**serialVersionUID*/
	private static final long serialVersionUID = 2804989993106670445L;
	
	public enum NewsEnTypeEnum {
		enterprise,industry,international;
	}
	
	/**新闻标题*/
	private String  title;
	/**新闻内容*/
	private String content;
	/**所属区域*/
	private NewsareaEntity newsareaEntity;
	/**阅读次数*/
	private int readtime;
	/**新闻类型*/
	private NewsTypeEnum newstype;
	/**创建人*/
	private String creatuser;
	/**修改人*/
	private String updateuser;
	/**是否推荐*/
	private int isrecommend;
	/**排序*/
	private int sort=0;
	
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
	public int getIsrecommend() {
		return isrecommend;
	}
	public void setIsrecommend(int isrecommend) {
		this.isrecommend = isrecommend;
	}
	public NewsTypeEnum getNewstype() {
		return newstype;
	}
	public void setNewstype(NewsTypeEnum newstype) {
		this.newstype = newstype;
	}
	
	@ManyToOne
	@JoinColumn(name="newsarea_id")
	public NewsareaEntity getNewsareaEntity() {
		return newsareaEntity;
	}
	public void setNewsareaEntity(NewsareaEntity newsareaEntity) {
		this.newsareaEntity = newsareaEntity;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	

	
	
}
