/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.admin;

import java.util.Date;

import javax.validation.constraints.Pattern;

import net.boocu.web.enums.ChildEnum;
import net.boocu.web.enums.GenderEnum;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

/**
 * Bean - 个人
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class PersBean {

    /** 姓名 */
    private String name;

    /** 身份证号码 */
    private String idNo;

    /** 身份证签发日期 */
    private Date idIssueDate;

    /** 身份证到期日期 */
    private Date idExpiryDate;

    /** 性别 */
    private GenderEnum gender;

    /** 出生日期 */
    private Date birth;

    /** 邮箱地址 */
    private String email;

    /** 手机号码 */
    private String mobile;

    /** 电话号码 */
    private String phone;

    /** QQ号码 */
    private String qq;

    /** 最高学历 */
    private String educ;

    /** 毕业院校 */
    private String univ;

    /** 入校日期 */
    private Date univEnrolDate;

    /** 籍贯 */
    private Long birthplace;

    /** 户籍 */
    private Long domicilePlace;

    /** 居住地 */
    private Long abodePlace;

    /** 居住地地址 */
    private String abodePlaceAddr;

    /** 居住地邮编 */
    private String abodePlaceZipcode;

    /** 居住地电话 */
    private String abodePlacePhone;
 

    /** 子女情况 */
    private ChildEnum child;

    /** 直系亲属姓名 */
    private String directRelativeName;

    /** 直系亲属关系 */
    private String directRelativeRelation;

    /** 直系亲属手机 */
    private String directRelativeMobile;

    /** 其他联系人姓名 */
    private String otherContactName;

    /** 其他联系人关系 */
    private String otherContactRelation;

    /** 其他联系人手机 */
    private String otherContactMobile;

    /** 更多联系人手机 */
    private String moreContactName;

    /** 更多联系人关系 */
    private String moreContactRelation;

    /** 更多联系人手机 */
    private String moreContactMobile;

    /** 职业状态 */
    private String occup;

    /** 公司名称 */
    private String corpName;

    /** 公司类别 */
    private String corpType;

    /** 公司行业 */
    private String corpDomain;

    /** 公司规模 */
    private String corpScale;

    /** 公司所在地 */
    private Long corpLocality;

    /** 公司地址 */
    private String corpAddr;

    /** 公司邮编 */
    private String corpZipcode;

    /** 职位 */
    private String post;

    /** 工作年限 */
    private String workPeriod;

    /** 月收入 */
    private String monthlyIncome;

    /** 工作邮箱 */
    private String workEmail;

    /** 工作手机 */
    private String workMobile;

    /** 工作电话 */
    private String workPhone;

    /** 工作QQ */
    private String workQq;

    /** 有无房产 */
    private Boolean ownHouse;

    /** 有无房贷 */
    private Boolean withHouseLoan;

    /** 有无车产 */
    private Boolean ownCar;

    /** 有无车贷 */
    private Boolean withCarLoan;

    /** 每月信用卡账单 */
    private String monthlyCreditCardStatement;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]+$")
    @Length(max = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Pattern(regexp = "^([1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3})|([1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}(x|X)))$")
    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public Date getIdIssueDate() {
        return idIssueDate;
    }

    public void setIdIssueDate(Date idIssueDate) {
        this.idIssueDate = idIssueDate;
    }

    public Date getIdExpiryDate() {
        return idExpiryDate;
    }

    public void setIdExpiryDate(Date idExpiryDate) {
        this.idExpiryDate = idExpiryDate;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Email
    @Length(max = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Pattern(regexp = "^1[3,5,8]\\d{9}$")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Length(max = 20)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Length(max = 20)
    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    @Length(max = 20)
    public String getEduc() {
        return educ;
    }

    public void setEduc(String educ) {
        this.educ = educ;
    }

    @Length(max = 50)
    public String getUniv() {
        return univ;
    }

    public void setUniv(String univ) {
        this.univ = univ;
    }

    public Date getUnivEnrolDate() {
        return univEnrolDate;
    }

    public void setUnivEnrolDate(Date univEnrolDate) {
        this.univEnrolDate = univEnrolDate;
    }

    public Long getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(Long birthplace) {
        this.birthplace = birthplace;
    }

    public Long getDomicilePlace() {
        return domicilePlace;
    }

    public void setDomicilePlace(Long domicilePlace) {
        this.domicilePlace = domicilePlace;
    }

    public Long getAbodePlace() {
        return abodePlace;
    }

    public void setAbodePlace(Long abodePlace) {
        this.abodePlace = abodePlace;
    }

    @Length(max = 200)
    public String getAbodePlaceAddr() {
        return abodePlaceAddr;
    }

    public void setAbodePlaceAddr(String abodePlaceAddr) {
        this.abodePlaceAddr = abodePlaceAddr;
    }

    @Length(max = 200)
    public String getAbodePlaceZipcode() {
        return abodePlaceZipcode;
    }

    public void setAbodePlaceZipcode(String abodePlaceZipcode) {
        this.abodePlaceZipcode = abodePlaceZipcode;
    }

    @Length(max = 20)
    public String getAbodePlacePhone() {
        return abodePlacePhone;
    }

    public void setAbodePlacePhone(String abodePlacePhone) {
        this.abodePlacePhone = abodePlacePhone;
    }
 
    public ChildEnum getChild() {
        return child;
    }

    public void setChild(ChildEnum child) {
        this.child = child;
    }

    @Length(max = 50)
    public String getDirectRelativeName() {
        return directRelativeName;
    }

    public void setDirectRelativeName(String directRelativeName) {
        this.directRelativeName = directRelativeName;
    }

    @Length(max = 50)
    public String getDirectRelativeRelation() {
        return directRelativeRelation;
    }

    public void setDirectRelativeRelation(String directRelativeRelation) {
        this.directRelativeRelation = directRelativeRelation;
    }

    @Length(max = 20)
    public String getDirectRelativeMobile() {
        return directRelativeMobile;
    }

    public void setDirectRelativeMobile(String directRelativeMobile) {
        this.directRelativeMobile = directRelativeMobile;
    }

    @Length(max = 50)
    public String getOtherContactName() {
        return otherContactName;
    }

    public void setOtherContactName(String otherContactName) {
        this.otherContactName = otherContactName;
    }

    @Length(max = 50)
    public String getOtherContactRelation() {
        return otherContactRelation;
    }

    public void setOtherContactRelation(String otherContactRelation) {
        this.otherContactRelation = otherContactRelation;
    }

    @Length(max = 20)
    public String getOtherContactMobile() {
        return otherContactMobile;
    }

    public void setOtherContactMobile(String otherContactMobile) {
        this.otherContactMobile = otherContactMobile;
    }

    @Length(max = 50)
    public String getMoreContactName() {
        return moreContactName;
    }

    public void setMoreContactName(String moreContactName) {
        this.moreContactName = moreContactName;
    }

    @Length(max = 50)
    public String getMoreContactRelation() {
        return moreContactRelation;
    }

    public void setMoreContactRelation(String moreContactRelation) {
        this.moreContactRelation = moreContactRelation;
    }

    @Length(max = 20)
    public String getMoreContactMobile() {
        return moreContactMobile;
    }

    public void setMoreContactMobile(String moreContactMobile) {
        this.moreContactMobile = moreContactMobile;
    }

    @Length(max = 50)
    public String getOccup() {
        return occup;
    }

    public void setOccup(String occup) {
        this.occup = occup;
    }

    @Length(max = 50)
    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    @Length(max = 50)
    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(String corpType) {
        this.corpType = corpType;
    }

    @Length(max = 50)
    public String getCorpDomain() {
        return corpDomain;
    }

    public void setCorpDomain(String corpDomain) {
        this.corpDomain = corpDomain;
    }

    @Length(max = 50)
    public String getCorpScale() {
        return corpScale;
    }

    public void setCorpScale(String corpScale) {
        this.corpScale = corpScale;
    }

    public Long getCorpLocality() {
        return corpLocality;
    }

    public void setCorpLocality(Long corpLocality) {
        this.corpLocality = corpLocality;
    }

    @Length(max = 200)
    public String getCorpAddr() {
        return corpAddr;
    }

    public void setCorpAddr(String corpAddr) {
        this.corpAddr = corpAddr;
    }

    @Length(max = 20)
    public String getCorpZipcode() {
        return corpZipcode;
    }

    public void setCorpZipcode(String corpZipcode) {
        this.corpZipcode = corpZipcode;
    }

    @Length(max = 50)
    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Length(max = 50)
    public String getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(String workPeriod) {
        this.workPeriod = workPeriod;
    }

    @Length(max = 200)
    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    @Email
    @Length(max = 200)
    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    @Length(max = 20)
    public String getWorkMobile() {
        return workMobile;
    }

    public void setWorkMobile(String workMobile) {
        this.workMobile = workMobile;
    }

    @Length(max = 20)
    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    @Length(max = 20)
    public String getWorkQq() {
        return workQq;
    }

    public void setWorkQq(String workQq) {
        this.workQq = workQq;
    }

    public Boolean getOwnHouse() {
        return ownHouse;
    }

    public void setOwnHouse(Boolean ownHouse) {
        this.ownHouse = ownHouse;
    }

    public Boolean getWithHouseLoan() {
        return withHouseLoan;
    }

    public void setWithHouseLoan(Boolean withHouseLoan) {
        this.withHouseLoan = withHouseLoan;
    }

    public Boolean getOwnCar() {
        return ownCar;
    }

    public void setOwnCar(Boolean ownCar) {
        this.ownCar = ownCar;
    }

    public Boolean getWithCarLoan() {
        return withCarLoan;
    }

    public void setWithCarLoan(Boolean withCarLoan) {
        this.withCarLoan = withCarLoan;
    }

    @Length(max = 50)
    public String getMonthlyCreditCardStatement() {
        return monthlyCreditCardStatement;
    }

    public void setMonthlyCreditCardStatement(String monthlyCreditCardStatement) {
        this.monthlyCreditCardStatement = monthlyCreditCardStatement;
    }

}