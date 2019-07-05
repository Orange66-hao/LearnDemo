/*
 * Copyright 2014-2015 四季时科技. All rights reserved.
 * Support: 四季时科技
 */
package net.boocu.web.service;

import net.boocu.web.elem.ImageElem;

/**
 * Service - 图片
 * 
 * @author Lv Yulin
 * @version 1.0
 */
public interface ImageService {

    /**
     * 生成图片
     * 
     * @param image
     *            图片
     */
    void build(ImageElem image);

}