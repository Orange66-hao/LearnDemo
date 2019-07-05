/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao.impl;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import net.boocu.framework.enums.SnTypeEnum;
import net.boocu.framework.util.FreemarkerUtils;
import net.boocu.web.dao.SnDao;
import net.boocu.web.entity.SnEntity;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import freemarker.template.TemplateException;

/**
 * Dao - 序列号
 * 
 * @author Lv Yulin
 * @version 1.0
 */
@Repository("snDaoImpl")
public class SnDaoImpl implements SnDao, InitializingBean {

    /** 支付高低位算法 */
    private HiloOptimizer paymentHiloOptimizer;

    /** 退款高低位算法 */
    private HiloOptimizer refundHiloOptimizer;

    @PersistenceContext
    private EntityManager entityManager;

    /** 支付前缀 */
    @Value("${sn.payment.prefix}")
    private String paymentPrefix;

    /** 支付最大低位值 */
    @Value("${sn.payment.maxLo}")
    private int paymentMaxLo;

    /** 退款前缀 */
    @Value("${sn.refund.prefix}")
    private String refundPrefix;

    /** 退款最大低位值 */
    @Value("${sn.refund.maxLo}")
    private int refundMaxLo;

    @Override
    public void afterPropertiesSet() throws Exception {
        /** 初始化支付高低位算法 */
        paymentHiloOptimizer = new HiloOptimizer(SnTypeEnum.payment, paymentPrefix, paymentMaxLo);
        /** 初始化退款高低位算法 */
        refundHiloOptimizer = new HiloOptimizer(SnTypeEnum.refund, refundPrefix, refundMaxLo);
    }

    @Override
    public String generate(SnTypeEnum type) {
        Assert.notNull(type);

        if (type == SnTypeEnum.payment) {
            return paymentHiloOptimizer.generate();
        } else if (type == SnTypeEnum.refund) {
            return refundHiloOptimizer.generate();
        }
        return null;
    }

    /**
     * 获取末值
     * 
     * @param type
     *            类型
     * @return 末值
     */
    private long getLastValue(SnTypeEnum type) {
        String jpql = "select sns from SnEntity sns where sns.type = :type";
        SnEntity sn = entityManager.createQuery(jpql, SnEntity.class).setFlushMode(FlushModeType.COMMIT)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE).setParameter("type", type).getSingleResult();
        long lastValue = sn.getLastValue();
        sn.setLastValue(lastValue + 1);
        entityManager.merge(sn);
        return lastValue;
    }

    /**
     * 高低位算法
     */
    private class HiloOptimizer {

        /** 类型 */
        private SnTypeEnum type;

        /** 末值 */
        private long lastValue;

        /** 前缀 */
        private String prefix;

        /** 最大低位值 */
        private int maxLo;

        /** 低位值 */
        private int lo;

        /** 高位值 */
        private long hi;

        /**
         * 构造函数
         * 
         * @param type
         *            类型
         * @param prefix
         *            前缀
         * @param maxLo
         *            最大低位值
         */
        public HiloOptimizer(SnTypeEnum type, String prefix, int maxLo) {
            this.type = type;
            this.prefix = prefix != null ? prefix.replace("{", "${") : "";
            this.maxLo = maxLo;
            this.lo = maxLo + 1;
        }

        /**
         * 生成序列号
         * 
         * @return 序列号
         */
        public synchronized String generate() {
            // 低位值 > 最大低位值，时
            if (lo > maxLo) {
                // 获取末值
                lastValue = getLastValue(type);
                // 设置高位值
                hi = lastValue * (maxLo + 1);
                // 高位值初始为0时，低位值初始为1
                lo = lastValue == 0 ? 1 : 0;
            }
            try {
                return FreemarkerUtils.process(prefix, null) + (hi + lo++);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                e.printStackTrace();
            }
            return String.valueOf(hi + lo++);
        }
    }

}