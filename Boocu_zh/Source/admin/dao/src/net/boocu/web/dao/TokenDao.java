/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.TokenEntity;

/**
 * Dao - 令牌
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface TokenDao extends BaseDao<TokenEntity, Long> {

    /**
     * 查找令牌（忽略大小写）
     * 
     * @param addr
     *            通讯地址
     * @return 令牌，若不存在则返回null
     */
    TokenEntity findByAddr(String addr);

}