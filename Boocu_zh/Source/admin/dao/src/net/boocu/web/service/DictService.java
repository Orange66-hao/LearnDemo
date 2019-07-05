/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;
 
import net.boocu.web.entity.DictEntity;

/**
 * Service - 词典
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface DictService extends BaseService<DictEntity, Long> {

    /**
     * 判断名称是否存在（忽略大小写）
     * 
     * @param name
     *            名称
     * @return 名称是否存在
     */
    boolean nameExists(String name);

    /**
     * 判断名称是否唯一（忽略大小写）
     * 
     * @param previousName
     *            修改前名称
     * @param currentName
     *            当前名称
     * @return 名称是否唯一
     */
    boolean nameUnique(String previousName, String currentName);

    /**
     * 判断标识是否存在（忽略大小写）
     * 
     * @param ident
     *            标识
     * @return 标识是否存在
     */
    boolean identExists(String ident);

    /**
     * 判断标识是否唯一（忽略大小写）
     * 
     * @param previousIdent
     *            修改前标识
     * @param currentIdent
     *            当前标识
     * @return 标识是否唯一
     */
    boolean identUnique(String previousIdent, String currentIdent);

    /**
     * 根据标识查找词典
     * 
     * @param ident
     *            标识
     * @return 词典，若不存在则返回null
     */
    DictEntity findByIdent(String ident);

}