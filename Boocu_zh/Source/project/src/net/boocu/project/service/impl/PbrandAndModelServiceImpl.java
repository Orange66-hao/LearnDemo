/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.boocu.project.service.impl;

import net.boocu.project.dao.BrandAndModelDao;
import net.boocu.project.dao.IndustryClassDao;
import net.boocu.project.dao.PbrandAndModelDao;
import net.boocu.project.entity.BrandAndModelEntity;
import net.boocu.project.entity.IndustryClassEntity;
import net.boocu.project.entity.PbrandAndModelEntity;
import net.boocu.project.entity.ProductclassEntity;
import net.boocu.project.service.BrandAndModelService;
import net.boocu.project.service.PbrandAndModelService;
import net.boocu.project.service.ProductclassService;
import net.boocu.web.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class PbrandAndModelServiceImpl extends BaseServiceImpl<PbrandAndModelEntity,Long> implements PbrandAndModelService{
    @Autowired
    private PbrandAndModelDao pbrandAndModelDao;
    @Autowired
    private IndustryClassDao industryClassDao;

    @Autowired
    private ProductclassService productclassService;
    @Autowired
    public void setBaseDao(PbrandAndModelDao pbrandAndModelDao) {
        super.setBaseDao(pbrandAndModelDao);
    }

    @Override
    public List<Map<String, Object>> queryrows(HttpServletRequest request, HttpServletResponse response, Model model) {
        return pbrandAndModelDao.queryrows(request,response,model);
    }

    @Override
    public List<Map> query(HttpServletRequest request, HttpServletResponse response, Model model) {
        //产品分类数据 industryClass
        List<Map<String, Object>> queryrows = pbrandAndModelDao.query(request, response, model);
        List<Map> resultList2 = new ArrayList<>();
        for (Map<String, Object> entity : queryrows) {

            Map<String, Object> item = new HashMap<String, Object>();

            if (entity.get("p_id") != null && entity.get("p_id") != "") {
                item.put("p_id", entity.get("p_id"));
            }

            if (entity.get("p_name") != null && entity.get("p_name") != "") {
                item.put("p_name", entity.get("p_name"));
            }
            if (entity.get("p_modify_date") != null && entity.get("p_modify_date") != "") {
                item.put("p_modify_date", entity.get("p_modify_date"));
            }
            if (entity.get("i_id") != null && entity.get("i_id") != "") {
                item.put("i_id", entity.get("i_id"));
            }

            if (entity.get("i_name") != null && entity.get("i_name") != "") {
                    item.put("i_name", entity.get("i_name"));
            }

            if (entity.get("p_id") != null && entity.get("i_id") != null) {
                List<Map<String, Object>> list = pbrandAndModelDao.findListByIndustryClassAndProductclass(entity.get("p_id").toString(), entity.get("i_id").toString());
                if(list!=null){
                    List<Map> pbmlist=new ArrayList<>();//品牌型号集合
                    for (Map<String, Object> map : list) {
                        Map bm=new HashMap();
                        bm.put("b_id",map.get("id"));
                        bm.put("b_model",map.get("model"));
                        pbmlist.add(bm);
                    }
                    item.put("bmList",pbmlist);
                }
            }
            resultList2.add(item);
        }
        return resultList2;
    }
}
