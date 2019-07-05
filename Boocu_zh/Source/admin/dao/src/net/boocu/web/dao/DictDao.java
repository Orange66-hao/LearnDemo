/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.DictEntity;

/**
 * Dao - 词典
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface DictDao extends BaseDao<DictEntity, Long> {

    /**
     * 判断名称是否存在（忽略大小写）
     * 
     * @param name
     *            名称
     * @return 名称是否存在
     */
    boolean nameExists(String name);

    /**
     * 判断标识是否存在（忽略大小写）
     * 
     * @param ident
     *            标识
     * @return 标识是否存在
     */
    boolean identExists(String ident);

    /**
     * 根据标识查找词典（忽略大小写）
     * 
     * @param ident
     *            标识
     * @return 词典，不存在时返回NULL
     */
    DictEntity findByIdent(String ident);

}