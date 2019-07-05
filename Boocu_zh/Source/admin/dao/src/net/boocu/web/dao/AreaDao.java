/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.entity.AreaEntity;

/**
 * Dao - 地区
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface AreaDao extends BaseDao<AreaEntity, Long> {

    /**
     * 判断名称是否存在（忽略大小写）
     * 
     * @param parentId
     *            上级ID
     * @param name
     *            名称
     * @return 名称是否存在
     */
    boolean nameExists(String parentId, String name);

}