/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service.impl;

import javax.annotation.Resource;

import net.boocu.web.dao.MenuDao;
import net.boocu.web.entity.MenuEntity;
import net.boocu.web.service.MenuService;

import org.springframework.stereotype.Service;

/**
 * Service - 序列号
 * 
 * @author Lv YuLin
 * @version 1.0
 */
@Service
public class MenuServiceImpl  extends BaseServiceImpl<MenuEntity, Long> implements MenuService {

    @Resource
    private MenuDao menuDao;
    
    @Resource
    public void setBaseDao(MenuDao menuDao) {
        super.setBaseDao(menuDao);
    }

}