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
 * 站内信
 * 
 * @author wanbite
 *
 */
@Entity
@Table(name = "jhj_sysmessage")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "jhj_sysmessage_sequence")
public class SysMessageEntity extends BaseEntity {
	/** serialVersionUID */
	private static final long serialVersionUID = -4871749029999530626L;

	private MemberEntity sendEntity; //发送人
 
	private AdminEntity recEntity; //接收人 

	private int state; //状态(0：未读,  1 ：已读)

	private MessageTextEntity messageEntity; //短消息

	@OneToOne
	@JoinColumn(name = "SendID")
	public MemberEntity getSendEntity() {
		return sendEntity;
	}

	public void setSendEntity(MemberEntity sendEntity) {
		this.sendEntity = sendEntity;
	}

	@OneToOne
	@JoinColumn(name = "RecID")
	public AdminEntity getRecEntity() {
		return recEntity;
	}

	public void setRecEntity(AdminEntity recEntity) {
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
