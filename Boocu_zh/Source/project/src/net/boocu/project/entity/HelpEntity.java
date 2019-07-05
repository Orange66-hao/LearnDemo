package net.boocu.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;

import net.boocu.framework.entity.BaseEntity;

/**
 * 帮助信息管理 author deng
 * 
 * 20150811 version 1.0
 * 
 */

@Entity
@Table(name = "jhj_help")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_help_sequence")
public class HelpEntity extends BaseEntity {

	/** serialVersionUID */
	private static final long serialVersionUID = -3213487939769750256L;

	/** 新闻标题 */
	private String title;
	/** 新闻标题 */
	private String titleEn;
	/** 新闻内容 */
	private String content;
	/** 新闻英文内容 */
	private String contentEn;

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

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}

	public String getContentEn() {
		return contentEn;
	}

	public void setContentEn(String contentEn) {
		this.contentEn = contentEn;
	}

}
