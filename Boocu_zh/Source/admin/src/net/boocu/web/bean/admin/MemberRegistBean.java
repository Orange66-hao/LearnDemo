/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.admin;

import java.util.Date;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import net.boocu.web.enums.GenderEnum;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Bean - 会员注册
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class MemberRegistBean {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 姓名 */
    private String name;

    /** 身份证号码 */
    private String idNo;

    /** 性别 */
    private GenderEnum gender;

    /** 出生日期 */
    private Date birth;

    /** 邮箱地址 */
    private String email;

    /** 手机号码 */
    private String mobile;

    /** 是否启用 */
    private Boolean enabled;

    @NotBlank
    @Pattern(regexp = "^[0-9a-z_A-Z\\u4e00-\\u9fa5]+$")
    @Length(min = 2, max = 20)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.lowerCase(username);
    }

    @NotBlank
    @Pattern(regexp = "^[^\\s&\"<>]+$")
    @Length(min = 4, max = 20)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
        this.idNo = StringUtils.upperCase(idNo);
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
        this.email = StringUtils.lowerCase(email);
    }

    @Pattern(regexp = "^1[3,5,8]\\d{9}$")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @NotNull
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 解析身份证号码
     */
    @Transient
    public void parseIdNo() {

        String idNo = getIdNo();

        try {
            if (StringUtils.isNotBlank(idNo)) {

                Date birth = null;
                GenderEnum gender = null;

                // 18位身份证号码
                if (StringUtils.length(idNo) == 18) {
                    // 获取出生日期
                    birth = DateUtils.parseDate(StringUtils.substring(idNo, 6, 14), "yyyyMMdd");
                    // 获取性别
                    gender = Integer.parseInt(StringUtils.left(StringUtils.right(idNo, 2), 1)) % 2 == 0 ? GenderEnum.female
                            : GenderEnum.male;
                }
                // 15位身份证号码
                else if (StringUtils.length(idNo) == 15) {
                    // 获取出生日期
                    birth = DateUtils.parseDate("19" + StringUtils.substring(idNo, 6, 12), "yyyyMMdd");
                    // 获取性别
                    gender = Integer.parseInt(StringUtils.right(idNo, 1)) % 2 == 0 ? GenderEnum.female
                            : GenderEnum.male;
                }

                // 更新性别、出生日期
                if (getGender() == null) {
                    setGender(gender);
                }
                if (getBirth() == null) {
                    setBirth(birth);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}