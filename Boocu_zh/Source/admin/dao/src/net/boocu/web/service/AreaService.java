/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import java.util.List;
import java.util.Map;

import net.boocu.web.entity.AreaEntity;

/**
 * Service - 地区
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface AreaService extends BaseService<AreaEntity, Long> {

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

    /**
     * 判断名称是否唯一（忽略大小写）
     * 
     * @param parentId
     *            上级ID
     * @param previousName
     *            修改前名称
     * @param currentName
     *            当前名称
     * @return 名称是否唯一
     */
    boolean nameUnique(String parentId, String previousName, String currentName);

    /**
     * 查找顶级地区集合
     * 
     * @return 顶级地区集合
     */
    List<AreaEntity> findRoots();

    /**
     * 查找下级地区集合
     * 
     * @param parentId
     *            上级ID
     * @return 下级地区集合
     */
    List<AreaEntity> findChildren(String parentId);
    /**
     * 查找下级地区集合
     * 
     * @param parentId
     *            上级ID
     * @return 下级地区集合
     */
    List<Map<String, Object>> findChildrenBySql(Long parentId);
    /**
     * 查找下级地区集合
     * 
     * @param parentName
     *            上级name
     * @return 下级地区集合
     */
    List<Map<String, Object>> findChildrenBySql(String parentName);

}