/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.bean.admin;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Bean - 账户扣费
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public class AccountChargeBean {

    /** 金额 */
    private BigDecimal amount;

    /** 备注 */
    private String memo;

    @NotNull
    @Min(0)
    @Digits(integer = 19, fraction = 2)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Length(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}