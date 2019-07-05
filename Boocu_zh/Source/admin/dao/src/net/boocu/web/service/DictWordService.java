/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;

import net.boocu.web.entity.DictWordEntity;

/**
 * Service - 词典单词
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface DictWordService extends BaseService<DictWordEntity, Long> {

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

    /**
     * 判断名称是否唯一（忽略大小写）
     * 
     * @param dictId
     *            词典ID
     * @param previousName
     *            修改前名称
     * @param currentName
     *            当前名称
     * @return 名称是否唯一
     */
    boolean nameUnique(String dictId, String previousName, String currentName);

    /**
     * 查找词典单词集合
     * 
     * @param dictId
     *            词典ID
     * @return 词典单词集合
     */
    List<DictWordEntity> findListByDictId(String dictId);

    /**
     * 根据词典标识查找词典单词集合
     * 
     * @param dictIdent
     *            词典标识
     * @return 词典单词单词集合
     */
    List<DictWordEntity> findListByDictIdent(String dictIdent);

}