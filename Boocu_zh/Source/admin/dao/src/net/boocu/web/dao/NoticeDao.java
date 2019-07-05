/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.dao;

import net.boocu.framework.dao.BaseDao;
import net.boocu.web.Page;
import net.boocu.web.Pageable;
import net.boocu.web.entity.NoticeEntity;

/**
 * Dao - 通知
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface NoticeDao extends BaseDao<NoticeEntity, Long> {

    /**
     * 查找收信人分页
     * 
     * @param receiver
     *            收信人
     * @param pageable
     *            分页信息
     * @return 分页
     */
    Page<NoticeEntity> findReceiverPage(String receiver, Pageable pageable);

}