package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;

/***
 * 站内信消息
 * 
 * @author wanbite
 *
 */
@Entity
@Table(name = "jhj_messagetext")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_messagetext_sequence")
public class MessageTextEntity extends BaseEntity {

	private static final long serialVersionUID = 1709582370009090312L;

	private String message_title; // 短消息标题
	private String message_context; // 短消息内容

	public String getMessage_title() {
		return message_title;
	}

	public void setMessage_title(String message_title) {
		this.message_title = message_title;
	}

	public String getMessage_context() {
		return message_context;
	}

	public void setMessage_context(String message_context) {
		this.message_context = message_context;
	}

}
