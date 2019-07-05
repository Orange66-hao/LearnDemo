/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.DictWordEntity;

/**
 * Dao - 词典单词
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface DictWordDao extends BaseDao<DictWordEntity, Long> {

    /**
     * 判断名称是否存在（忽略大小写）
     * 
     * @param dictId
     *            词典ID
     * @param name
     *            名称
     * @return 名称是否存在
     */
    boolean nameExists(String dictId, String name);

}