package net.boocu.project.entity;

import net.boocu.framework.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author zhang
 * @date 2019年03月07日 09:22
 */
@Entity
@Table(name = "sys_flow")
public class FlowEntity extends BaseEntity {
    private Long member_id;
    private Long product_id;
    private EmailRecordEntity.OpenType openType;

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public EmailRecordEntity.OpenType getOpenType() {
        return openType;
    }

    public void setOpenType(EmailRecordEntity.OpenType openType) {
        this.openType = openType;
    }
}
