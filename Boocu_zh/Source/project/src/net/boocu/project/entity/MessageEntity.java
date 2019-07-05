package net.boocu.project.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.boocu.framework.entity.BaseEntity;
import net.boocu.web.entity.AdminEntity;
import net.boocu.web.entity.MemberEntity;

/***
 * 用户站内信
 * 
 * @author wanbite
 *
 */
@Entity
@Table(name = "jhj_message")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_message_sequence")
public class MessageEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8190867223037654009L;
	private AdminEntity sendEntity; // 发送人

	private MemberEntity recEntity; // 接收人

	private int state; // 状态(0：未读, 1 ：已读)

	private MessageTextEntity messageEntity; // 短消息

	@OneToOne
	@JoinColumn(name = "SendID")
	public AdminEntity getSendEntity() {
		return sendEntity;
	}

	public void setSendEntity(AdminEntity sendEntity) {
		this.sendEntity = sendEntity;
	}

	@OneToOne
	@JoinColumn(name = "RecID")
	public MemberEntity getRecEntity() {
		return recEntity;
	}

	public void setRecEntity(MemberEntity recEntity) {
		this.recEntity = recEntity;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@OneToOne
	@JoinColumn(name = "MessageID")
	public MessageTextEntity getMessageEntity() {
		return messageEntity;
	}

	public void setMessageEntity(MessageTextEntity messageEntity) {
		this.messageEntity = messageEntity;
	}

}
