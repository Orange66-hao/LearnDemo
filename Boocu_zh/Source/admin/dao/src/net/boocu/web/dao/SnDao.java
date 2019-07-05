/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.enums.SnTypeEnum;

/**
 * Dao - 序列号
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface SnDao {

    /**
     * 生成序列号
     * 
     * @param type
     *            类型
     * @return 序列号
     */
    String generate(SnTypeEnum type);

}