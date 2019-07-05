package net.boocu.project.entity;

import net.boocu.framework.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 删除日志
 *
 * fang in 20190108
 *
 * */
@Entity
@Table(name="delete_log")
public class DeleteLogEntity extends BaseEntity {
    /**删除的公司名称*/
    private String delete_mc_company_name;

    /**删除的公司名称id*/
    private String delete_mc_company_name_id;

    /**删除的联系人id*/
    private String delete_mc_contact_id;

    /**原来公司的负责人*/
    private String delete_blame;

    /**操作人*/
    private String operator;

    /**操作人所在的ip地址*/
    private String operator_address_ip;

    /**操作人的电脑名称*/
    private String operator_host_name;

    public String getDelete_mc_company_name() {
        return delete_mc_company_name;
    }

    public void setDelete_mc_company_name(String delete_mc_company_name) {
        this.delete_mc_company_name = delete_mc_company_name;
    }

    public String getDelete_mc_company_name_id() {
        return delete_mc_company_name_id;
    }

    public void setDelete_mc_company_name_id(String delete_mc_company_name_id) {
        this.delete_mc_company_name_id = delete_mc_company_name_id;
    }

    public String getDelete_mc_contact_id() {
        return delete_mc_contact_id;
    }

    public void setDelete_mc_contact_id(String delete_mc_contact_id) {
        this.delete_mc_contact_id = delete_mc_contact_id;
    }

    public String getDelete_blame() {
        return delete_blame;
    }

    public void setDelete_blame(String delete_blame) {
        this.delete_blame = delete_blame;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator_address_ip() {
        return operator_address_ip;
    }

    public void setOperator_address_ip(String operator_address_ip) {
        this.operator_address_ip = operator_address_ip;
    }

    public String getOperator_host_name() {
        return operator_host_name;
    }

    public void setOperator_host_name(String operator_host_name) {
        this.operator_host_name = operator_host_name;
    }
}
